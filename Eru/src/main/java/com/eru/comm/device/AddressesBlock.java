package com.eru.comm.device;

import com.eru.entities.Address;
import com.eru.util.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 3/16/2016.
 */
public class AddressesBlock {

    public static final int DEFAULT_MAXIMUM_CAPACITY = Preferences.getInstance().getModbusBlockMaxLimit();

    private int             capacity;
    private List<Address>   block;

    public AddressesBlock() {
        this(DEFAULT_MAXIMUM_CAPACITY);
    }

    public AddressesBlock(int capacity) {
        this.block      = new ArrayList<>();
        this.capacity   = capacity;
    }

    public void addAddressInBlock(Address address){
        block.add(address);
    }
    public boolean isValidToAdd(Address address){
        return block.isEmpty() || (isContiguousWith(address) && !isMaximumCapacityReached() && hasSameDataModel(address));
    }

    private boolean isMaximumCapacityReached(){
        return block.size() == DEFAULT_MAXIMUM_CAPACITY;
    }
    private boolean isContiguousWith(Address address) {
        return isNextToTheFirstAddress(address) || isNextToTheLastAddress(address);
    }
    private boolean isNextToTheFirstAddress(Address address) {
        return (address.getNetworkID() + 1) == getFirstAddressInBlock().getNetworkID();
    }
    private boolean isNextToTheLastAddress(Address address) {
        return (address.getNetworkID() - 1) == getLastAddressInBlock().getNetworkID();
    }
    private boolean hasSameDataModel(Address address){
        return getFirstAddressInBlock().getDataModel().equals(address.getDataModel());
    }

    public List<Address> get(){
        return block;
    }
    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    public Address getFirstAddressInBlock() {
        return block.get(0);
    }
    public Address getLastAddressInBlock() {
        return block.get(block.size() - 1);
    }
    public Address getAddressWithId(int ID){
        Address found = null;
        for(Address address : block){
            if (address.getNetworkID() == ID) {
                found = address;
                break;
            }
        }
        return found;
    }
    public int getAddressesTotal() {
        return block.size();
    }

    @Override
    public String toString() {
        String blockName;
        if(block.isEmpty()) {
            blockName = "EMPTY";
        } else {
          blockName = "("+getFirstAddressInBlock()+") "+ getAddressesTotal() + " (" + getLastAddressInBlock()+")";
        }
        return "["+blockName+"]";
    }
}
