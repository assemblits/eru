package com.eru.scene.control.skin;

import com.eru.scene.control.Switch;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * Created by mtrujillo on 09/02/2015.
 */
public class SwitchSkin extends SkinBase<Switch> {

    private static final double PREFERRED_HEIGHT = 40;
    private static final double PREFERRED_WIDTH = 40;
    private static final double MINIMUM_WIDTH = 5;
    private static final double MINIMUM_HEIGHT = 5;
    private static final double MAXIMUM_WIDTH = 1024;
    private static final double MAXIMUM_HEIGHT = 1024;
    private static final double BAR_WIDTH_SIZE_FACTOR = 0.1;
    private static final double BAR_HEIGHT_SIZE_FACTOR = 0.25;
    private static final double NAME_TEXT_SIZE_FACTOR = 0.5;
    private static double       angleInOpenPosition;
    private static final double ANGLE_IN_CLOSED_POSITION = 180;
    private final Timeline      timeline                       = new Timeline();
    private double width;
    private double height;
    private double size;
    private double centerX;
    private double centerY;
    private double barWidth;
    private double barHeight;
    private double mobileBarHeight;
    private double xDistanceToCenterBar;

    private Region  background;
    private Region  upBar;
    private Region  downBar;
    private Region  mobileBar;
    private Text    name;
    private Pane    pane;

    private Rotate barRotate;

    public SwitchSkin(){
        super(new Switch());
    }

    public SwitchSkin(Switch control) {
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

        upBar = new Region();
        upBar.getStyleClass().setAll("up-bar");

        downBar = new Region();
        downBar.getStyleClass().setAll("down-bar");

        barRotate = new Rotate(ANGLE_IN_CLOSED_POSITION);

        mobileBar = new Region();
        mobileBar.getStyleClass().setAll("mobile-bar");
        mobileBar.getTransforms().add(barRotate);

        name = new Text(getSkinnable().getName());
        name.getStyleClass().setAll("name-text");

        pane = new Pane(background, upBar, downBar, mobileBar, name);

        getChildren().add(pane);
        resize();
    }

    private void registerListeners() {
        getSkinnable().prefWidthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().prefHeightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().energizedProperty().addListener(observable -> handleControlPropertyChanged("ENERGIZED"));
        getSkinnable().closedProperty().addListener(observable ->  handleControlPropertyChanged("CLOSED"));
    }


    /* ********** Methods ********** */
    private void handleControlPropertyChanged(final String PROPERTY) {
        switch (PROPERTY) {
            case "RESIZE":
                resize();
                break;
            case "ENERGIZED":
                updateState();
                break;
            case "CLOSED":
                updateState();
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
        width  = getSkinnable().getWidth();
        height = getSkinnable().getHeight();

        if (width > 0 && height > 0) {

            centerX                 = width * 0.5;
            centerY                 = height * 0.5;
            barWidth                = width * BAR_WIDTH_SIZE_FACTOR;
            barHeight               = height * BAR_HEIGHT_SIZE_FACTOR;
            barWidth                = barWidth > barHeight ? barHeight : barWidth;
            mobileBarHeight         = height - (barHeight*2);
            xDistanceToCenterBar    = centerX - ((barWidth)/2);

            background.setPrefSize(width, height);

            upBar.setPrefSize(barWidth, barHeight);
            upBar.setTranslateX(xDistanceToCenterBar);
            upBar.setTranslateY(0);

            mobileBar.setPrefSize(barWidth, mobileBarHeight);
            mobileBar.setTranslateX(xDistanceToCenterBar + barWidth);
            mobileBar.setTranslateY(height - barHeight);
            angleInOpenPosition = 180 + Math.toDegrees(Math.atan(barWidth/mobileBarHeight)) * 3;

            downBar.setPrefSize(barWidth, barHeight);
            downBar.setTranslateX(xDistanceToCenterBar);
            downBar.setTranslateY(height - barHeight);

            name.setText(getSkinnable().getName());
            name.setFont(Font.font(size*NAME_TEXT_SIZE_FACTOR));
            name.setX((size - name.getLayoutBounds().getWidth()) * 0.5);
            name.setY((size - name.getLayoutBounds().getHeight()) * 0.5);
            name.setTextOrigin(VPos.TOP);
            updateState();
        }
    }

    private void updateState() {
        upBar.setStyle(getSkinnable().getEnergized() ? "-fx-background-color:-energized-color" : "-fx-background-color:-de-energized-color");
        downBar.setStyle(getSkinnable().getEnergized() ? "-fx-background-color:-energized-color" : "-fx-background-color:-de-energized-color");
        mobileBar.setStyle(getSkinnable().getEnergized() ? "-fx-background-color:-energized-color" : "-fx-background-color:-de-energized-color");

        if (getSkinnable().getAnimated()) {
            timeline.stop();
            final KeyValue KEY_VALUE = new KeyValue(barRotate.angleProperty(), getSkinnable().getClosed() ? ANGLE_IN_CLOSED_POSITION : angleInOpenPosition, Interpolator.EASE_BOTH);
            final KeyFrame KEY_FRAME = new KeyFrame(Duration.millis(getSkinnable().getAnimationDuration()), KEY_VALUE);
            timeline.getKeyFrames().setAll(KEY_FRAME);
            timeline.play();
        } else {
            barRotate.setAngle(getSkinnable().getClosed() ? ANGLE_IN_CLOSED_POSITION : angleInOpenPosition);
        }
    }
}
