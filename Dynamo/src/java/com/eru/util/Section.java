package com.eru.util;

import javafx.beans.property.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Section {
    private final DoubleProperty          start;
    private final BooleanProperty         startVisible;
    private final DoubleProperty          stop;
    private final BooleanProperty         stopVisible;
    private final StringProperty          text;
    private final ObjectProperty<Color>   color;
    private final ObjectProperty<Image>   icon;
    private final StringProperty          styleClass;


    // ******************** Constructors **************************************
    public Section() {
        this(-1, -1);
    }
    public Section(final double START, final double STOP) {
        this(START, STOP, "");
    }
    public Section(final double START, final double STOP, final String TEXT) {
        this(START, STOP, TEXT, Color.DARKRED);
    }
    public Section(final double START, final double STOP, final String TEXT, final Color color) {
        this(START, STOP, TEXT, color, null);
    }
    public Section(final double START, final double STOP, final String TEXT, Color color, final Image ICON){
        this(START, STOP, TEXT, color, ICON, "");
    }
    public Section(final double START, final double STOP, final String TEXT, Color COLOR, final Image ICON, final String STYLE_CLASS) {
        this.start          = new SimpleDoubleProperty(this, "start", START);
        this.startVisible   = new SimpleBooleanProperty(this, "startVisible", true);
        this.stop           = new SimpleDoubleProperty(this, "stop", STOP);
        this.stopVisible    = new SimpleBooleanProperty(this, " stopVisible", true);
        this.text           = new SimpleStringProperty(this, "text", TEXT);
        this.color          = new SimpleObjectProperty<>(this, "color", COLOR);
        this.icon           = new SimpleObjectProperty<>(this, "icon", ICON);
        this.styleClass     = new SimpleStringProperty(this, "styleClass", STYLE_CLASS);
    }


    // ******************** Methods *******************************************
    public double getStart() {
        return start.get();
    }
    public DoubleProperty startProperty() {
        return start;
    }
    public void setStart(double start) {
        this.start.set(start);
    }

    public boolean getStartVisible() {
        return startVisible.get();
    }
    public BooleanProperty startVisibleProperty() {
        return startVisible;
    }
    public void setStartVisible(boolean startVisible) {
        this.startVisible.set(startVisible);
    }

    public double getStop() {
        return stop.get();
    }
    public DoubleProperty stopProperty() {
        return stop;
    }
    public void setStop(double stop) {
        this.stop.set(stop);
    }

    public boolean getStopVisible() {
        return stopVisible.get();
    }
    public BooleanProperty stopVisibleProperty() {
        return stopVisible;
    }
    public void setStopVisible(boolean stopVisible) {
        this.stopVisible.set(stopVisible);
    }

    public String getText() {
        return text.get();
    }
    public StringProperty textProperty() {
        return text;
    }
    public void setText(String text) {
        this.text.set(text);
    }

    public Color getColor() {
        return color.get();
    }
    public ObjectProperty<Color> colorProperty() {
        return color;
    }
    public void setColor(Color color) {
        this.color.set(color);
    }

    public Image getIcon() {
        return icon.get();
    }
    public ObjectProperty<Image> iconProperty() {
        return icon;
    }
    public void setIcon(Image icon) {
        this.icon.set(icon);
    }

    public String getStyleClass() {
        return styleClass.get();
    }
    public StringProperty styleClassProperty() {
        return styleClass;
    }
    public void setStyleClass(String styleClass) {
        this.styleClass.set(styleClass);
    }

    public boolean contains(final double VALUE) {
        return ((Double.compare(VALUE, getStart()) >= 0 && Double.compare(VALUE, getStop()) <= 0));
    }

    public boolean equals(final Section SECTION) {
        return (Double.compare(SECTION.getStart(), getStart()) == 0 &&
                Double.compare(SECTION.getStop(), getStop()) == 0 &&
                SECTION.getText().equals(getText()));
    }

    @Override public String toString() {
        final StringBuilder NAME = new StringBuilder();
        NAME.append("Section: ").append("\n");
        NAME.append("text      : ").append(getText()).append("\n");
        NAME.append("color     : ").append(getColor().toString()).append("\n");
        NAME.append("startValue: ").append(getStart()).append("\n");
        NAME.append("stopValue : ").append(getStop()).append("\n");
        return NAME.toString();
    }

}
