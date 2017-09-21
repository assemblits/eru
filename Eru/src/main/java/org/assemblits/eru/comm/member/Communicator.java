package org.assemblits.eru.comm.member;

/**
 * Created by mtrujillo on 3/9/2016.
 */
public abstract class Communicator {
    private boolean selfRepeatable;

    abstract public void communicate() throws Exception;

    boolean isSelfRepeatable() {
        return selfRepeatable;
    }
    void setSelfRepeatable(boolean selfRepeatable) {
        this.selfRepeatable = selfRepeatable;
    }

    public abstract String toString();
}
