package com.marlontrujillo.eru.dolphin

import com.marlontrujillo.eru.comm.CommunicationsManager
import com.marlontrujillo.eru.tag.Tag
import com.marlontrujillo.eru.alarming.Alarm
import com.marlontrujillo.eru.alarming.Alarming
import com.marlontrujillo.eru.logger.LiveAppender
import com.marlontrujillo.eru.persistence.Container
import com.marlontrujillo.eru.user.User
import org.opendolphin.LogConfig
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry


import static com.marlontrujillo.eru.util.Commands.*
import static com.marlontrujillo.eru.util.DolphinConstants.*

/**
 * Created by mtrujillo on 9/14/2015.
 */
class ServerActions extends DolphinServerAction {

    private final String ID;
    private Map<String, Tag> tagSlotMap;

    ServerActions(String ID) {
        this.ID = ID;
        tagSlotMap = new HashMap<>();
    }

    @Override
    void registerIn(ActionRegistry registry) {
        registry.register(INITIALIZE_SYSTEM){ NamedCommand command, response ->
            println ID + ": Initializing.";
            getServerDolphin().presentationModel(SYSTEM_PM_ID, TYPE_SYSTEM,
                    new DTO(
                            new Slot(ATT_SYSTEM_ID, ID),
                            new Slot(ATT_SYSTEM_OK, true),
                            new Slot(ATT_SYSTEM_STATUS, "STARTED"),
                            new Slot(ATT_COMMUNICATIONS_CONNECTED, ""),
                    ));
            LogConfig.noLogs();
        }

        registry.register(INITIALIZE_PM){ NamedCommand command, response ->
            println ID + ": Initializing Presentation model."

            // Cleaning
            getServerDolphin().removeAllPresentationModelsOfType(TYPE_TAG);
            getServerDolphin().removeAllPresentationModelsOfType(TYPE_TO_WRITE);
            getServerDolphin().removeAllPresentationModelsOfType(TYPE_ALARM);
            getServerDolphin().removeAllPresentationModelsOfType(TYPE_USER);

            // Create AddressToWrite Presentation Model
            final DTO TAG_TO_WRITE_PRESENTATION_MODEL = new DTO(
                    new Slot(ATT_TO_WRITE_NAME, "", tagToWriteQualifier(TAG_TO_WRITE_PM_ID, ATT_TO_WRITE_NAME)),
                    new Slot(ATT_TO_WRITE_NEWVALUE, "", tagToWriteQualifier(TAG_TO_WRITE_PM_ID, ATT_TO_WRITE_NEWVALUE))
            )
            getServerDolphin().presentationModel(TAG_TO_WRITE_PM_ID, TYPE_TO_WRITE, TAG_TO_WRITE_PRESENTATION_MODEL)

            // Create Tags Presentation Models
            Container.getInstance().getTagsAgent().sendAndWait {
                try{
                    final DTO TAGS_PRESENTATION_MODEL = new DTO();
                    for(Tag tag : it){
                        final Slot slotInTagPM = new Slot(tag.name, tag.currentText, tagQualifier(tag.name, ATT_TAG_VALUE));
                        TAGS_PRESENTATION_MODEL.slots.add(slotInTagPM);
                        tagSlotMap.put(tag.getName(), tag);
                    }
                    getServerDolphin().presentationModel(TAGS_PM_ID, TYPE_TAG, TAGS_PRESENTATION_MODEL);
                } catch (Exception e){
                    getServerDolphin().getAt(SYSTEM_PM_ID).getAt(ATT_SYSTEM_OK).setValue(false)
                    println e.localizedMessage;
                }
            }

            // Create alarms Presentation Models
            Alarming.getInstance().getAlarmsAgent().sendAndWait {
                try{
                    for(Alarm alarm : it){
                        final String ALARM_PM_ID = alarm.toString();
                        final DTO ALARM_PRESENTATION_MODEL = new DTO(
                                new Slot(ATT_ALARM_DATE, alarm.timeStamp.toString(), alarmQualifier(ALARM_PM_ID, ATT_ALARM_DATE)),
                                new Slot(ATT_ALARM_DESCRIPTION, alarm.description, alarmQualifier(ALARM_PM_ID, ATT_ALARM_DESCRIPTION)),
                                new Slot(ATT_ALARM_GROUP, alarm.groupName, alarmQualifier(ALARM_PM_ID, ATT_ALARM_GROUP)),
                                new Slot(ATT_ALARM_ACK, alarm.acknowledged, alarmQualifier(ALARM_PM_ID, ATT_ALARM_ACK))
                        );
                        getServerDolphin().presentationModel(ALARM_PM_ID, TYPE_ALARM, ALARM_PRESENTATION_MODEL);
                    }
                } catch (Exception e){
                    getServerDolphin().getAt(SYSTEM_PM_ID).getAt(ATT_SYSTEM_OK).setValue(false)
                    println e.localizedMessage;
                }
            }

            // Create users Presentation Model
            Container.getInstance().getUsersAgent().sendAndWait {
                try{
                    final DTO USER_PRESENTATION_MODEL = new DTO(
                            new Slot(ATT_USER_NAME, ""),
                            new Slot(ATT_USER_PASSWORD, ""),
                            new Slot(ATT_USER_LOGGED, false)
                    );
                    getServerDolphin().presentationModel(USER_PM_ID, TYPE_USER, USER_PRESENTATION_MODEL);
                } catch (Exception e){
                    getServerDolphin().getAt(SYSTEM_PM_ID).getAt(ATT_SYSTEM_OK).setValue(false)
                    println e.localizedMessage;
                }
            }
        }

        registry.register (SYNCHRONIZE_PM) { NamedCommand command, response ->
//            Update TAGS
            for(ServerAttribute attribute : getServerDolphin().getAt(TAGS_PM_ID).getAttributes()){
                attribute.setValue(tagSlotMap.get(attribute.getPropertyName()).getCurrentText())
            }

            // Status
            getServerDolphin().getAt(SYSTEM_PM_ID).getAt(ATT_SYSTEM_STATUS).setValue(LiveAppender.getLastLog())
            getServerDolphin().getAt(SYSTEM_PM_ID).getAt(ATT_COMMUNICATIONS_CONNECTED).setValue(CommunicationsManager.getInstance().isRunning() ? "ONLINE" : "OFFLINE")


            // Update Alarms
            Alarming.getInstance().getAlarmsAgent().sendAndWait { final List<Alarm> ALARMS_IN_ALARMING_MODULE ->
                final List<ServerPresentationModel> ALARMS_IN_PRESENTATION_MODEL  = getServerDolphin().findAllPresentationModelsByType(TYPE_ALARM);

                for(Alarm moduleAlarm : ALARMS_IN_ALARMING_MODULE) {
                    // Look if the module alarm exist in the presentation model. if exist update, if not, add new DTO
                    final String MODULE_ALARM_ID          = moduleAlarm.timeStamp.toString();
                    final ServerPresentationModel alarmPM = ALARMS_IN_PRESENTATION_MODEL.find { it.getAt(ATT_ALARM_DATE).getValue().equals(MODULE_ALARM_ID)}

                    if(alarmPM == null){
                        final DTO dto = new DTO(
                                new Slot(ATT_ALARM_DATE, moduleAlarm.timeStamp.toString(), alarmQualifier(MODULE_ALARM_ID, ATT_ALARM_DATE)),
                                new Slot(ATT_ALARM_DESCRIPTION, moduleAlarm.description, alarmQualifier(MODULE_ALARM_ID, ATT_ALARM_DESCRIPTION)),
                                new Slot(ATT_ALARM_GROUP, moduleAlarm.groupName, alarmQualifier(MODULE_ALARM_ID, ATT_ALARM_GROUP)),
                                new Slot(ATT_ALARM_ACK, moduleAlarm.acknowledged, alarmQualifier(MODULE_ALARM_ID, ATT_ALARM_ACK))
                        );
                        getServerDolphin().presentationModel(MODULE_ALARM_ID, TYPE_ALARM, dto);
                    } else {
                        alarmPM.getAt(ATT_ALARM_ACK).setValue(moduleAlarm.getAcknowledged())
                    }
                }

                // Look alarms deleted in the Alarming module, but alive in the presentation model
                List<ServerPresentationModel> orphanPMs;
                for(ServerPresentationModel alarmInPresentationModel : ALARMS_IN_PRESENTATION_MODEL) {
                    if (ALARMS_IN_ALARMING_MODULE.find { it.timeStamp.toString().equals(alarmInPresentationModel.getAt(ATT_ALARM_DATE).value) } == null) {
                        if (orphanPMs == null) orphanPMs = new ArrayList<>();
                        orphanPMs.add(alarmInPresentationModel)
                    }
                }
                if(orphanPMs != null) orphanPMs.each{ getServerDolphin().remove(it) }
            }
            sleep(800)
        }

        registry.register(CHECK_USER_ENTRY){  NamedCommand command, response ->
            Container.getInstance().getUsersAgent().sendAndWait {
                for(User user : it){
                    if(getServerDolphin().getAt(USER_PM_ID).getAt(ATT_USER_NAME).value.equals(user.getUserName()) &&
                            getServerDolphin().getAt(USER_PM_ID).getAt(ATT_USER_PASSWORD).value.equals(user.getPassword())){
                        getServerDolphin().getAt(USER_PM_ID).getAt(ATT_USER_LOGGED).setValue(true);
                        break;
                    }
                }
            }
        }
    }

}
