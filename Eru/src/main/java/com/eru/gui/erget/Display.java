package com.eru.gui.erget;

import com.eru.util.TagLinksManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by mtrujillo on 8/24/17.
 */
public class Display extends com.eru.scene.control.Display {

    /**
     * The map to linkToConnections {@code Display} and {@code Tags}. This map is useful for
     * finding a specific Display within the scene graph and get the setted tag. While the id of a Node
     * should be unique within the scene graph, this uniqueness is supported by the {@code ComponentsIdsGenerator}.
     */


    private StringProperty currentValueTagID;

    public Display() {
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
        TagLinksManager.DYNAMO_ID_TAG_ID.put(getId(), currentValueTagID);
    }
}
