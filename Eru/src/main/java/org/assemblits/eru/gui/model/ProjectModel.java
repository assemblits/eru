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

    private ObjectProperty<Project> project;
    private IntegerProperty id;
    private StringProperty name;
    private ObjectProperty<EruGroup> group;
    private ObservableList<Device> devices;
    private ObservableList<Connection> connections;
    private ObservableList<Tag> tags;
    private ObservableList<User> users;
    private ObservableList<Display> displays;

    public ProjectModel() {
    }

    public void load(Project project){
        this.project = new SimpleObjectProperty<>(project);
        this.id = new SimpleIntegerProperty(project.getId());
        this.name = new SimpleStringProperty(project.getName());
        this.group = new SimpleObjectProperty<>(project.getGroup());
        this.devices = FXCollections.observableList(project.getDevices());
        this.connections = FXCollections.observableList(project.getConnections());
        this.tags = FXCollections.observableList(project.getTags());
        this.users = FXCollections.observableList(project.getUsers());
        this.displays = FXCollections.observableList(project.getDisplays());
    }

    public Project getProject(){
        return project.getValue();
    }
}
