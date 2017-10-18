package org.assemblits.eru.bus.protocols.modbus;

import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.bus.actors.BusExecutor;
import org.assemblits.eru.bus.context.Message;
import org.assemblits.eru.entities.Address;
import org.assemblits.eru.entities.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 3/9/2016.
 */
@Slf4j
public class DeviceBlocksReader implements BusExecutor {

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
