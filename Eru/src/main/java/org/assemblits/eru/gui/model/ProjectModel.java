package org.assemblits.eru.gui.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.assemblits.eru.entities.*;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProjectModel {

    private IntegerProperty id;
    private StringProperty name;
    private ObjectProperty<EruGroup> group;
    private ObservableList<Device> devices;
    private ObservableList<Connection> connections;
    private ObservableList<Tag> tags;
    private ObservableList<User> users;
    private ObservableList<Display> displays;

    public void set(Project project) {
        this.id = new SimpleIntegerProperty(project.getId());
        this.name = new SimpleStringProperty(project.getName());
        this.group = new SimpleObjectProperty<>(project.getGroup());
        this.devices = FXCollections.observableList(project.getDevices());
        this.connections = FXCollections.observableList(project.getConnections());
        this.tags = FXCollections.observableList(project.getTags());
        this.users = FXCollections.observableList(project.getUsers());
        this.displays = FXCollections.observableList(project.getDisplays());
    }

    public Project get() {
        return Project.builder()
                .id(id.get())
                .name(name.get())
                .group(group.get())
                .devices(devices)
                .connections(connections)
                .tags(tags)
                .users(users)
                .displays(displays).build();
    }

    @Override
    public String toString() {
        return "ProjectModel{" +
                "id=" + id.getValue() +
                ", name=" + name.getValue() +
                ", group=" + group.getName() +
                ", devices=" + devices +
                ", connections=" + connections +
                ", tags=" + tags +
                ", users=" + users +
                ", displays=" + displays +
                '}';
    }
}
