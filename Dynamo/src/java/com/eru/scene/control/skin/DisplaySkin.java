package com.eru.scene.control.skin;

import com.eru.fonts.Fonts;
import javafx.geometry.VPos;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import com.eru.scene.control.Display;

/**
 * Created by mtrujillo on 18/06/2014.
 */
public class DisplaySkin extends SkinBase<Display> {
    /* ********** Fields ********** */
    private static final double PREFERRED_WIDTH     = 150;
    private static final double PREFERRED_HEIGHT    = 40;
    private static final double MINIMUM_WIDTH       = 20;
    private static final double MINIMUM_HEIGHT      = 10;
    private static final double MAXIMUM_WIDTH       = 1024;
    private static final double MAXIMUM_HEIGHT      = 1024;
    private double              width;
    private double              height;
    private double              size;

    private Pane                pane;
    private Text                currentValueText;
    private Text                unitText;
    private Text                titleText;
    private Font                currentValueFont;
    private Font                unitFont;
    private Font                titleFont;
    private Region              frame;
    private Region              background;
    private Region              warningIcon;

    /* ********** Constructors ********** */
    public DisplaySkin(){
        super(new Display());
    }

    public DisplaySkin(Display control) {
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
        frame.setStyle(getSkinnable().getAlarmed() ? "-fx-background-color: -warning-color" : "-fx-background-color: -frame-color");

        warningIcon = new Region();
        warningIcon.getStyleClass().setAll("warning-icon");
        warningIcon.setOpacity(getSkinnable().getAlarmed() ? 1 : 0);

        currentValueText = new Text(getSkinnable().getCurrentText());
        currentValueText.getStyleClass().setAll("text");
        currentValueText.setTextOrigin(VPos.BOTTOM);
        currentValueText.setFill(getSkinnable().getTextFill());

        unitText = new Text(getSkinnable().getUnit());
        unitText.getStyleClass().setAll("text");
        unitText.setOpacity(getSkinnable().getUnitVisible() ? 1 : 0);
        unitText.setFill(getSkinnable().getTextFill());
        unitText.setVisible(getSkinnable().getAlarmed());
        titleText = new Text(getSkinnable().getTitle());
        titleText.getStyleClass().setAll("text");
        titleText.setFill(getSkinnable().getTextFill());

        pane = new Pane();
        pane.getChildren().setAll(background, frame, warningIcon, currentValueText, unitText, titleText);

        getChildren().setAll(pane);

        resize();
        updateDisplay();
    }

    private void registerListeners() {
        // Resizing
        getSkinnable().prefWidthProperty().addListener(observable -> handleControlPropertyChanged("PREF_SIZE") );
        getSkinnable().prefHeightProperty().addListener(observable -> handleControlPropertyChanged("PREF_SIZE") );
        getSkinnable().widthProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );
        getSkinnable().heightProperty().addListener(observable -> handleControlPropertyChanged("RESIZE") );

        // Update values
        getSkinnable().currentTextProperty().addListener(observable -> handleControlPropertyChanged("UPDATE_VALUE") );
        getSkinnable().titleProperty().addListener(observable -> handleControlPropertyChanged("UPDATE_TITLE"));
        getSkinnable().unitProperty().addListener(observable -> handleControlPropertyChanged("UPDATE_UNIT") );


        //Fonts and Visibility
        getSkinnable().textFillProperty().addListener(observable -> handleControlPropertyChanged("TEXT_FILL"));
        getSkinnable().unitVisibleProperty().addListener(observable -> handleControlPropertyChanged("UNIT_VISIBLE"));
        getSkinnable().valueFontProperty().addListener(observable -> handleControlPropertyChanged("FONTS") );
        getSkinnable().unitFontProperty().addListener(observable -> handleControlPropertyChanged("FONTS") );
        getSkinnable().titleFontProperty().addListener(observable -> handleControlPropertyChanged("FONTS") );
        getSkinnable().alarmedProperty().addListener(observable -> handleControlPropertyChanged("ALARMED"));
    }

    /* ********** Methods ********** */
    protected void handleControlPropertyChanged(final String PROPERTY) {
        switch (PROPERTY){
            case "TEXT_FILL":
                titleText.setFill(getSkinnable().getTextFill());
                currentValueText.setFill(getSkinnable().getTextFill());
                unitText.setFill(getSkinnable().getTextFill());
                break;
            case "RESIZE":
                resize();
                updateDisplay();
                break;
            case "UPDATE_VALUE":
                updateDisplay();
                break;
            case "UPDATE_TITLE":
                titleText.setText(getSkinnable().getTitle());
                titleText.setX((width - titleText.getLayoutBounds().getWidth()) * 0.5);
                break;
            case "UPDATE_UNIT":
                unitText.setTextOrigin(VPos.BOTTOM);
                unitText.setTextAlignment(TextAlignment.RIGHT);
                unitText.setText(getSkinnable().getUnit());
                unitText.setX((width - unitText.getLayoutBounds().getWidth()) - height * 0.08);
                unitText.setY(height * 0.9);
                break;
            case "UNIT_VISIBLE":
                unitText.setOpacity(getSkinnable().getUnitVisible() ? 1 : 0);
                break;
            case "FONTS":
                updateFonts();
                break;
            case "ALARMED":
                warningIcon.setOpacity(getSkinnable().getAlarmed() ? 1 : 0);
                frame.setStyle(getSkinnable().getAlarmed() ? "-fx-background-color: -warning-color" : "-fx-background-color: -frame-color");
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
        size = getSkinnable().getWidth() < getSkinnable().getHeight() ? getSkinnable().getWidth() : getSkinnable().getHeight();

        if (width > 0 && height > 0) {
            updateFonts();                                                                          // Set fonts size

            warningIcon.setPrefSize(width*0.12, height*0.3);

            titleText.setTextOrigin(VPos.TOP);
            titleText.setTextAlignment(TextAlignment.CENTER);
            titleText.setText(getSkinnable().getTitle());
            titleText.setX((width - titleText.getLayoutBounds().getWidth()) * 0.5);                         // In the middle

            background.setPrefSize(width, (height -(titleText.getLayoutBounds().getHeight())));
            background.setTranslateY((height - (height - titleText.getLayoutBounds().getHeight())));    // Under the titleText

            frame.setPrefSize(width, titleText.getLayoutBounds().getHeight());
            frame.setTranslateY(height - (height - titleText.getLayoutBounds().getHeight()));           // With background

            updateDisplay();                                                                      // Inside the frame
        }
    }

    private void updateFonts() {
        // Update the size and Style
        switch(getSkinnable().getValueFont()) {
            case LCD:
                currentValueFont = Fonts.digital(0.45 * size);
                break;
            case DIGITAL:
                currentValueFont = Fonts.digitalReadout(0.55 * size);
                break;
            case DIGITAL_BOLD:
                currentValueFont = Fonts.digitalReadoutBold(0.55 * size);
                break;
            case ELEKTRA:
                currentValueFont = Fonts.elektra(0.45 * size);
                break;
            case XOLONIUM:
                currentValueFont = Fonts.xoloniumBold(0.30 * size);
                break;
            case STANDARD:
            default:
                currentValueFont = Fonts.xoloniumRegular(0.45 * size);
                break;
        }

        switch(getSkinnable().getUnitFont()) {
            case LCD:
                unitFont = Fonts.digital(0.35 * size);
                break;
            case DIGITAL:
                unitFont = Fonts.digitalReadout(0.45 * size);
                break;
            case DIGITAL_BOLD:
                unitFont = Fonts.digitalReadoutBold(0.45 * size);
                break;
            case ELEKTRA:
                unitFont = Fonts.elektra(0.35 * size);
                break;
            case XOLONIUM:
                unitFont = Fonts.xoloniumBold(0.25 * size);
                break;
            case STANDARD:
            default:
                unitFont = Fonts.xoloniumRegular(0.35 * size);
                break;
        }

        switch(getSkinnable().getTitleFont()) {
            case LCD:
                titleFont = Fonts.digital(0.4 * size);
                break;
            case DIGITAL:
                titleFont = Fonts.digitalReadout(0.5 * size);
                break;
            case DIGITAL_BOLD:
                titleFont = Fonts.digitalReadoutBold(0.5 * size);
                break;
            case ELEKTRA:
                titleFont = Fonts.elektra(0.4 * size);
                break;
            case XOLONIUM:
                titleFont = Fonts.xoloniumBold(0.29 * size);
                break;
            case STANDARD:
            default:
                titleFont = Fonts.xoloniumRegular(0.35 * size);
                break;
        }

        currentValueText.setFont(currentValueFont);
        titleText.setFont(titleFont);
        unitText.setFont(unitFont);
    }

    private void updateDisplay() {
        // Update currentValue with format
        currentValueText.setText(getSkinnable().getCurrentText());

        // Set the currentValue position in display
        currentValueText.setX((width - currentValueText.getLayoutBounds().getWidth()) * 0.5);
        currentValueText.setY(height * 0.8);

        unitText.setX(width - (width - currentValueText.getLayoutBounds().getWidth())*0.5);
        unitText.setY(height * 0.8);
    }

}
