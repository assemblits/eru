package com.eru.comm.context;

/**
 * Created by mtrujillo on 5/18/2016.
 */
public class MessageToWriteSerialAddress implements Message {
    @Override
    public void create() {

    }

    @Override
    public void send() {

    }

    @Override
    public void collectResponse() {

    }

//    private ModbusSerialDevice      device;
//    private Address                 address;
//    private SerialConnection        connection;
//    private ModbusSerialTransaction transaction;
//    private ModbusRequest           request;
//    private boolean                 wasSuccessful;
//
//    private Address.DataModel       dataModel;
//    private int                     offset;
//
//    /* ** Constructor ** */
//    public MessageToWriteSerialAddress(ModbusSerialDevice device, Address address, SerialConnection connection) {
//        this.device     = device;
//        this.address    = address;
//        this.connection = connection;
//    }
//
//    @Override
//    public void create() {
//        offset              = device.getZeroBased() ? 1 : 0;
//        dataModel           = address.getAddressPK().getDataModel();
//        switch (dataModel) {
//            case BOOLEAN_WRITE:
//                final int refCoil = address.getAddressPK().getId() - offset;
//                request = new WriteCoilRequest(refCoil, (address.getCurrentValue() > 0));
//                break;
//            case ANALOG_WRITE:
//                final int refHolding = address.getAddressPK().getId() - offset;
//                final Integer newValue = Math.round(address.getCurrentValue());
//                final Register newRegister = new SimpleRegister(newValue);
//                request = new WriteSingleRegisterRequest(refHolding, newRegister);
//                break;
//            default:
//                break;
//        }
//        request.setUnitID(device.getUnitIdentifier());
//        request.setHeadless();
//        transaction = new ModbusSerialTransaction(connection);
//        transaction.setResponseDelayMS(device.getResponseDelay());
//        transaction.setRetries(device.getRetries());
//        transaction.setRequest(request);
//    }
//
//    @Override
//    public void send() {
//        try {
//            transaction.execute();
//            updateDeviceStatus("Last write:" + LocalTime.now());
//            wasSuccessful = true;
//        } catch (ModbusException e) {
//            updateDeviceStatus(e.getLocalizedMessage());
//            e.printStackTrace();
//            wasSuccessful = false;
//        }
//    }
//
//    private void updateDeviceStatus(String errorMessage) {
//        device.setConnected(connection.isOpen());
//        device.setStatus(errorMessage);
//        address.setConnected(connection.isOpen());
//        address.setStatus(errorMessage);
//    }
//
//    @Override
//    public void collectResponse() {
//        if (wasSuccessful){
//            updateAddressWithResponse();
//        }
//    }
//
//    private void updateAddressWithResponse() {
//        switch (dataModel) {
//            case BOOLEAN_WRITE:
//                WriteCoilResponse writeCoilResponse = (WriteCoilResponse) transaction.getResponse();
//                int responseCoilValue = writeCoilResponse.getCoil() ? 1 : 0;
//                if(address.getCurrentValue() == responseCoilValue){
//                    address.setCurrentValue(responseCoilValue);
//                    address.setTimestamp(new Timestamp(System.currentTimeMillis()));
//                }
//                break;
//            case ANALOG_WRITE:
//                WriteSingleRegisterResponse writeRegisterResponse = (WriteSingleRegisterResponse) transaction.getResponse();
//                int responseRegisterValue = writeRegisterResponse.getRegisterValue();
//                if(address.getCurrentValue() == responseRegisterValue){
//                    address.setCurrentValue(responseRegisterValue);
//                    address.setTimestamp(new Timestamp(System.currentTimeMillis()));
//                }
//                break;
//            default:
//                break;
//        }
//    }
}
