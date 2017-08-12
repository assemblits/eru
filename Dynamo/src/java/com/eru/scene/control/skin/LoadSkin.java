package com.eru.scene.control.skin;

import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import com.eru.scene.control.Load;

/**
 * Created by mtrujillo on 09/02/2015.
 */
public class LoadSkin extends SkinBase<Load> {

    public boolean HOLIS;

    private static final double PREFERRED_HEIGHT = 40;
    private static final double PREFERRED_WIDTH = 40;
    private static final double MINIMUM_WIDTH = 5;
    private static final double MINIMUM_HEIGHT = 5;
    private static final double MAXIMUM_WIDTH = 1024;
    private static final double MAXIMUM_HEIGHT = 1024;
    private static final double NAME_TEXT_SIZE_FACTOR = 0.5;
    private double width;
    private double height;
    private double size;

    private Region  background;
    private Region  load;
    private Text    name;
    private Pane    pane;

    public LoadSkin(){
        super(new Load());
    }

    public LoadSkin(Load control) {
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

        load = new Region();
        load.getStyleClass().addAll("load-shape");

        name = new Text(getSkinnable().getName());
        name.getStyleClass().setAll("name-text");

        pane = new Pane(background, load, name);

        getChildren().add(pane);
        resize();
    }

    private void registerListeners() {
        getSkinnable().prefWidthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().prefHeightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE"));
        getSkinnable().energizedProperty().addListener(observable -> handleControlPropertyChanged("ENERGIZED"));
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
            size    = getSkinnable().getWidth() < getSkinnable().getHeight() ? getSkinnable().getWidth() : getSkinnable().getHeight();

            background.setPrefSize(width, height);
            load.setPrefSize(width, height);

            name.setText(getSkinnable().getName());
            name.setFont(Font.font(size*NAME_TEXT_SIZE_FACTOR));
            name.setX((size - name.getLayoutBounds().getWidth()) * 0.5);
            name.setY((size - name.getLayoutBounds().getHeight()) * 0.5);
            name.setTextOrigin(VPos.TOP);
            updateState();
        }
    }

    private void updateState() {
        load.setStyle(getSkinnable().getEnergized() ? "-fx-background-color:-energized-color" : "-fx-background-color:-de-energized-color");
    }
}
