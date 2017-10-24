package org.assemblits.eru.gui.dynamo;

import eu.hansolo.medusa.Gauge;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Created by marlontrujillo1080 on 10/24/17.
 */
public class MedusaGauge extends Gauge implements ValuableDynamo {

    private IntegerProperty currentValueTagID;

    public MedusaGauge() {
        super();
        this.currentValueTagID = new SimpleIntegerProperty(this, "currentValueTagID", -1);
    }

    @Override
    public void setCurrentTagValue(String value) {
        this.setValue(Double.parseDouble(value));
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
