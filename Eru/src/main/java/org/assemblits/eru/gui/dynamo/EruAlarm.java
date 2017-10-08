package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.scene.control.Alarm;

/**
 * Created by mtrujillo on 8/26/17.
 */
public class EruAlarm extends Alarm implements Dynamo<Boolean> {
    private StringProperty currentValueTagID;

    public EruAlarm() {
        super();
        this.currentValueTagID = new SimpleStringProperty(this, "currentValueTagID", "");
    }

    @Override
    public void setCurrentTagValue(String value) {
        this.setCurrentValue(Boolean.parseBoolean(value));
    }

    @Override
    public Boolean getCurrentTagValue() {
        return getCurrentValue();
    }

    public String getCurrentValueTagID() {
        return currentValueTagID.get();
    }
    public StringProperty currentValueTagIDProperty() {
        return currentValueTagID;
    }
    public void setCurrentValueTagID(String currentValueTagID) {
        this.currentValueTagID.set(currentValueTagID);
    }
}
