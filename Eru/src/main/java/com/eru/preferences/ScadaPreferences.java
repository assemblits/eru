package com.eru.preferences;

import org.springframework.stereotype.Component;

/**
 * Created by mtrujillo on 9/13/17.
 */
@Component
public class ScadaPreferences {

    static final boolean DEFAULT_ANIMATIONS_ENABLED = true;
    static final String ANIMATIONS_ENABLED = "ANIMATIONS_ENABLED";
    private boolean animationsEnabled = DEFAULT_ANIMATIONS_ENABLED;

    static final double DEFAULT_ANIMATIONS_DURATION = 5000;
    static final String ANIMATIONS_DURATION = "ANIMATIONS_DURATION";
    private double animationsDuration = DEFAULT_ANIMATIONS_DURATION;

    public ScadaPreferences() {
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
}
