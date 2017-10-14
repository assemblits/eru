package org.assemblits.eru.gui.model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.comm.actors.Director;
import org.assemblits.eru.comm.modbus.ModbusDeviceReader;
import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.exception.TagLinkException;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.dynamo.DynamoExtractor;
import org.assemblits.eru.gui.dynamo.base.Dynamo;
import org.assemblits.eru.javafx.GenericLinker;
import org.assemblits.eru.javafx.InvalidObservableLinker;
import org.assemblits.eru.javafx.Linker;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.sql.Timestamp;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by mtrujillo on 9/9/2015.
 */
@Slf4j
@Component
public class ProjectListener {

    private ProjectModel projectModel;

    public void setProjectModel(ProjectModel projectModel) {
        this.projectModel = projectModel;
        listenDevicesChanges();
        listenConnectionsChanges();
        listenTagsChanges();
        listenUsersChanges();
        listenDisplaysChanges();
    }

    private void listenDevicesChanges(){
        // TODO
    }

    private void listenConnectionsChanges(){
        // Current Connections
        this.projectModel.getConnections().forEach(this::installLink);
        // Future Connections
        this.projectModel.getConnections().addListener((ListChangeListener<Connection>) c -> {
            while (c.next()) {
                for (Connection newConnection : c.getAddedSubList()) {
                    installLink(newConnection);
                }
            }
        });
    }

    private void listenTagsChanges(){
        // TODO
    }

    private void listenUsersChanges(){
        // TODO
    }

    private void listenDisplaysChanges(){
        // Current Displays
        this.projectModel.getDisplays().forEach(this::installLink);
        // Future Displays
        this.projectModel.getDisplays().addListener((ListChangeListener<Display>) c -> {
            while (c.next()) {
                for (Display newDisplay : c.getAddedSubList()) {
                    installLink(newDisplay);
                }
            }
        });
    }

    private void installLink(Display display){
        display.fxNodeProperty().addListener((o1, oldNode, newNode) -> {
            if(newNode != null){
                DynamoExtractor extractor = new DynamoExtractor();
                List<Dynamo> dynamos = extractor.extractFrom(newNode);
                projectModel.getTags().forEach(tag ->
                        dynamos.stream()
                                .filter(dynamo  -> dynamo.getCurrentValueTagID() == tag.getId())
                                .forEach(dynamo -> tag.valueProperty().addListener((obj, old, newValue) -> dynamo.setCurrentTagValue(newValue)))
                );
            }
        });
    }

    private void installLink(Connection connection){
        ProjectLinks projectLinks = ApplicationContextHolder.getApplicationContext().getBean(ProjectLinks.class);
        connection.connectedProperty().addListener((observable, wasConnected, isConnected) -> {
            if (isConnected){
                projectModel.getDevices()
                        .stream()
                        .filter(Device::getEnabled)
                        .filter(device -> device.getConnection().equals(connection))
                        .forEach(device -> {
                            Director commDirector = ApplicationContextHolder.getApplicationContext().getBean(Director.class);
                            ModbusDeviceReader reader = new ModbusDeviceReader(device);
                            BiConsumer<Director, ModbusDeviceReader> link = (d, r) -> d.getCommunicators().add(r);
                            BiConsumer<Director, ModbusDeviceReader> unlink = (d, r) -> d.getCommunicators().remove(r);
                            GenericLinker<Director, ModbusDeviceReader> linker = new GenericLinker<>(commDirector, reader, link, unlink);
                            projectLinks.getConnectionLinksContainer().addLink(connection, linker);
                        });
                projectModel.getTags()
                        .stream()
                        .filter(Tag::getEnabled)
                        .filter(tag -> tag.getLinkedAddress() != null)
                        .filter(tag -> tag.getLinkedAddress().getOwner().getConnection().equals(connection))
                        .forEach(tag -> {
                            ObjectProperty<Timestamp> object = tag.getLinkedAddress().timestampProperty();
                            TagAddressInvalidationListener listener = new TagAddressInvalidationListener(tag);
                            InvalidObservableLinker linker = new InvalidObservableLinker(object, listener);
                            projectLinks.getConnectionLinksContainer().addLink(connection, linker);
                        });
                projectLinks.getConnectionLinksContainer().getLinksOf(connection).forEach(Linker::link);
            } else {
                projectLinks.getConnectionLinksContainer().getLinksOf(connection).forEach(Linker::unlink);
            }
        });
    }
}

@Slf4j
class TagAddressInvalidationListener implements InvalidationListener {
    private Tag tagToUpdate;

    TagAddressInvalidationListener(Tag tagToUpdate) {
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
            // TODO
        }
    }

    private void updateValueAndTimestamp() throws Exception {
        if ((tagToUpdate.getLinkedAddress() == null)) throw new TagLinkException(tagToUpdate + "has a no address");
        tagToUpdate.setValue(String.valueOf(tagToUpdate.getLinkedAddress().getCurrentValue()));
        tagToUpdate.setTimestamp(tagToUpdate.getLinkedAddress().getTimestamp());
    }
}
