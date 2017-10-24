package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.scene.control.Display;

/**
 * Created by mtrujillo on 8/24/17.
 */
public class EruDisplay extends Display implements ValuableDynamo {

    private StringProperty currentValueTagName;

    public EruDisplay() {
        super();
        this.currentValueTagName = new SimpleStringProperty(this, "currentValueTagName", "");
    }

    @Override
    public void setCurrentTagValue(String value) {
        setCurrentText(value);
    }

    @Override
    public String getCurrentTagValue() {
        return getCurrentText();
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
}
