package org.assemblits.eru.gui.dynamo.base;

import javafx.beans.property.IntegerProperty;

/**
 * Created by mtrujillo on 10/8/17.
 */
public interface ValueDynamic<T> {
    void setCurrentTagValue(String value);
    T getCurrentTagValue();

    Integer getCurrentValueTagID();
    IntegerProperty currentValueTagIDProperty();
    void setCurrentValueTagID(Integer currentValueTagID);
}
