package com.eru.gui.erget;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mtrujillo on 8/24/17.
 */
public class Display extends com.eru.scene.control.Display {

    public static Map<StringProperty, String> propertiesAndTags = new HashMap<>();

    private StringProperty currentValueTagID;

    public Display() {
        super();
        this.currentValueTagID = new SimpleStringProperty(this, "currentValueTagID", "");
    }

    public String getCurrentValueTagID() {
        return currentValueTagID.get();
    }
    public StringProperty currentValueTagIDProperty() {
        return currentValueTagID;
    }
    public void setCurrentValueTagID(String currentValueTagID) {
        this.currentValueTagID.set(currentValueTagID);
        propertiesAndTags.put(super.currentTextProperty(), currentValueTagID);
    }
}
