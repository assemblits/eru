package com.eru.scene.control;

import com.eru.scene.control.skin.AlarmSkin;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Created by mtrujillo on 07/07/2014.
 */
public class Alarm extends Control {
    /* ********** Fields ********** */
    private final BooleanProperty   currentValue;
    private final StringProperty    name;


    /* ********** Constructor ********** */
    public Alarm() {
        getStyleClass().add("alarm");
        currentValue                = new BooleanPropertyBase(false) {
            @Override
            public Object getBean() {
                return Alarm.this;
            }
            @Override
            public String getName() {
                return "currentValue";
            }
        };
        name                        = new SimpleStringProperty(Alarm.this, "name", "");
    }


    /* ********** Setters and Getters ********** */
    public boolean getCurrentValue() {
        return currentValue.get();
    }
    public BooleanProperty currentValueProperty() {
        return currentValue;
    }
    public void setCurrentValue(boolean currentValue) {
        this.currentValue.set(currentValue);
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


    /* ********** Style and Skin ********** */
    @Override public String getUserAgentStylesheet() {
        return getClass().getResource("alarm.css").toExternalForm();
    }
    @Override protected Skin<?> createDefaultSkin() {
        return new AlarmSkin(this);
    }

}
