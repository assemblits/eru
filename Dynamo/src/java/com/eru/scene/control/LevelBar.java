package com.eru.scene.control;

import com.eru.util.Section;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import com.eru.scene.control.skin.LevelBarSkin;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mtrujillo on 23/06/2014.
 */
public class LevelBar extends Dynamo {
    /* ********** Static Fields ********** */
    public static final Color   DEFAULT_TEXT_FILL           = Color.LIGHTGRAY;
    private static final double DEFAULT_ANIMATION_DURATION  = 300;
    private static final int    DEFAULT_DECIMALS            = 2;

    /* ********** Fields ********** */
    private ObjectProperty<Paint>   textFill;
    private DoubleProperty          minValue;
    private DoubleProperty          maxValue;
    private IntegerProperty         decimals;
    private ObservableList<Section> sections;
    private StringProperty          title;
    private StringProperty          unit;
    private BooleanProperty         unitVisible;
    private BooleanProperty         animated;
    private DoubleProperty          animationDuration;


    /* ********** Constructor ********** */
    public LevelBar() {
        getStyleClass().add("level-bar");
        textFill            = new SimpleObjectProperty<>(this, "textFill", DEFAULT_TEXT_FILL);
        minValue            = new SimpleDoubleProperty(this, "minValue", 0.0);
        maxValue            = new SimpleDoubleProperty(this, "maxValue", 0.0);
        decimals            = new SimpleIntegerProperty(this, "decimals", DEFAULT_DECIMALS);
        sections            = FXCollections.observableArrayList();
        title               = new SimpleStringProperty(this, "title", "TITLE");
        unit                = new SimpleStringProperty(this, "unit", "Unit");
        unitVisible         = new SimpleBooleanProperty(this, "unitVisible", true);
        animated            = new SimpleBooleanProperty(this, "animated", true);
        animationDuration   = new SimpleDoubleProperty(this, "animationDuration", DEFAULT_ANIMATION_DURATION);
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

    public double getMinValue() {
        return minValue.get();
    }
    public DoubleProperty minValueProperty() {
        return minValue;
    }
    public void setMinValue(double minValue) {
        this.minValue.set(minValue);
    }

    public double getMaxValue() {
        return maxValue.get();
    }
    public DoubleProperty maxValueProperty() {
        return maxValue;
    }
    public void setMaxValue(double maxValue) {
        this.maxValue.set(maxValue);
    }

    public int getDecimals() {
        return decimals.get();
    }
    public IntegerProperty decimalsProperty() {
        return decimals;
    }
    public void setDecimals(int decimals) {
        this.decimals.set(decimals);
    }

    public final ObservableList<Section> getSections() {
        return sections;
    }
    public final void setSections(final List<Section> SECTIONS) {
        sections.setAll(SECTIONS);
    }
    public final void setSections(final Section... SECTIONS) {
        setSections(Arrays.asList(SECTIONS));
    }
    public final void addSection(final Section SECTION) {
        if (!sections.contains(SECTION)) sections.add(SECTION);
    }
    public final void removeSection(final Section SECTION) {
        if (sections.contains(SECTION)) sections.remove(SECTION);
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

    public String getUnit() {
        return unit.get();
    }
    public StringProperty unitProperty() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit.set(unit);
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

    public boolean getAnimated() {
        return animated.get();
    }
    public BooleanProperty animatedProperty() {
        return animated;
    }
    public void setAnimated(boolean animated) {
        this.animated.set(animated);
    }

    public double getAnimationDuration() {
        return animationDuration.get();
    }
    public DoubleProperty animationDurationProperty() {
        return animationDuration;
    }
    public void setAnimationDuration(double animationDuration) {
        this.animationDuration.set(animationDuration);
    }

    /* ********** Style and Skin ********** */
    @Override public String getUserAgentStylesheet() {
        return getClass().getResource("level-bar.css").toExternalForm();
    }
    @Override protected Skin<?> createDefaultSkin() {
        return new LevelBarSkin(this);
    }
}

