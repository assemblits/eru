package com.eru.util;

import javafx.beans.property.SimpleObjectProperty;

import java.sql.Timestamp;

/**
 * Created by mtrujillo on 23/05/17.
 */
public class TimeStampProperty extends SimpleObjectProperty<Timestamp> {

    public TimeStampProperty() {
        super();
    }

    public void setValueAndFireListeners(Timestamp newValue){
        this.set(newValue);
        fireValueChangedEvent();
    }

}
