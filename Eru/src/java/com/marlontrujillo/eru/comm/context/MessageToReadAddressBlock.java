package com.marlontrujillo.eru.comm.context;

import com.marlontrujillo.eru.comm.connection.SerialConnection;
import com.marlontrujillo.eru.comm.connection.TcpConnection;
import com.marlontrujillo.eru.comm.device.Address;
import com.marlontrujillo.eru.comm.device.Address.DataModel;
import com.marlontrujillo.eru.comm.device.AddressesBlock;
import com.marlontrujillo.eru.comm.device.Device;
import javafx.application.Platform;
import net.wimpi.modbus.io.ModbusSerialTransaction;
import net.wimpi.modbus.io.ModbusTCPTransaction;
import net.wimpi.modbus.io.ModbusTransaction;
import net.wimpi.modbus.msg.*;

import java.sql.Timestamp;
import java.time.LocalTime;

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
    private Timestamp           now;

    /* ** Constructor ** */
    public MessageToReadAddressBlock(Device device, AddressesBlock block) {
        this.device     = device;
        this.block      = block;
        this.now        = new Timestamp(System.currentTimeMillis());
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
            case BOOLEAN_READ:
                request = new ReadInputDiscretesRequest(firstSlotToRead, totalSlotsToRead);
                break;
            case BOOLEAN_WRITE:
                request = new ReadCoilsRequest(firstSlotToRead, totalSlotsToRead);
                break;
            case ANALOG_READ:
                request = new ReadInputRegistersRequest(firstSlotToRead, totalSlotsToRead);
                break;
            case ANALOG_WRITE:
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
    public void send(){
        try {
            transaction.execute();
            updateDeviceStatus("Last update:" + LocalTime.now());
            wasSuccessful = true;
        } catch (Exception e) {
            e.printStackTrace();
            updateDeviceStatus(e.getLocalizedMessage());
            wasSuccessful = false;
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
        now.setTime(System.currentTimeMillis());
        Platform.runLater(() -> {
            for (int slot = firstSlotToRead; slot <= lastSlotToRead; slot++) {
                final Address address = getAddressFromBlock(slot);
                address.setCurrentValue(extractSlotValueFromResponse(response, slot));
                address.setTimestampValueAndFireListeners(now);
            }
        });
    }

    private Address getAddressFromBlock(int slot){
        return block.getAddressWithId(slot + offset);
    }
    private int extractSlotValueFromResponse(ModbusResponse response, int slot){
        final int slotInResponse = slot - firstSlotToRead;
        switch (dataModel) {
            case BOOLEAN_READ:
                final ReadInputDiscretesResponse booleanWriteResponse = (ReadInputDiscretesResponse) response;
                return booleanWriteResponse.getDiscretes().getBit(slotInResponse) ? 1: 0;
            case BOOLEAN_WRITE:
                final ReadCoilsResponse booleanReadResponse = (ReadCoilsResponse) response;
                return booleanReadResponse.getCoilStatus(slotInResponse) ? 1 : 0;
            case ANALOG_READ:
                final ReadInputRegistersResponse analogReadResponse = (ReadInputRegistersResponse) response;
                return analogReadResponse.getRegisterValue(slotInResponse);
            case ANALOG_WRITE:
                final ReadMultipleRegistersResponse analogWriteResponse = (ReadMultipleRegistersResponse) response;
                return analogWriteResponse.getRegisterValue(slotInResponse);
            default:
                return 0;
        }
    }

}