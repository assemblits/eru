package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.scene.control.Gauge;

/**
 * Created by mtrujillo on 8/25/17.
 */
public class EruGauge extends Gauge implements ValuableDynamo {
    private StringProperty currentValueTagName;

    public EruGauge() {
        super();
        this.currentValueTagName = new SimpleStringProperty(this, "currentValueTagName", "");
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
    public String getCurrentValueTagName() {
        return currentValueTagName.get();
    }

    @Override
    public StringProperty currentValueTagNameProperty() {
        return currentValueTagName;
    }

    @Override
    public void setCurrentValueTagName(String currentValueTagID) {
        this.currentValueTagName.set(currentValueTagID);
    }

    @Override public String getUserAgentStylesheet() {
        return Gauge.class.getResource("gauge.css").toExternalForm();
    }
}
