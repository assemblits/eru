package org.assemblits.eru.entities;

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
@Table(name = "eru_group", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class EruGroup {

    private IntegerProperty id;
    private StringProperty name;
    private ObjectProperty<EruType> type;
    private ObjectProperty<EruGroup> parent;
    private List<EruGroup> children;

    public EruGroup() {
        this.id = new SimpleIntegerProperty();
        this.name = new SimpleStringProperty();
        this.type = new SimpleObjectProperty<>(EruType.UNKNOWN);
        this.parent = new SimpleObjectProperty<>();
        this.children = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(Integer id) {
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

    public EruType getType() {
        return type.get();
    }
    public ObjectProperty<EruType> typeProperty() {
        return type;
    }
    public void setType(EruType type) {
        this.type.set(type);
    }

    @OneToOne(cascade= CascadeType.ALL, orphanRemoval = true)
    public EruGroup getParent() {
        return parent.get();
    }
    public ObjectProperty<EruGroup> parentProperty() {
        return parent;
    }
    public void setParent(EruGroup parent) {
        this.parent.set(parent);
    }

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<EruGroup> getChildren() {
        return children;
    }
    public void setChildren(List<EruGroup> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "EruGroup{" + getName() +
                "=" + super.toString() +
                ", children= <" + children +
                "> }";
    }
}
