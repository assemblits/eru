package com.eru.scene.control;

import com.eru.scene.control.skin.GaugeSkin;
import com.eru.util.Section;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Skin;

import java.util.Arrays;
import java.util.List;

/**
 * Created by mtrujillo on 12/06/2014.
 */
public class Gauge extends Dynamo{

    /* ********** Static Fields ********** */
    public static enum DisplayFont {
        STANDARD("Open Sans"),
        LCD("Digital-7"),
        DIGITAL("Digital Readout Upright"),
        DIGITAL_BOLD("Digital Readout Thick Upright"),
        ELEKTRA("Elektra");

        private final String name;

        private DisplayFont(final String font){
            name = font;
        }
        public String getFontname() {
            return name;
        }
    }
    public static enum TickLabelOrientation {
        ORTHOGONAL,
        HORIZONTAL,
        TANGENT
    }
    public static final int DEFAULT_ANIMATION_DURATION = 300;

    /* ********** Fields ********** */
    private final DoubleProperty                          currentValue;
    private final ObjectProperty<DisplayFont>             valueFont;
    private final DoubleProperty                          minValue;
    private final DoubleProperty                          maxValue;
    private final DoubleProperty                          threshold;
    private final DoubleProperty                          minMeasuredValue;
    private final DoubleProperty                          maxMeasuredValue;
    private final IntegerProperty                         decimals;
    private final StringProperty                          title;
    private final StringProperty                          unit;
    private final DoubleProperty                          startAngle;
    private final DoubleProperty                          angleRange;
    private final BooleanProperty                         clockwise;
    private final ObjectProperty<TickLabelOrientation>    tickLabelOrientation;
    private final DoubleProperty                          majorTickSpace;
    private final DoubleProperty                          minorTickSpace;
    private final BooleanProperty                         ticksRoundedToInteger;
    private final BooleanProperty                         customTickLabelsEnabled;
    private final ObservableList<String>                  customTickLabels;
    private final BooleanProperty                         plainValue;
    private final BooleanProperty                         histogramEnabled;
    private final BooleanProperty                         dropShadowEnabled;
    private final BooleanProperty                         sectionsVisible;
    private final BooleanProperty                         thresholdVisible;
    private final BooleanProperty                         minMeasuredValueVisible;
    private final BooleanProperty                         maxMeasuredValueVisible;
    private final BooleanProperty                         animated;
    private final DoubleProperty                          animationDuration;
    private final ObservableList<Section>                 sections;

    // ******************** Constructors **************************************
    public Gauge() {
        getStyleClass().add("gauge");
        valueFont               = new SimpleObjectProperty<>(this, "valueFont", DisplayFont.DIGITAL);
        currentValue            = new DoublePropertyBase(0) {
            @Override protected void invalidated() {
                set(clamp(getMinValue(), getMaxValue(), get()));
            }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "currentValue"; }
        };
        minValue                = new DoublePropertyBase(0) {
            @Override protected void invalidated() {
                if (getValue() < get()) setValue(get());
                if (getThreshold() < get()) setThreshold(get());
                for (Section section : sections) {
                    if (section.getStart() < get()) section.setStart(get());
                    if (section.getStop() < get()) section.setStop(get());
                }
            }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "minValue"; }
        };
        maxValue                = new DoublePropertyBase(100) {
            @Override protected void invalidated() {
                if (getValue() > get()) setValue(get());
                if (getThreshold() > get()) setThreshold(get());
                for (Section section : sections) {
                    if (section.getStart() > get()) section.setStart(get());
                    if (section.getStop() > get()) section.setStop(get());
                }
            }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "maxValue"; }
        };
        threshold               = new DoublePropertyBase(50) {
            @Override protected void invalidated() { set(clamp(getMinValue(), getMaxValue(), get())); }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "threshold"; }
        };
        minMeasuredValue        = new SimpleDoubleProperty(this, "minMeasuredValue", 0);
        maxMeasuredValue        = new SimpleDoubleProperty(this, "maxMeasuredValue", 0);
        decimals                = new IntegerPropertyBase(1) {
            @Override
            protected void invalidated() {
                set(clamp(0, 3, get()));
            }

            @Override
            public Object getBean() {
                return this;
            }

            @Override
            public String getName() {
                return "decimals";
            }
        };
        title                   = new SimpleStringProperty(this, "title", "");
        unit                    = new SimpleStringProperty(this, "unit", "");
        startAngle              = new DoublePropertyBase(0) {
            @Override protected void invalidated() { set(clamp(0d, 360d, get())); }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "startAngle"; }
        };
        angleRange              = new DoublePropertyBase(360) {
            @Override protected void invalidated() { set(clamp(0d, 360d, get())); }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "angleRange"; }
        };
        clockwise               = new SimpleBooleanProperty(this, "clockwise", true);
        tickLabelOrientation    = new SimpleObjectProperty<>(this, "tickLabelOrientation", TickLabelOrientation.HORIZONTAL);
        sections                = FXCollections.observableArrayList();
        majorTickSpace          = new SimpleDoubleProperty(this, "majorTickSpace", 10);
        minorTickSpace          = new SimpleDoubleProperty(this, "majorTickSpace", 1);
        ticksRoundedToInteger   = new SimpleBooleanProperty(this, "ticksRoundedToInteger", true);
        customTickLabelsEnabled = new SimpleBooleanProperty(this, "customTickLabelsEnabled", false);
        customTickLabels        = FXCollections.observableArrayList();
        plainValue              = new SimpleBooleanProperty(this, "plainValue", true);
        histogramEnabled        = new SimpleBooleanProperty(this, "historigramEnabled", true);
        dropShadowEnabled       = new SimpleBooleanProperty(this, "dropShadowEnabled", true);
        sectionsVisible         = new SimpleBooleanProperty(this, "sectionsVisible", true);
        thresholdVisible        = new SimpleBooleanProperty(this, "thresholdVisible", true);
        minMeasuredValueVisible = new SimpleBooleanProperty(this, "minMeasuredValueVisible", true);
        maxMeasuredValueVisible = new SimpleBooleanProperty(this, "maxMeasuredValueVisible", true);
        animated                = new SimpleBooleanProperty(this, "animated", true);
        animationDuration       = new SimpleDoubleProperty(this, "animationDuration", DEFAULT_ANIMATION_DURATION);
    }

    // ******************** Setters and Getters *******************************************
    @Override
    public double getCurrentValue() {
        return currentValue.get();
    }
    @Override
    public DoubleProperty currentValueProperty() {
        return currentValue;
    }
    @Override
    public void setCurrentValue(double currentValue) {
        this.currentValue.set(currentValue);
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

    public double getThreshold() {
        return threshold.get();
    }
    public DoubleProperty thresholdProperty() {
        return threshold;
    }
    public void setThreshold(double threshold) {
        this.threshold.set(clamp(getMinValue(), getMaxValue(), threshold));
    }

    public double getMinMeasuredValue() {
        return minMeasuredValue.get();
    }
    public DoubleProperty minMeasuredValueProperty() {
        return minMeasuredValue;
    }
    public void setMinMeasuredValue(double minMeasuredValue) {
        this.minMeasuredValue.set(minMeasuredValue);
    }

    public double getMaxMeasuredValue() {
        return maxMeasuredValue.get();
    }
    public DoubleProperty maxMeasuredValueProperty() {
        return maxMeasuredValue;
    }
    public void setMaxMeasuredValue(double maxMeasuredValue) {
        this.maxMeasuredValue.set(maxMeasuredValue);
    }

    public int getDecimals() {
        return decimals.get();
    }
    public IntegerProperty decimalsProperty() {
        return decimals;
    }
    public void setDecimals(int decimals) {
        this.decimals.set(clamp(0, 3, decimals));
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

    public double getStartAngle() {
        return startAngle.get();
    }
    public DoubleProperty startAngleProperty() {
        return startAngle;
    }
    public void setStartAngle(double startAngle) {
        this.startAngle.set(clamp(0, 360, startAngle));
    }

    public double getAngleRange() {
        return angleRange.get();
    }
    public DoubleProperty angleRangeProperty() {
        return angleRange;
    }
    public void setAngleRange(double angleRange) {
        this.angleRange.set(clamp(0.0, 360.0, angleRange));
    }

    public boolean getClockwise() {
        return clockwise.get();
    }
    public BooleanProperty clockwiseProperty() {
        return clockwise;
    }
    public void setClockwise(boolean clockwise) {
        this.clockwise.set(clockwise);
    }

    public TickLabelOrientation getTickLabelOrientation() {
        return tickLabelOrientation.get();
    }
    public ObjectProperty<TickLabelOrientation> tickLabelOrientationProperty() {
        return tickLabelOrientation;
    }
    public void setTickLabelOrientation(TickLabelOrientation tickLabelOrientation) {
        this.tickLabelOrientation.set(tickLabelOrientation);
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

    public double getMajorTickSpace() {
        return majorTickSpace.get();
    }
    public DoubleProperty majorTickSpaceProperty() {
        return majorTickSpace;
    }
    public void setMajorTickSpace(double majorTickSpace) {
        this.majorTickSpace.set(majorTickSpace);
    }

    public double getMinorTickSpace() {
        return minorTickSpace.get();
    }
    public DoubleProperty minorTickSpaceProperty() {
        return minorTickSpace;
    }
    public void setMinorTickSpace(double minorTickSpace) {
        this.minorTickSpace.set(minorTickSpace);
    }

    public boolean getTicksRoundedToInteger() {
        return ticksRoundedToInteger.get();
    }
    public BooleanProperty ticksRoundedToIntegerProperty() {
        return ticksRoundedToInteger;
    }
    public void setTicksRoundedToInteger(boolean ticksRoundedToInteger) {
        this.ticksRoundedToInteger.set(ticksRoundedToInteger);
    }

    public boolean getCustomTickLabelsEnabled() {
        return customTickLabelsEnabled.getValue();
    }
    public void setCustomTickLabelsEnabled(final boolean ENABLED) {
        customTickLabelsEnabled.setValue(ENABLED);
    }
    public BooleanProperty customTickLabelsEnabledProperty() {
        return customTickLabelsEnabled;
    }

    public List<String> getCustomTickLabels() { return customTickLabels; }
    public void setCustomTickLabels(final List<String> TICK_LABELS) {
        customTickLabels.setAll(TICK_LABELS);
    }
    public void setCustomTickLabels(final String... TICK_LABELS) {
        setCustomTickLabels(Arrays.asList(TICK_LABELS));
    }

    /**
     * @return true if the value of the gauge will be drawn and visualized without a blend effect
     */
    public boolean getPlainValue() {
        return plainValue.get();
    }
    public BooleanProperty plainValueProperty() {
        return plainValue;
    }
    public void setPlainValue(boolean plainValue) {
        this.plainValue.set(plainValue);
    }

    public boolean getHistogramEnabled() {
        return histogramEnabled.get();
    }
    public BooleanProperty histogramEnabledProperty() {
        return histogramEnabled;
    }
    public void setHistogramEnabled(boolean histogramEnabled) {
        this.histogramEnabled.set(histogramEnabled);
    }

    public boolean getDropShadowEnabled() {
        return dropShadowEnabled.get();
    }
    public BooleanProperty dropShadowEnabledProperty() {
        return dropShadowEnabled;
    }
    public void setDropShadowEnabled(boolean dropShadowEnabled) {
        this.dropShadowEnabled.set(dropShadowEnabled);
    }

    public boolean getSectionsVisible() {
        return sectionsVisible.get();
    }
    public BooleanProperty sectionsVisibleProperty() {
        return sectionsVisible;
    }
    public void setSectionsVisible(boolean sectionsVisible) {
        this.sectionsVisible.set(sectionsVisible);
    }

    public boolean getThresholdVisible() {
        return thresholdVisible.get();
    }
    public BooleanProperty thresholdVisibleProperty() {
        return thresholdVisible;
    }
    public void setThresholdVisible(boolean thresholdVisible) {
        this.thresholdVisible.set(thresholdVisible);
    }

    public boolean getMinMeasuredValueVisible() {
        return minMeasuredValueVisible.get();
    }
    public BooleanProperty minMeasuredValueVisibleProperty() {
        return minMeasuredValueVisible;
    }
    public void setMinMeasuredValueVisible(boolean minMeasuredValueVisible) {
        this.minMeasuredValueVisible.set(minMeasuredValueVisible);
    }

    public boolean getMaxMeasuredValueVisible() { return maxMeasuredValueVisible.get(); }
    public BooleanProperty maxMeasuredValueVisibleProperty() {
        return maxMeasuredValueVisible;
    }
    public void setMaxMeasuredValueVisible(boolean maxMeasuredValueVisible) { this.maxMeasuredValueVisible.set(maxMeasuredValueVisible); }

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

    private double clamp(final double MIN_VALUE, final double MAX_VALUE, final double VALUE) {
        if (VALUE < MIN_VALUE) return MIN_VALUE;
        if (VALUE > MAX_VALUE) return MAX_VALUE;
        return VALUE;
    }
    private int clamp(final int MIN_VALUE, final int MAX_VALUE, final int VALUE) {
        if (VALUE < MIN_VALUE) return MIN_VALUE;
        if (VALUE > MAX_VALUE) return MAX_VALUE;
        return VALUE;
    }


    // ******************** Style related *************************************
    @Override protected Skin createDefaultSkin() {
        return new GaugeSkin(this);
    }

    @Override public String getUserAgentStylesheet() {
        return getClass().getResource("gauge.css").toExternalForm();
    }

}
