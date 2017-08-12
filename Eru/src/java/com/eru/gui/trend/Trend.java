package com.eru.gui.trend;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.XYChart;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by mtrujillo on 5/12/2016.
 */
public class Trend {
    /* ********** Dynamic Fields ********** */
    private String                                  name;
    private XYChart.Series<LocalDateTime, Number>   series;
    private ObjectProperty<Number>                  selectedValue;
    private ObjectProperty<Number>                  minValue;
    private ObjectProperty<Number>                  maxValue;
    private ObjectProperty<Number>                  averageValue;
    private ObjectProperty<LocalDateTime>           firstTimeStamp;
    private ObjectProperty<LocalDateTime>           lastTimeStamp;
    private TrendColor                              color;


    /* ********** Constructors ********** */
    public Trend() {
        this("", new XYChart.Series<>());
    }

    public Trend(String name, XYChart.Series<LocalDateTime, Number> series) {
        this.name = name;
        this.series = series;
        this.color = TrendColor.DEFAULT;
        this.selectedValue = new SimpleObjectProperty<>();  //TODO: add Lazy and control updates when gets!
        this.minValue = new SimpleObjectProperty<>();  //TODO: add Lazy and control updates when gets!
        this.maxValue = new SimpleObjectProperty<>();  //TODO: add Lazy and control updates when gets!
        this.averageValue = new SimpleObjectProperty<>();  //TODO: add Lazy and control updates when gets!
        this.firstTimeStamp = new SimpleObjectProperty<>();  //TODO: add Lazy and control updates when gets!
        this.lastTimeStamp = new SimpleObjectProperty<>();  //TODO: add Lazy and control updates when gets!
    }

    /* ********** Methods ********** */
    public boolean hasData(XYChart.Data data){
        return series.getData().contains(data);
    }

    private void updateMinValue() {
        List<Double> yValues= getYValues().stream().map(Number::doubleValue).collect(Collectors.toList());
        this.minValue.set(Collections.min(yValues));
    }
    private void updateMaxValue() {
        List<Double> yValues= getYValues().stream().map(Number::doubleValue).collect(Collectors.toList());
        this.maxValue.set(Collections.max(yValues));
    }
    private void updateAverageValue() {
        double sum = 0;
        int count = series.getData().size();
        for (XYChart.Data<LocalDateTime, Number> data: series.getData()) {
            sum += data.YValueProperty().get().doubleValue();
        }
        this.averageValue.set(count == 0 ? 0 : sum / count);
    }
    private void updateFirstTimeStamp() {
        this.firstTimeStamp.set(series.getData().isEmpty() ? null : getXValues().get(0));
    }
    private void updateLastTimeStamp() {
        this.lastTimeStamp.set(series.getData().isEmpty() ? null : getXValues().get(series.getData().size()-1));
    }


    /* ********** Setters and Getters ********** */
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public XYChart.Series<LocalDateTime, Number> getSeries() {
        return series;
    }
    public void setSeries(XYChart.Series<LocalDateTime, Number> series) {
        this.series = series;
    }

    public Number getSelectedValue() {
        return selectedValue.get();
    }
    public ObjectProperty<Number> selectedValueProperty() {
        return selectedValue;
    }
    public void setSelectedValue(Number selectedValue) {
        this.selectedValue.set(selectedValue);
    }

    public Number getMinValue() {
        return minValue.get();
    }
    public ObjectProperty<Number> minValueProperty() {
        updateMinValue();
        return minValue;
    }
    public void setMinValue(Number minValue) {
        this.minValue.set(minValue);
    }

    public Number getMaxValue() {
        return maxValue.get();
    }
    public ObjectProperty<Number> maxValueProperty() {
        updateMaxValue();
        return maxValue;
    }
    public void setMaxValue(Number maxValue) {
        this.maxValue.set(maxValue);
    }

    public Number getAverageValue() {
        return averageValue.get();
    }
    public ObjectProperty<Number> averageValueProperty() {
        updateAverageValue();
        return averageValue;
    }
    public void setAverageValue(Number averageValue) {
        this.averageValue.set(averageValue);
    }

    public LocalDateTime getFirstTimeStamp() {
        return firstTimeStamp.get();
    }
    public ObjectProperty<LocalDateTime> firstTimeStampProperty() {
        updateFirstTimeStamp();
        return firstTimeStamp;
    }
    public void setFirstTimeStamp(LocalDateTime firstTimeStamp) {
        this.firstTimeStamp.set(firstTimeStamp);
    }

    public LocalDateTime getLastTimeStamp() {
        return lastTimeStamp.get();
    }
    public ObjectProperty<LocalDateTime> lastTimeStampProperty() {
        updateLastTimeStamp();
        return lastTimeStamp;
    }
    public void setLastTimeStamp(LocalDateTime lastTimeStamp) {
        this.lastTimeStamp.set(lastTimeStamp);
    }

    public List<Number> getYValues(){
        return series.getData().stream().map(XYChart.Data::getYValue).collect(Collectors.toList());
    }
    public List<LocalDateTime> getXValues(){
        return series.getData().stream().map(XYChart.Data::getXValue).collect(Collectors.toList());
    }

    public TrendColor getColor() {
        return color;
    }
    public void setColor(TrendColor color) {
        if(this.series.getNode() == null) {
            System.err.println("Trend cannot set new color because series is not added on a linechart");
            return;
        }
        this.series.getNode().setStyle(color.getCssRule());
        this.color = color;
    }
}