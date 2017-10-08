package org.assemblits.eru.gui.dynamo.base;

import javafx.beans.property.StringProperty;

/**
 * Created by mtrujillo on 10/8/17.
 */
public interface ValueDynamic<T> {
    void setCurrentTagValue(String value);
    T getCurrentTagValue();

    String getCurrentValueTagID();
    StringProperty currentValueTagIDProperty();
    void setCurrentValueTagID(String currentValueTagID);
}
