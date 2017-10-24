package org.assemblits.eru.entities;

import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.jfx.scenebuilder.SceneFxmlManager;

import javax.persistence.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Entity
@Table(name = "display", schema = "public")
public class Display {

    private final IntegerProperty id;
    private StringProperty name;
    private StringProperty groupName;
    private BooleanProperty initialDisplay;
    private ObjectProperty<StageType> stageType;
    private ObjectProperty<Parent> fxNode;

    public Display() {
        this.id = new SimpleIntegerProperty(this, "display_id");
        this.name = new SimpleStringProperty(this, "name", "");
        this.groupName = new SimpleStringProperty(this, "name", "");
        this.initialDisplay = new SimpleBooleanProperty(this, "initial_display", false);
        this.stageType = new SimpleObjectProperty<>(this, "stageType", StageType.REPLACE);
        this.fxNode = new SimpleObjectProperty<>(this, "fxNode", null);
    }

    @Id
    @Column(name = "display_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(Integer id) {
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

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName.get();
    }

    public StringProperty groupNameProperty() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    @Column(name = "initial_display")
    public boolean isInitialDisplay() {
        return initialDisplay.get();
    }

    public BooleanProperty initialDisplayProperty() {
        return initialDisplay;
    }

    public void setInitialDisplay(boolean initialDisplay) {
        this.initialDisplay.set(initialDisplay);
    }

    @Transient
    public StageType getStageType() {
        return stageType.get();
    }

    public ObjectProperty<StageType> stageTypeProperty() {
        return stageType;
    }

    public void setStageType(StageType stageType) {
        this.stageType.set(stageType);
    }

    @Column(name = "stage_type")
    public String getStageTypeName() {
        return getStageType() == null ? "" : getStageType().name();
    }

    public void setStageTypeName(String name) {
        setStageType(name == null || name.isEmpty() ? StageType.REPLACE : StageType.valueOf(name));
    }

    @Transient
    public Parent getFxNode() {
        return fxNode.get();
    }

    public ObjectProperty<Parent> fxNodeProperty() {
        return fxNode;
    }

    public void setFxNode(Parent fxNode) {
        this.fxNode.set(fxNode);
    }

    public enum StageType {REPLACE, NEW}

    @Override
    public String toString() {
        return getGroupName()+":"+getName();
    }
}
