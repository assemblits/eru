package org.assemblits.eru.gui.model;

import javafx.beans.Observable;
import lombok.AllArgsConstructor;
import org.assemblits.eru.bus.actors.BusDirector;
import org.assemblits.eru.bus.actors.BusExecutor;
import org.assemblits.eru.bus.protocols.modbus.DeviceBlocksReader;
import org.assemblits.eru.contract.*;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.tag.TagAddressInvalidationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by marlontrujillo1080 on 10/17/17.
 */
@Component
@AllArgsConstructor
public class ProjectContractor {

    private final Agency agency;

    public void startModbusDeviceReading(Device device) {
        BusDirector busDirector = ApplicationContextHolder.getApplicationContext().getBean(BusDirector.class);

        if(!busDirector.isAlive()){
            busDirector.setDaemon(true);
            busDirector.setName("Fieldbus director");
            busDirector.start();
        }

        Agent agent = agency.findAgentByClient(device);

        if(agent == null) {
            agent = new Agent();
            agency.getAgents().add(agent);
        } else {
            agent.getContracts().forEach(Contract::revoke);
        }
        List<Contract> contracts = new ArrayList<>();
        BusExecutor executor = new DeviceBlocksReader(device);
        BiConsumer<BusDirector, BusExecutor> startCommunication = (d, be) -> d.getContexts().add(be);
        BiConsumer<BusDirector, BusExecutor> stopCommunication = (d, be) -> {be.stop(); d.getContexts().remove(be);};
        contracts.add(new GenericContract<>(busDirector, executor, startCommunication, stopCommunication));
        agent.setClient(device);
        agent.setContracts(contracts);
        agent.getContracts().forEach(Contract::accept);
    }

    public void startLinkedAddressListening(Tag tag) {
        Agent agent = agency.findAgentByClient(tag);

        if(agent == null) {
            agent = new Agent();
            agency.getAgents().add(agent);
        } else {
            agent.getContracts().forEach(Contract::revoke);
        }
        List<Contract> contracts = new ArrayList<>();
        Observable observable = tag.getLinkedAddress().timestampProperty();
        TagAddressInvalidationListener listener = new TagAddressInvalidationListener(tag);
        contracts.add(new InvalidObservableContract(observable, listener));
        agent.setClient(tag);
        agent.setContracts(contracts);
        agent.getContracts().forEach(Contract::accept);
    }

    public void stopTasks(Object client) {
        Agent agent = agency.findAgentByClient(client);

        if (agent != null) {
            agent.getContracts().forEach(Contract::revoke);
            agency.getAgents().remove(agent);
        }
    }
}
