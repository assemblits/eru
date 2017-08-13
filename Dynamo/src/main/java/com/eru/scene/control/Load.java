package com.eru.scene.control;

import com.eru.scene.control.skin.LoadSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Skin;

/**
 * Created by mtrujillo on 09/02/2015.
 */
public class Load extends Dynamo {

    /* ********** Dynamic Fields ********** */
    private StringProperty name;
    private BooleanProperty energized;


    /* ********** Constructors ********** */
    public Load() {
        getStyleClass().add("load");
        name                = new SimpleStringProperty(this, "name", "");
        energized           = new SimpleBooleanProperty(this, "energized", false);
    }

    /* ********** Setters and Getters ********** */
    public String getName() {
        return name.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public boolean getEnergized() {
        return energized.get();
    }
    public BooleanProperty energizedProperty() {
        return energized;
    }
    public void setEnergized(boolean energized) {
        this.energized.set(energized);
    }

    /* ********** Style and Skin ********** */
    @Override public String getUserAgentStylesheet() {
        return  getClass().getResource("load.css").toExternalForm();
    }
    @Override protected Skin<?> createDefaultSkin() {
        return new LoadSkin(this);
    }
}
