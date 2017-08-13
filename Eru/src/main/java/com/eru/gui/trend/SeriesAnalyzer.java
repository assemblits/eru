package com.eru.gui.trend;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mtrujillo on 5/12/2016.
 */
public class SeriesAnalyzer<X,Y> {
    /* ********** Constructors ********** */
    private ObservableList<XYChart.Series<X,Y>>  chartSeries;


    /* ********** Constructors ********** */
    public SeriesAnalyzer() {
        this(FXCollections.observableArrayList());
    }
    public SeriesAnalyzer(ObservableList<XYChart.Series<X,Y>> chartSeries) {
        this.chartSeries = chartSeries;
    }

    /* ********** Methods ********** */
    public ObservableList<XYChart.Data<X,Y>> getClosestDataListToXPosition(double xPosition) {
        final ObservableList<XYChart.Data<X,Y>>    nearestXDataToSelected = FXCollections.observableArrayList();
        nearestXDataToSelected.clear();
        for(XYChart.Series<X,Y> s : chartSeries){
            if(s.getData().isEmpty()) continue;
            nearestXDataToSelected.add(getClosestDataToXPosition(xPosition, s));
        }
        return nearestXDataToSelected;
    }
    public XYChart.Data<X,Y> getClosestDataToXPosition(double xPosition){
        final Map<Long, XYChart.Data<X,Y>> dataDistances = new HashMap<>();

        for(XYChart.Series<X, Y>  population : chartSeries) {
            for (XYChart.Data<X, Y> sampleData : population.getData()) {
                final long distanceFromSampleToSelected = (long) Math.abs(xPosition - sampleData.getNode().getLayoutX());
                dataDistances.put(distanceFromSampleToSelected, sampleData);
            }
        }

        if(dataDistances.isEmpty()){
            return null;
        } else {
            final Long minDistance = Collections.min(dataDistances.keySet());
            return dataDistances.get(minDistance);
        }
    }
    private XYChart.Data<X,Y> getClosestDataToXPosition(double xPosition, XYChart.Series<X, Y> population) {
        final Map<Long, XYChart.Data<X,Y>> dataDistances = new HashMap<>();

        for(XYChart.Data<X,Y> sampleData : population.getData()){
            final long distanceFromSampleToSelected = (long) Math.abs(xPosition - sampleData.getNode().getLayoutX());
            dataDistances.put(distanceFromSampleToSelected, sampleData);
        }

        final Long minDistance = Collections.min(dataDistances.keySet());
        return dataDistances.get(minDistance);
    }

    public ObservableList<XYChart.Data<X,Y>> getClosestDataListToYPosition(double yPosition) {
        final ObservableList<XYChart.Data<X,Y>> nearestYDataToSelected = FXCollections.observableArrayList();
        nearestYDataToSelected.clear();
        for(XYChart.Series<X,Y> s : chartSeries){
            if(s.getData().isEmpty()) continue;
            nearestYDataToSelected.add(getClosestDataToYPosition(yPosition, s));
        }
        return nearestYDataToSelected;
    }
    public XYChart.Data<X,Y> getClosestDataToYPosition(double xPosition){
        final Map<Long, XYChart.Data<X,Y>> dataDistances = new HashMap<>();

        for(XYChart.Series<X, Y>  population : chartSeries) {
            for (XYChart.Data<X, Y> sampleData : population.getData()) {
                final long distanceFromSampleToSelected = (long) Math.abs(xPosition - sampleData.getNode().getLayoutY());
                dataDistances.put(distanceFromSampleToSelected, sampleData);
            }
        }
        final Long minDistance = Collections.min(dataDistances.keySet());
        return dataDistances.get(minDistance);
    }
    private XYChart.Data<X,Y> getClosestDataToYPosition(double yPosition, XYChart.Series<X, Y> population) {
        final Map<Long, XYChart.Data<X,Y>> dataDistances = new HashMap<>();

        for(XYChart.Data<X,Y> sampleData : population.getData()){
            final long distanceFromSampleToSelected = (long) Math.abs(yPosition - sampleData.getNode().getLayoutX());
            dataDistances.put(distanceFromSampleToSelected, sampleData);
        }

        final Long minDistance = Collections.min(dataDistances.keySet());
        return dataDistances.get(minDistance);
    }

    /* ********** Setters and Getters ********** */
    public ObservableList<XYChart.Series<X,Y>> getChartSeries() {
        return chartSeries;
    }
    public void setChartSeries(ObservableList<XYChart.Series<X,Y>> chartSeries) {
        this.chartSeries = chartSeries;
    }
}
