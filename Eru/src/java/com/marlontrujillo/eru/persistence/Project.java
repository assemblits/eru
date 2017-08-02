package com.marlontrujillo.eru.persistence;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.device.Device;
import com.marlontrujillo.eru.gui.toolbars.tree.Group;
import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.user.User;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 7/30/17.
 */
@Entity
@Table(name = "project", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Project {

    private IntegerProperty id;
    private StringProperty name;
    private ObjectProperty<Group> group;
    private ObservableList<Device> devices;
    private ObservableList<Connection> connections;
    private ObservableList<Tag> tags;
    private ObservableList<User> users;

    public Project() {
        this.id = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty("");
        this.group = new SimpleObjectProperty<>();
        this.devices = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.connections = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.tags = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.users = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(int id) {
        this.id.set(id);
    }

    @Column(name = "name")
    public String getName() {
        return name.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    public Group getGroup() {
        return group.get();
    }
    public ObjectProperty<Group> groupProperty() {
        return group;
    }
    public void setGroup(Group group) {
        this.group.set(group);
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Device> getDevices() {
        return new ArrayList<>(devices);
    }
    public ObservableList<Device> devicesProperty(){
        return devices;
    }
    public void setDevices(List<Device> devices) {
        this.devices.setAll(devices);
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Connection> getConnections() {
        return new ArrayList<>(connections);
    }
    public ObservableList<Connection> connectionsProperty(){
        return connections;
    }
    public void setConnections(List<Connection> connections) {
        this.connections.setAll(connections);
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }
    public ObservableList<Tag> tagsProperty(){
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags.setAll(tags);
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<User> getUsers() {
        return new ArrayList<>(users);
    }
    public ObservableList<User> usersProperty(){
        return users;
    }
    public void setUsers(List<User> users) {
        this.users.setAll(users);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name=" + name +
                ", group=" + group +
                ", devices=" + devices +
                ", connections=" + connections +
                ", tags=" + tags +
                ", users=" + users +
                '}';
    }
}