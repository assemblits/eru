package org.assemblits.eru.gui.dynamo;

import org.assemblits.eru.scene.control.Display;
import org.assemblits.eru.util.ProjectDynamicBehavior;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by mtrujillo on 8/24/17.
 */
public class EruDisplay extends Display {

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

    public String getCurrentValueTagID() {
        return currentValueTagID.get();
    }
    public StringProperty currentValueTagIDProperty() {
        return currentValueTagID;
    }
    public void setCurrentValueTagID(String currentValueTagID) {
        this.currentValueTagID.set(currentValueTagID);
        ProjectDynamicBehavior.DYNAMO_ID_VS_TAG_ID.put(getId(), currentValueTagID);
    }
}
