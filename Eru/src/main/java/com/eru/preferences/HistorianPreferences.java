package com.eru.preferences;

import org.springframework.stereotype.Component;

/**
 * Created by mtrujillo on 9/13/17.
 */
@Component
public class HistorianPreferences {
    static final boolean DEFAULT_HISTORIAN_ENABLED = true;
    static final String HISTORIAN_ENABLED = "HISTORIAN_ENABLED";
    private boolean historianEnabled = DEFAULT_HISTORIAN_ENABLED;

    static final int DEFAULT_LIMIT = 1000;
    static final String LIMIT = "LIMIT";
    private int limit = DEFAULT_LIMIT;

    static final int DEFAULT_SAMPLING_TIME = 600_000;
    static final String SAMPLING_TIME = "SAMPLING_TIME";
    private int samplingTime = DEFAULT_SAMPLING_TIME;

    public HistorianPreferences() {
    }

    public boolean isHistorianEnabled() {
        return historianEnabled;
    }

    public void setHistorianEnabled(boolean historianEnabled) {
        this.historianEnabled = historianEnabled;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSamplingTime() {
        return samplingTime;
    }

    public void setSamplingTime(int samplingTime) {
        this.samplingTime = samplingTime;
    }
}
