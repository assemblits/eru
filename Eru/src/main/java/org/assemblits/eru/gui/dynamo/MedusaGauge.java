package org.assemblits.eru.gui.dynamo;

import eu.hansolo.medusa.Gauge;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by marlontrujillo1080 on 10/24/17.
 */
public class MedusaGauge extends Gauge implements ValuableDynamo {

    private StringProperty currentValueTagName;

    public MedusaGauge() {
        super();
        this.currentValueTagName = new SimpleStringProperty(this, "currentValueTagID", "");
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
    public String getCurrentValueTagName() {
        return String.valueOf(currentValueTagName.get());
    }

    @Override
    public StringProperty currentValueTagNameProperty() {
        return currentValueTagName;
    }

    @Override
    public void setCurrentValueTagName(String currentValueTagName) {
        this.currentValueTagName.set(currentValueTagName);
    }

    @Override public String getUserAgentStylesheet() { return Gauge.class.getResource("gauge.css").toExternalForm(); }
}
