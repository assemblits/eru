package com.eru.comm.member;

import com.eru.comm.context.Message;
import com.eru.comm.context.MessageToReadAddressBlock;
import com.eru.comm.device.AddressesBlock;
import com.eru.entities.Address;
import com.eru.entities.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 3/9/2016.
 */
public class ModbusDeviceCommunicator extends Communicator {

    private Device          device;
    private List<Message>   messages;

    public ModbusDeviceCommunicator(Device device) {
        this.device   = device;
        this.messages = new ArrayList<>();
        this.setSelfRepeatable(true);
        createMessagesToReadAllDevice();
    }

    private void createMessagesToReadAllDevice() {
        final List<AddressesBlock> addressesBlocksToRead = new ArrayList<>();

        // Extract All AddressBlocks from device in order
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.BOOLEAN_READ));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.BOOLEAN_WRITE));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.ANALOG_READ));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.ANALOG_WRITE));

        // Create messages to read all that blocks
        for(AddressesBlock addressesBlock : addressesBlocksToRead){
            if(!addressesBlock.get().isEmpty()){
                final MessageToReadAddressBlock messageToReadAddressBlock = new MessageToReadAddressBlock(device, addressesBlock);
                messageToReadAddressBlock.create();
                messages.add(messageToReadAddressBlock);
            }
        }
    }

    @Override
    public void communicate() throws Exception {
        for (Message msg : messages) {
            try {
                msg.send();
                msg.collectResponse();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return device.getName();
    }
}
