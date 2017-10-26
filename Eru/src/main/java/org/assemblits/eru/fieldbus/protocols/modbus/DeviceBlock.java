/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.fieldbus.protocols.modbus;

import org.assemblits.eru.entities.Address;

import java.util.ArrayList;
import java.util.List;

public class DeviceBlock {

    private static final int DEFAULT_MAXIMUM_CAPACITY = 120;

    private int             capacity;
    private List<Address>   block;

    public DeviceBlock() {
        this(DEFAULT_MAXIMUM_CAPACITY);
    }

    public DeviceBlock(int capacity) {
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
