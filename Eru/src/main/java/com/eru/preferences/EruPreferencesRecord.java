package com.eru.preferences;

import com.eru.gui.App.Theme;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Created by mtrujillo on 9/2/17.
 */
public class EruPreferencesRecord {

    // Default values
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

    // Global preferences
    private double rootContainerHeight = DEFAULT_ROOT_CONTAINER_HEIGHT;
    private double rootContainerWidth = DEFAULT_ROOT_CONTAINER_WIDTH;
    private Theme theme = DEFAULT_THEME;
    private final List<String> recentItems = new ArrayList<>();
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

    public double getRootContainerHeight() {
        return rootContainerHeight;
    }

    public void setRootContainerHeight(double rootContainerHeight) {
        this.rootContainerHeight = rootContainerHeight;
    }

    public double getRootContainerWidth() {
        return rootContainerWidth;
    }

    public void setRootContainerWidth(double rootContainerWidth) {
        this.rootContainerWidth = rootContainerWidth;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public List<String> getRecentItems() {
        return recentItems;
    }

    public int getRecentItemsSize() {
        return recentItemsSize;
    }

    public void setRecentItemsSize(int recentItemsSize) {
        this.recentItemsSize = recentItemsSize;
    }

    public boolean isAnimationsEnabled() {
        return animationsEnabled;
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        this.animationsEnabled = animationsEnabled;
    }

    public double getAnimationsDuration() {
        return animationsDuration;
    }

    public void setAnimationsDuration(double animationsDuration) {
        this.animationsDuration = animationsDuration;
    }

    public int getModbusBlockMaxLimit() {
        return modbusBlockMaxLimit;
    }

    public void setModbusBlockMaxLimit(int modbusBlockMaxLimit) {
        this.modbusBlockMaxLimit = modbusBlockMaxLimit;
    }

    public boolean isHistorianEnabled() {
        return historianEnabled;
    }

    public void setHistorianEnabled(boolean historianEnabled) {
        this.historianEnabled = historianEnabled;
    }

    public int getHistorianLimit() {
        return historianLimit;
    }

    public void setHistorianLimit(int historianLimit) {
        this.historianLimit = historianLimit;
    }

    public int getHistorianSamplingTime() {
        return historianSamplingTime;
    }

    public void setHistorianSamplingTime(int historianSamplingTime) {
        this.historianSamplingTime = historianSamplingTime;
    }

    public boolean isAlarmingEnabled() {
        return alarmingEnabled;
    }

    public void setAlarmingEnabled(boolean alarmingEnabled) {
        this.alarmingEnabled = alarmingEnabled;
    }

    public int getAlarmingDatabaseLimit() {
        return alarmingDatabaseLimit;
    }

    public void setAlarmingDatabaseLimit(int alarmingDatabaseLimit) {
        this.alarmingDatabaseLimit = alarmingDatabaseLimit;
    }

    public int getAlarmingRuntimeLimit() {
        return alarmingRuntimeLimit;
    }

    public void setAlarmingRuntimeLimit(int alarmingRuntimeLimit) {
        this.alarmingRuntimeLimit = alarmingRuntimeLimit;
    }

    public int getAlarmingSamplingTime() {
        return alarmingSamplingTime;
    }

    public void setAlarmingSamplingTime(int alarmingSamplingTime) {
        this.alarmingSamplingTime = alarmingSamplingTime;
    }

    public boolean isAlarmingHornEnabled() {
        return alarmingHornEnabled;
    }

    public void setAlarmingHornEnabled(boolean alarmingHornEnabled) {
        this.alarmingHornEnabled = alarmingHornEnabled;
    }

    public void readFromJavaPreferences() {
        // TODO
    }

    public void writeToJavaPreferences(String key) {
        // TODO
    }
}
