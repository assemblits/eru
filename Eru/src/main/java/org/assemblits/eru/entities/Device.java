package org.assemblits.eru.entities;

import javafx.beans.property.*;
import org.assemblits.eru.fieldbus.protocols.modbus.DeviceBlock;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mtrujillo on 13/05/14.
 */
@Entity
@Table(name = "device", schema = "public")
public class  Device {
    /* ********** Fields ********** */
    private final IntegerProperty       id;
    private final StringProperty        name;
    private final IntegerProperty       unitIdentifier;
    private final StringProperty        status;
    private final IntegerProperty       retries;
    private final BooleanProperty       enabled;
    private List<Address>               addresses;
    private BooleanProperty             zeroBased;
    private ObjectProperty<Connection>  connection;
    private StringProperty              groupName;

    /* ********** Constructors ********** */
    public Device() {
        id                  = new SimpleIntegerProperty();
        name                = new SimpleStringProperty("");
        unitIdentifier      = new SimpleIntegerProperty(0);
        status              = new SimpleStringProperty("");
        retries             = new SimpleIntegerProperty(3);
        enabled             = new SimpleBooleanProperty(false);
        addresses           = new ArrayList<>();
        zeroBased           = new SimpleBooleanProperty(true);
        connection          = new SimpleObjectProperty<>();
        groupName           = new SimpleStringProperty("");
    }

    /* ********** Setters and Getters ********** */
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer getId() {
        return id.get();
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public void setId(Integer id) {
        this.id.set(id);
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

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade= CascadeType.ALL, orphanRemoval = true, mappedBy = "owner")
    public List<Address> getAddresses() {
        return addresses;
    }
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    @Transient
    public List<Address> getAddressesByModel(Address.DataModel dataModel){
        return addresses.stream().filter(a -> a.getDataModel().equals(dataModel)).collect(Collectors.toList());
    }
    @Transient
    public List<DeviceBlock> getAddressesBlocks(Address.DataModel dataModel){
        return getAddressBlocksFrom(getAddressesByModel(dataModel));
    }
    @Transient
    private List<DeviceBlock> getAddressBlocksFrom(List<Address> addresses) {
        List<DeviceBlock> deviceBlocks = new ArrayList<>();
        deviceBlocks.add(new DeviceBlock());
        Collections.sort(addresses);
        for(Address address : addresses){
            if ( !getLastBlockIn(deviceBlocks).isValidToAdd(address) ) deviceBlocks.add(new DeviceBlock());
            getLastBlockIn(deviceBlocks).addAddressInBlock(address);
        }
        return deviceBlocks;
    }
    @Transient
    private DeviceBlock getLastBlockIn(List<DeviceBlock> blockList){
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

    @ManyToOne(cascade= CascadeType.ALL)
    public Connection getConnection() {
        return connection.get();
    }
    public ObjectProperty<Connection> connectionProperty() {
        return connection;
    }
    public void setConnection(Connection connection) {
        this.connection.set(connection);
    }

    @Column(name = "group_name")
    public String getGroupName() {
        return groupName.get();
    }
    public StringProperty groupNameProperty() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName.set(groupName);
    }

    @Transient
    public String getStatus() {
        return status.get();
    }
    public StringProperty statusProperty() {
        return status;
    }
    public void setStatus(String status) {
        this.status.set(status);
    }

    @Override
    public String toString() {
        return getGroupName()+":"+getName();
    }
}
