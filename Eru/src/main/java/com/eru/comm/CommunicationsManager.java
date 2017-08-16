package com.eru.comm;

import com.eru.comm.member.Communicator;
import com.eru.comm.member.Director;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtrujillo on 22/05/17.
 */
public class CommunicationsManager {

    // Singleton
    private static final CommunicationsManager instance = new CommunicationsManager();
    public static CommunicationsManager getInstance(){
        return instance;
    }

    // Director
    private Director            director;
    private List<Communicator>  communicators;
    private boolean             running;

    private CommunicationsManager() {
        this.director       = new Director();
        this.communicators  = new ArrayList<>();
    }

    public void start() throws Exception {
        // Update communicator
        director.removeAllCommunicators();
        for (Communicator c : communicators) {
            director.addCommunicator(c);
        }

        // Create a new Thread to this communication
        Thread directorThread = new Thread(director, "COMMUNICATIONS_MANAGER");
        directorThread.setDaemon(true);
        directorThread.start();

        running = directorThread.isAlive();
    }

    public void stop() throws InterruptedException {
        running = director.stop();
    }

    public List<Communicator> getCommunicators() {
        return communicators;
    }
    public void setCommunicators(List<Communicator> communicators) {
        this.communicators = communicators;
    }

    public boolean isRunning() {
        return running;
    }
    public void setRunning(boolean running) {
        this.running = running;
    }
}
