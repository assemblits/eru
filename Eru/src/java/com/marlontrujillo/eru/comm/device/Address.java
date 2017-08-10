package com.marlontrujillo.eru.comm.device;

import com.marlontrujillo.eru.util.TimeStampProperty;
import javafx.beans.property.*;

import javax.persistence.*;
import java.sql.Timestamp;

import static com.marlontrujillo.eru.comm.device.Address.DataModel.*;

/**
 * Created by mtrujillo on 13/05/14.
 */
@Entity
@Table(name = "address", schema = "public")
public class Address implements Comparable<Address> {

    /* ********** Static Fields ********** */
    public enum DataModel {BOOLEAN_READ, BOOLEAN_WRITE, ANALOG_READ, ANALOG_WRITE}

    /* ********** Fields ********** */
    private final IntegerProperty                   id;
    private final ObjectProperty<Device>            device;
    private final IntegerProperty                   networkID;
    private final ObjectProperty<Address.DataModel> dataModel;
    private final IntegerProperty                   currentValue;
    private final BooleanProperty                   connected;
    private final StringProperty                    status;
    private final TimeStampProperty                 timestamp;

    /* ********** Constructor ********** */
    public Address() {
        this.id            = new SimpleIntegerProperty(0);
        this.device        = new SimpleObjectProperty<>();
        this.networkID     = new SimpleIntegerProperty(0);
        this.dataModel     = new SimpleObjectProperty<>(ANALOG_WRITE);
        this.currentValue  = new SimpleIntegerProperty();
        this.connected     = new SimpleBooleanProperty();
        this.status        = new SimpleStringProperty();
        this.timestamp     = new TimeStampProperty();
    }

    /* ********** Setters and Getters ********** */

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(int id) {
        this.id.set(id);
    }

    @ManyToOne
    public Device getDevice() {
        return device.get();
    }
    public ObjectProperty<Device> deviceProperty() {
        return device;
    }
    public void setDevice(Device device) {
        this.device.set(device);
    }

    @Column(name = "network_id")
    public int getNetworkID() {
        return networkID.get();
    }
    public IntegerProperty networkIDProperty() {
        return networkID;
    }
    public void setNetworkID(int networkID) {
        this.networkID.set(networkID);
    }

    @Column(name = "data_model")
    public DataModel getDataModel() {
        return dataModel.get();
    }
    public ObjectProperty<DataModel> dataModelProperty() {
        return dataModel;
    }
    public void setDataModel(DataModel dataModel) {
        this.dataModel.set(dataModel);
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
        int compareAddr = address.getNetworkID();

        //ascending order
        return this.getNetworkID() - compareAddr;

        //descending order
        //return compareAddr - this.address;
    }

    @Override
    public String toString() {
        String dataModelStands = "";
        switch (this.getDataModel()) {
            case BOOLEAN_READ:
                dataModelStands = "BR";
                break;
            case BOOLEAN_WRITE:
                dataModelStands = "BW";
                break;
            case ANALOG_READ:
                dataModelStands = "AR";
                break;
            case ANALOG_WRITE:
                dataModelStands = "AW";
                break;
        }

        return String.valueOf(getNetworkID()) + "-" + dataModelStands;
    }

}
