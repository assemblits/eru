package com.marlontrujillo.eru.gui.trend;

import com.marlontrujillo.eru.tag.Tag;
import com.marlontrujillo.eru.persistence.Dao;
import com.marlontrujillo.eru.util.JpaUtil;
import com.marlontrujillo.eru.util.PSVAlert;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Callback;
import javafx.util.Duration;
import ve.marlontrujillo.util.LocalDateTimeAxis;

import javax.persistence.EntityManager;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by mtrujillo on 5/11/2016.
 */
public class TrendsGraphicAnalyzer extends AnchorPane implements Initializable {

    /* ********** Fields ********** */
    public static final int CAPACITY = 5;
    public static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd\nHH:mm:ss";
    public static final String boldLineStyle = "-fx-stroke-width: 5px;";

    @FXML private LineChart<LocalDateTime, Number>  lineChart;
    @FXML private TableView<Trend>                  selectedDataTableView;
    @FXML private Line                              trendMovingLine;
    @FXML private Line                              trendSelectionLine;
    @FXML private Rectangle                         zoomRectangle;
    @FXML private Button                            removeTrendButton;
    @FXML private Button                            zoomOutButton;
    @FXML private Button                            zoomInButton;
    @FXML private Button                            cleanSelectionButton;
    @FXML private ListView<Tag>                     tagsWithHistorianEnabled;
    private ObservableList<Trend>                   trends;
    private Tooltip                                 tooltip;
    private SeriesAnalyzer<LocalDateTime, Number>   seriesAnalyzer;
    private List<FadeTransition>                    nodeFadeTransitions;
    private ObjectProperty<Point2D>                 mouseAnchor;

    /* ********** Constructor ********** */
    public TrendsGraphicAnalyzer() {
    }


    /* ********** Public Methods ********** */
    public void addNewTrend(Trend... trendsToAdd) {
        for (Trend t : trendsToAdd) {
            if (trends.size() >= CAPACITY) {
                PSVAlert alert = new PSVAlert(Alert.AlertType.ERROR);
                alert.setHeaderText("Trend Graphic Analyzer has reached the capacity (" + CAPACITY + ")");
                alert.showAndWait();
                break;
            }
            lineChart.getData().add(t.getSeries());
            trends.add(t);
            setUniqueColor(t);
        }
    }

    public void removeTrend(Trend trendToRemove) {
        if (trendToRemove == null) {
            PSVAlert alert = new PSVAlert(Alert.AlertType.ERROR);
            alert.setHeaderText("Please select a tag history to remove...");
            alert.showAndWait();
        } else {
            lineChart.getData().remove(trendToRemove.getSeries());
            trends.remove(trendToRemove);
        }
    }

    /* ********** Private Methods ********** */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.trends              = selectedDataTableView.getItems();
        this.tooltip             = new Tooltip();
        this.nodeFadeTransitions = new ArrayList<>();
        this.mouseAnchor         = new SimpleObjectProperty<>();
        initializeTableView();
        initializeLineChart();
        initializeZoomRectangle();
        initializeButtons();
        fillListViewWithTagsThatHaveHistorianEnabled();
    }

    private void initializeTableView() {
        selectedDataTableView.getColumns().stream().forEach(this::installTrendColorDetector);
        selectedDataTableView.getColumns().get(0).prefWidthProperty().bind(selectedDataTableView.widthProperty().multiply(0.14));
        selectedDataTableView.getColumns().get(1).prefWidthProperty().bind(selectedDataTableView.widthProperty().multiply(0.14));
        selectedDataTableView.getColumns().get(2).prefWidthProperty().bind(selectedDataTableView.widthProperty().multiply(0.14));
        selectedDataTableView.getColumns().get(3).prefWidthProperty().bind(selectedDataTableView.widthProperty().multiply(0.14));
        selectedDataTableView.getColumns().get(4).prefWidthProperty().bind(selectedDataTableView.widthProperty().multiply(0.14));
        selectedDataTableView.getColumns().get(5).prefWidthProperty().bind(selectedDataTableView.widthProperty().multiply(0.14));
        selectedDataTableView.getColumns().get(6).prefWidthProperty().bind(selectedDataTableView.widthProperty().multiply(0.14));
        selectedDataTableView.getSelectionModel().selectedItemProperty().addListener(this::increaseWidthOfSelectedTrendInChart);
    }

    private void installTrendColorDetector(TableColumn trendTableColumn) {
        trendTableColumn.setCellFactory(new Callback<TableColumn<Trend, Object>, TableCell<Trend, Object>>() {
            @Override
            public TableCell<Trend, Object> call(TableColumn<Trend, Object> param) {
                return new TableCell<Trend, Object>(){
                    @Override
                    protected void updateItem(Object item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        if (item == null || isEmpty) {
                            setText(null);
                            setStyle("");
                        } else {
                            // Format date.
                            setText(item.toString());
                            updateColor();
                        }
                    }

                    void updateColor() {
                        final TableRow tableRow = getTableRow();
                        if (null == tableRow) return;
                        final Trend trend = (Trend) tableRow.getItem();
                        if (null == trend) return;

                        //Set the CSS style on the cell and set the cell's text.
                        setStyle("-fx-text-fill:" + trend.getColor().getBackground());
                    }
                };
            }
        });
    }

    private void increaseWidthOfSelectedTrendInChart(Observable observable) {
        if (trends.isEmpty()) return;
        final Trend selectedTrend = selectedDataTableView.getSelectionModel().getSelectedItem();
        if(selectedTrend == null) return;
        cleanLineChartWidth();
        selectedTrend.getSeries().getNode().setStyle(selectedTrend.getSeries().getNode().getStyle().concat(boldLineStyle));
    }

    private void cleanLineChartWidth(){
        trends.stream().forEach(trend -> trend.getSeries().getNode().setStyle(trend.getSeries().getNode().getStyle().replace(boldLineStyle, "")));
    }

    private void initializeLineChart() {
        seriesAnalyzer = new SeriesAnalyzer<>(lineChart.getData());
        lineChart.setOnMouseMoved(this::moveMovingLineOnLineChartMouseMoved);
        lineChart.setOnMouseClicked(this::selectClosestDataToTheMovingLineOnLineChartMouseClicked);
        lineChart.setOnMouseEntered(this::showMovingLineOnLineChartMouseEntered);
        lineChart.setOnMouseExited(this::hideMovingLineAndTooltipOnLineChartMouseExited);
    }

    private void moveMovingLineOnLineChartMouseMoved(MouseEvent mouseEvent) {
        if (trends.isEmpty()) return;
        final Node chartBackground = getChartBackgroundNode();
        final Bounds chartBackgroundNodeBoundsInParent = chartBackground.getBoundsInParent();
        final double cursorPositionOnLineChart = mouseEvent.getX() - chartBackgroundNodeBoundsInParent.getMinX();
        final XYChart.Data<LocalDateTime, Number> closestDataToXPosition = seriesAnalyzer.getClosestDataToXPosition(cursorPositionOnLineChart);
        final Bounds boundsOfClosestData = closestDataToXPosition.getNode().getBoundsInParent();

        trendMovingLine.setLayoutX(boundsOfClosestData.getMinX() + chartBackgroundNodeBoundsInParent.getMinX() + (boundsOfClosestData.getWidth() / 2));
        trendMovingLine.setLayoutY(chartBackgroundNodeBoundsInParent.getMinY());
        trendMovingLine.setEndY(chartBackgroundNodeBoundsInParent.getHeight());
    }

    private void selectClosestDataToTheMovingLineOnLineChartMouseClicked(MouseEvent mouseEvent) {
        if (trends.isEmpty()) return;
        final Node chartBackgroundNode = getChartBackgroundNode();
        final Bounds chartBackgroundNodeBoundsInParent = chartBackgroundNode.getBoundsInParent();
        final double cursorPositionOnLineChart = mouseEvent.getX() - chartBackgroundNodeBoundsInParent.getMinX();
        final XYChart.Data<LocalDateTime, Number> closestDataToXPosition = seriesAnalyzer.getClosestDataToXPosition(cursorPositionOnLineChart);
        final ObservableList<XYChart.Data<LocalDateTime, Number>> closestDataListToXPosition = seriesAnalyzer.getClosestDataListToXPosition(cursorPositionOnLineChart);
        final Bounds boundsOfClosestData = closestDataToXPosition.getNode().getBoundsInParent();
        final Point2D newTooltipPosition = closestDataToXPosition.getNode().localToScreen(closestDataToXPosition.getNode().getLayoutBounds().getMaxX(), closestDataToXPosition.getNode().getLayoutBounds().getMaxY());

        fadeDataNodes(closestDataListToXPosition);

        trendSelectionLine.setLayoutX(boundsOfClosestData.getMinX() + chartBackgroundNodeBoundsInParent.getMinX() + (boundsOfClosestData.getWidth() / 2));
        trendSelectionLine.setLayoutY(chartBackgroundNodeBoundsInParent.getMinY());
        trendSelectionLine.setEndY(chartBackgroundNodeBoundsInParent.getHeight());

        tooltip.hide();
        tooltip.setText(closestDataToXPosition.getXValue().format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN)));
        tooltip.show(closestDataToXPosition.getNode(), newTooltipPosition.getX(), newTooltipPosition.getY());

        for (Trend trend : trends) {
            seriesAnalyzer.getClosestDataListToXPosition(cursorPositionOnLineChart).stream().filter(trend::hasData).forEach(data -> trend.setSelectedValue(data.getYValue()));
        }
    }

    private void fadeDataNodes(ObservableList<XYChart.Data<LocalDateTime, Number>> nearestDataToTheSelected) {
        if (trends.isEmpty()) return;
        for (FadeTransition ft : nodeFadeTransitions) {
            ft.getNode().setOpacity(1.0);
            ft.stop();
            ft.setNode(null);
        }
        nodeFadeTransitions.clear();

        for (XYChart.Data<LocalDateTime, Number> data : nearestDataToTheSelected) {
            FadeTransition ft = new FadeTransition(Duration.millis(300), data.getNode());
            ft.setFromValue(1.0);
            ft.setToValue(0.2);
            ft.setCycleCount(Animation.INDEFINITE);
            ft.setAutoReverse(true);
            ft.play();
            nodeFadeTransitions.add(ft);
        }
        FadeTransition ft = new FadeTransition(Duration.millis(250), trendSelectionLine);
        ft.setFromValue(1.0);
        ft.setToValue(0.2);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
        nodeFadeTransitions.add(ft);
    }

    private void showMovingLineOnLineChartMouseEntered(MouseEvent mouseEvent) {
        if (trends.isEmpty()) return;
        trendMovingLine.setVisible(true);
    }

    private void hideMovingLineAndTooltipOnLineChartMouseExited(MouseEvent mouseEvent) {
        tooltip.hide();
        trendMovingLine.setVisible(false);
    }

    private void initializeZoomRectangle() {
        lineChart.setOnMousePressed(this::initializeRectangleOnLineChartMousePressed);
        lineChart.setOnMouseDragged(this::drawRectangleOnLineChartMouseDragged);
    }

    private void initializeRectangleOnLineChartMousePressed(MouseEvent mouseEvent) {
        if (trends.isEmpty()) return;

        mouseAnchor.set(new Point2D(mouseEvent.getX(), mouseEvent.getY()));
        zoomRectangle.setWidth(0);
        zoomRectangle.setHeight(0);
        lineChart.getXAxis().setAutoRanging(false);
        lineChart.getYAxis().setAutoRanging(false);
    }

    private void drawRectangleOnLineChartMouseDragged(MouseEvent mouseEvent) {
        if (trends.isEmpty()) return;

        double xMousePosition = mouseEvent.getX();
        double yMousePosition = mouseEvent.getY();
        zoomRectangle.setX(Math.min(xMousePosition, mouseAnchor.get().getX()));
        zoomRectangle.setY(Math.min(yMousePosition, mouseAnchor.get().getY()));
        zoomRectangle.setWidth(Math.abs(xMousePosition - mouseAnchor.get().getX()));
        zoomRectangle.setHeight(Math.abs(yMousePosition - mouseAnchor.get().getY()));
    }

    private void initializeButtons() {
        zoomInButton.setOnAction(event -> doZoom());
        cleanSelectionButton.setOnAction(event -> {
            cleanLineChartWidth();
            selectedDataTableView.getSelectionModel().clearSelection();
        });
        removeTrendButton.setOnAction(event -> removeTrend(selectedDataTableView.getSelectionModel().getSelectedItem()));
        zoomOutButton.setOnAction(event -> {
            final LocalDateTimeAxis xAx = (LocalDateTimeAxis) lineChart.getXAxis();
            xAx.setAutoRanging(true);
            final NumberAxis yAx = (NumberAxis) lineChart.getYAxis();
            yAx.setAutoRanging(true);
            zoomRectangle.setWidth(0);
            zoomRectangle.setHeight(0);
        });
        final BooleanBinding disableZoomIn = zoomRectangle.widthProperty().lessThan(10).or(zoomRectangle.heightProperty().lessThan(10));
        zoomInButton.disableProperty().bind(disableZoomIn);
    }

    private void doZoom() {
        if (trends.isEmpty()) return;
        Point2D zoomTopLeft = new Point2D(zoomRectangle.getX(), zoomRectangle.getY());
        Point2D zoomBottomRight = new Point2D(zoomRectangle.getX() + zoomRectangle.getWidth(), zoomRectangle.getY() + zoomRectangle.getHeight());

        final LocalDateTimeAxis xAxis = (LocalDateTimeAxis) lineChart.getXAxis();
        final NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();

        LocalDateTime lowerX = seriesAnalyzer.getClosestDataToXPosition(zoomTopLeft.getX()).getXValue();
        LocalDateTime upperX = seriesAnalyzer.getClosestDataToXPosition(zoomBottomRight.getX()).getXValue();
        xAxis.setLowerBound(lowerX);
        xAxis.setUpperBound(upperX);

        Number lowerY = seriesAnalyzer.getClosestDataToYPosition(zoomBottomRight.getY()).getYValue();
        Number upperY = seriesAnalyzer.getClosestDataToYPosition(zoomTopLeft.getY()).getYValue();
        yAxis.setLowerBound(lowerY.doubleValue());
        yAxis.setUpperBound(upperY.doubleValue());

        zoomRectangle.setWidth(0);
        zoomRectangle.setHeight(0);
    }

    private Node getChartBackgroundNode() {
        final String CHART_PLOT_BACKGROUND_CSS_CLASS = ".chart-plot-background";
        return lineChart.lookup(CHART_PLOT_BACKGROUND_CSS_CLASS);
    }

    private void setUniqueColor(Trend trend) {
        final List<TrendColor> currentTrendsColors = trends.stream().map(Trend::getColor).collect(Collectors.toList());
        TrendColor newUniqueTrendColor;
        do {
            newUniqueTrendColor = TrendColor.randomColor();
        } while (currentTrendsColors.contains(newUniqueTrendColor));
        trend.setColor(newUniqueTrendColor);
    }

    private void fillListViewWithTagsThatHaveHistorianEnabled() {
        EntityManager entityManager   = JpaUtil.getEntityManagerFactory().createEntityManager();
        Dao<Tag> tagDao= new Dao<>(entityManager, Tag.class);
        for(Tag t : tagDao.findEntities("name", Dao.Order.ASC)){
            if(t.getHistoricalEnabled()) tagsWithHistorianEnabled.getItems().add(t);
        }

        tagsWithHistorianEnabled.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                Tag selectedTag = tagsWithHistorianEnabled.getSelectionModel().getSelectedItem();

                if(selectedTag == null){
                    PSVAlert alert = new PSVAlert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Please, select a tag to insert.");
                    alert.show();
                } else {
                    boolean alreadyInserted = !trends.stream().filter(trend -> trend.getName().equals(selectedTag.getName())).collect(Collectors.toList()).isEmpty();
                    if(alreadyInserted) {
                        PSVAlert alert = new PSVAlert(Alert.AlertType.ERROR);
                        alert.setHeaderText(selectedTag.getName() + " is already inserted.");
                        alert.show();
                    }  else {
                        DatabaseTrendExtractor databaseTrendExtractor = new DatabaseTrendExtractor(entityManager, selectedTag.getName());
                        databaseTrendExtractor.setOnSucceeded(event -> addNewTrend((Trend) event.getSource().getValue()));
                        databaseTrendExtractor.start();
                    }
                }
            }
        });
    }

    @FXML
    private void deleteTableTrendMenuItemFired(ActionEvent actionEvent) {
        removeTrend(selectedDataTableView.getSelectionModel().getSelectedItem());
    }
}