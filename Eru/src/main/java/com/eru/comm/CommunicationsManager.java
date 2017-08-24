package com.eru.comm;

import com.eru.comm.member.Communicator;
import com.eru.comm.member.Director;
import com.eru.comm.member.ModbusDeviceCommunicator;
import com.eru.entities.Connection;
import com.eru.gui.EruController;
import com.eru.util.EngineScriptUtil;
import com.eru.util.TagUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 22/05/17.
 */
public class CommunicationsManager {

    private final EruController      eruController;
    private final Director           director      = new Director();
    private final List<Communicator> communicators = new ArrayList<>();

    public CommunicationsManager(EruController eruController) {
        this.eruController  = eruController;
    }

    public void connect(){
        this.eruController.getProject().getConnections()
                .stream()
                .filter(Connection::isEnabled)
                .forEach(Connection::connect);
        this.eruController.getProject().getDevices()
                .stream()
                .filter(device -> device.getConnection() != null && device.getConnection().isConnected())
                .forEach(device -> {
                    device.setStatus("CONNECTED");
                    communicators.add(new ModbusDeviceCommunicator(device));
                });
        this.eruController.getProject().getTags().forEach(tag -> EngineScriptUtil.getInstance().loadTag(tag));
        this.eruController.getProject().getTags().forEach(tag -> TagUtil.installLink(tag, this.eruController.getProject().getTags()));
        startDirector();
    }

    public void disconnect(){
        this.eruController.getProject().getConnections()
                .stream()
                .filter(Connection::isConnected)
                .forEach(Connection::discconnect);
        this.eruController.getProject().getDevices()
                .stream()
                .filter(device -> device.getConnection() != null && device.getConnection().isConnected())
                .forEach(device -> device.setStatus("DISCONNECTED"));
        stopDirector();
    }

    private void startDirector() {
        director.getCommunicators().clear();
        for (Communicator c : communicators) {
            director.getCommunicators().add(c);
        }

        Thread directorThread = new Thread(director, "COMMUNICATIONS_MANAGER");
        directorThread.setDaemon(true);
        directorThread.start();
    }

    private void stopDirector() {
        director.stop();
    }

}
