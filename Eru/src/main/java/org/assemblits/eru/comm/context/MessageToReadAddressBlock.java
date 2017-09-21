package org.assemblits.eru.comm.context;

import org.assemblits.eru.entities.SerialConnection;
import org.assemblits.eru.entities.TcpConnection;
import org.assemblits.eru.entities.Address;
import org.assemblits.eru.entities.Address.DataModel;
import org.assemblits.eru.comm.device.AddressesBlock;
import org.assemblits.eru.entities.Device;
import javafx.application.Platform;
import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.*;

import java.sql.Timestamp;

/**
 * Created by mtrujillo on 3/10/2016.
 */
public class MessageToReadAddressBlock implements Message {

    private Device              device;
    private AddressesBlock      block;
    private ModbusTransaction   transaction;
    private ModbusRequest       request;
    private boolean             wasSuccessful;
    private DataModel           dataModel;
    private int                 offset;
    private int                 firstSlotToRead;
    private int                 lastSlotToRead;

    /* ** Constructor ** */
    public MessageToReadAddressBlock(Device device, AddressesBlock block) {
        this.device     = device;
        this.block      = block;
    }

    /* ** Methods ** */
    @Override
    public void create(){
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

//        transaction.setResponseDelayMS(device.getResponseDelay());
        transaction.setRetries(device.getRetries());
        transaction.setRequest(request);
    }

    @Override
    public void send() throws Exception {
        try {
            transaction.execute();
            updateDeviceStatus("OK");
            wasSuccessful = true;
        } catch (Exception e) {
            wasSuccessful = false;
            updateDeviceStatus(e.getLocalizedMessage());
            throw  e;
        }
    }

    private void updateDeviceStatus(String errorMessage) {
        device.setStatus(errorMessage);
        block.get().forEach(address -> address.setStatus(errorMessage));
    }

    @Override
    public void collectResponse() {
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