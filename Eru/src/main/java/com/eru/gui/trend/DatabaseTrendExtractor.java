package com.eru.gui.trend;

import com.eru.historian.HistoricDao;
import com.eru.util.JpaUtil;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.StageStyle;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Created by mtrujillo on 5/17/2016.
 */
public class DatabaseTrendExtractor extends Service<Trend> {
    private String tagName;
    private Alert popupProgressInformator = new Alert(Alert.AlertType.INFORMATION);
    private EntityManager entityManager = JpaUtil.getGlobalEntityManager();

    public DatabaseTrendExtractor(EntityManager entityManager, String name) {
        this.entityManager = entityManager;
        this.tagName       = name;
    }

    @Override
    protected Task<Trend> createTask() {
        setupPopupProgressInformator();

        return new Task<Trend>() {
            protected Trend call() throws Exception {
                updateMessage("Connecting with database");
                HistoricDao historicDao = new HistoricDao(entityManager);
                XYChart.Series<LocalDateTime, Number> series = new XYChart.Series<>();
                    List<Map<String, String>> table = historicDao.getTagsHistoric(tagName);
                    for (Map<String, String> row : table) {

                        updateMessage("Extracting timestamp...");
                        String localTimeString = row.get("time_stamp");
                        if (localTimeString.length() > 19) {
                            localTimeString = localTimeString.substring(0, 19);
                        }
                        LocalDateTime rowTimeStamp = LocalDateTime.parse(localTimeString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                        updateMessage("Extracting value...");
                        long cellExtractionProgress = 0;
                        for (String cellName : row.keySet()) {
                            updateProgress(++cellExtractionProgress, row.size());
                            if (cellName == null || cellName.equals("time_stamp")) continue;
                            series.getData().add(new XYChart.Data<>(rowTimeStamp, Double.valueOf(row.get(cellName))));
                        }
                    }

                return new Trend(tagName, series);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                Platform.runLater(() -> popupProgressInformator.close());
            }

            @Override
            protected void failed() {
                super.failed();
                getException().printStackTrace();
                popupProgressInformator.setHeaderText("Error: " + getException().getLocalizedMessage());
                popupProgressInformator.getDialogPane().lookupButton(ButtonType.OK).setVisible(true);
            }
        };
    }

    private void setupPopupProgressInformator(){
        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(progressProperty());

        Label progressLabel = new Label();
        progressLabel.textProperty().bind(messageProperty());

        popupProgressInformator.setHeaderText("Trend database extraction progress");
        popupProgressInformator.initStyle(StageStyle.TRANSPARENT);
        popupProgressInformator.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        popupProgressInformator.setTitle("Progress notification");
        popupProgressInformator.getDialogPane().setContent(progressBar);
        popupProgressInformator.show();
    }

    public void setTagNameToExtractHistory(String tagName) {
        this.tagName = tagName;
    }
}
