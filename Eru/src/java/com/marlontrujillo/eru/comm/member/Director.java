package com.marlontrujillo.eru.comm.member;

import com.marlontrujillo.eru.logger.LogUtil;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mtrujillo on 22/05/17.
 */
public class Director implements Runnable {
    private LinkedBlockingQueue<Communicator> communicators = new LinkedBlockingQueue<>();
    private volatile boolean                   run     = false;
    private volatile boolean                   running = false;

    @Override
    public void run() {
        run     = true;
        running = true;
        while(run){
            try {
                Communicator headParticipant = communicators.take();
                headParticipant.communicate();
                if (headParticipant.isSelfRepeatable()) communicators.put(headParticipant);
            } catch (Exception e) {
                LogUtil.logger.error("Director detected a communication error:" + e.toString(), e);
            }
        }
        running = false;
    }

    public synchronized void addCommunicator(Communicator participant) throws InterruptedException {
        communicators.put(participant);
    }

    public void stop(){
        run = false;
        while (running && !communicators.isEmpty()){
            try {
                Thread.sleep(50);
                LogUtil.logger.info("Waiting for communicators task finishing...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LogUtil.logger.info("Communications stopped...");
    }

    public void removeAllCommunicators() {
        communicators.clear();
    }
}
