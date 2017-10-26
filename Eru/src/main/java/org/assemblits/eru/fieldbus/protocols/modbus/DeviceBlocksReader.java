/******************************************************************************
 * Copyright (c) 2007 Assemblits contributors                                 *
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

import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.fieldbus.actors.Executor;
import org.assemblits.eru.fieldbus.context.Message;
import org.assemblits.eru.entities.Address;
import org.assemblits.eru.entities.Device;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DeviceBlocksReader implements Executor {

    private Device device;
    private final List<Message> messages;
    private boolean repeatable;

    public DeviceBlocksReader(Device device) {
        this.device = device;
        this.messages = new ArrayList<>();
        this.repeatable = true;
    }

    @Override
    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public boolean isPrepared() {
        return !messages.isEmpty();
    }

    @Override
    public void prepare() {
        final List<DeviceBlock> deviceBlocksToRead = new ArrayList<>();

        // Extract All AddressBlocks from device in order
        deviceBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.COIL));
        deviceBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.DISCRETE_INPUT));
        deviceBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.INPUT_REGISTER));
        deviceBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.HOLDING_REGISTER));

        // Create messages to read all that blocks
        for(DeviceBlock deviceBlock : deviceBlocksToRead){
            if(!deviceBlock.get().isEmpty()){
                final ReadDeviceBlockMessage transmissionToReadAddressBlock = new ReadDeviceBlockMessage(device, deviceBlock);
                transmissionToReadAddressBlock.create();
                messages.add(transmissionToReadAddressBlock);
            }
        }
    }

    @Override
    public void execute() {
        for (Message message : messages) {
            message.send();
            message.receive();
        }
    }

    @Override
    public void stop() {
        repeatable = false;
    }
}
