package org.assemblits.eru.util;

import javafx.scene.Parent;
import org.assemblits.eru.comm.actors.Communicator;
import org.assemblits.eru.comm.actors.Director;
import org.assemblits.eru.comm.modbus.ModbusDeviceReader;
import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.exception.TagLinkException;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.dynamo.Dynamo;
import org.assemblits.eru.gui.dynamo.ValuableDynamo;
import org.assemblits.eru.gui.model.ProjectModel;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
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

    private final Map<Tag, List<TagLink>> tagLinksMap = new HashMap<>();
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
                linkTagsToDisplayNode(newNode);
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
                projectModel.getDevices()
                        .stream()
                        .filter(Device::getEnabled)
                        .forEach(device -> linkDevicesToConnections(device, connection));
                projectModel.getTags()
                        .stream()
                        .filter(Tag::getEnabled)
                        .forEach(this::linkTag);
            } else {
                projectModel.getDevices().forEach(device -> unlinkDevicesFromConnections(device, connection));
                projectModel.getTags().forEach(this::unlinkTag);
            }
        });
    }

    private void linkTagsToDisplayNode(Parent displayNode) {
        for (Node innerNode : displayNode.getChildrenUnmodifiable()) {
            if (innerNode instanceof ValuableDynamo) {
                ValuableDynamo dynamo = (ValuableDynamo) innerNode;
                projectModel.getTags()
                        .stream()
                        .filter(tag -> (!dynamo.getCurrentValueTagID().isEmpty()) && (tag.getId() == Integer.valueOf(dynamo.getCurrentValueTagID())))
                        .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> dynamo.setCurrentTagValue(newValue)));
            }
        }
    }

    private void linkTag(Tag tag) {
        switch (tag.getType()) {
            case INPUT:
                TagLink link;
                if (tag.getLinkedAddress() == null) return;
                link = new AddressChangeLink(tag);
                tag.getLinkedAddress().timestampProperty().addListener(link);
                registerLink(tag, link);
                break;
            case SCRIPT:
                projectModel.getTags().stream()
                        .filter(t -> tag.getScript().contains(t.getName()))
                        .forEach(tagInScript -> {            // Find tags in the script
                            TagLink scriptUpdating = new TagCurrentValueLinkForScriptUpdating(tagInScript, tag);
                            tagInScript.valueProperty().addListener(scriptUpdating);
                            registerLink(tagInScript, scriptUpdating);
                        });
                break;
            case OUTPUT:
                break;
            case LOGICAL:
                break;
        }
    }

    private void registerLink(Tag tag, TagLink link) {
        if (tagLinksMap.keySet().contains(tag)) {
            tagLinksMap.get(tag).add(link);
        } else {
            tagLinksMap.put(tag, new ArrayList<>(Arrays.asList(link)));
        }
    }

    private void unlinkTag(Tag tag) {
        if (!tagLinksMap.containsKey(tag)) return;
        for (TagLink link : tagLinksMap.get(tag)) {
            if (link instanceof AddressChangeLink) {
                tag.getLinkedAddress().timestampProperty().removeListener(link);
            }
        }
        tagLinksMap.remove(tag);
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