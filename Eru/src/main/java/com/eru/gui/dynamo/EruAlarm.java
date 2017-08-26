package com.eru.gui.dynamo;

import com.eru.scene.control.Alarm;
import com.eru.util.TagLinksManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by mtrujillo on 8/26/17.
 */
public class EruAlarm extends Alarm {
    /**
     * The map to linkToConnections {@code EruDisplay} and {@code Tags}. This map is useful for
     * finding a specific EruDisplay within the scene graph and get the setted tag. While the id of a Node
     * should be unique within the scene graph, this uniqueness is supported by the {@code ComponentsIdsGenerator}.
     */
    private StringProperty currentValueTagID;

    public EruAlarm() {
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
        TagLinksManager.DYNAMO_ID_VS_TAG_ID.put(getId(), currentValueTagID);
    }
}
