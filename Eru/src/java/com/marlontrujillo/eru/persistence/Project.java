package com.marlontrujillo.eru.persistence;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.device.Device;
import com.marlontrujillo.eru.gui.toolbars.tree.Group;
import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.user.User;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

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

    private IntegerProperty         id;
    private StringProperty          name;
    private ObjectProperty<Group>   group;
    private List<Device>            devices;
    private List<Connection>        connections;
    private List<Tag>               tags;
    private List<User>              users;

    public Project() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.group = new SimpleObjectProperty<>();
        this.devices = new SimpleListProperty<>(FXCollections.observableArrayList());
        this._devices  = new ArrayList<>();
        this.connections = new SimpleListProperty<>(FXCollections.observableArrayList());
        this._connections = new ArrayList<>();
        this.tags = new SimpleListProperty<>(FXCollections.observableArrayList());
        this._tags = new ArrayList<>();
        this.users = new SimpleListProperty<>(FXCollections.observableArrayList());
        this._users = new ArrayList<>();
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
        return this._devices;
    }
    public ListProperty<Device> devicesProperty() {
        return devices;
    }
    public void setDevices(List<Device> devices) {
        this._devices = devices;
        this.devices.setValue(FXCollections.observableArrayList(devices));
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Connection> getConnections() {
        return _connections;
    }
    public ListProperty<Connection> connectionsProperty() {
        return connections;
    }
    public void setConnections(List<Connection> connections) {
        this._connections = connections;
        this.connections = new SimpleListProperty<>(FXCollections.observableArrayList(connections));
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Tag> getTags() {
        return _tags;
    }
    public ListProperty<Tag> tagsProperty() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this._tags = tags;
        this.tags = new SimpleListProperty<>(FXCollections.observableArrayList(tags));
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<User> getUsers() {
        return _users;
    }
    public ListProperty<User> usersProperty() {
        return users;
    }
    public void setUsers(List<User> users) {
        this._users = users;
        this.users = new SimpleListProperty<>(FXCollections.observableArrayList(users));
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