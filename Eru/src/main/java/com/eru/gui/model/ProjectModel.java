package com.eru.gui.model;


import com.eru.entities.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProjectModel {

    IntegerProperty id;
    StringProperty name;
    ObjectProperty<TreeElementsGroup> group;
    ObservableList<Device> devices;
    ObservableList<Connection> connections;
    ObservableList<Tag> tags;
    ObservableList<User> users;
    ObservableList<Display> displays;

    public static ProjectModel from(Project project) {
        return ProjectModel.builder()
                .id(new SimpleIntegerProperty(project.getId()))
                .name(new SimpleStringProperty(project.getName()))
                .group(new SimpleObjectProperty<>(project.getGroup()))
                .devices(FXCollections.observableList(project.getDevices()))
                .connections(FXCollections.observableList(project.getConnections()))
                .tags(FXCollections.observableList(project.getTags()))
                .users(FXCollections.observableList(project.getUsers()))
                .displays(FXCollections.observableList(project.getDisplays()))
                .build();
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
}
