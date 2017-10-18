package org.assemblits.eru.bus.actors;

/**
 * Created by marlontrujillo1080 on 10/17/17.
 */
public interface BusExecutor {
    boolean isPrepared();
    void prepare();
    void execute() throws Exception;
    void stop();
    boolean isRepeatable();
}
