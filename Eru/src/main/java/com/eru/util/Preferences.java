package com.eru.util;

import com.eru.gui.App.Theme;
import com.eru.gui.App;

/**
 *
 * @author marlon
 * 
 * Patron Singleton
 * 
 */
public class Preferences {

    private static final Preferences INSTANCE = new Preferences();
    private final java.util.prefs.Preferences preferences = java.util.prefs.Preferences.userNodeForPackage(getClass());

    private Preferences() {
    }

    public static Preferences getInstance() {
        return INSTANCE;
    }


    /* ********** Visual Module ********** */
    public String getTheme() {
        return preferences.get("visual.eru.theme", Theme.DEFAULT.name());
    }
    public void setTheme(Theme theme) {
        preferences.put("visual.eru.theme", theme.name());
    }

    public boolean getAnimationsEnabled(){
        return preferences.getBoolean("visual.animations.enabled", true);
    }
    public void setAnimationsEnabled(Boolean enabled){
        preferences.put("visual.animations.enabled", enabled.toString());
    }

    public double getAnimationsDuration(){
        return preferences.getDouble("visual.animations.duration", 500);
    }
    public void setAnimationsDuration(double duration){
        preferences.put("visual.animations.duration", String.valueOf(duration));
    }


    /* ********** Communication Module ********** */
    public int getModbusBlockMaxLimit(){
        return preferences.getInt("communications.modbus.blockMaxLimit", 120);// Limite modbus... comprobado en haiti easygens
    }
    public void setModbusBlockMaxLimit(int modbusBlockMaxLimit){
        preferences.put("communications.modbus.blockMaxLimit", String.valueOf(modbusBlockMaxLimit));
    }


    /* ********** Historian Module ********** */
    public boolean getHistorianEnabled(){
        return preferences.getBoolean("report.enabled", true);
    }
    public void setHistorianEnabled(Boolean enabled){
        preferences.put("report.enabled", enabled.toString());
    }

    public int getHistorianCountLimit(){
        return preferences.getInt("report.count.limit", 1000);
    }
    public void setHistorianCountLimit(int historicCountLimit){
        preferences.put("report.count.limit", String.valueOf(historicCountLimit));
    }

    public int getHistorianSamplingTimeMillis(){
        final int TEN_MINUTES = 600_000; //1000*60*10
        return preferences.getInt("report.sampling.time", TEN_MINUTES);
    }
    public void setHistorianSamplingTimeMillis(int historianSamplingTime){
        preferences.put("report.sampling.time", String.valueOf(historianSamplingTime));
    }


    /* ********** Alarm Module ********** */
    public boolean getAlarmingEnabled(){
        return preferences.getBoolean("alarming.enabled", true);
    }
    public void setAlarmingEnabled(Boolean enabled){
        preferences.put("alarming.enabled", enabled.toString());
    }

    public int getAlarmsDatabaseLimit() {
        return  preferences.getInt("alarm.database.limit", 1000);
    }
    public void setAlarmsDatabaseLimit(int limit) {
        preferences.put("alarm.database.limit", String.valueOf(limit));
    }

    public int getAlarmsInMemoryLimit() {
        return  preferences.getInt("alarm.memory.limit", 50);
    }
    public void setAlarmsInMemoryLimit(int limit) {
        preferences.put("alarm.memory.limit", String.valueOf(limit));
    }

    public boolean getAudibleAlarmMuteStatus() {
        return preferences.getBoolean("audibleAlarmMuteStatus", false);
    }
    public void setAudibleAlarmMuteStatus(Boolean muted){
        preferences.put("audibleAlarmMuteStatus", muted.toString());
    }

    public String getAcknowledgedAlarmColor() {
        return preferences.get("alarm.acknowledged.color", "BLACK");
    }
    public void setAcknowledgedAlarmColor(String color) {
        preferences.put("alarm.acknowledged.color", color);
    }

    public String getUnacknowledgedAlarmColor() {
        return preferences.get("alarm.unacknowledged.color", "#A60000");
    }
    public void setUnacknowledgedAlarmColor(String color) {
        preferences.put("alarm.unacknowledged.color", color);
    }

}
