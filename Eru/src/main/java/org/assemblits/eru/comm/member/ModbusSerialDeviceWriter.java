package org.assemblits.eru.comm.member;


/**
 * Created by mtrujillo on 5/18/2016.
 */
public class ModbusSerialDeviceWriter extends Communicator {
    @Override
    public void communicate() throws Exception {

    }

    @Override
    public String toString() {
        return null;
    }
//
//    private Device            device;
//    private Address             address;
//    private SerialConnection    connection;
//    private Message             message;
//    private boolean             prepared;
//
//    public ModbusSerialDeviceWriter(Device device, Address address) {
//        this.device  = device;
//        this.address = address;
//    }
//
//    @Override
//    public void prepareToCommunicate() throws Exception {
//        establishesConnection();
//        prepared = true;
//    }
//
//    private void establishesConnection() throws Exception {
////        this.connection = ConnectionFactory.getConnectionFor(device);
////        if(!connection.isOpen()) connection.open();
//    }
//
//    @Override
//    public void communicate() throws Exception {
//        if (!isPreparedToCommunicate()) {
//            throw new Exception("Modbus Serial Reader is not prepared to communicate");
//        } else {
//            createMessageToWriteDevice();
//            message.send();
//            message.collectResponse();
//        }
//    }
//
//    private void createMessageToWriteDevice() {
//        message = new MessageToWriteSerialAddress(device, address, connection);
//        message.create();
//    }
//
//    @Override
//    public void endCommunication() {
//        if(connection!=null && connection.isOpen()) connection.close();
//        message = null;
//        prepared = false;
//    }
//
//    @Override
//    public boolean isPreparedToCommunicate() {
//        return prepared;
//    }
//
//    @Override
//    public String toString() {
//        return device.getName();
//    }
}
