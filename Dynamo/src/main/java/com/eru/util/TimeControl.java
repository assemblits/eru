package com.eru.util;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;

/**
 * Created by mtrujillo on 5/7/2015.
 */
public class TimeControl {
    public static final int DEFAULT_SAMPLING_TIME       = 5_000; // 5 Seconds
    public static final int DEFAULT_SAMPLING_LIMIT      = 10;
    private static final int DEFAULT_LAST_UPDATE_MILLIS = 0;

    private IntegerProperty samplingTime;
    private IntegerProperty samplingLimit;
    private LongProperty    lastUpdate;

    public TimeControl() {
        this(DEFAULT_SAMPLING_TIME, DEFAULT_SAMPLING_LIMIT, DEFAULT_LAST_UPDATE_MILLIS);
    }

    public TimeControl(int samplingTime, int samplingLimit){
        this(samplingTime, samplingLimit, DEFAULT_LAST_UPDATE_MILLIS);
    }

    public TimeControl(int samplingTime, int samplingLimit, long lastUpdateMillis) {
        this.samplingTime   = new SimpleIntegerProperty(this, "samplingTime", samplingTime);
        this.samplingLimit  = new SimpleIntegerProperty(this, "samplingLimit", samplingLimit);
        this.lastUpdate     = new SimpleLongProperty(this, "lastUpdate", lastUpdateMillis);
    }

    public int getSamplingTime() {
        return samplingTime.get();
    }
    public IntegerProperty samplingTimeProperty() {
        return samplingTime;
    }
    public void setSamplingTime(int samplingTime) {
        this.samplingTime.set(samplingTime);
    }

    public int getSamplingLimit() {
        return samplingLimit.get();
    }
    public IntegerProperty samplingLimitProperty() {
        return samplingLimit;
    }
    public void setSamplingLimit(int samplingLimit) {
        this.samplingLimit.set(samplingLimit);
    }

    public long getLastUpdate() {
        return lastUpdate.get();
    }
    public LongProperty lastUpdateProperty() {
        return lastUpdate;
    }
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate.set(lastUpdate);
    }
}
