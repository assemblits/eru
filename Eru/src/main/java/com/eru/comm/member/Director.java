package com.eru.comm.member;

import lombok.extern.log4j.Log4j;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mtrujillo on 22/05/17.
 */
@Log4j
public class Director implements Runnable {
    private LinkedBlockingQueue<Communicator>  communicators = new LinkedBlockingQueue<>();
    private volatile boolean directorShallRun = false;
    private volatile boolean loopRunning     = false;

    @Override
    public void run() {
        directorShallRun = !communicators.isEmpty();
        loopRunning      = true;
        while(directorShallRun){
            try {
                Communicator headParticipant = communicators.take();
                headParticipant.communicate();
                if (headParticipant.isSelfRepeatable()) communicators.put(headParticipant);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loopRunning = false;
    }

    public synchronized void addCommunicator(Communicator participant) throws InterruptedException {
        communicators.put(participant);
    }

    public boolean stop(){
        log.info("Stopping communicators updating...");
        while (loopRunning){
            try {
                directorShallRun = false;
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return loopRunning = true;
            }
        }
        if (!loopRunning) log.info("Communicators updating stopped");
        else log.info("Communicators updating cannot be stopped");
        return loopRunning;
    }

    public void removeAllCommunicators() {
        communicators.clear();
    }
}
