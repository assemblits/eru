package com.marlontrujillo.eru.comm.member;

/**
 * Created by mtrujillo on 3/9/2016.
 */
public abstract class Communicator {
    private boolean selfRepeatable;

    abstract public void communicate() throws Exception;

    public boolean isSelfRepeatable() {
        return selfRepeatable;
    }
    public void setSelfRepeatable(boolean selfRepeatable) {
        this.selfRepeatable = selfRepeatable;
    }

    public abstract String toString();
}
