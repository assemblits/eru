package com.eru.entity;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mtrujillo on 7/25/17.
 */
@Entity
@Table(name = "tree_elements_group", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class TreeElementsGroup {

    public enum Type {ROOT, CONNECTION, DEVICE, TAG, USER, DISPLAY}

    private IntegerProperty                     id;
    private StringProperty                      name;
    private ObjectProperty<Type>                type;
    private ObjectProperty<TreeElementsGroup>   parent;
    private List<TreeElementsGroup>             children;

    public TreeElementsGroup() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.type = new SimpleObjectProperty<>();
        this.parent = new SimpleObjectProperty<>();
        this.children = new SimpleListProperty<>(FXCollections.observableArrayList());
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
    public TreeElementsGroup getParent() {
        return parent.get();
    }
    public ObjectProperty<TreeElementsGroup> parentProperty() {
        return parent;
    }
    public void setParent(TreeElementsGroup parent) {
        this.parent.set(parent);
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<TreeElementsGroup> getChildren() {
        return children;
    }
    public void setChildren(List<TreeElementsGroup> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "TreeElementsGroup{" + getName() +
                "=" + super.toString() +
                ", children= <" + children +
                "> }";
    }
}
