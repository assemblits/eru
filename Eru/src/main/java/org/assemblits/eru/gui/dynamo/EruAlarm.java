package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.assemblits.eru.scene.control.Alarm;

/**
 * Created by mtrujillo on 8/26/17.
 */
public class EruAlarm extends Alarm implements ValuableDynamo<Boolean> {
    private IntegerProperty currentValueTagID;

    public EruAlarm() {
        super();
        this.currentValueTagID = new SimpleIntegerProperty(this, "currentValueTagID", -1);
    }

    @Override
    public void setCurrentTagValue(String value) {
        this.setCurrentValue(Boolean.parseBoolean(value));
    }

    @Override
    public Boolean getCurrentTagValue() {
        return getCurrentValue();
    }

    public Integer getCurrentValueTagID() {
        return currentValueTagID.get();
    }
    public IntegerProperty currentValueTagIDProperty() {
        return currentValueTagID;
    }
    public void setCurrentValueTagID(Integer currentValueTagID) {
        this.currentValueTagID.set(currentValueTagID);
    }
}
