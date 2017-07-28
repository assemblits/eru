package com.marlontrujillo.eru.gui.toolbars.tree;

import javafx.beans.InvalidationListener;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

/**
 * Created by mtrujillo on 7/25/17.
 */
@Entity
@Table(name = "group", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class Group {

    enum Type {ROOT, CONNECTION, DEVICE, TAG, USER}

    private int                   id;
    private StringProperty        name;
    private Type                  type;
    private ObjectProperty<Long>  size;
    private ObservableList<Group> children;
    private ObjectProperty<Date>  lastModified;

    public Group() {
        this("", Type.ROOT);
    }

    public Group(String name, Type type) {
        this.name         = new SimpleStringProperty(name);
        this.size         = new SimpleObjectProperty<>(0L);
        this.children     = new SimpleListProperty<>(FXCollections.observableArrayList());
        this.lastModified = new SimpleObjectProperty<>(Date.from(Instant.now()));
        this.type         = type;

        this.children.addListener((InvalidationListener) observable -> {
            setLastModified(Date.from(Instant.now()));
            setSize((long) getObservableChildren().size());
        });
    }

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id;
    }
    private void setId(int id) {
        this.id = id;
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

    @Column(name = "type")
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    @Column(name = "size")
    public Long getSize() {
        return size.get();
    }
    public ObjectProperty<Long> sizeProperty() {
        return size;
    }
    public void setSize(Long size) {
        this.size.set(size);
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true) @Column(name = "children")
    public ObservableList<Group> getObservableChildren() {
        return children;
    }
    public void setObservableChildren(ObservableList<Group> children) {
        this.children = children;
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
        return name.getValue();
    }
}
