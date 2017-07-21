package com.marlontrujillo.eru.comm.member;

import com.marlontrujillo.eru.comm.context.Message;
import com.marlontrujillo.eru.comm.context.MessageToReadAddressBlock;
import com.marlontrujillo.eru.comm.device.Address;
import com.marlontrujillo.eru.comm.device.AddressesBlock;
import com.marlontrujillo.eru.comm.device.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 3/9/2016.
 */
public class ModbusDeviceReader extends Communicator {

    private Device          device;
    private List<Message>   messages;

    public ModbusDeviceReader(Device device) {
        this.device   = device;
        this.messages = new ArrayList<>();
        this.setSelfRepeatable(true);
        createMessagesToReadAllDevice();
    }

    private void createMessagesToReadAllDevice() {
        final List<AddressesBlock> addressesBlocksToRead = new ArrayList<>();

        // Extract All AddressBlocks from device
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.BOOLEAN_READ));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.BOOLEAN_WRITE));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.ANALOG_READ));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.ANALOG_WRITE));

        // Create messages to read all that blocks
        for(AddressesBlock addressesBlock : addressesBlocksToRead){
            if(!addressesBlock.get().isEmpty()){
                MessageToReadAddressBlock messageToReadAddressBlock = new MessageToReadAddressBlock(device, addressesBlock);
                messageToReadAddressBlock.create();
                messages.add(messageToReadAddressBlock);
            }
        }
    }

    @Override
    public void communicate() throws Exception {
        if (!device.getConnection().isConnected()) throw new Exception(device.getName() + " is not connected.");
        for (Message msg : messages) {
            msg.send();
            msg.collectResponse();
        }
    }

    @Override
    public String toString() {
        return device.getName();
    }
}
