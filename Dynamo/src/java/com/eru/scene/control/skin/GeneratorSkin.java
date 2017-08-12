package com.eru.scene.control.skin;

import javafx.animation.*;
import javafx.geometry.VPos;
import javafx.scene.CacheHint;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import com.eru.scene.control.Generator;

/**
 * Created by mtrujillo on 09/02/2015.
 */
public class GeneratorSkin extends SkinBase<Generator> {

    private static final double PREFERRED_HEIGHT = 40;
    private static final double PREFERRED_WIDTH = 40;
    private static final double MINIMUM_WIDTH = 5;
    private static final double MINIMUM_HEIGHT = 5;
    private static final double MAXIMUM_WIDTH = 1024;
    private static final double MAXIMUM_HEIGHT = 1024;
    private double width;
    private double height;
    private double size;
    private double centerX;
    private double centerY;

    private static final float CIRCLE_FRAME_SIZE_FACTOR = 0.92f;
    private static final float SIN_CURVE_SIZE_FACTOR    = 0.2f;
    private static final float NAME_TEXT_SIZE_FACTOR    = 0.4f;

    private Region  background;
    private Region  circleFrame;
    private Region  circleGears;
    private Region  sinCurve;
    private Text    nameText;
    private Pane    pane;

    private RotateTransition    rotateTransition;
    private Timeline            blinkTimeline;
    private boolean             isFrameVisible;

    public GeneratorSkin(){
        super(new Generator());
    }

    public GeneratorSkin(Generator control) {
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
        background = new Region();
        background.getStyleClass().setAll("background");

        circleFrame = new Region();
        circleFrame.getStyleClass().setAll("circle-frame");

        circleGears = new Region();
        circleGears.getStyleClass().setAll("circle-gears");
        circleGears.setCache(true);
        circleGears.setCacheHint(CacheHint.SPEED);

        rotateTransition = new RotateTransition(Duration.millis(getSkinnable().getAnimationDuration()), circleGears);
        blinkTimeline = new Timeline(new KeyFrame(Duration.seconds(0.35), event -> circleFrame.setOpacity(circleFrame.getOpacity() == 0 ? 1 : 0)));

        sinCurve = new Region();
        sinCurve.getStyleClass().setAll("sin-curve");

        nameText = new Text(getSkinnable().getName());
        nameText.getStyleClass().setAll("name-text");

        pane = new Pane(background, circleFrame, circleGears, sinCurve, nameText);

        getChildren().add(pane);
        resize();
        updateStateAspect();
        updateAlarmStatus();
    }

    private void registerListeners() {
        getSkinnable().prefWidthProperty().addListener(observable -> handleControlPropertyChanged("PREF_SIZE"));
        getSkinnable().prefHeightProperty().addListener(observable -> handleControlPropertyChanged("PREF_SIZE"));
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().stateProperty().addListener(observable -> handleControlPropertyChanged("STATE"));
        getSkinnable().alarmedProperty().addListener(observable -> handleControlPropertyChanged("ALARMED"));
    }


    /* ********** Methods ********** */
    private void handleControlPropertyChanged(final String PROPERTY) {
        switch (PROPERTY) {
            case "RESIZE":
                resize();
                break;
            case "STATE":
                updateStateAspect();
                break;
            case "COLOR":
                break;
            case "ALARMED":
                updateAlarmStatus();
                break;
        }
    }

    @Override
    protected double computeMinWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMinWidth(Math.max(MINIMUM_HEIGHT, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computeMinHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMinHeight(Math.max(MINIMUM_WIDTH, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computeMaxWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMaxWidth(Math.min(MAXIMUM_HEIGHT, HEIGHT - TOP_INSET - BOTTOM_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computeMaxHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        return super.computeMaxHeight(Math.min(MAXIMUM_WIDTH, WIDTH - LEFT_INSET - RIGHT_INSET), TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computePrefWidth(final double HEIGHT, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        double prefHeight = PREFERRED_HEIGHT;
        if (HEIGHT != -1) {
            prefHeight = Math.max(0, HEIGHT - TOP_INSET - BOTTOM_INSET);
        }
        return super.computePrefWidth(prefHeight, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }

    @Override
    protected double computePrefHeight(final double WIDTH, double TOP_INSET, double RIGHT_INSET, double BOTTOM_INSET, double LEFT_INSET) {
        double prefWidth = PREFERRED_WIDTH;
        if (WIDTH != -1) {
            prefWidth = Math.max(0, WIDTH - LEFT_INSET - RIGHT_INSET);
        }
        return super.computePrefHeight(prefWidth, TOP_INSET, RIGHT_INSET, BOTTOM_INSET, LEFT_INSET);
    }


    private void resize() {
        width = getSkinnable().getWidth();
        height = getSkinnable().getHeight();

        if (width > 0 && height > 0) {
            size    = getSkinnable().getWidth() < getSkinnable().getHeight() ? getSkinnable().getWidth() : getSkinnable().getHeight();
            centerX = size * 0.5;
            centerY = size * 0.5;

            background.setPrefSize(size, size);

            circleFrame.setPrefSize(size * CIRCLE_FRAME_SIZE_FACTOR, size * CIRCLE_FRAME_SIZE_FACTOR);
            circleFrame.setLayoutX(centerX - (circleFrame.getPrefWidth() * 0.5));
            circleFrame.setLayoutY(centerY - (circleFrame.getPrefHeight() * 0.5));

            circleGears.getStyleClass().clear();
            circleGears.getStyleClass().setAll("circle-gears");
            circleGears.setPrefSize(size, size);

            sinCurve.setPrefSize(size * SIN_CURVE_SIZE_FACTOR, size * SIN_CURVE_SIZE_FACTOR * 0.8);
            sinCurve.setLayoutX(centerX - (sinCurve.getPrefWidth() * 0.5));
            sinCurve.setLayoutY(size * 0.7);

            nameText.setText(getSkinnable().getName());
            nameText.setFont(Font.font(size*NAME_TEXT_SIZE_FACTOR));
            nameText.setX((size - nameText.getLayoutBounds().getWidth()) * 0.5);
            nameText.setY((size - nameText.getLayoutBounds().getHeight()) * 0.5);
            nameText.setTextOrigin(VPos.TOP);
        }
    }

    private void updateStateAspect(){
        switch (getSkinnable().getState()) {
            case OFF:
                circleFrame.setStyle("-fx-background-color:-de-energized-color");
                circleGears.setStyle("-fx-background-color:TRANSPARENT");
                rotateTransition.stop();
                blinkTimeline.stop();
                break;
            case PREGLOW:
                circleFrame.setStyle("-fx-background-color:-starting-color");
                circleGears.setStyle("-fx-background-color:-starting-color");
                rotateTransition.stop();
                blinkTimeline.stop();
                break;
            case CRANK:
                circleFrame.setStyle("-fx-background-color:-starting-color");
                circleGears.setStyle("-fx-background-color:-starting-color");
                if(getSkinnable().getAnimated()){
                    rotateTransition.stop();
                    rotateTransition.setDuration(Duration.millis(getSkinnable().getAnimationDuration()));
                    rotateTransition.setByAngle(360);
                    rotateTransition.setInterpolator(Interpolator.LINEAR);
                    rotateTransition.setCycleCount(Timeline.INDEFINITE);
                    rotateTransition.play();
                }
                blinkTimeline.stop();
                break;
            case RUN:
                circleFrame.setStyle("-fx-background-color:-energized-color");
                circleGears.setStyle("-fx-background-color:TRANSPARENT");
                rotateTransition.stop();
                blinkTimeline.stop();
                break;
            case COOLDOWN:
                circleFrame.setStyle("-fx-background-color:-starting-color");
                circleGears.setStyle("-fx-background-color:-starting-color");
                rotateTransition.stop();
                blinkTimeline.stop();
                break;
            case RESTART:
                break;
            case FAILED:
                circleFrame.setStyle("-fx-background-color:-failed-color");
                circleGears.setStyle("-fx-background-color:TRANSPARENT");
                rotateTransition.stop();
                blinkTimeline.setCycleCount(Timeline.INDEFINITE);
                blinkTimeline.play();
                break;
            case UNKNOWN:
                rotateTransition.stop();
                blinkTimeline.stop();
                circleFrame.setStyle("-fx-background-color:-unknown-color");
                circleGears.setStyle("-fx-background-color:TRANSPARENT");
                circleFrame.setOpacity(1.0);
                break;
        }
    }

    private void updateAlarmStatus() {
        if (getSkinnable().getAlarmed()){
            sinCurve.setStyle("-fx-background-color: -failed-color");
            nameText.setStyle("-fx-fill: -failed-color");
        } else {
            sinCurve.setStyle("-fx-background-color: -unknown-color");
            nameText.setStyle("-fx-fill: -name-text-fill");
        }
    }
}
