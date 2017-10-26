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

import com.ghgande.j2mod.modbus.io.ModbusSerialTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.io.ModbusTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import javafx.application.Platform;
import org.assemblits.eru.entities.Address;
import org.assemblits.eru.entities.Address.DataModel;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.SerialConnection;
import org.assemblits.eru.entities.TcpConnection;
import org.assemblits.eru.fieldbus.context.Message;

import java.sql.Timestamp;

public class ReadDeviceBlockMessage implements Message {

    private final Device device;
    private final DeviceBlock block;
    private ModbusTransaction transaction;
    private ModbusRequest request;
    private boolean wasSuccessful;
    private DataModel dataModel;
    private int offset;
    private int firstSlotToRead;
    private int lastSlotToRead;

    /* ** Constructor ** */
    public ReadDeviceBlockMessage(Device device, DeviceBlock block) {
        this.device     = device;
        this.block      = block;
    }

    /* ** Methods ** */
    @Override
    public void create(){
        if (device.getConnection() == null) throw new IllegalStateException("Device should have a connection.");
        offset              = device.isZeroBased() ? 1 : 0;
        firstSlotToRead     = block.getFirstAddressInBlock().getNetworkID() - offset;
        lastSlotToRead      = block.getLastAddressInBlock().getNetworkID() - offset;
        dataModel           = block.get().get(0).getDataModel();
        int totalSlotsToRead = block.getAddressesTotal();

        switch (dataModel) {
            case COIL:
                request = new ReadCoilsRequest(firstSlotToRead, totalSlotsToRead);
                break;
            case DISCRETE_INPUT:
                request = new ReadInputDiscretesRequest(firstSlotToRead, totalSlotsToRead);
                break;
            case INPUT_REGISTER:
                request = new ReadInputRegistersRequest(firstSlotToRead, totalSlotsToRead);
                break;
            case HOLDING_REGISTER:
                request = new ReadMultipleRegistersRequest(firstSlotToRead, totalSlotsToRead);
                break;
        }
        request.setUnitID(device.getUnitIdentifier());

        if (device.getConnection() instanceof SerialConnection){
            request.setHeadless();
            transaction = new ModbusSerialTransaction(((SerialConnection) device.getConnection()).getCoreConnection());
        } else {
            transaction = new ModbusTCPTransaction(((TcpConnection) device.getConnection()).getCoreConnection());
        }

        transaction.setRetries(device.getRetries());
        transaction.setRequest(request);
    }

    @Override
    public void send() {
        try {
            transaction.execute();
            updateDeviceStatus("OK");
            wasSuccessful = true;
        } catch (Exception e) {
            updateDeviceStatus(e.getMessage());
            wasSuccessful = false;
        }
    }

    private void updateDeviceStatus(String errorMessage) {
        device.setStatus(errorMessage);
        block.get().forEach(address -> address.setStatus(errorMessage));
    }

    @Override
    public void receive() {
        if (wasSuccessful){
            updateBlockWithResponse();
        }
    }
    private void updateBlockWithResponse() {
        final ModbusResponse response = transaction.getResponse();
        Platform.runLater(() -> {
            for (int slot = firstSlotToRead; slot <= lastSlotToRead; slot++) {
                final Address address = getAddressFromBlock(slot);
                address.setCurrentValue(extractSlotValueFromResponse(response, slot));
                address.setTimestamp(new Timestamp(System.currentTimeMillis()));    // TODO: This is a memory overwork, has to be fixed
            }
        });
    }

    private Address getAddressFromBlock(int slot){
        return block.getAddressWithId(slot + offset);
    }
    private int extractSlotValueFromResponse(ModbusResponse response, int slot){
        final int slotInResponse = slot - firstSlotToRead;
        switch (dataModel) {
            case COIL:
                final ReadInputDiscretesResponse booleanWriteResponse = (ReadInputDiscretesResponse) response;
                return booleanWriteResponse.getDiscretes().getBit(slotInResponse) ? 1: 0;
            case DISCRETE_INPUT:
                final ReadCoilsResponse booleanReadResponse = (ReadCoilsResponse) response;
                return booleanReadResponse.getCoilStatus(slotInResponse) ? 1 : 0;
            case INPUT_REGISTER:
                final ReadInputRegistersResponse analogReadResponse = (ReadInputRegistersResponse) response;
                return analogReadResponse.getRegisterValue(slotInResponse);
            case HOLDING_REGISTER:
                final ReadMultipleRegistersResponse analogWriteResponse = (ReadMultipleRegistersResponse) response;
                return analogWriteResponse.getRegisterValue(slotInResponse);
            default:
                return 0;
        }
    }

}