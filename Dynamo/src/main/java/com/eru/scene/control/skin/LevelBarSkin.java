package com.eru.scene.control.skin;

import com.eru.scene.control.LevelBar;
import com.eru.util.Section;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import com.eru.fonts.Fonts;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by mtrujillo on 23/06/2014.
 */
public class LevelBarSkin extends SkinBase<LevelBar> {
    private static final double         PREFERRED_WIDTH                = 120;
    private static final double         PREFERRED_HEIGHT               = 200;
    private static final double         MINIMUM_WIDTH                  = 5;
    private static final double         MINIMUM_HEIGHT                 = 10;
    private static final double         MAXIMUM_WIDTH                  = 1024;
    private static final double         MAXIMUM_HEIGHT                 = 1024;
    private static final DecimalFormat  DEC_FORMAT                     = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
    private static final StringBuilder  decBuffer                      = new StringBuilder(16);
    private static final double         CURRENT_VALUE_FONT_SIZE_FACTOR = 0.16;
    private static final double         TITLE_FONT_SIZE_FACTOR         = 0.10;
    private static final double         UNIT_FONT_SIZE_FACTOR          = 0.07;
    private static final double         LIMITS_FONT_SIZE_FACTOR        = 0.078;
    private static final double         BAR_WIDTH_FACTOR               = 0.1;
    private final Timeline              timeline                       = new Timeline();
    private double                      width;
    private double                      height;
    private double                      barWidth;
    private double                      barHeight;

    private Canvas                      sectionsCanvas;
    private GraphicsContext             sections;
    private double                      barPosX;
    private double                      barPosY;
    private double                      offset;
    private double                      limitTextPosX;

    private Pane                        pane;
    private Region                      main;
    private Region                      frame;

    private Group                       currentValuePointerGroup;
    private Region                      currentValuePointer;
    private Text                        currentValueText;
    private Text                        titleText;
    private Text                        unitText;
    private double                      size;

    /* ********** Constructors ********** */
    public LevelBarSkin(){
        super(new LevelBar());
    }

    public LevelBarSkin(LevelBar control) {
        super(control);
        initDimension();
        initGraphics();
        registerListeners();
    }

    /* ********** Initialization ********** */
    private void initDimension() {
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
        main = new Region();
        main.getStyleClass().setAll("main");

        frame = new Region();
        frame.getStyleClass().setAll("frame");

        sectionsCanvas = new Canvas(width, barHeight);
        sections = sectionsCanvas.getGraphicsContext2D();

        currentValuePointer = new Region();
        currentValuePointer.getStyleClass().setAll("normal-current-value-pointer");
        currentValuePointer.setPrefSize(50, 20);
        currentValuePointer.setCache(true);
        currentValuePointer.setCacheHint(CacheHint.SPEED);

        currentValueText = new Text(Double.toString(getSkinnable().getCurrentValue()));
        currentValueText.getStyleClass().setAll("current-value-text");
        currentValueText.setCache(true);
        currentValueText.setCacheHint(CacheHint.SPEED);

        currentValuePointerGroup = new Group(currentValuePointer, currentValueText);
        currentValuePointerGroup.setCache(true);
        currentValuePointerGroup.setCacheHint(CacheHint.SPEED);

        titleText = new Text(getSkinnable().getTitle());
        titleText.getStyleClass().setAll("title");

        unitText = new Text(getSkinnable().getUnit());
        unitText.getStyleClass().setAll("unit");

        pane = new Pane();
        pane.getChildren().setAll(main,
                frame,
                sectionsCanvas,
                unitText,
                titleText,
                currentValuePointerGroup);

        getChildren().setAll(pane);

        resize();
    }

    private void registerListeners() {
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().currentValueProperty().addListener(observable -> handleControlPropertyChanged("CURRENT_VALUE") );
        getSkinnable().textFillProperty().addListener(observable -> handleControlPropertyChanged("TEXT_FILL"));
        getSkinnable().titleProperty().addListener(observable -> handleControlPropertyChanged("LABELS") );
        getSkinnable().unitProperty().addListener(observable -> handleControlPropertyChanged("LABELS") );
        getSkinnable().unitVisibleProperty().addListener(observable -> handleControlPropertyChanged("VISIBILITY") );
    }

    /* ********** Methods ********** */
    private void handleControlPropertyChanged(String property) {
        switch (property) {
            case "RESIZE":
                resize();
                break;
            case "TEXT_FILL":
                titleText.setFill(getSkinnable().getTextFill());
                currentValueText.setFill(getSkinnable().getTextFill());
                unitText.setFill(getSkinnable().getTextFill());
                break;
            case "CURRENT_VALUE":
                updatePointer();
                break;
            case "VISIBILITY":
                updateVisibleGraphics();
                break;
            case "LABELS":
                break;
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

    private void resize() {
        width   = getSkinnable().getWidth();
        height  = getSkinnable().getHeight();
        size    = getSkinnable().getWidth() < getSkinnable().getHeight() ? getSkinnable().getWidth() : getSkinnable().getHeight();

        if (width > 0 && height > 0) {
            main.setPrefSize(width, height);

            titleText.setFont(Font.font("Xolonium", width * TITLE_FONT_SIZE_FACTOR));
            titleText.setTextOrigin(VPos.TOP);
            titleText.setTextAlignment(TextAlignment.CENTER);
            titleText.setText(getSkinnable().getTitle());
            titleText.setX((width - titleText.getLayoutBounds().getWidth()) * 0.5);

            frame.setPrefSize(width, titleText.getLayoutBounds().getHeight());
            frame.setTranslateY(height - (height - titleText.getLayoutBounds().getHeight()));

            barWidth  = width * BAR_WIDTH_FACTOR;
            barHeight = (height - titleText.getLayoutBounds().getHeight()) * 0.95;

            unitText.setText(getSkinnable().getUnitVisible() ? getSkinnable().getUnit() : "");
            unitText.setFont(Fonts.xoloniumBold(size * UNIT_FONT_SIZE_FACTOR));
            unitText.setTextOrigin(VPos.BOTTOM);
            unitText.setTextAlignment(TextAlignment.RIGHT);
            unitText.setX(width - unitText.getLayoutBounds().getWidth());
            unitText.setY(titleText.getLayoutBounds().getHeight());

            sectionsCanvas.setWidth(width);
            sectionsCanvas.setHeight(barHeight);
            sectionsCanvas.setTranslateY(height - barHeight);
            sections.clearRect(0,0,sectionsCanvas.getWidth(),sectionsCanvas.getHeight());

            drawSections();
            updatePointer();
        }
    }

    private void drawSections(){
        final double MIN_VALUE      = getSkinnable().getMinValue();
        final double MAX_VALUE      = getSkinnable().getMaxValue();
        final double CANVAS_HEIGHT  = sectionsCanvas.getHeight();
        final double CANVAS_WIDTH   = sectionsCanvas.getWidth();
        barPosX                     = (CANVAS_WIDTH / 2) - (barWidth/2);
        barPosY                     = 0.0;
        offset                      = 0.0;
        limitTextPosX               = (barPosX + barWidth)*1.1;

        for(int i = 0; i < getSkinnable().getSections().size(); i++){
            final Section SECTION = getSkinnable().getSections().get(i);
            final double rangeValue = SECTION.getStop() - SECTION.getStart();

            if (SECTION.getStart() > MAX_VALUE || SECTION.getStop() < MIN_VALUE) continue;

            // Calculate the percent that this range represents in the whole scale with rule of three: "If the scale
            // represents 100%, how much the rangeValue represents in the scale?"
            final double percentOfScaleRangeInScale = (rangeValue / (MAX_VALUE - MIN_VALUE)) * 100.0;
            // Calculate the percent in pixel for the bar size with another rule of three: "If the size of the barVBox
            // represents 100%, how much represents the percentOfRangeValueInScale
            final double SECTION_HEIGHT = (percentOfScaleRangeInScale *CANVAS_HEIGHT)/100;

            barPosY  = offset;                               // Draw from
            offset  += SECTION_HEIGHT;

            sections.setStroke(SECTION.getColor());
            sections.setLineWidth(barWidth * 0.09);
            sections.strokeRect(barPosX, barPosY, barWidth, SECTION_HEIGHT);

            sections.beginPath();
            sections.moveTo((barPosX + barWidth), barPosY);
            sections.lineTo((barPosX + barWidth) * 1.1, barPosY);
            sections.stroke();

            if(SECTION.getStopVisible()){
                sections.setFill(SECTION.getColor());
                sections.setFont(Fonts.xoloniumBold(size * LIMITS_FONT_SIZE_FACTOR));
                sections.setTextBaseline(i == 0? VPos.TOP : VPos.CENTER);
                sections.fillText(String.valueOf(SECTION.getStop()), limitTextPosX, barPosY, CANVAS_WIDTH/2);
            }

            if(i == (getSkinnable().getSections().size()-1)){
                final double lastTextPosY = barPosY + SECTION_HEIGHT;
                sections.beginPath();
                sections.moveTo((barPosX + barWidth), lastTextPosY);
                sections.lineTo((barPosX + barWidth) * 1.1, lastTextPosY);
                sections.stroke();

                if(SECTION.getStartVisible()){
                    sections.setTextBaseline(VPos.BOTTOM);
                    sections.fillText(String.valueOf(SECTION.getStart()), limitTextPosX, lastTextPosY, CANVAS_WIDTH/2);
                }
            }
        }
    }

    private void updatePointer() {
        currentValueText.setText(formatCurrentValue(getSkinnable().getCurrentValue(), getSkinnable().getDecimals()));
        currentValueText.setFont(Font.font("Digital-7", width * CURRENT_VALUE_FONT_SIZE_FACTOR));
        currentValueText.setTextOrigin(VPos.TOP);
        currentValueText.setTextAlignment(TextAlignment.RIGHT);

        currentValuePointer.getStyleClass().clear();
        currentValuePointer.getStyleClass().setAll("normal-current-value-pointer");
        currentValuePointer.setPrefSize(currentValueText.getLayoutBounds().getWidth()*1.10, currentValueText.getLayoutBounds().getHeight()*1.10);
        currentValuePointerGroup.setTranslateX((width/2 + barWidth/2) - currentValuePointerGroup.getLayoutBounds().getWidth());

        final double newPosition =  getSkinnable().getCurrentValue() < getSkinnable().getMinValue() ? height - (currentValuePointerGroup.getLayoutBounds().getHeight() * 0.5) :
                                    getSkinnable().getCurrentValue() > getSkinnable().getMaxValue() ? height - barHeight - (currentValuePointerGroup.getLayoutBounds().getHeight() * 0.5) :
                                    height - (currentValuePointerGroup.getLayoutBounds().getHeight() * 0.5) - (barHeight * (getSkinnable().getCurrentValue()-getSkinnable().getMinValue()) / (getSkinnable().getMaxValue()-getSkinnable().getMinValue()));

        if(getSkinnable().getAnimated()){
            timeline.stop();
            final KeyValue KEY_VALUE = new KeyValue(currentValuePointerGroup.translateYProperty(), newPosition, Interpolator.EASE_BOTH);
            final KeyFrame KEY_FRAME = new KeyFrame(Duration.millis(getSkinnable().getAnimationDuration()), KEY_VALUE);
            timeline.getKeyFrames().setAll(KEY_FRAME);
            timeline.play();
        }else {
            currentValuePointerGroup.setTranslateY(newPosition);
        }
    }


    private void updateVisibleGraphics() {
        unitText.setText(getSkinnable().getUnitVisible() ? getSkinnable().getUnit() : "");
    }

    private String formatCurrentValue(final double VALUE, final int DECIMALS) {
        decBuffer.setLength(0);
        decBuffer.append("0");

        if (DECIMALS > 0) {
            decBuffer.append(".");
        }

        for (int i = 0; i < DECIMALS; i++) {
            decBuffer.append("0");
        }

        decBuffer.trimToSize();

        DEC_FORMAT.applyPattern(decBuffer.toString());

        return DEC_FORMAT.format(VALUE);
    }
}
