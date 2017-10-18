package org.assemblits.eru.bus.actors;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.bus.context.Context;
import org.springframework.stereotype.Component;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mtrujillo on 22/05/17.
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
@Component
public class BusDirector extends Thread {
    private final LinkedBlockingQueue<BusExecutor> contexts = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        log.info("Starting contexts updating...");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                BusExecutor headParticipant = contexts.take();
                if (!headParticipant.isPrepared()) headParticipant.prepare();
                headParticipant.execute();
                if (headParticipant.isRepeatable()) contexts.put(headParticipant);
            } catch (InterruptedException e) {
                 log.info("Director stopped.");
            } catch (Exception e) {
                log.error("Director halt", e);
            }
        }
    }

}
