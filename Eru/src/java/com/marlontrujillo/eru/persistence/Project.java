package com.marlontrujillo.eru.persistence;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.device.Device;
import com.marlontrujillo.eru.gui.tree.Group;
import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.user.User;
import javafx.beans.property.*;

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
        this.devices = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.users = new ArrayList<>();
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
        return devices;
    }
    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Connection> getConnections() {
        return connections;
    }
    public void setConnections(List<Connection> connections) {
        this.connections = connections;
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Tag> getTags() {
        return tags;
    }
    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<User> getUsers() {
        return users;
    }
    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id.get() +
                ", name=" + name.get() +
                ", group=" + group.get() +
                ", devices=" + devices +
                ", connections=" + connections +
                ", tags=" + tags +
                ", users=" + users +
                '}';
    }
}