package org.assemblits.eru.gui.model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by mtrujillo on 9/9/2015.
 */
@Slf4j
@Component
public class ProjectListener {

    private List<Linker> linkers;
    private ProjectModel projectModel;

    public ProjectListener() {
        linkers = new ArrayList<>();
    }

    public void setProjectModel(ProjectModel projectModel) {
        this.projectModel = projectModel;
    }

    public ProjectModel getProjectModel(){
        return projectModel;
    }

    public void listen(){
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
        this.projectModel.getConnections().forEach(this::installConnectedListener);
        // Future Connections
        this.projectModel.getConnections().addListener((ListChangeListener<Connection>) c -> {
            while (c.next()) {
                for (Connection newConnection : c.getAddedSubList()) {
                    installConnectedListener(newConnection);
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
        this.projectModel.getDisplays().forEach(this::installFXNodeListener);
        this.projectModel.getDisplays().addListener((ListChangeListener<Display>) c -> {
            while (c.next()) {
                for (Display newDisplay : c.getAddedSubList()) {
                    installFXNodeListener(newDisplay);
                }
            }
        });
    }

    private void installFXNodeListener(Display display){
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

    private void installConnectedListener(Connection connection){
        connection.connectedProperty().addListener((observable, wasConnected, isConnected) -> {
            if (isConnected){
                projectModel.getDevices()
                        .stream()
                        .filter(Device::getEnabled)
                        .forEach(device -> {
                            Director commDirector = ApplicationContextHolder.getApplicationContext().getBean(Director.class);
                            ModbusDeviceReader modbusDeviceReader = new ModbusDeviceReader(device);
                            BiConsumer<Director, ModbusDeviceReader> link = (d, reader) -> d.getCommunicators().add(reader);
                            BiConsumer<Director, ModbusDeviceReader> unlink = (d, reader) -> d.getCommunicators().remove(reader);
                            GenericLinker<Director, ModbusDeviceReader> linker = new GenericLinker<>(commDirector, modbusDeviceReader, link, unlink);
                            linker.link();
                            linkers.add(linker);
                        });
                projectModel.getTags()
                        .stream()
                        .filter(Tag::getEnabled)
                        .forEach(tag -> {
                            ObjectProperty<Timestamp> object = tag.getLinkedAddress().timestampProperty();
                            TagAddressInvalidationListener listener = new TagAddressInvalidationListener(tag);
                            InvalidObservableLinker linker = new InvalidObservableLinker(object, listener);
                            linker.link();
                            linkers.add(linker);
                        });
            } else {
                linkers.forEach(Linker::unlink);
            }
        });
    }
}

@Slf4j
class TagAddressInvalidationListener implements InvalidationListener {
    Tag tagToUpdate;

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
