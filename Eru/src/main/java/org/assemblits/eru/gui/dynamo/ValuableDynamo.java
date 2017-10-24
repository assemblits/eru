package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.StringProperty;

/**
 * Created by mtrujillo on 10/8/17.
 */
public interface ValuableDynamo extends Dynamo {
    void setCurrentTagValue(String value);
    String getCurrentTagValue();

    String getCurrentValueTagName();
    StringProperty currentValueTagNameProperty();
    void setCurrentValueTagName(String currentValueTagName);
}
