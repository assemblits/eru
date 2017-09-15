package com.eru.preferences;

/**
 * Created by mtrujillo on 9/13/17.
 */
public class AlarmingPreferences {
    static final boolean DEFAULT_ALARMING_ENABLED = true;
    static final String ALARMING_ENABLED = "ALARMING_ENABLED";
    private boolean alarmingEnabled = DEFAULT_ALARMING_ENABLED;

    static final int DEFAULT_DATABASE_LIMIT = 1000;
    static final String DATABASE_LIMIT = "DATABASE_LIMIT";
    private int databaseLimit = DEFAULT_DATABASE_LIMIT;

    static final int DEFAULT_RUNTIME_LIMIT = 100;
    static final String RUNTIME_LIMIT = "RUNTIME_LIMIT";
    private int runtimeLimit = DEFAULT_RUNTIME_LIMIT;

    static final int DEFAULT_SAMPLING_TIME = 500;
    static final String SAMPLING_TIME = "SAMPLING_TIME";
    private int samplingTime = DEFAULT_SAMPLING_TIME;

    static final boolean DEFAULT_HORN_ENABLED = true;
    static final String HORN_ENABLED = "HORN_ENABLED";
    private boolean hornEnabled = DEFAULT_HORN_ENABLED;

    public AlarmingPreferences() {
    }

    public boolean isAlarmingEnabled() {
        return alarmingEnabled;
    }

    public void setAlarmingEnabled(boolean alarmingEnabled) {
        this.alarmingEnabled = alarmingEnabled;
    }

    public int getDatabaseLimit() {
        return databaseLimit;
    }

    public void setDatabaseLimit(int databaseLimit) {
        this.databaseLimit = databaseLimit;
    }

    public int getRuntimeLimit() {
        return runtimeLimit;
    }

    public void setRuntimeLimit(int runtimeLimit) {
        this.runtimeLimit = runtimeLimit;
    }

    public int getSamplingTime() {
        return samplingTime;
    }

    public void setSamplingTime(int samplingTime) {
        this.samplingTime = samplingTime;
    }

    public boolean isHornEnabled() {
        return hornEnabled;
    }

    public void setHornEnabled(boolean hornEnabled) {
        this.hornEnabled = hornEnabled;
    }
}
