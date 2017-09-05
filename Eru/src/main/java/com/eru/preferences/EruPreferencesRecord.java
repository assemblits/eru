package com.eru.preferences;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static com.eru.gui.Application.Theme;

/**
 * Created by mtrujillo on 9/2/17.
 */
@Data
public class EruPreferencesRecord {

    private static final double DEFAULT_ROOT_CONTAINER_HEIGHT = 400;
    private static final double DEFAULT_ROOT_CONTAINER_WIDTH = 600;
    private static final Theme DEFAULT_THEME = Theme.DEFAULT;
    private static final int DEFAULT_RECENT_ITEMS_SIZE = 5;

    private static final boolean DEFAULT_ANIMATIONS_ENABLED = true;
    private static final double DEFAULT_ANIMATIONS_DURATION = 5000;

    private static final int DEFAULT_MODBUS_BLOCK_MAX_LIMIT = 120;

    private static final boolean DEFAULT_HISTORIAN_ENABLED = true;
    private static final int DEFAULT_HISTORIAN_LIMIT = 1000;
    private static final int DEFAULT_HISTORIAN_SAMPLING_TIME = 600_000;

    private static final boolean DEFAULT_ALARMING_ENABLED = true;
    private static final int DEFAULT_ALARMING_DATABASE_LIMIT = 1000;
    private static final int DEFAULT_ALARMING_RUNTIME_LIMIT = 100;
    private static final int DEFAULT_ALARMING_SAMPLING_TIME = 500;
    private static final boolean DEFAULT_ALARMING_HORN_ENABLED = true;
    private final Preferences applicationRootPreferences;
    private final List<String> recentItems = new ArrayList<>();
    // Global preferences
    private double rootContainerHeight = DEFAULT_ROOT_CONTAINER_HEIGHT;
    private double rootContainerWidth = DEFAULT_ROOT_CONTAINER_WIDTH;
    private Theme theme = DEFAULT_THEME;
    private int recentItemsSize = DEFAULT_RECENT_ITEMS_SIZE;

    // SCADA preferences
    private boolean animationsEnabled = DEFAULT_ANIMATIONS_ENABLED;
    private double animationsDuration = DEFAULT_ANIMATIONS_DURATION;

    // COMMUNICATIONS preferences
    private int modbusBlockMaxLimit = DEFAULT_MODBUS_BLOCK_MAX_LIMIT;

    // HISTORIAN preferences
    private boolean historianEnabled = DEFAULT_HISTORIAN_ENABLED;
    private int historianLimit = DEFAULT_HISTORIAN_LIMIT;
    private int historianSamplingTime = DEFAULT_HISTORIAN_SAMPLING_TIME;

    // ALARMING preferences
    private boolean alarmingEnabled = DEFAULT_ALARMING_ENABLED;
    private int alarmingDatabaseLimit = DEFAULT_ALARMING_DATABASE_LIMIT;
    private int alarmingRuntimeLimit = DEFAULT_ALARMING_RUNTIME_LIMIT;
    private int alarmingSamplingTime = DEFAULT_ALARMING_SAMPLING_TIME;
    private boolean alarmingHornEnabled = DEFAULT_ALARMING_HORN_ENABLED;

    public EruPreferencesRecord(Preferences applicationRootPreferences) {
        this.applicationRootPreferences = applicationRootPreferences;
    }

    public void readFromJavaPreferences() {
        // TODO
    }

    public void writeToJavaPreferences(String key) {
        // TODO
    }
}
