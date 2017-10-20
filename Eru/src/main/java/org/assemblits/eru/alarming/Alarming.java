package org.assemblits.eru.alarming;

import org.assemblits.eru.entities.Alarm;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.persistence.AlarmRepository;
import org.assemblits.eru.preferences.EruPreferences;
import groovy.lang.Closure;
import groovyx.gpars.agent.Agent;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mtrujillo on 15/10/2014.
 */
@Slf4j
public class Alarming {
    private static final Alarming ourInstance = new Alarming();

    private AlarmRepository alarmRepository;
    private Agent<ObservableList<Alarm>> alarmsAgent;
    private boolean running;
    private StringProperty status;
    private BooleanProperty alarmed;
    private ExecutorService executorService;
    private Map<Tag, AlarmListenerToCreateNewAlarms> tagsRegisteredListeners;
    private EruPreferences eruPreferences;

    /* ********** Constructor ********** */
    private Alarming() {
        running = false;
        alarmRepository = ApplicationContextHolder.getApplicationContext().getBean(AlarmRepository.class);
        eruPreferences = ApplicationContextHolder.getApplicationContext().getBean(EruPreferences.class);
        alarmsAgent = new Agent<>(FXCollections.observableArrayList());
        status = new SimpleStringProperty();
        alarmed = new SimpleBooleanProperty();
        executorService = Executors.newSingleThreadExecutor(r -> {
            Thread newThread = new Thread(r);
            newThread.setDaemon(true);
            return newThread;
        });
        tagsRegisteredListeners = new HashMap<>();
    }

    public static Alarming getInstance() {
        return ourInstance;
    }

    /* ********** Methods ********** */
    public synchronized boolean start() {
        if (running) {
            log.error("Alarming: detected a load command, but the communications are already started.");
        } else {
            try {
                alarmsAgent.getVal().clear();

                // Setting limits for alarms in memory
                final long alarmTableCount = alarmRepository.count();
                final int alarmsToShowLimit = eruPreferences.getAlarmingLimit().getValue();

                // Loading alarms from database
                log.info("Loading {}  of {}  alarms from database.", alarmsToShowLimit, alarmTableCount);
//                final long firstResult = alarmTableCount - alarmsToShowLimit < 0 ? 0 : alarmTableCount - alarmsToShowLimit;
//                final ObservableList<Alarm> databaseAlarms = FXCollections.observableArrayList(alarmDao.findEntities("timeStamp", Dao.Order.ASC, alarmsToShowLimit, firstResult));
//                alarmsAgent.updateValue(databaseAlarms);

                final ObservableList<Alarm> databaseAlarms =
                        FXCollections.observableArrayList(alarmRepository.findAllByOrderByTimeStampAsc(
                                new PageRequest((int) (alarmTableCount / alarmsToShowLimit), alarmsToShowLimit)));
                alarmsAgent.updateValue(databaseAlarms);

                updateStatus();

                running = true;

//                installAlarmListenerOnTags(Container.getInstance().getTagsAgent().getInstantVal());
                log.info("Alarming: alarms loaded.");
            } catch (Exception e) {
                final String errorMSG = "Alarming module cannot load.";
                status.setValue(errorMSG);
                alarmed.setValue(true);
                log.error(errorMSG, e);
            }
        }
        return running;
    }

    public synchronized void stop() {
        removeAlarmListenerOnRegisteredTags();
        running = false;
    }

    public void load(Alarm newAlarm) {
        if (newAlarm == null) return;

        final int alarmsInDatabaseLimit = eruPreferences.getAlarmingLimit().getValue();
        final int alarmsInMemoryLimit = eruPreferences.getAlarmingLimit().getValue();

        executorService.execute(() -> {
                    // In Database
                    alarmRepository.save(newAlarm);
                    while (alarmRepository.count() > alarmsInDatabaseLimit) {
                        alarmRepository.delete(alarmRepository.findFirstByOrderByTimeStampAsc());
                    }

                    // In memory
                    alarmsAgent.send(new Closure(this) {
                        void doCall(ObservableList<Alarm> alarms) {
                            alarms.add(newAlarm);
                            while (alarms.size() > alarmsInMemoryLimit) {
                                alarms.remove(0);
                            }
                        }
                    });

                    // Update status
                    updateStatus();
                }
        );
    }

    public void installAlarmListenerOnTags(List<Tag> tagsToListen) {
        log.info("Alarming: Installing listeners on tags:");

        if (tagsRegisteredListeners.size() > 0) {
            removeAlarmListenerOnRegisteredTags();
        }

        // Check if Alarming module is running, has to be running cause the observe create new alarms on database.
        if (running) {
            tagsToListen.stream()
                    .filter(Tag::getAlarmEnabled)
                    .forEach(alarmEnabledTag -> {
                        AlarmListenerToCreateNewAlarms listener = new AlarmListenerToCreateNewAlarms(alarmEnabledTag);
                        alarmEnabledTag.alarmedProperty().addListener(listener);
                        tagsRegisteredListeners.put(alarmEnabledTag, listener);
                    });
            log.info("Alarming: listeners on tags installed.");
        } else {
            log.error("Alarming: Tags cannot be observe because there are no db connection in Alarming module.");
        }
    }

    public void removeAlarmListenerOnRegisteredTags() {
        log.info("Alarming: Removing listeners on tags...");
        for (Tag tag : tagsRegisteredListeners.keySet()) {
            tag.alarmedProperty().removeListener(tagsRegisteredListeners.get(tag));
        }
        tagsRegisteredListeners.clear();
        log.info("Alarming: listeners on tags removed.");
    }

    public void setAcknowledgeAllAlarms(String userInCharge) {
        log.info("Acknowledging all alarms...");
        alarmsAgent.send(new Closure(this) {
            void doCall(ObservableList<Alarm> alarms) {
                alarms.forEach(alarm -> {
                    if (!alarm.getAcknowledged()) {
                        alarm.setAcknowledged(true);
                        alarm.setUserInCharge(userInCharge);
                        alarmRepository.save(alarm);
                    }
                });
            }
        });
    }

    // TODO:
//    public void setAcknowledge(String alarmTimeStamp,String userInCharge){
//        if (alarmTimeStamp == null) return;
//
//        dtoAlarmsAgent.send(new Closure(this){
//            void doCall(List<DTO> dtos){
//                dtos.stream()
//                        .filter(dto -> dto.getSlots().get(0).toString().equals(alarmTimeStamp))
//                        .forEach(selectedDTO -> {
//
//                            if (!al.getAcknowledged()) {
//                                al.setAcknowledged(true);
//                                al.setUserInCharge(userInCharge);
//                                alarmDaoList.updateOnDB(al);
//                                updateStatus();
//                            }
//                        });
//            }
//        });
//    }

    /* ********** Private Methods ********** */
    private void updateStatus() {
        // Clean Status
        alarmed.set(false);
        status.set("");

        // Look new Alarm not acknowledge
        alarmsAgent.send(new Closure(this) {
            void doCall(ObservableList<Alarm> alarms) {
                for (Alarm a : alarms) {
                    if (!a.getAcknowledged()) {
                        status.set("LAST ALARM: " + a.getDescription());
                        alarmed.set(true);
                    }
                }
            }
        });
    }

    /* ********** Setters and Getters ********** */
    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public boolean getAlarmed() {
        return alarmed.get();
    }

    public BooleanProperty alarmedProperty() {
        return alarmed;
    }

    public Agent<ObservableList<Alarm>> getAlarmsAgent() {
        return alarmsAgent;
    }

    public boolean isRunning() {
        return running;
    }
}

class AlarmListenerToCreateNewAlarms implements InvalidationListener {
    private final Tag tagToListen;

    public AlarmListenerToCreateNewAlarms(Tag tagToListen) {
        this.tagToListen = tagToListen;
    }

    @Override
    public void invalidated(Observable observable) {
        if (tagToListen.getAlarmed()) {
            Alarming.getInstance().load(
                    new Alarm(Timestamp.from(Instant.now()),
                            tagToListen.getDescription().concat(" alarm = ACTIVATED"),
                            "",
                            tagToListen.getGroupName(),
                            Alarm.Priority.HIHI));
        } else {
            Alarming.getInstance().load(
                    new Alarm(Timestamp.from(Instant.now()),
                            tagToListen.getDescription().concat(" alarm = DEACTIVATED"),
                            "",
                            tagToListen.getGroupName(),
                            Alarm.Priority.NOMINAL));
        }
    }
}