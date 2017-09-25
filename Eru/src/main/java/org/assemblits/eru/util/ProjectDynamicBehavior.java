package org.assemblits.eru.util;

import org.assemblits.eru.comm.actors.Communicator;
import org.assemblits.eru.comm.actors.Director;
import org.assemblits.eru.comm.modbus.ModbusDeviceReader;
import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.exception.TagLinkException;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.dynamo.EruAlarm;
import org.assemblits.eru.gui.dynamo.EruDisplay;
import org.assemblits.eru.gui.dynamo.EruGauge;
import org.assemblits.eru.gui.dynamo.EruLevelBar;
import org.assemblits.eru.gui.model.ProjectModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Control;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by mtrujillo on 9/9/2015.
 */
@Slf4j
@Component
public class ProjectDynamicBehavior {

    public static final Map<String, String> DYNAMO_ID_VS_TAG_ID = new HashMap<>();
    private final Map<Tag, List<TagLink>> TAG_LINK_MAP = new HashMap<>();
    private final Map<Device, Communicator> deviceCommunicatorMap = new HashMap<>();

    private ProjectModel projectModel;

    public void setProjectModel(ProjectModel projectModel) {
        this.projectModel = projectModel;

        addListeners();
    }

    private void addListeners() {
        // For connections: To update Tags when a connection is been updated
        // Current Connections
        this.projectModel.getConnections().forEach(this::installListenerOnConnection);
        // Future Connections
        this.projectModel.getConnections().addListener((ListChangeListener<Connection>) c -> {
            while (c.next()) {
                for (Connection newConnection : c.getAddedSubList()) {
                    installListenerOnConnection(newConnection);
                }
            }
        });

        // For displays: To update displays when tags are been updated by a connection
        this.projectModel.getDisplays().forEach(this::installListenerOnDisplay);
        this.projectModel.getDisplays().addListener((ListChangeListener<Display>) c -> {
            while (c.next()) {
                for (Display newDisplay : c.getAddedSubList()) {
                    installListenerOnDisplay(newDisplay);
                }
            }
        });
    }

    private void installListenerOnDisplay(Display display){
        display.fxNodeProperty().addListener((observable, oldNode, newNode) -> {
            if(newNode != null){
                linkTagsToFXNode(newNode);
            }
        });
    }

    private void installListenerOnConnection(Connection connection){
        connection.connectedProperty().addListener((observable, wasConnected, isConnected) -> {
            if(isConnected){
                final Director commDirector = ApplicationContextHolder.getApplicationContext().getBean(Director.class);
                if (!commDirector.isAlive()) {
                    commDirector.setDaemon(true);
                    commDirector.start();
                }
                projectModel.getDevices().forEach(device -> linkDevicesToConnections(device, connection));
                projectModel.getTags().forEach(this::linkTagsToDevices);
            } else {
                projectModel.getDevices().forEach(device -> unlinkDevicesFromConnections(device, connection));
                projectModel.getTags().forEach(this::unlinkTagsFromDevices);
            }
        });
    }

    private void linkTagsToFXNode(Node anchorPane) {
        for (String dynamoID : DYNAMO_ID_VS_TAG_ID.keySet()) {
            Control extractedControl = (Control) anchorPane.lookup("#".concat(dynamoID));
            if (extractedControl instanceof EruAlarm) {
                EruAlarm extractedAlarm = (EruAlarm) extractedControl;
                projectModel.getTags()
                        .stream()
                        .filter(tag -> (!extractedAlarm.getCurrentValueTagID().isEmpty()) && (tag.getId() == Integer.valueOf(extractedAlarm.getCurrentValueTagID())))
                        .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> extractedAlarm.setCurrentValue(Boolean.parseBoolean(newValue))));
            } else if (extractedControl instanceof EruDisplay) {
                EruDisplay extractedDisplay = (EruDisplay) extractedControl;
                projectModel.getTags()
                        .stream()
                        .filter(tag -> (!extractedDisplay.getCurrentValueTagID().isEmpty()) && (tag.getId() == Integer.valueOf(extractedDisplay.getCurrentValueTagID())))
                        .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> extractedDisplay.setCurrentText(newValue)));
            } else if (extractedControl instanceof EruGauge) {
                EruGauge extractedGauge = (EruGauge) extractedControl;
                projectModel.getTags()
                        .stream()
                        .filter(tag -> (!extractedGauge.getCurrentValueTagID().isEmpty()) && (tag.getId() == Integer.valueOf(extractedGauge.getCurrentValueTagID())))
                        .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> extractedGauge.setCurrentValue(Double.parseDouble(newValue))));
                projectModel.getTags()
                        .stream()
                        .filter(tag -> (!extractedGauge.getCurrentTitleTagID().isEmpty()) && (tag.getId() == Integer.valueOf(extractedGauge.getCurrentTitleTagID())))
                        .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> extractedGauge.setTitle(newValue)));
            } else if (extractedControl instanceof EruLevelBar) {
                EruLevelBar extractedLevelBar = (EruLevelBar) extractedControl;
                projectModel.getTags()
                        .stream()
                        .filter(tag -> (!extractedLevelBar.getCurrentValueTagID().isEmpty()) && (tag.getId() == Integer.valueOf(extractedLevelBar.getCurrentValueTagID())))
                        .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> extractedLevelBar.setCurrentValue(Double.parseDouble(newValue))));
                projectModel.getTags()
                        .stream()
                        .filter(tag -> (!extractedLevelBar.getCurrentTitleTagID().isEmpty()) && (tag.getId() == Integer.valueOf(extractedLevelBar.getCurrentTitleTagID())))
                        .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> extractedLevelBar.setTitle(newValue)));
            }
        }
    }

    private void linkTagsToDevices(Tag tag) {
        log.debug("Installing updater linkToConnections to {}", tag.getName());
        TagLink link;
        switch (tag.getType()) {
            case INPUT:
                link = new AddressChangeLink(tag);
                tag.getLinkedAddress().timestampProperty().addListener(link);
                registerLink(tag, link);
                break;
            case MASK:
                link = new TagCurrentValueLinkForMaskUpdating(tag.getLinkedTag(), tag);
                tag.getLinkedTag().valueProperty().addListener(link);
                registerLink(tag.getLinkedTag(), link);
                break;
            case MATH:
                projectModel.getTags().stream().filter(t -> tag.getScript().contains(t.getName())).forEach(tagInScript -> {            // Find tags in the script
                    TagLink scriptUpdating = new TagCurrentValueLinkForScriptUpdating(tagInScript, tag);
                    tagInScript.valueProperty().addListener(scriptUpdating);
                    registerLink(tagInScript, scriptUpdating);
                });
                break;
            case STATUS:
                Map<String, String> statusMap = new HashMap<>();                                                      // Create Map value:statusName
                StringTokenizer scriptTokenizer = new StringTokenizer(tag.getScript() == null ? "" : tag.getScript(), ",");
                while (scriptTokenizer.hasMoreElements()) {
                    String pair = scriptTokenizer.nextElement().toString();
                    StringTokenizer pairTokenizer = new StringTokenizer(pair, "=");
                    while (pairTokenizer.hasMoreElements()) {
                        String value = pairTokenizer.nextElement().toString();
                        String status = pairTokenizer.nextElement().toString();
                        statusMap.put(value, status);
                    }
                }
                link = new TagCurrentValueLinkForStatusUpdating(tag.getLinkedTag(), tag, statusMap);
                tag.getLinkedTag().valueProperty().addListener(link);
                registerLink(tag.getLinkedTag(), link);
                break;
            case OUTPUT:
                break;
            case LOGICAL:
                break;
        }
    }

    private void registerLink(Tag tag, TagLink link) {
        if (TAG_LINK_MAP.keySet().contains(tag)) {
            TAG_LINK_MAP.get(tag).add(link);
        } else {
            TAG_LINK_MAP.put(tag, new ArrayList<>(Arrays.asList(link)));
        }
    }

    private void unlinkTagsFromDevices(Tag tag) {
        for (TagLink link : TAG_LINK_MAP.get(tag)) {
            log.debug("Removing updater linkToConnections to {} with linkToConnections {}", tag.getName(), link);
            if (link instanceof AddressChangeLink) {
                tag.getLinkedAddress().timestampProperty().removeListener(link);
            } else {
                tag.timestampProperty().removeListener(link);
            }
        }
        TAG_LINK_MAP.remove(tag);
    }

    private void linkDevicesToConnections(Device device, Connection conn){
        final Director commDirector = ApplicationContextHolder.getApplicationContext().getBean(Director.class);
        if (device.getConnection().equals(conn)){
            deviceCommunicatorMap.put(device, new ModbusDeviceReader(device));
            commDirector.getCommunicators().add(deviceCommunicatorMap.get(device));
        }
    }

    private void unlinkDevicesFromConnections(Device device, Connection conn) {
        if (device.getConnection().equals(conn)) {
            final Communicator deviceReader = deviceCommunicatorMap.get(device);
            if (deviceReader != null) deviceReader.stop();
        }
    }

}

@Slf4j
abstract class TagLink implements InvalidationListener {
    Tag tagToListen;
    Tag tagToUpdate;

    TagLink(Tag tagToListen, Tag tagToUpdate) {
        this.tagToListen = tagToListen;
        this.tagToUpdate = tagToUpdate;
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            updateValueAndTimestamp();
            updateAlarmStatus();
            tagToUpdate.setStatus("OK");
        } catch (Exception e) {
            tagToUpdate.setStatus(e.getLocalizedMessage());
            log.error("Error updating tag", e);
        }
    }

    private void updateAlarmStatus() throws ScriptException {
        if (tagToUpdate.getAlarmEnabled()) {
            tagToUpdate.setAlarmed(Boolean.parseBoolean(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getAlarmScript()))));
        }
    }

    protected abstract void updateValueAndTimestamp() throws Exception;
}

class AddressChangeLink extends TagLink {

    AddressChangeLink(Tag tagToUpdate) {
        super(null, tagToUpdate);
    }

    @Override
    protected void updateValueAndTimestamp() throws Exception {
        if ((tagToUpdate.getLinkedAddress() == null)) throw new TagLinkException(tagToUpdate + "has a no address");
        tagToUpdate.setValue(String.valueOf(tagToUpdate.getLinkedAddress().getCurrentValue()));
        tagToUpdate.setTimestamp(tagToUpdate.getLinkedAddress().getTimestamp());
    }
}

class TagCurrentValueLinkForMaskUpdating extends TagLink {
    TagCurrentValueLinkForMaskUpdating(Tag tagToListen, Tag tagToUpdate) {
        super(tagToListen, tagToUpdate);
    }

    @Override
    protected void updateValueAndTimestamp() throws Exception {
        tagToUpdate.setValue(String.valueOf((Integer.getInteger(tagToListen.getValue())) & tagToUpdate.getMask()));
        tagToUpdate.setTimestamp(tagToListen.getTimestamp());
    }
}

class TagCurrentValueLinkForScriptUpdating extends TagLink {
    TagCurrentValueLinkForScriptUpdating(Tag tagToListen, Tag tagToUpdate) {
        super(tagToListen, tagToUpdate);
    }

    @Override
    protected void updateValueAndTimestamp() throws Exception {
        if (tagToUpdate.getScript() == null || tagToUpdate.getScript().isEmpty() || tagToListen == null)
            throw new TagLinkException(tagToUpdate + "has a null script");
        tagToUpdate.setValue(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getScript())));
        tagToUpdate.setTimestamp(tagToListen.getTimestamp() == null ? new Timestamp(System.currentTimeMillis()) : tagToListen.getTimestamp());
    }
}

class TagCurrentValueLinkForStatusUpdating extends TagLink {
    private final Map<String, String> statusMap;

    TagCurrentValueLinkForStatusUpdating(Tag tagToListen, Tag tagToUpdate, Map<String, String> statusMap) {
        super(tagToListen, tagToUpdate);
        this.statusMap = statusMap;
    }

    @Override
    protected void updateValueAndTimestamp() throws Exception {
        tagToUpdate.setValue(statusMap.get(tagToListen.getValue()));
        tagToUpdate.setTimestamp(tagToListen.getTimestamp());
    }
}