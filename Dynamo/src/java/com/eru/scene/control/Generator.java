package com.eru.scene.control;

import com.eru.scene.control.skin.GeneratorSkin;
import javafx.beans.property.*;
import javafx.scene.control.Skin;

/**
 * Created by mtrujillo on 09/02/2015.
 */
public class Generator extends Dynamo {

    public static final int DEFAULT_ANIMATION_DURATION = 2500;

    public static enum EngineState{
        OFF,
        PREGLOW,
        CRANK,
        RUN,
        COOLDOWN,
        RESTART,
        FAILED,
        UNKNOWN
    }

    /* ********** Dynamic Fields ********** */
    private BooleanProperty             animated;
    private IntegerProperty             animationDuration;
    private StringProperty              name;
    private ObjectProperty<EngineState> state;
    private DoubleProperty              voltagePhaseALL;
    private DoubleProperty              voltagePhaseBLL;
    private DoubleProperty              voltagePhaseCLL;
    private DoubleProperty              currentPhaseALL;
    private DoubleProperty              currentPhaseBLL;
    private DoubleProperty              currentPhaseCLL;
    private DoubleProperty              frequency;
    private IntegerProperty             engineRuntime;
    private DoubleProperty              temperature;
    private DoubleProperty              oilPressure;
    private BooleanProperty             alarmed;
    private StringProperty              easyGenStatus;


    /* ********** Constructors ********** */
    public Generator() {
        getStyleClass().add("generator");
        animated            = new SimpleBooleanProperty(this, "animated", true);
        animationDuration   = new SimpleIntegerProperty(this, "animationDuration", DEFAULT_ANIMATION_DURATION);
        voltagePhaseALL     = new SimpleDoubleProperty(this, "voltagePhaseALL", 0.0);
        voltagePhaseBLL     = new SimpleDoubleProperty(this, "voltagePhaseBLL", 0.0);
        voltagePhaseCLL     = new SimpleDoubleProperty(this, "voltagePhaseCLL", 0.0);
        currentPhaseALL     = new SimpleDoubleProperty(this, "currentPhaseALL", 0.0);
        currentPhaseBLL     = new SimpleDoubleProperty(this, "currentPhaseBLL", 0.0);
        currentPhaseCLL     = new SimpleDoubleProperty(this, "currentPhaseCLL", 0.0);
        frequency           = new SimpleDoubleProperty(this, "frequency", 0.0);
        engineRuntime       = new SimpleIntegerProperty(this, "engineRuntime", 0);
        temperature         = new SimpleDoubleProperty(this, "temperature", 0.0);
        oilPressure         = new SimpleDoubleProperty(this, "oilPressure", 0.0);
        state               = new SimpleObjectProperty<>(this, "state", EngineState.OFF);
        alarmed             = new SimpleBooleanProperty(this, "alarmed", false);
        name                = new SimpleStringProperty(this, "name", "G");
        easyGenStatus       = new SimpleStringProperty(this, "easyGenStatus", "");
    }

    /* ********** Setters and Getters ********** */

    public boolean getAnimated() {
        return animated.get();
    }
    public BooleanProperty animatedProperty() {
        return animated;
    }
    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public int getAnimationDuration() {
        return animationDuration.get();
    }
    public IntegerProperty animationDurationProperty() {
        return animationDuration;
    }
    public void setAnimationDuration(int animationDuration) {
        this.animationDuration.set(animationDuration);
    }

    public String getName() {
        return name.get();
    }
    public StringProperty nameProperty() {
        return name;
    }
    public void setName(String name) {
        this.name.set(name);
    }

    public EngineState getState() {
        return state.get();
    }
    public ObjectProperty<EngineState> stateProperty() {
        return state;
    }
    public void setState(EngineState state) {
        this.state.set(state);
    }

    public double getVoltagePhaseALL() {
        return voltagePhaseALL.get();
    }
    public DoubleProperty voltagePhaseALLProperty() {
        return voltagePhaseALL;
    }
    public void setVoltagePhaseALL(double voltagePhaseALL) {
        this.voltagePhaseALL.set(voltagePhaseALL);
    }

    public double getVoltagePhaseBLL() {
        return voltagePhaseBLL.get();
    }
    public DoubleProperty voltagePhaseBLLProperty() {
        return voltagePhaseBLL;
    }
    public void setVoltagePhaseBLL(double voltagePhaseBLL) {
        this.voltagePhaseBLL.set(voltagePhaseBLL);
    }

    public double getVoltagePhaseCLL() {
        return voltagePhaseCLL.get();
    }
    public DoubleProperty voltagePhaseCLLProperty() {
        return voltagePhaseCLL;
    }
    public void setVoltagePhaseCLL(double voltagePhaseCLL) {
        this.voltagePhaseCLL.set(voltagePhaseCLL);
    }

    public double getCurrentPhaseALL() {
        return currentPhaseALL.get();
    }
    public DoubleProperty currentPhaseALLProperty() {
        return currentPhaseALL;
    }
    public void setCurrentPhaseALL(double currentPhaseALL) {
        this.currentPhaseALL.set(currentPhaseALL);
    }

    public double getCurrentPhaseBLL() {
        return currentPhaseBLL.get();
    }
    public DoubleProperty currentPhaseBLLProperty() {
        return currentPhaseBLL;
    }
    public void setCurrentPhaseBLL(double currentPhaseBLL) {
        this.currentPhaseBLL.set(currentPhaseBLL);
    }

    public double getCurrentPhaseCLL() {
        return currentPhaseCLL.get();
    }
    public DoubleProperty currentPhaseCLLProperty() {
        return currentPhaseCLL;
    }
    public void setCurrentPhaseCLL(double currentPhaseCLL) {
        this.currentPhaseCLL.set(currentPhaseCLL);
    }

    public double getFrequency() {
        return frequency.get();
    }
    public DoubleProperty frequencyProperty() {
        return frequency;
    }
    public void setFrequency(double frequency) {
        this.frequency.set(frequency);
    }

    public int getEngineRuntime() {
        return engineRuntime.get();
    }
    public IntegerProperty engineRuntimeProperty() {
        return engineRuntime;
    }
    public void setEngineRuntime(int engineRuntime) {
        this.engineRuntime.set(engineRuntime);
    }

    public double getTemperature() {
        return temperature.get();
    }
    public DoubleProperty temperatureProperty() {
        return temperature;
    }
    public void setTemperature(double temperature) {
        this.temperature.set(temperature);
    }

    public double getOilPressure() {
        return oilPressure.get();
    }
    public DoubleProperty oilPressureProperty() {
        return oilPressure;
    }
    public void setOilPressure(double oilPressure) {
        this.oilPressure.set(oilPressure);
    }

    public boolean getAlarmed() {
        return alarmed.get();
    }
    public BooleanProperty alarmedProperty() {
        return alarmed;
    }
    public void setAlarmed(boolean alarmed) {
        this.alarmed.set(alarmed);
    }

    public String getEasyGenStatus() {
        return easyGenStatus.get();
    }
    public StringProperty easyGenStatusProperty() {
        return easyGenStatus;
    }
    public void setEasyGenStatus(String easyGenStatus) {
        this.easyGenStatus.set(easyGenStatus);
        switch (easyGenStatus){
            case "Start":
            case "Ramp to rated":
            case "Loading Generator":
            case "Gen stable time":
            case "Unloading Generator":
                setState(EngineState.CRANK);
                break;
            case "In Operation":
            case "Start w/o Load":
                setState(EngineState.RUN);
                break;
            default:
                setState(EngineState.OFF);
        }
    }


    /* ********** Style and Skin ********** */
    @Override public String getUserAgentStylesheet() {
        return getClass().getResource("generator.css").toExternalForm();
    }
    @Override protected Skin<?> createDefaultSkin() {
        return new GeneratorSkin(this);
    }
}
