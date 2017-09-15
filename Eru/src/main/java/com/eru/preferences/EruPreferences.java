package com.eru.preferences;

import com.eru.gui.Application;
import org.springframework.stereotype.Component;

import java.util.prefs.Preferences;

/**
 * Created by mtrujillo on 9/2/17.
 */
@Component
public class EruPreferences {

    private final Preferences appPreferences;

    public EruPreferences() {
        appPreferences = Preferences.userNodeForPackage(Application.class);
    }

    public Application.Theme getTheme() {
        return Application.Theme.valueOf(appPreferences.get("theme", Application.Theme.DEFAULT.name()));
    }

    public void setTheme(Application.Theme theme) {
        appPreferences.put("theme", theme.name());
    }

    public boolean isAnimationsEnabled() {
        return appPreferences.getBoolean("animations.enabled", true);
    }

    public void setAnimationsEnabled(boolean animationsEnabled) {
        appPreferences.putBoolean("animations.enabled", animationsEnabled);
    }

    public double getAnimationsDuration() {
        return appPreferences.getDouble("animations.duration", 5000);
    }

    public void setAnimationsDuration(double animationsDuration) {
        appPreferences.putDouble("animations.duration", animationsDuration);
    }

    public int getModbusBlockMaxLimit() {
        return appPreferences.getInt("modbus.block.maxLimit", 120);
    }

    public void setModbusBlockMaxLimit(int modbusBlockMaxLimit) {
        appPreferences.putInt("modbus.block.maxLimit", modbusBlockMaxLimit);
    }

    public boolean isHistorianEnabled() {
        return appPreferences.getBoolean("historian.enabled", true);
    }

    public void setHistorianEnabled(boolean historianEnabled) {
        appPreferences.putBoolean("historian.enabled", historianEnabled);
    }

    public int getHistorianLimit() {
        return appPreferences.getInt("historian.limit", 1000);
    }

    public void setHistorianLimit(int limit) {
        appPreferences.putInt("historian.limit", limit);
    }

    public int getHistorianSamplingTime() {
        return appPreferences.getInt("historian.samplingTime", 600_000);
    }

    public void setHistorianSamplingTime(int samplingTime) {
        appPreferences.putInt("historian.samplingTime", samplingTime);
    }

    public boolean isAlarmingEnabled() {
        return appPreferences.getBoolean("alarming.enabled", true);
    }

    public void setAlarmingEnabled(boolean alarmingEnabled) {
        appPreferences.putBoolean("alarming.enabled", alarmingEnabled);
    }

    public int getAlarmingDatabaseLimit() {
        return appPreferences.getInt("alarming.databaseLimit", 1000);
    }

    public void setAlarmingDatabaseLimit(int databaseLimit) {
        appPreferences.putInt("alarming.databaseLimit", databaseLimit);
    }

    public int getAlarmingRuntimeLimit() {
        return appPreferences.getInt("alarming.runtimeLimit", 100);
    }

    public void setAlarmingRuntimeLimit(int runtimeLimit) {
        appPreferences.putInt("alarming.runtimeLimit", runtimeLimit);
    }

    public int getAlarmingSamplingTime() {
        return appPreferences.getInt("alarming.samplingTime", 500);
    }

    public void setAlarmingSamplingTime(int samplingTime) {
        appPreferences.putInt("alarming.samplingTime", samplingTime);
    }

    public boolean isAlarmingHornEnabled() {
        return appPreferences.getBoolean("alarming.hornEnabled", true);
    }

    public void setAlarmingHornEnabled(boolean hornEnabled) {
        appPreferences.putBoolean("alarming.horEnabled", hornEnabled);
    }

    public String getApplicationDirectory() {
        return appPreferences.get("app.directory", "~/.eru");
    }

    public void setApplicationDirectory(String directoryPath) {
        appPreferences.put("app.directory", directoryPath);
    }

    public boolean isApplicationConfigured() {
        return appPreferences.getBoolean("app.configured", false);
    }

    public void setApplicationConfigured(boolean configured) {
        appPreferences.putBoolean("app.configured", configured);
    }

}
