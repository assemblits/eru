package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.scene.control.LevelBar;

/**
 * Created by mtrujillo on 8/26/17.
 */
public class EruLevelBar extends LevelBar implements Dynamo<Double> {
    private StringProperty currentValueTagID;

    public EruLevelBar() {
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
