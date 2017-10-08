package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.gui.dynamo.base.Dynamo;
import org.assemblits.eru.scene.control.Gauge;

/**
 * Created by mtrujillo on 8/25/17.
 */
public class EruGauge extends Gauge implements Dynamo<Double> {
    private StringProperty currentValueTagID;

    public EruGauge() {
        super();
        this.currentValueTagID = new SimpleStringProperty(this, "currentValueTagID", "");
    }

    @Override
    public void setCurrentTagValue(String value) {
        this.setCurrentValue(Double.parseDouble(value));
    }

    @Override
    public Double getCurrentTagValue() {
        return getCurrentValue();
    }

    @Override
    public String getCurrentValueTagID() {
        return currentValueTagID.get();
    }

    @Override
    public StringProperty currentValueTagIDProperty() {
        return currentValueTagID;
    }

    @Override
    public void setCurrentValueTagID(String currentValueTagID) {
        this.currentValueTagID.set(currentValueTagID);
    }

}
