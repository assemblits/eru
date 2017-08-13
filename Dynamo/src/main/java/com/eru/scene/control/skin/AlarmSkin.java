package com.eru.scene.control.skin;

import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import com.eru.scene.control.Alarm;

/**
 * Created by mtrujillo on 07/07/2014.
 */
public class AlarmSkin extends SkinBase<Alarm> {
    /* ********** Fields ********** */
    private static final double PREFERRED_HEIGHT = 24;
    private static final double PREFERRED_WIDTH  = 125;
    private static final double MINIMUM_WIDTH    = 5;
    private static final double MINIMUM_HEIGHT   = 5;
    private static final double MAXIMUM_WIDTH    = 1024;
    private static final double MAXIMUM_HEIGHT   = 1024;
    private static final String WARNING_COLOR    = "#874443";
    private static final String NORMAL_COLOR     = "TRANSPARENT";
    private static double       aspectRatio      = PREFERRED_HEIGHT / PREFERRED_WIDTH;
    private double              width;
    private double              height;
    private Pane                pane;
    private Region              frame;
    private Region              background;
    private Text                nameText;

    /* ********** Constructor ********** */
    public AlarmSkin(){
        super(new Alarm());
    }

    public AlarmSkin(Alarm control) {
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

        frame = new Region();
        frame.getStyleClass().setAll("frame");
        frame.setVisible(getSkinnable().getCurrentValue());

        nameText = new Text(getSkinnable().getName());
        nameText.getStyleClass().setAll("text");
        nameText.setVisible(getSkinnable().getCurrentValue());

        pane = new Pane(background, frame, nameText);

        getChildren().add(pane);
        resize();
    }

    private void registerListeners() {
        getSkinnable().prefWidthProperty().addListener(observable -> handleControlPropertyChanged("PREF_SIZE") );
        getSkinnable().prefHeightProperty().addListener(observable -> handleControlPropertyChanged("PREF_SIZE") );
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().currentValueProperty().addListener(observable -> handleControlPropertyChanged("UPDATE_VALUE"));
        getSkinnable().nameProperty().addListener(observable -> handleControlPropertyChanged("UPDATE_NAME"));
    }


    /* ********** Methods ********** */
    private void handleControlPropertyChanged(final String PROPERTY) {
        switch (PROPERTY){
            case "UPDATE_VALUE":
                update();
                break;
            case "UPDATE_NAME":
                nameText.setText(getSkinnable().getName());
                break;
            case "RESIZE":
                resize();
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
        width  = getSkinnable().getWidth();
        height = getSkinnable().getHeight();

//        if (aspectRatio * width > height) {
//            width = 1 / (aspectRatio / height);
//        } else if (1 / (aspectRatio / height) > width) {
//            height = aspectRatio * width;
//        }

        if (width > 0 && height > 0) {
            background.setPrefSize(width, height);
            frame.setPrefSize(width, height);

            update();
        }
    }

    private void update(){
        nameText.setVisible(getSkinnable().getCurrentValue());
        nameText.setText(getSkinnable().getName().toUpperCase());
        nameText.setFont(Font.font(width * 0.07));

        background.setStyle(getSkinnable().getCurrentValue() ? "-background-color:".concat(WARNING_COLOR).concat(";") : "-background-color:".concat(NORMAL_COLOR).concat(";"));

        nameText.setTextAlignment(TextAlignment.CENTER);
        nameText.setX((width - nameText.getLayoutBounds().getWidth()) * 0.5);
        nameText.setY((height - nameText.getLayoutBounds().getHeight()) * 0.5);
        nameText.setTextOrigin(VPos.TOP);

        frame.setVisible(getSkinnable().getCurrentValue());
    }
}
