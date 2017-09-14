package com.eru.preferences;

import com.eru.gui.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.eru.preferences.GlobalPreferences.*;
import static com.eru.preferences.ScadaPreferences.*;
import static com.eru.preferences.CommunicationsPreferences.*;
import static com.eru.preferences.HistorianPreferences.*;
import static com.eru.preferences.AlarmingPreferences.*;

import java.util.prefs.Preferences;

/**
 * Created by mtrujillo on 9/2/17.
 */
@Component
public class PreferencesController {

    private final Preferences globalRootPreferences;
    private final Preferences scadaRootPreferences;
    private final Preferences communicationsRootPreferences;
    private final Preferences historianRootPreferences;
    private final Preferences alarmingRootPreferences;

    @Autowired
    private GlobalPreferences globalPreferences;
    @Autowired
    private ScadaPreferences scadaPreferences;
    @Autowired
    private CommunicationsPreferences communicationsPreferences;
    @Autowired
    private HistorianPreferences historianPreferences;
    @Autowired
    private AlarmingPreferences alarmingPreferences;

    private PreferencesController() {
        globalRootPreferences = Preferences.userNodeForPackage(GlobalPreferences.class);
        scadaRootPreferences = Preferences.userNodeForPackage(ScadaPreferences.class);
        communicationsRootPreferences = Preferences.userNodeForPackage(CommunicationsPreferences.class);
        historianRootPreferences = Preferences.userNodeForPackage(HistorianPreferences.class);
        alarmingRootPreferences = Preferences.userNodeForPackage(AlarmingPreferences.class);
        load();
    }

    public void load(){
        // Load Global
        final String theme = globalRootPreferences.get(THEME, DEFAULT_THEME.name());
        globalPreferences.setTheme(Application.Theme.valueOf(theme));

        // Load SCADA
        final boolean animationEnabled = scadaRootPreferences.getBoolean(ANIMATIONS_ENABLED, DEFAULT_ANIMATIONS_ENABLED);
        scadaPreferences.setAnimationsEnabled(animationEnabled);

        final double animationDuration = scadaRootPreferences.getDouble(ANIMATIONS_DURATION, DEFAULT_ANIMATIONS_DURATION);
        scadaPreferences.setAnimationsDuration(animationDuration);

        // Load Communications
        final int modbusBlockMaxLimit = communicationsRootPreferences.getInt(MODBUS_BLOCK_MAX_LIMIT, DEFAULT_MODBUS_BLOCK_MAX_LIMIT);
        communicationsPreferences.setModbusBlockMaxLimit(modbusBlockMaxLimit);

        // Load Historian
        final boolean historianEnabled = historianRootPreferences.getBoolean(HISTORIAN_ENABLED, DEFAULT_HISTORIAN_ENABLED);
        historianPreferences.setHistorianEnabled(historianEnabled);

        final int historianLimit = historianRootPreferences.getInt(LIMIT, DEFAULT_LIMIT);
        historianPreferences.setLimit(historianLimit);

        final int historianSamplingTime = historianRootPreferences.getInt(HistorianPreferences.SAMPLING_TIME, HistorianPreferences.DEFAULT_SAMPLING_TIME);
        historianPreferences.setSamplingTime(historianSamplingTime);

        // Load Alarming
        final boolean alarmingEnabled = alarmingRootPreferences.getBoolean(ALARMING_ENABLED, DEFAULT_ALARMING_ENABLED);
        alarmingPreferences.setAlarmingEnabled(alarmingEnabled);

        final int alarmingDatabaseLimit = alarmingRootPreferences.getInt(DATABASE_LIMIT, DEFAULT_DATABASE_LIMIT);
        alarmingPreferences.setDatabaseLimit(alarmingDatabaseLimit);

        final int alarmingRuntimeLimit = alarmingRootPreferences.getInt(RUNTIME_LIMIT, DEFAULT_RUNTIME_LIMIT);
        alarmingPreferences.setRuntimeLimit(alarmingRuntimeLimit);

        final int alarmingSamplingTime = alarmingRootPreferences.getInt(AlarmingPreferences.SAMPLING_TIME, AlarmingPreferences.DEFAULT_SAMPLING_TIME);
        alarmingPreferences.setSamplingTime(alarmingSamplingTime);

        final boolean alarmingHornEnabled = alarmingRootPreferences.getBoolean(HORN_ENABLED, DEFAULT_HORN_ENABLED);
        alarmingPreferences.setHornEnabled(alarmingHornEnabled);
    }

    public void save(){
        // Save Global
        globalRootPreferences.put(THEME, globalPreferences.getTheme().name());

        // Save SCADA
        scadaRootPreferences.putBoolean(ANIMATIONS_ENABLED, scadaPreferences.isAnimationsEnabled());
        scadaRootPreferences.putDouble(ANIMATIONS_DURATION, scadaPreferences.getAnimationsDuration());

        // Save Communications
        communicationsRootPreferences.putInt(MODBUS_BLOCK_MAX_LIMIT, communicationsPreferences.getModbusBlockMaxLimit());

        // Save Historian
        historianRootPreferences.putBoolean(HISTORIAN_ENABLED, historianPreferences.isHistorianEnabled());
        historianRootPreferences.putInt(LIMIT, historianPreferences.getLimit());
        historianRootPreferences.putInt(HistorianPreferences.SAMPLING_TIME, historianPreferences.getSamplingTime());

        // Save Alarming
        alarmingRootPreferences.getBoolean(ALARMING_ENABLED, alarmingPreferences.isAlarmingEnabled());
        alarmingRootPreferences.putInt(DATABASE_LIMIT, alarmingPreferences.getDatabaseLimit());
        alarmingRootPreferences.putInt(RUNTIME_LIMIT, alarmingPreferences.getRuntimeLimit());
        alarmingRootPreferences.putInt(AlarmingPreferences.SAMPLING_TIME, alarmingPreferences.getSamplingTime());
        alarmingRootPreferences.putBoolean(HORN_ENABLED, alarmingPreferences.isHornEnabled());
    }

    public GlobalPreferences getGlobalPreferences() {
        return globalPreferences;
    }

    public ScadaPreferences getScadaPreferences() {
        return scadaPreferences;
    }

    public CommunicationsPreferences getCommunicationsPreferences() {
        return communicationsPreferences;
    }

    public HistorianPreferences getHistorianPreferences() {
        return historianPreferences;
    }

    public AlarmingPreferences getAlarmingPreferences() {
        return alarmingPreferences;
    }

}
