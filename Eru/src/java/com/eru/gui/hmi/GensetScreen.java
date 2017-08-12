package com.eru.gui.hmi;

import com.eru.scene.control.*;
import eu.hansolo.medusa.Gauge;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.AnchorPane;

public class GensetScreen extends AnchorPane {

    /* ********** SYNCHRONIZATION ********** */
    public LineChart<String, Number> syncVoltagesLineChart;
    public LineChart<String, Number> syncFrequemcyLineChart;

    /* ********** ELECTRICAL GAUGES ********** */
    public Gauge genVoltL1L2Gauge;
    public Gauge genVoltL2L3Gauge;
    public Gauge genVoltL3L1Gauge;
    public Gauge genAmpL1Gauge;
    public Gauge genAmpL2Gauge;
    public Gauge genAmpL3Gauge;
    public Gauge pfGauge;
    public Gauge actPowerGauge;
    public Gauge reactPowerGauge;

    /* ********** CHART ********** */
    public LineChart<String, Number> activePowerLineChart;

    /* ********** MECHANICAL GAUGES ********** */
    public Gauge oilPressureGauge;
    public Gauge fuelPressureGauge;
    public Gauge OilPrefilterGauge;
    public Gauge airFilterGauge;
    public Gauge fuelFilterGauge;
    public Gauge fuelRateGauge;

    /* ********** TEMPERATURES ********** */
    public LevelBar intercoolerTempLevelBar;
    public LevelBar oilTempLevelBar;
    public LevelBar coolantTempLevelBar;
    public LevelBar exhManTempLeftLevelBar;
    public LevelBar exhManTempRightLevelBar;

    /* ********** ALARMS ********** */
    public Alarm alarmPreOverspeed;
    public Alarm alarmOverspeed;
    public Alarm alarmPreUnderspeed;
    public Alarm alarmUnderspeed;
    public Alarm alarmUnintendedStop;
    public Alarm alarmShutdown;
    public Alarm alarmGCBFailToClose;
    public Alarm alarmMaintHoursEx;
    public Alarm alarmHighBat;
    public Alarm alarmLowBat;
    public Alarm alarmGroundFault;
    public Alarm alarmGenPhaseRotMiss;
    public Alarm alarmInvTimeOvCurr;
    public Alarm alarmFailStart;
    public Alarm alarmEmergencyButton;
    public Alarm alarmPreOverFreq;
    public Alarm alarmOverFreq;
    public Alarm alarmPreUnderFreq;
    public Alarm alarmUnderFreq;
    public Alarm alarmPreOverVolt;
    public Alarm alarmOverVolt;
    public Alarm alarmPreUnderVolt;
    public Alarm alarmUnderVolt;
    public Alarm alarmPreOverCurr;
    public Alarm alarmOverCurr;
    public Alarm alarmPreRvRdPow;
    public Alarm alarmRvRdPow;
    public Alarm alarmPreUnbalancLoad;
    public Alarm alarmUnbalancLoad;
    public Alarm alarmGenAssym;

    /* ********** DISPLAY ********** */
    public Display hrsNextMaintDisplay;
    public Display controlModeDisplay;
    public Display stateDisplay;

    /* ********** FIGURE ********** */
    public Generator gensetFigure;
    public Switch switchFigure;
    public Load loadFigure;



    /* ********** Constructor ********** */
    public GensetScreen() {
    }

}