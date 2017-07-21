package com.marlontrujillo.eru.comm.device;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created by mtrujillo on 18/07/2014.
 */
@Embeddable
public class AddressPK implements Serializable {
    private final IntegerProperty                   id;
    private final ObjectProperty<Address.DataModel> dataModel;
    private Device                                  device;

    public AddressPK() {
        id        = new SimpleIntegerProperty(AddressPK.this, "id", 0);
        device    = new Device();
        dataModel = new SimpleObjectProperty<>();
    }

    public int getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(int id) {
        this.id.set(id);
    }

    @ManyToOne()
    public Device getDevice() {
        return device;
    }
    public void setDevice(Device device) {
        this.device = device;
    }

    @Transient
    public Address.DataModel getDataModel() {
        return dataModel.get();
    }
    public ObjectProperty<Address.DataModel> dataModelProperty() {
        return dataModel;
    }
    public void setDataModel(Address.DataModel dataModel) {
        this.dataModel.set(dataModel);
    }
    @Column(name = "data_model")
    public String getDataModelName() {
        return getDataModel() == null ? "" : getDataModel().name();
    }
    public void setDataModelName(String dataModelName) {
        if(dataModelName == null || dataModelName.isEmpty()){
            return;
        } else {
            setDataModel(Address.DataModel.valueOf(dataModelName));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(getId()).concat(" - ").concat(getDataModelName());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
