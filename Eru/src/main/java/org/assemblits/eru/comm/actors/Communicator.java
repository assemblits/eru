package org.assemblits.eru.comm.actors;

import lombok.Data;

/**
 * Created by mtrujillo on 3/9/2016.
 */
@Data
public abstract class Communicator<T> {
    abstract public boolean isRepeatable();
    abstract public boolean isPrepared();
    abstract public void prepare();
    abstract public void communicate() throws Exception;
    abstract public void stop();
}
