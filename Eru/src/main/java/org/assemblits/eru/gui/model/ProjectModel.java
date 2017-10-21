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

    private Project project;
    private IntegerProperty id;
    private StringProperty name;
    private ObjectProperty<EruGroup> group;
    private ObservableList<Device> devices;
    private ObservableList<Connection> connections;
    private ObservableList<Tag> tags;
    private ObservableList<User> users;
    private ObservableList<Display> displays;

    public void set(Project project) {
        this.project = project;
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
        return project;
    }

    @Override
    public String toString() {
        return "ProjectModel{" + "\n" +
                "\tid=" + id.getValue() +"\n" +
                "\tname=" + name.getValue() + "\n" +
                "\tgroup=" + group.getName() + "\n" +
                "\tdevices=" + devices + "\n" +
                "\tconnections=" + connections + "\n" +
                "\ttags=" + tags + "\n" +
                "\tusers=" + users + "\n" +
                "\tdisplays=" + displays + "\n" +
                '}';
    }
}
