package org.assemblits.eru.fieldbus.actors;

/**
 * Created by marlontrujillo1080 on 10/17/17.
 */
public interface Executor {
    boolean isPrepared();
    void prepare();
    void execute() throws Exception;
    void stop();
    boolean isRepeatable();
}
