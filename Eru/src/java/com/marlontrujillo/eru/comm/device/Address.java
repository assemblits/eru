package com.marlontrujillo.eru.comm.device;

import com.marlontrujillo.eru.util.TimeStampProperty;
import javafx.beans.property.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * Created by mtrujillo on 13/05/14.
 */
@Entity
@Table(name = "address", schema = "public")
public class Address implements Comparable<Address> {

    /* ********** Static Fields ********** */
    public static enum DataModel {BOOLEAN_READ, BOOLEAN_WRITE, ANALOG_READ, ANALOG_WRITE}

    /* ********** Fields ********** */
    private AddressPK                         addressPK;
    private final IntegerProperty             currentValue;
    private final BooleanProperty             connected;
    private final StringProperty              status;
    private final TimeStampProperty timestamp;

    /* ********** Constructor ********** */
    public Address() {
        addressPK    = new AddressPK();
        currentValue = new SimpleIntegerProperty();
        connected    = new SimpleBooleanProperty();
        status       = new SimpleStringProperty();
        timestamp    = new TimeStampProperty();
    }

    /* ********** Setters and Getters ********** */
    @EmbeddedId
    public AddressPK getAddressPK() {
        return addressPK;
    }
    public void setAddressPK(AddressPK addressPK) {
        this.addressPK = addressPK;
    }

    @Column(name = "current_value")
    public int getCurrentValue() {
        return currentValue.get();
    }
    public IntegerProperty currentValueProperty() {
        return currentValue;
    }
    public void setCurrentValue(int currentValue) {
        this.currentValue.set(currentValue);
    }

    @Column(name = "connected")
    public boolean getConnected() {
        return connected.get();
    }
    public BooleanProperty connectedProperty() {
        return connected;
    }
    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    @Column(name = "status")
    public String getStatus() {
        return status.get();
    }
    public StringProperty statusProperty() {
        return status;
    }
    public void setStatus(String status) {
        this.status.set(status);
    }

    @Column(name = "time_stamp")
    public Timestamp getTimestamp() {
        return timestamp.get();
    }
    public ObjectProperty<Timestamp> timestampProperty() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp.set(timestamp);
    }
    public void setTimestampValueAndFireListeners(Timestamp newValue){
        this.timestamp.setValueAndFireListeners(newValue);
    }

    /* ********** Override Methods ********** */
    @Override
    public int compareTo(Address address) {
        int compareAddr = address.getAddressPK().getId();

        //ascending order
        return this.getAddressPK().getId() - compareAddr;

        //descending order
        //return compareAddr - this.address;
    }

    @Override
    public String toString() {
        return getAddressPK().toString();
    }
}
