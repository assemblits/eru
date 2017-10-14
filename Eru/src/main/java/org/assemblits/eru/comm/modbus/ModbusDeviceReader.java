package org.assemblits.eru.comm.modbus;

import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.comm.context.Transmission;
import org.assemblits.eru.comm.actors.Communicator;
import org.assemblits.eru.entities.Address;
import org.assemblits.eru.entities.Device;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 3/9/2016.
 */
@Slf4j
public class ModbusDeviceReader extends Communicator<Device> {

    private Device target;
    private final List<Transmission> transmissions;
    private boolean repeatCommunications;

    public ModbusDeviceReader(Device target) {
        this.target = target;
        this.transmissions = new ArrayList<>();
        this.repeatCommunications = true;
    }

    @Override
    public boolean isRepeatable() {
        return repeatCommunications;
    }

    @Override
    public boolean isPrepared() {
        return !transmissions.isEmpty();
    }

    @Override
    public void prepare() {
        final List<AddressesBlock> addressesBlocksToRead = new ArrayList<>();

        // Extract All AddressBlocks from device in order
        addressesBlocksToRead.addAll(target.getAddressesBlocks(Address.DataModel.COIL));
        addressesBlocksToRead.addAll(target.getAddressesBlocks(Address.DataModel.DISCRETE_INPUT));
        addressesBlocksToRead.addAll(target.getAddressesBlocks(Address.DataModel.INPUT_REGISTER));
        addressesBlocksToRead.addAll(target.getAddressesBlocks(Address.DataModel.HOLDING_REGISTER));

        // Create transmissions to read all that blocks
        for(AddressesBlock addressesBlock : addressesBlocksToRead){
            if(!addressesBlock.get().isEmpty()){
                final TransmissionToReadAddressBlock transmissionToReadAddressBlock = new TransmissionToReadAddressBlock(target, addressesBlock);
                transmissionToReadAddressBlock.create();
                transmissions.add(transmissionToReadAddressBlock);
            }
        }
    }

    @Override
    public void communicate() {
        for (Transmission transmission : transmissions) {
            transmission.send();
            transmission.receive();
        }
    }

    @Override
    public void stop() {
        repeatCommunications = false;
    }
}
