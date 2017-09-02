package com.eru.preferences;

import java.util.prefs.Preferences;

/**
 * Created by mtrujillo on 9/2/17.
 */
public class EruPreferences {

    // GLOBAL PREFERENCES
    static final String DOCUMENTS = "DOCUMENTS";
    static final String SCADA = "SCADA";
    static final String COMMUNICATIONS = "SCADA";
    static final String HISTORIAN = "HISTORIAN";
    static final String ALARMING = "ALARMING";
    static final String ROOT_CONTAINER_HEIGHT = "ROOT_CONTAINER_HEIGHT";
    static final String ROOT_CONTAINER_WIDTH = "ROOT_CONTAINER_WIDTH";
    static final String THEME = "THEME";
    static final String RECENT_ITEMS = "RECENT_ITEMS";
    static final String RECENT_ITEMS_SIZE = "RECENT_ITEMS_SIZE";

    // SCADA PREFERENCES
    static final String ANIMATIONS_ENABLED = "ANIMATIONS_ENABLED";
    static final String ANIMATIONS_DURATION = "ANIMATIONS_DURATION";

    // COMMUNICATIONS PREFERENCES
    static final String MODBUS_BLOCK_MAX_LIMIT = "MODBUS_BLOCK_MAX_LIMIT";

    // HISTORIAN PREFERENCES
    static final String HISTORIAN_ENABLED = "HISTORIAN_ENABLED";
    static final String HISTORIAN_LIMIT = "HISTORIAN_LIMIT";
    static final String HISTORIAN_SAMPLING_TIME = "HISTORIAN_SAMPLING_TIME";

    // ALARMING PREFERENCES
    static final String ALARMING_ENABLED = "ALARMING_ENABLED";
    static final String ALARMING_DATABASE_LIMIT = "ALARMING_DATABASE_LIMIT";
    static final String ALARMING_RUNTIME_LIMIT = "ALARMING_RUNTIME_LIMIT";
    static final String ALARMING_SAMPLING_TIME = "ALARMING_SAMPLING_TIME";
    static final String ALARMING_HORN_ENABLED = "ALARMING_HORN_ENABLED";


    private static final EruPreferences INSTANCE = new EruPreferences();

    private final Preferences applicationRootPreferences;
    private final Preferences documentsRootPreferences;
    private final Preferences scadaRootPreferences;
    private final Preferences communicationsRootPreferences;
    private final Preferences historianRootPreferences;
    private final Preferences alarmingRootPreferences;

    private final EruPreferencesRecord eruPreferencesRecord;

    private EruPreferences() {
        applicationRootPreferences = Preferences.userNodeForPackage(EruPreferences.class);
        documentsRootPreferences = applicationRootPreferences.node(DOCUMENTS);
        scadaRootPreferences = applicationRootPreferences.node(SCADA);
        communicationsRootPreferences = applicationRootPreferences.node(COMMUNICATIONS);
        historianRootPreferences = applicationRootPreferences.node(HISTORIAN);
        alarmingRootPreferences = applicationRootPreferences.node(ALARMING);

        eruPreferencesRecord = new EruPreferencesRecord(applicationRootPreferences);
    }

    public static synchronized EruPreferences getInstance() {
        return INSTANCE;
    }

    public EruPreferencesRecord getEruPreferencesRecord() {
        return eruPreferencesRecord;
    }
}
