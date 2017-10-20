package org.assemblits.eru.gui.model;

import javafx.beans.Observable;
import lombok.Data;
import org.assemblits.eru.fieldbus.actors.Director;
import org.assemblits.eru.fieldbus.actors.Executor;
import org.assemblits.eru.fieldbus.protocols.modbus.DeviceBlocksReader;
import org.assemblits.eru.contract.*;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.tag.TagAddressInvalidationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Created by marlontrujillo1080 on 10/17/17.
 */
@Component
@Data
public class ProjectContractor {

    private final Agency agency;
    private final Director fieldbusDirector;

    public void startModbusDeviceReading(Device device) {
        if(!fieldbusDirector.isAlive()){
            fieldbusDirector.setDaemon(true);
            fieldbusDirector.setName("Fieldbus fieldbusDirector");
            fieldbusDirector.start();
        }

        Agent agent = agency.findAgentByClient(device);

        if(agent == null) {
            agent = new Agent();
            agency.getAgents().add(agent);
        } else {
            agent.getContracts().forEach(Contract::revoke);
        }
        List<Contract> contracts = new ArrayList<>();
        Executor executor = new DeviceBlocksReader(device);
        BiConsumer<Director, Executor> startCommunication = (d, be) -> d.getExecutors().add(be);
        BiConsumer<Director, Executor> stopCommunication = (d, be) -> {be.stop(); d.getExecutors().remove(be);};
        contracts.add(new GenericContract<>(fieldbusDirector, executor, startCommunication, stopCommunication));
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
