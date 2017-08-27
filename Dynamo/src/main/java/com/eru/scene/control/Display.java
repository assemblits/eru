package com.eru.scene.control;

import com.eru.scene.control.skin.DisplaySkin;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.concurrent.*;

/**
 * Created by mtrujillo on 18/06/2014.
 */
public class Display extends Control {

    /* ********** Static Fields ********** */
    public static enum DisplayFont {
        STANDARD("Open Sans"),
        LCD("Digital-7"),
        DIGITAL("Digital Readout Upright"),
        DIGITAL_BOLD("Digital Readout Thick Upright"),
        ELEKTRA("Elektra"),
        XOLONIUM("Xolonium");

        private final String name;

        private DisplayFont(final String font){
            name = font;
        }
        public String getFontname() {
            return name;
        }
    }

    public static final Color           DEFAULT_TEXT_FILL           = Color.LIGHTGRAY;
    public static final DisplayFont     DEFAULT_DISPLAY_FONT        = DisplayFont.XOLONIUM;
    public static final int             DEFAULT_BLINKING_INTERVAL   = 500;

    /* ********** Fields ********** */
    private ObjectProperty<Paint>               textFill;
    private StringProperty                      currentText;
    private ObjectProperty<DisplayFont>         valueFont;
    private BooleanProperty                     alarmed;
    private StringProperty                      title;
    private ObjectProperty<DisplayFont>         titleFont;
    private StringProperty                      unit;
    private ObjectProperty<DisplayFont>         unitFont;
    private BooleanProperty                     unitVisible;
    private BooleanProperty                     blinking;
    private IntegerProperty                     blinkingInterval;


    /* ********** Fields ********** */
    private volatile ScheduledFuture<?>         periodicBlinkTask;
    private static ScheduledExecutorService     periodicBlinkExecutorService;


    /* ********** Constructor ********** */
    public Display() {
        getStyleClass().add("display");
        textFill            = new SimpleObjectProperty<>(this, "textFill", DEFAULT_TEXT_FILL);
        currentText         = new SimpleStringProperty(this, "currentTex", "VALUE");
        valueFont           = new SimpleObjectProperty<>(this, "valueFont", DEFAULT_DISPLAY_FONT);
        alarmed             = new SimpleBooleanProperty(this, "alarmed", false);
        title               = new SimpleStringProperty(this, "title", "TITLE");
        titleFont           = new SimpleObjectProperty<>(this, "titleFont", DEFAULT_DISPLAY_FONT);
        unit                = new SimpleStringProperty(this, "unit", "Unit");
        unitFont            = new SimpleObjectProperty<>(this, "unitFont", DEFAULT_DISPLAY_FONT);
        unitVisible         = new SimpleBooleanProperty(this, "unitVisible", true);
        blinking            = new SimpleBooleanProperty(this, "blinking", false);
        blinkingInterval    = new SimpleIntegerProperty(this, "blinkingInterval", DEFAULT_BLINKING_INTERVAL);
    }

    /* ********** Setters and Getters ********** */

    public Paint getTextFill() {
        return textFill.get();
    }
    public ObjectProperty<Paint> textFillProperty() {
        return textFill;
    }
    public void setTextFill(Paint textFill) {
        this.textFill.set(textFill);
    }

    public String getCurrentText() {
        return currentText.get();
    }
    public StringProperty currentTextProperty() {
        return currentText;
    }
    public void setCurrentText(String currentText) {
        this.currentText.set(currentText);
    }

    public DisplayFont getValueFont() {
        return valueFont.get();
    }
    public ObjectProperty<DisplayFont> valueFontProperty() {
        return valueFont;
    }
    public void setValueFont(DisplayFont valueFont) {
        this.valueFont.set(valueFont);
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

    public String getTitle() {
        return title.get();
    }
    public StringProperty titleProperty() {
        return title;
    }
    public void setTitle(String title) {
        this.title.set(title);
    }

    public DisplayFont getTitleFont() {
        return titleFont.get();
    }
    public ObjectProperty<DisplayFont> titleFontProperty() {
        return titleFont;
    }
    public void setTitleFont(DisplayFont titleFont) {
        this.titleFont.set(titleFont);
    }

    public String getUnit() {
        return unit.get();
    }
    public StringProperty unitProperty() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit.set(unit);
    }

    public DisplayFont getUnitFont() {
        return unitFont.get();
    }
    public ObjectProperty<DisplayFont> unitFontProperty() {
        return unitFont;
    }
    public void setUnitFont(DisplayFont unitFont) {
        this.unitFont.set(unitFont);
    }

    public boolean getUnitVisible() {
        return unitVisible.get();
    }
    public BooleanProperty unitVisibleProperty() {
        return unitVisible;
    }
    public void setUnitVisible(boolean unitVisible) {
        this.unitVisible.set(unitVisible);
    }

    public boolean getBlinking() {
        return blinking.get();
    }
    public BooleanProperty blinkingProperty() {
        return blinking;
    }
    public void setBlinking(boolean blinking) {
        this.blinking.set(blinking);
    }

    public int getBlinkingInterval() {
        return blinkingInterval.get();
    }
    public IntegerProperty blinkingIntervalProperty() {
        return blinkingInterval;
    }
    public void setBlinkingInterval(int blinkingInterval) {
        this.blinkingInterval.set(blinkingInterval);
    }

    // ******************** Scheduled tasks ***********************************
    private synchronized static void enableBlinkExecutorService() {
        if (null == periodicBlinkExecutorService) {
            periodicBlinkExecutorService = new ScheduledThreadPoolExecutor(1, getThreadFactory("BlinkDisplay", true));
        }
    }
    private synchronized void scheduleBlinkTask() {
        enableBlinkExecutorService();
        stopTask(periodicBlinkTask);
        periodicBlinkTask = periodicBlinkExecutorService.scheduleAtFixedRate(() ->
                Platform.runLater(() -> setAlarmed(!getAlarmed())), 0, getBlinkingInterval(), TimeUnit.MILLISECONDS);
    }

    private static ThreadFactory getThreadFactory(final String THREAD_NAME, final boolean IS_DAEMON) {
        return runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(IS_DAEMON);
            return thread;
        };
    }

    private void stopTask(ScheduledFuture<?> task) {
        if (null == task) return;
        task.cancel(true);
        task = null;
    }

    /* ********** Style and Skin ********** */
    @Override public String getUserAgentStylesheet() {
        return getClass().getResource("display.css").toExternalForm();
    }
    @Override protected Skin<?> createDefaultSkin() {
        return new DisplaySkin(this);
    }
}
