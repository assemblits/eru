package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.assemblits.eru.scene.control.Gauge;

/**
 * Created by mtrujillo on 8/25/17.
 */
public class EruGauge extends Gauge implements ValuableDynamo {
    private IntegerProperty currentValueTagID;

    public EruGauge() {
        super();
        this.currentValueTagID = new SimpleIntegerProperty(this, "currentValueTagID", -1);
    }

    @Override
    public void setCurrentTagValue(String value) {
        this.setCurrentValue(Double.parseDouble(value));
    }

    @Override
    public String getCurrentTagValue() {
        return String.valueOf(getCurrentValue());
    }

    @Override
    public Integer getCurrentValueTagID() {
        return currentValueTagID.get();
    }

    @Override
    public IntegerProperty currentValueTagIDProperty() {
        return currentValueTagID;
    }

    @Override
    public void setCurrentValueTagID(Integer currentValueTagID) {
        this.currentValueTagID.set(currentValueTagID);
    }

}
