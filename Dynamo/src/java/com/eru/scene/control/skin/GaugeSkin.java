package com.eru.scene.control.skin;

import com.eru.util.Section;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.effect.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import com.eru.scene.control.Gauge;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Created by mtrujillo on 12/06/2014.
 */
public class GaugeSkin extends SkinBase<Gauge> {
    private static final double      PREFERRED_WIDTH                = 210;
    private static final double      PREFERRED_HEIGHT               = 210;
    private static final double      MINIMUM_WIDTH                  = 20;
    private static final double      MINIMUM_HEIGHT                 = 20;
    private static final double      MAXIMUM_WIDTH                  = 1024;
    private static final double      MAXIMUM_HEIGHT                 = 1024;
    private final Color              TICK_AND_MARK_COLOR            = Color.WHITE;
    private static final double      CURRENT_VALUE_FONT_SIZE_FACTOR = 0.15;
    private final Timeline           timeline                       = new Timeline();
    private double                   size;
    private Pane                     pane;
    private Region                   background;
    private Canvas                   ticksAndSectionsCanvas;
    private GraphicsContext          ticksAndSections;
    private Region                   threshold;
    private Rotate                   thresholdRotate;
    private boolean                  thresholdExceeded;

    /* minMeasuredValue: Little mark used to show the min value point in the tick range*/
    private Region                   minMeasuredValue;
    private Rotate                   minMeasuredValueRotate;

    /* minMeasuredValue: Little mark used to show the max value point in the tick range*/
    private Region                   maxMeasuredValue;
    private Rotate                   maxMeasuredValueRotate;

    private Region                   needle;
    private Region                   needleHighlight;
    private Rotate                   needleRotate;
    private Region                   knob;
    private Group needleGroup;
    private DropShadow               dropShadow;
    private Text                     title;
    private Text                     unit;
    private Text                     value;
    private Font                     currentValueFont;
    private DropShadow               valueBlendBottomShadow;
    private InnerShadow              valueBlendTopShadow;
    private Blend                    valueBlend;
    private Path                     histogram;
    /* angleStep : Steps that needle has to do to get all numeric range, this is a dependency to integer values.
    Example: Numeric Range : Min=0, Max=3000
             Radial Range  : 180°
             Steps = 180/3000 = 0.06
                Example of movement: Move to half (1500) is : 0.06*1500 = 90°
    */
    private double                   angleStep;

    // ******************** Constructors **************************************
    public GaugeSkin(){
        super(new Gauge());
    }

    public GaugeSkin(Gauge gauge) {
        super(gauge);
        init();
        initGraphics();
        registerListeners();
    }

    // ******************** Initialization ************************************
    private void init() {
        if (Double.compare(getSkinnable().getPrefWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getPrefHeight(), 0.0) <= 0 ||
                Double.compare(getSkinnable().getWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getHeight(), 0.0) <= 0) {
            if (getSkinnable().getPrefWidth() > 0 && getSkinnable().getPrefHeight() > 0) {
                getSkinnable().setPrefSize(getSkinnable().getPrefWidth(), getSkinnable().getPrefHeight());
            } else {
                getSkinnable().setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT);
            }
        }

        if (Double.compare(getSkinnable().getMinWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMinHeight(), 0.0) <= 0) {
            getSkinnable().setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT);
        }

        if (Double.compare(getSkinnable().getMaxWidth(), 0.0) <= 0 || Double.compare(getSkinnable().getMaxHeight(), 0.0) <= 0) {
            getSkinnable().setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT);
        }
    }

    private void initGraphics() {
        valueBlendBottomShadow = new DropShadow();
        valueBlendBottomShadow.setBlurType(BlurType.TWO_PASS_BOX);
        valueBlendBottomShadow.setColor(Color.rgb(255, 255, 255, 0.5));
        valueBlendBottomShadow.setOffsetX(0);
        valueBlendBottomShadow.setOffsetY(0.005 * PREFERRED_WIDTH);
        valueBlendBottomShadow.setRadius(0);

        valueBlendTopShadow = new InnerShadow();
        valueBlendTopShadow.setBlurType(BlurType.TWO_PASS_BOX);
        valueBlendTopShadow.setColor(Color.rgb(0, 0, 0, 0.7));
        valueBlendTopShadow.setOffsetX(0);
        valueBlendTopShadow.setOffsetY(0.005 * PREFERRED_WIDTH);
        valueBlendTopShadow.setRadius(0.005 * PREFERRED_WIDTH);

        valueBlend = new Blend();
        valueBlend.setMode(BlendMode.MULTIPLY);
        valueBlend.setBottomInput(valueBlendBottomShadow);
        valueBlend.setTopInput(valueBlendTopShadow);

        background = new Region();
        background.getStyleClass().setAll("background");

        ticksAndSectionsCanvas = new Canvas(PREFERRED_WIDTH, PREFERRED_HEIGHT);
        ticksAndSections = ticksAndSectionsCanvas.getGraphicsContext2D();

        histogram = new Path();
        histogram.setFillRule(FillRule.NON_ZERO);
        histogram.getStyleClass().add("histogram-fill");

        angleStep = getSkinnable().getAngleRange() / (getSkinnable().getMaxValue() - getSkinnable().getMinValue());

        minMeasuredValue = new Region();
        minMeasuredValue.getStyleClass().setAll("min-measured-value");
        minMeasuredValueRotate = new Rotate(180 - getSkinnable().getStartAngle());
        minMeasuredValue.getTransforms().setAll(minMeasuredValueRotate);
        minMeasuredValue.setOpacity(getSkinnable().getMinMeasuredValueVisible() ? 1 : 0);
        minMeasuredValue.setManaged(getSkinnable().getMinMeasuredValueVisible());

        maxMeasuredValue = new Region();
        maxMeasuredValue.getStyleClass().setAll("max-measured-value");
        maxMeasuredValueRotate = new Rotate(180 - getSkinnable().getStartAngle());
        maxMeasuredValue.getTransforms().setAll(maxMeasuredValueRotate);
        maxMeasuredValue.setOpacity(getSkinnable().getMaxMeasuredValueVisible() ? 1 : 0);
        maxMeasuredValue.setManaged(getSkinnable().getMaxMeasuredValueVisible());

        threshold = new Region();
        threshold.getStyleClass().setAll("threshold");
        thresholdRotate = new Rotate(180 - getSkinnable().getStartAngle());
        threshold.getTransforms().setAll(thresholdRotate);
        threshold.setOpacity(getSkinnable().getThresholdVisible() ? 1 : 0);
        threshold.setManaged(getSkinnable().getThresholdVisible());
        thresholdExceeded = false;

        needleRotate = new Rotate((180 - getSkinnable().getStartAngle()) + (getSkinnable().getCurrentValue() * angleStep));

        needle = new Region();
        needle.getStyleClass().setAll("needle-standard");
        needle.getTransforms().add(needleRotate);
        needle.setCache(true);
        needle.setCacheHint(CacheHint.SPEED);

        needleHighlight = new Region();
        needleHighlight.getStyleClass().setAll("needle-highlight");
        needleHighlight.getTransforms().addAll(needleRotate);
        needleHighlight.setCache(true);
        needleHighlight.setCacheHint(CacheHint.SPEED);

        knob = new Region();
        knob.setPickOnBounds(false);
        knob.getStyleClass().setAll("knob");

        dropShadow = new DropShadow();
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.25));
        dropShadow.setBlurType(BlurType.TWO_PASS_BOX);
        dropShadow.setRadius(0.015 * PREFERRED_WIDTH);
        dropShadow.setOffsetY(0.015 * PREFERRED_WIDTH);

        needleGroup = new Group(needle, needleHighlight);
        needleGroup.setEffect(getSkinnable().getDropShadowEnabled() ? dropShadow : null);
        needleGroup.setCache(true);
        needleGroup.setCacheHint(CacheHint.SPEED);

        title = new Text(getSkinnable().getTitle());
        title.setTextOrigin(VPos.CENTER);
        title.getStyleClass().setAll("title");

        unit = new Text(getSkinnable().getUnit());
        unit.setMouseTransparent(true);
        unit.setTextOrigin(VPos.CENTER);
        unit.getStyleClass().setAll("unit");

        value = new Text(String.format(Locale.US, "%." + getSkinnable().getDecimals() + "f", getSkinnable().getCurrentValue()));
        value.setMouseTransparent(true);
        value.setTextOrigin(VPos.CENTER);
        value.getStyleClass().setAll("value");
        value.setEffect(getSkinnable().getPlainValue() ? null : valueBlend);

        // Add all nodes
        pane = new Pane();
        pane.getChildren().setAll(background,
                histogram,
                ticksAndSectionsCanvas,
                minMeasuredValue,
                maxMeasuredValue,
                threshold,
                title,
                needleGroup,
                knob,
                unit,
                value);

        getChildren().setAll(pane);
    }

    private void registerListeners() {
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().currentValueProperty().addListener(observable -> handleControlPropertyChanged("VALUE"));
        getSkinnable().minValueProperty().addListener(observable -> handleControlPropertyChanged("RECALC"));
        getSkinnable().maxValueProperty().addListener(observable -> handleControlPropertyChanged("RECALC"));
        getSkinnable().minMeasuredValueProperty().addListener(observable -> handleControlPropertyChanged("MIN_MEASURED_VALUE"));
        getSkinnable().minMeasuredValueVisibleProperty().addListener(observable -> handleControlPropertyChanged("MIN_MEASURED_VALUE_VISIBLE"));
        getSkinnable().maxMeasuredValueProperty().addListener(observable -> handleControlPropertyChanged("MAX_MEASURED_VALUE"));
        getSkinnable().maxMeasuredValueVisibleProperty().addListener(observable -> handleControlPropertyChanged("MAX_MEASURED_VALUE_VISIBLE"));
        getSkinnable().tickLabelOrientationProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().thresholdProperty().addListener(observable -> handleControlPropertyChanged("THRESHOLD"));
        getSkinnable().thresholdVisibleProperty().addListener(observable -> handleControlPropertyChanged("THRESHOLD_VISIBLE"));
        getSkinnable().angleRangeProperty().addListener(observable -> handleControlPropertyChanged("ANGLE_RANGE"));
        getSkinnable().plainValueProperty().addListener(observable -> handleControlPropertyChanged("PLAIN_VALUE"));
        getSkinnable().histogramEnabledProperty().addListener(observable -> handleControlPropertyChanged("HISTOGRAM"));
        getSkinnable().dropShadowEnabledProperty().addListener(observable -> handleControlPropertyChanged("DROP_SHADOW"));
        getSkinnable().getSections().addListener((ListChangeListener<Section>) change -> handleControlPropertyChanged("CANVAS_REFRESH"));
        getSkinnable().customTickLabelsEnabledProperty().addListener(observable1 -> handleControlPropertyChanged("CANVAS_REFRESH"));
        getSkinnable().valueFontProperty().addListener(observble -> handleControlPropertyChanged("VALUE_FONT"));
        needleRotate.angleProperty().addListener(observable -> handleControlPropertyChanged("ANGLE"));
    }

    // ******************** Methods *******************************************
    protected void handleControlPropertyChanged(final String PROPERTY) {
        if ("RESIZE".equals(PROPERTY)) {
            resize();
        } else if ("VALUE".equals(PROPERTY)) {
            rotateNeedle();
        } else if ("RECALC".equals(PROPERTY)) {
            if (getSkinnable().getMinValue() < 0) {
                angleStep = getSkinnable().getAngleRange() / (getSkinnable().getMaxValue() - getSkinnable().getMinValue());
                needleRotate.setAngle(180 - getSkinnable().getStartAngle() - (getSkinnable().getMinValue()) * angleStep);
            } else {
                angleStep = getSkinnable().getAngleRange() / (getSkinnable().getMaxValue() + getSkinnable().getMinValue());
                needleRotate.setAngle(180 - getSkinnable().getStartAngle() * angleStep);
            }
            resize();
        } else if ("ANGLE".equals(PROPERTY)) {
            final double currentValue = (needleRotate.getAngle() + getSkinnable().getStartAngle() - 180) / angleStep + getSkinnable().getMinValue();
            value.setText(String.format(Locale.US, "%." + getSkinnable().getDecimals() + "f", currentValue));
            value.setTranslateX((size - value.getLayoutBounds().getWidth()) * 0.5);

            // Check threshold
            if (thresholdExceeded) {
                if (currentValue < getSkinnable().getThreshold()) {
                    thresholdExceeded = false;
                }
            } else {
                if (currentValue > getSkinnable().getThreshold()) {
                    thresholdExceeded = true;
                }
            }

            // Check min- and maxMeasuredValue
            if (currentValue < getSkinnable().getMinMeasuredValue()) {
                getSkinnable().setMinMeasuredValue(currentValue);
                minMeasuredValueRotate.setAngle(currentValue * angleStep - 180 - getSkinnable().getStartAngle() - getSkinnable().getMinValue() * angleStep);
            }
            if (currentValue > getSkinnable().getMaxMeasuredValue()) {
                getSkinnable().setMaxMeasuredValue(currentValue);
                maxMeasuredValueRotate.setAngle(currentValue * angleStep - 180 - getSkinnable().getStartAngle() - getSkinnable().getMinValue() * angleStep);
            }
        } else if ("PLAIN_VALUE".equals(PROPERTY)) {
            value.setEffect(getSkinnable().getPlainValue() ? null : valueBlend);
        } else if ("HISTOGRAM".equals(PROPERTY)) {
            histogram.setVisible(getSkinnable().getHistogramEnabled());
            histogram.setManaged(getSkinnable().getHistogramEnabled());
        } else if ("DROP_SHADOW".equals(PROPERTY)) {
            needleGroup.setEffect(getSkinnable().getDropShadowEnabled() ? dropShadow : null);
        } else if ("CANVAS_REFRESH".equals(PROPERTY)) {
            ticksAndSections.clearRect(0, 0, size, size);
            drawSections(ticksAndSections);
            drawTickMarks(ticksAndSections);
        } else if ("THRESHOLD".equals(PROPERTY)) {
            thresholdRotate.setAngle(getSkinnable().getThreshold() * angleStep - 180 - getSkinnable().getStartAngle());
        } else if ("THRESHOLD_VISIBLE".equals(PROPERTY)) {
            threshold.setOpacity(getSkinnable().getThresholdVisible() ? 1 : 0);
            threshold.setManaged(getSkinnable().getThresholdVisible());
        } else if ("MIN_MEASURED_VALUE_VISIBLE".equals(PROPERTY)) {
            minMeasuredValue.setOpacity(getSkinnable().getMinMeasuredValueVisible() ? 1 : 0);
            minMeasuredValue.setManaged(getSkinnable().getMinMeasuredValueVisible());
        } else if ("MAX_MEASURED_VALUE_VISIBLE".equals(PROPERTY)) {
            maxMeasuredValue.setOpacity(getSkinnable().getMaxMeasuredValueVisible() ? 1 : 0);
            maxMeasuredValue.setManaged(getSkinnable().getMaxMeasuredValueVisible());
        } else if ("VALUE_FONT".equals(PROPERTY)){
            updateFonts();
        }
    }

    @Override protected double computeMinWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMinWidth(Math.max(MINIMUM_HEIGHT, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }
    @Override protected double computeMinHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMinHeight(Math.max(MINIMUM_WIDTH, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override protected double computeMaxWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMaxWidth(Math.min(MAXIMUM_HEIGHT, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }
    @Override protected double computeMaxHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMaxHeight(Math.min(MAXIMUM_WIDTH, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override protected double computePrefWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        double prefHeight = PREFERRED_HEIGHT;
        if (HEIGHT != -1) {
            prefHeight = Math.max(0, HEIGHT - TOP_INSET - BOTTOM_INSET);
        }
        return super.computePrefWidth(prefHeight, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }
    @Override protected double computePrefHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        double prefWidth = PREFERRED_WIDTH;
        if (WIDTH != -1) {
            prefWidth = Math.max(0, WIDTH - LEFT_INSET - RIGHT_INSET);
        }
        return super.computePrefHeight(prefWidth, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }


    // ******************** Private Methods ***********************************
    private void rotateNeedle() {
        // Update the angleStep
        angleStep = getSkinnable().getAngleRange() / (getSkinnable().getMaxValue() - getSkinnable().getMinValue());

        // Go to origin and to value after
        final double TARGET_ANGLE = (180 - getSkinnable().getStartAngle()) + (getSkinnable().getCurrentValue() * angleStep) - (getSkinnable().getMinValue()*angleStep);

        if (getSkinnable().getAnimated()) {
            timeline.stop();
            final KeyValue KEY_VALUE = new KeyValue(needleRotate.angleProperty(), TARGET_ANGLE, Interpolator.EASE_BOTH);
            final KeyFrame KEY_FRAME = new KeyFrame(Duration.millis(getSkinnable().getAnimationDuration()), KEY_VALUE);
            timeline.getKeyFrames().setAll(KEY_FRAME);
            timeline.play();
        } else {
            needleRotate.setAngle(TARGET_ANGLE);
        }
    }

    private void drawTickMarks(final GraphicsContext CTX) {
        if (getSkinnable().getHistogramEnabled()) {
            double xy;
            double wh;
            double step         = 0;
            double OFFSET       = 90 - getSkinnable().getStartAngle();
            double ANGLE_EXTEND = (getSkinnable().getMaxValue()) * angleStep;
            CTX.setStroke(Color.rgb(200, 200, 200));
            CTX.setLineWidth(size * 0.001);
            CTX.setLineCap(StrokeLineCap.BUTT);
            for (int i = 0 ; i < 5 ; i++) {
                xy = (size - (0.435 + step) * size) / 2;
                wh = size * (0.435 + step);
                CTX.strokeArc(xy, xy, wh, wh, -OFFSET, -ANGLE_EXTEND, ArcType.OPEN);
                step += 0.075;
            }
        }

        double  sinValue;
        double  cosValue;
        double  startAngle = getSkinnable().getStartAngle();
        double  orthText   = Gauge.TickLabelOrientation.ORTHOGONAL == getSkinnable().getTickLabelOrientation() ? 0.33 : 0.31;

        Point2D center     = new Point2D(size * 0.5, size * 0.5);

        // ***** NOTE ***** //
        // This for loop was modified to use Big Decimals instead Primitives, the counter is the angle barrier and was added a method to round the floating points.

        for (double angle = 0.0, counter = getSkinnable().getMinValue() ; Double.compare(counter, getSkinnable().getMaxValue()) <= 0.0 ; angle -= angleStep * getSkinnable().getMinorTickSpace(), counter = round(getSkinnable().getMinorTickSpace() + counter, 1) ){
            sinValue = Math.sin(Math.toRadians(angle + startAngle));
            cosValue = Math.cos(Math.toRadians(angle + startAngle));

            Point2D innerMainPoint   = new Point2D(center.getX() + size * 0.368 * sinValue, center.getY() + size * 0.368 * cosValue);
            Point2D innerMediumPoint = new Point2D(center.getX() + size * 0.388 * sinValue, center.getY() + size * 0.388 * cosValue);
            Point2D innerMinorPoint  = new Point2D(center.getX() + size * 0.3975 * sinValue, center.getY() + size * 0.3975 * cosValue);
            Point2D outerPoint       = new Point2D(center.getX() + size * 0.432 * sinValue, center.getY() + size * 0.432 * cosValue);
            Point2D textPoint        = new Point2D(center.getX() + size * orthText * sinValue, center.getY() + size * orthText * cosValue);

            CTX.setStroke(TICK_AND_MARK_COLOR);

            if ( (BigDecimal.valueOf(counter).remainder(BigDecimal.valueOf(getSkinnable().getMajorTickSpace())).equals(BigDecimal.valueOf(0.0))) ) {    // counter % MajorTickSpace == 0
                // Draw major tickmark
                CTX.setLineWidth(size * 0.0055);
                CTX.strokeLine(innerMainPoint.getX(), innerMainPoint.getY(), outerPoint.getX(), outerPoint.getY());

                // Draw text
                CTX.save();
                CTX.translate(textPoint.getX(), textPoint.getY());
                switch(getSkinnable().getTickLabelOrientation()) {
                    case ORTHOGONAL:
                        if ((360 - startAngle - angle) % 360 > 90 && (360 - startAngle - angle) % 360 < 270) {
                            CTX.rotate((180 - startAngle - angle) % 360);
                        } else {
                            CTX.rotate((360 - startAngle - angle) % 360);
                        }
                        break;
                    case TANGENT:
                        if ((360 - startAngle - angle - 90) % 360 > 90 && (360 - startAngle - angle - 90) % 360 < 270) {
                            CTX.rotate((90 - startAngle - angle) % 360);
                        } else {
                            CTX.rotate((270 - startAngle - angle) % 360);
                        }
                        break;
                    case HORIZONTAL:
                    default:
                        break;
                }
                CTX.setFont(Font.font("Verdana", FontWeight.NORMAL, 0.045 * size));
                CTX.setTextAlign(TextAlignment.CENTER);
                CTX.setTextBaseline(VPos.CENTER);
                CTX.setFill(TICK_AND_MARK_COLOR);

                if(getSkinnable().getTicksRoundedToInteger()){
                    CTX.fillText(Integer.toString((int)counter), 0, 0);
                } else {
                    CTX.fillText(Double.toString(counter), 0, 0);
                }

                CTX.restore();
            } else if (!BigDecimal.valueOf(getSkinnable().getMinorTickSpace()).remainder(BigDecimal.valueOf(2.0)).equals(BigDecimal.valueOf(0.0))   &&      // MinorTickSpace % 2.0 != 0 && counter % 5.0 == 0.0
                    BigDecimal.valueOf(counter).remainder(BigDecimal.valueOf(5.0)).equals(BigDecimal.valueOf(0.0))) {
                CTX.setLineWidth(size * 0.0035);
                CTX.strokeLine(innerMediumPoint.getX(), innerMediumPoint.getY(), outerPoint.getX(), outerPoint.getY());
            } else if (BigDecimal.valueOf(counter).remainder(BigDecimal.valueOf(getSkinnable().getMinorTickSpace())).equals(BigDecimal.valueOf(0.0))) {     // counter % MinorTickSpace == 0
                CTX.setLineWidth(size * 0.00225);
                CTX.strokeLine(innerMinorPoint.getX(), innerMinorPoint.getY(), outerPoint.getX(), outerPoint.getY());
            }
        }
    }

    private void drawSections(final GraphicsContext CTX) {
        final double xy        = (size - 0.83 * size) / 2;
        final double wh        = size * 0.83;
        final double MIN_VALUE = getSkinnable().getMinValue();
        final double MAX_VALUE = getSkinnable().getMaxValue();
        final double OFFSET = 90 - getSkinnable().getStartAngle();
        for (int i = 0 ; i < getSkinnable().getSections().size() ; i++) {
            final Section SECTION = getSkinnable().getSections().get(i);

            final double SECTION_START_ANGLE;
            if (SECTION.getStart() > MAX_VALUE || SECTION.getStop() < MIN_VALUE) continue;

            if (SECTION.getStart() < MIN_VALUE && SECTION.getStop() < MAX_VALUE) {
                SECTION_START_ANGLE = MIN_VALUE * angleStep;
            } else {
                SECTION_START_ANGLE = (SECTION.getStart() - MIN_VALUE) * angleStep;
            }
            final double SECTION_ANGLE_EXTEND;
            if (SECTION.getStop() > MAX_VALUE) {
                SECTION_ANGLE_EXTEND = MAX_VALUE * angleStep;
            } else {
                SECTION_ANGLE_EXTEND = (SECTION.getStop() - SECTION.getStart()) * angleStep;
            }

            CTX.save();
            CTX.setStroke(SECTION.getColor());
            CTX.setLineWidth(size * 0.037);
            CTX.setLineCap(StrokeLineCap.BUTT);
            CTX.strokeArc(xy, xy, wh, wh, -(OFFSET + SECTION_START_ANGLE), -SECTION_ANGLE_EXTEND, ArcType.OPEN);
            CTX.restore();
        }
    }

    private void resize() {
        size = getSkinnable().getWidth() < getSkinnable().getHeight() ? getSkinnable().getWidth() : getSkinnable().getHeight();

        valueBlendBottomShadow.setOffsetY(0.005 * size);

        valueBlendTopShadow.setOffsetY(0.005 * size);
        valueBlendTopShadow.setRadius(0.005 * size);

        dropShadow.setRadius(0.015 * size);
        dropShadow.setOffsetY(0.015 * size);

        background.setPrefSize(size, size);

        ticksAndSectionsCanvas.setWidth(size);
        ticksAndSectionsCanvas.setHeight(size);
        ticksAndSections.clearRect(0, 0, size, size);
        drawSections(ticksAndSections);
        drawTickMarks(ticksAndSections);
        ticksAndSectionsCanvas.setCache(true);
        ticksAndSectionsCanvas.setCacheHint(CacheHint.QUALITY);

        minMeasuredValue.setPrefSize(0.03 * size, 0.03 * size);
        minMeasuredValue.relocate((size - minMeasuredValue.getPrefWidth()) * 0.5, size * 0.11);
        minMeasuredValueRotate.setPivotX(minMeasuredValue.getPrefWidth() * 0.5);
        minMeasuredValueRotate.setPivotY(size * 0.39);
        minMeasuredValueRotate.setAngle(getSkinnable().getMinMeasuredValue() * angleStep - 180 - getSkinnable().getStartAngle() - getSkinnable().getMinValue() * angleStep);

        maxMeasuredValue.setPrefSize(0.03 * size, 0.03 * size);
        maxMeasuredValue.relocate((size - maxMeasuredValue.getPrefWidth()) * 0.5, size * 0.11);
        maxMeasuredValueRotate.setPivotX(maxMeasuredValue.getPrefWidth() * 0.5);
        maxMeasuredValueRotate.setPivotY(size * 0.39);
        maxMeasuredValueRotate.setAngle(getSkinnable().getMaxMeasuredValue() * angleStep - 180 - getSkinnable().getStartAngle() - getSkinnable().getMinValue() * angleStep);

        threshold.setPrefSize(0.03 * size, 0.0275 * size);
        threshold.relocate((size - threshold.getPrefWidth()) * 0.5, size * 0.11);
        thresholdRotate.setPivotX(threshold.getPrefWidth() * 0.5);
        thresholdRotate.setPivotY(size * 0.39);
        thresholdRotate.setAngle(getSkinnable().getThreshold() * angleStep - 180 - getSkinnable().getStartAngle() - getSkinnable().getMinValue() * angleStep);

        value.setText(String.format(Locale.US, "%." + getSkinnable().getDecimals() + "f", (needleRotate.getAngle() + getSkinnable().getStartAngle() - 180) / angleStep));

        needle.getStyleClass().clear();
        needle.getStyleClass().setAll("needle-standard");
        needle.setPrefSize(size * 0.04, size * 0.425);
        needle.relocate((size - needle.getPrefWidth()) * 0.5, size * 0.5 - needle.getPrefHeight());
        needleRotate.setPivotX(needle.getPrefWidth() * 0.5);
        needleRotate.setPivotY(needle.getPrefHeight());

        needleHighlight.getStyleClass().clear();
        needleHighlight.getStyleClass().setAll("needle-highlight");
        needleHighlight.setPrefSize(size * 0.04, size * 0.425);
        needleHighlight.setTranslateX((size - needle.getPrefWidth()) * 0.5);
        needleHighlight.setTranslateY(size * 0.5 - needle.getPrefHeight());

        knob.getStyleClass().clear();
        knob.getStyleClass().setAll("knob");
        knob.setPrefSize(size * 0.4, size * 0.4);
        knob.setTranslateX((size - knob.getPrefWidth()) * 0.5);
        knob.setTranslateY((size - knob.getPrefHeight()) * 0.5);

        updateFonts();
    }

    private void updateFonts() {
        switch(getSkinnable().getValueFont()) {
            case LCD:
                currentValueFont = Font.font(Gauge.DisplayFont.LCD.getFontname(), (size * CURRENT_VALUE_FONT_SIZE_FACTOR));
                break;
            case DIGITAL:
                currentValueFont = Font.font(Gauge.DisplayFont.DIGITAL.getFontname(), (size * CURRENT_VALUE_FONT_SIZE_FACTOR));
                break;
            case DIGITAL_BOLD:
                currentValueFont = Font.font(Gauge.DisplayFont.DIGITAL_BOLD.getFontname(), (size * CURRENT_VALUE_FONT_SIZE_FACTOR));
                break;
            case ELEKTRA:
                currentValueFont = Font.font(Gauge.DisplayFont.ELEKTRA.getFontname(), (size * CURRENT_VALUE_FONT_SIZE_FACTOR));
                break;
            case STANDARD:
            default:
                currentValueFont = Font.font(Gauge.DisplayFont.STANDARD.getFontname(), FontWeight.NORMAL, ( size * CURRENT_VALUE_FONT_SIZE_FACTOR));
                break;
        }

        value.setFont(currentValueFont);
        value.setTranslateX((size - value.getLayoutBounds().getWidth()) * 0.5);
        value.setTranslateY(size * 0.5);

        title.setFont(Font.font("Xolonium", FontWeight.NORMAL, size * 0.05  ));
        title.setTranslateX((size - title.getLayoutBounds().getWidth()) * 0.5);
        title.setTranslateY(size * 0.85);

        unit.setFont(Font.font("Open Sans", FontWeight.NORMAL, size * 0.05));
        unit.setTranslateX((size - unit.getLayoutBounds().getWidth()) * 0.5);
        unit.setTranslateY(size * 0.6);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
