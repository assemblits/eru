package com.marlontrujillo.eru.comm.device;

import com.marlontrujillo.eru.comm.FieldBusCommunicator;
import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.member.ModbusDeviceReader;
import javafx.beans.property.*;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mtrujillo on 13/05/14.
 */
@Entity
@Table(name = "device", schema = "public")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "device_type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "DEVICE")
public class  Device {
    /* ********** Fields ********** */
    private int                     id;
    private final StringProperty    name;
    private final IntegerProperty   unitIdentifier;
    private final StringProperty    status;
    private final IntegerProperty   retries;
    private final BooleanProperty   enabled;
    private List<Address>           addresses;
    private BooleanProperty         zeroBased;
    private Connection              connection;

    /* ********** Constructors ********** */
    public Device() {
        name            = new SimpleStringProperty();
        unitIdentifier  = new SimpleIntegerProperty();
        status          = new SimpleStringProperty();
        retries         = new SimpleIntegerProperty();
        enabled         = new SimpleBooleanProperty();
        addresses       = new ArrayList<>();
        zeroBased       = new SimpleBooleanProperty(this, "zeroBased", true);
    }

    /* ********** Setters and Getters ********** */
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    @Column(name = "unit_identifier")
    public int getUnitIdentifier() {
        return unitIdentifier.get();
    }
    public IntegerProperty unitIdentifierProperty() {
        return unitIdentifier;
    }
    public void setUnitIdentifier(int unitIdentifier) {
        this.unitIdentifier.set(unitIdentifier);
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

    @Column(name = "retries")
    public int getRetries() {
        return retries.get();
    }
    public IntegerProperty retriesProperty() {
        return retries;
    }
    public void setRetries(int retries) {
        this.retries.set(retries);
    }

    @Column(name = "enabled")
    public boolean getEnabled() {
        return enabled.get();
    }
    public BooleanProperty enabledProperty() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true)
    public List<Address> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @ManyToOne(cascade= CascadeType.ALL)
    public Connection getConnection() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection = connection;
        this.connection.connectedProperty().addListener((observable, wasConnected, isConnected) -> {
            if(isConnected && getEnabled()){
                FieldBusCommunicator.getInstance().subscribe(new ModbusDeviceReader(this));
            }
        });
    }

    @Transient
    public List<Address> getAddresses(Address.DataModel dataModel){
        return getAddresses().stream().filter(a -> a.getAddressPK().getDataModel().equals(dataModel)).collect(Collectors.toList());
    }

    @Transient
    public List<AddressesBlock> getAddressesBlocks(Address.DataModel dataModel){
        return getAddressBlocksFrom(getAddresses(dataModel));
    }
    private List<AddressesBlock> getAddressBlocksFrom(List<Address> addresses) {
        List<AddressesBlock> addressesBlocks = new ArrayList<>();
        addressesBlocks.add(new AddressesBlock());
        Collections.sort(addresses);
        for(Address address : addresses){
            if ( !getLastBlockIn(addressesBlocks).isValidToAdd(address) ) addressesBlocks.add(new AddressesBlock());
            getLastBlockIn(addressesBlocks).addAddressInBlock(address);
        }
        return addressesBlocks;
    }
    private AddressesBlock getLastBlockIn(List<AddressesBlock> blockList){
        return blockList.get(blockList.size() - 1);
    }

    @Column(name = "zeroBased")
    public boolean isZeroBased() {
        return zeroBased.get();
    }
    public BooleanProperty zeroBasedProperty() {
        return zeroBased;
    }
    public void setZeroBased(boolean zeroBased) {
        this.zeroBased.set(zeroBased);
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
