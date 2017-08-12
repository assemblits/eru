package com.eru.scene.control;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.scene.control.Control;
import javafx.util.Duration;

/**
 * Created by mtrujillo on 5/16/2016.
 */
public class Dynamo extends Control implements Highlighted {

    private FadeTransition fadeTransitionForHighlight;
    private Boolean        highlighted;
    private DynamoMenu     dynamoMenu;
    private final DoubleProperty currentValue;

    public Dynamo() {
        currentValue            = new DoublePropertyBase(0) {
            @Override protected void invalidated() {
            }
            @Override public Object getBean() { return this; }
            @Override public String getName() { return "currentValue"; }
        };
        this.highlighted                = false;
        this.fadeTransitionForHighlight = new FadeTransition(Duration.millis(500), this);
    }

    public void installMenu(DynamoMenu menu){
        this.dynamoMenu = menu;
        this.dynamoMenu.setDynamo(this);
    }


    @Override
    public boolean isHighlighted() {
        return highlighted;
    }
    @Override
    public void setHighlighted(boolean highlighted) {
        if(highlighted){
            fadeTransitionForHighlight.setFromValue(1.0);
            fadeTransitionForHighlight.setToValue(0.2);
            fadeTransitionForHighlight.setCycleCount(Animation.INDEFINITE);
            fadeTransitionForHighlight.setAutoReverse(true);
            fadeTransitionForHighlight.play();
        } else {
            fadeTransitionForHighlight.getNode().setOpacity(1.0);
            fadeTransitionForHighlight.stop();
            fadeTransitionForHighlight.setNode(null);
        }
        this.highlighted = highlighted;
    }

    public double getCurrentValue() {
        return currentValue.get();
    }
    public DoubleProperty currentValueProperty() {
        return currentValue;
    }
    public void setCurrentValue(double currentValue) {
        this.currentValue.set(currentValue);
    }

    @Override
    public String toString() {
        return "Dynamo{" +
                "fadeTransitionForHighlight=" + fadeTransitionForHighlight +
                ", highlighted=" + highlighted +
                ", dynamoMenu=" + dynamoMenu +
                '}';
    }
}