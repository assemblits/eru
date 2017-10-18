package org.assemblits.eru.fieldbus.actors;

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
    private final LinkedBlockingQueue<Executor> executors = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        log.info("Starting executors updating...");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Executor headParticipant = executors.take();
                if (!headParticipant.isPrepared()) headParticipant.prepare();
                headParticipant.execute();
                if (headParticipant.isRepeatable()) executors.put(headParticipant);
            } catch (InterruptedException e) {
                 log.info("Director stopped.");
            } catch (Exception e) {
                log.error("Director halt", e);
            }
        }
    }

}
