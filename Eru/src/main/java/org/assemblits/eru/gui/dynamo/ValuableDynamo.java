package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.IntegerProperty;

/**
 * Created by mtrujillo on 10/8/17.
 */
public interface ValuableDynamo extends Dynamo {
    void setCurrentTagValue(String value);
    String getCurrentTagValue();

    Integer getCurrentValueTagID();
    IntegerProperty currentValueTagIDProperty();
    void setCurrentValueTagID(Integer currentValueTagID);
}
