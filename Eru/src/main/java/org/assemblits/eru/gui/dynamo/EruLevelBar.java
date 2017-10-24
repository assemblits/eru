package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.scene.control.LevelBar;

/**
 * Created by mtrujillo on 8/26/17.
 */
public class EruLevelBar extends LevelBar implements ValuableDynamo {
    private StringProperty currentValueTagName;

    public EruLevelBar() {
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
    public void setCurrentValueTagName(String currentValueTagName) {
        this.currentValueTagName.set(currentValueTagName);
    }
}
