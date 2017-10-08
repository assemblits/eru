package org.assemblits.eru.comm.actors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mtrujillo on 22/05/17.
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Component
public class Director extends Thread {
    private final LinkedBlockingQueue<Communicator> communicators = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        log.info("Starting communicators updating...");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Communicator headParticipant = communicators.take();
                if (!headParticipant.isPrepared()) headParticipant.prepare();
                headParticipant.communicate();
                if (headParticipant.isRepeatable()) communicators.put(headParticipant);
            } catch (InterruptedException e) {
                 log.info("Director stopped.");
            } catch (Exception e) {
                log.error("Director halt", e);
            }
        }
    }
    
}
