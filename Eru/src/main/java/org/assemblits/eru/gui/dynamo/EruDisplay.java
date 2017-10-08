package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.assemblits.eru.gui.dynamo.base.Dynamo;
import org.assemblits.eru.scene.control.Display;

/**
 * Created by mtrujillo on 8/24/17.
 */
public class EruDisplay extends Display implements Dynamo<String> {

    /**
     * The map to linkToConnections {@code EruDisplay} and {@code Tags}. This map is useful for
     * finding a specific EruDisplay within the scene graph and get the setted tag. While the id of a Node
     * should be unique within the scene graph, this uniqueness is supported by the {@code ComponentsIdsGenerator}.
     */
    private StringProperty currentValueTagID;

    public EruDisplay() {
        super();
        this.currentValueTagID = new SimpleStringProperty(this, "currentValueTagID", "");
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
