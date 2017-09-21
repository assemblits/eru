package org.assemblits.eru.comm.member;

import org.assemblits.eru.comm.context.Message;
import org.assemblits.eru.comm.context.MessageToReadAddressBlock;
import org.assemblits.eru.comm.device.AddressesBlock;
import org.assemblits.eru.entities.Address;
import org.assemblits.eru.entities.Device;

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
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.COIL));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.DISCRETE_INPUT));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.INPUT_REGISTER));
        addressesBlocksToRead.addAll(device.getAddressesBlocks(Address.DataModel.HOLDING_REGISTER));

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
