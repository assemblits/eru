package com.eru.scene.control;

import javafx.beans.property.*;
import javafx.scene.control.Skin;
import com.eru.scene.control.skin.SwitchSkin;

/**
 * Created by mtrujillo on 09/02/2015.
 */
public class Switch extends Dynamo {

    private static final int DEFAULT_ANIMATION_DURATION = 400;

    /* ********** Dynamic Fields ********** */
    private BooleanProperty     animated;
    private IntegerProperty     animationDuration;
    private StringProperty      name;
    private BooleanProperty     energized;
    private BooleanProperty     closed;

    /* ********** Constructors ********** */
    public Switch() {
        getStyleClass().add("switch");
        animated            = new SimpleBooleanProperty(this, "animated", true);
        animationDuration   = new SimpleIntegerProperty(this, "animationDuration", DEFAULT_ANIMATION_DURATION);
        name                = new SimpleStringProperty(this, "name", "NAME");
        energized           = new SimpleBooleanProperty(this, "energized", false);
        closed              = new SimpleBooleanProperty(this, "closed", false);
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

    public boolean getEnergized() {
        return energized.get();
    }
    public BooleanProperty energizedProperty() {
        return energized;
    }
    public void setEnergized(boolean energized) {
        this.energized.set(energized);
    }

    public boolean getClosed() {
        return closed.get();
    }
    public BooleanProperty closedProperty() {
        return closed;
    }
    public void setClosed(boolean closed) {
        this.closed.set(closed);
    }

    /* ********** Style and Skin ********** */
    @Override public String getUserAgentStylesheet() {
        return  getClass().getResource("switch.css").toExternalForm();
    }
    @Override protected Skin<?> createDefaultSkin() {
        return new SwitchSkin(this);
    }
}
