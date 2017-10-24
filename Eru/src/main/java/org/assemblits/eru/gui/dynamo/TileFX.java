package org.assemblits.eru.gui.dynamo;

import eu.hansolo.tilesfx.Tile;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by marlontrujillo1080 on 10/24/17.
 */
public class TileFX extends Tile implements ValuableDynamo {
    private StringProperty currentValueTagName;

    public TileFX() {
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

    @Override public String getUserAgentStylesheet() { return Tile.class.getResource("tilesfx.css").toExternalForm(); }
}
