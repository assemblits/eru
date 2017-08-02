package com.marlontrujillo.eru.gui.toolbars.tree;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mtrujillo on 7/25/17.
 */
@Entity
@Table(name = "group", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Group {

    public enum Type {ROOT, CONNECTION, DEVICE, TAG, USER}

    private IntegerProperty         id;
    private StringProperty          name;
    private ObjectProperty<Type>    type;
    private ObjectProperty<Group>   parent;
    private ObservableList<Group>   children;
    private ObjectProperty<Date>    lastModified;

    public Group() {
        this.id = new SimpleIntegerProperty(0);
        this.name = new SimpleStringProperty("");
        this.type = new SimpleObjectProperty<>(Type.ROOT);
        this.parent = new SimpleObjectProperty<>();
        this.children = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.lastModified = new SimpleObjectProperty<>();
    }

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
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

    public Type getType() {
        return type.get();
    }
    public ObjectProperty<Type> typeProperty() {
        return type;
    }
    public void setType(Type type) {
        this.type.set(type);
    }

    @OneToOne(cascade= CascadeType.ALL, orphanRemoval = true)
    public Group getParent() {
        return parent.get();
    }
    public ObjectProperty<Group> parentProperty() {
        return parent;
    }
    public void setParent(Group parent) {
        this.parent.set(parent);
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Group> getChildren(){
        return new ArrayList<>(children);
    }
    public ObservableList<Group> childrenProperty() {
        return children;
    }
    public void setChildren(List<Group> children){
        this.children.setAll(children);
    }

    @Column(name = "last_modified")
    public Date getLastModified() {
        return lastModified.get();
    }
    public ObjectProperty<Date> lastModifiedProperty() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified.set(lastModified);
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", name=" + name +
                ", children=" + children +
                '}';
    }
}
