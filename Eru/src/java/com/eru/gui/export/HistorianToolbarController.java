package com.eru.gui.export;

import com.eru.tag.Tag;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 09/10/2014.
 */
public class HistorianToolbarController extends AnchorPane implements Initializable {

    @FXML private ListView<Tag>         historicalTagsListView;
    @FXML private Button                playButton;
    @FXML private Button                stopButton;
    @FXML private Text                  currentStateText;
    @FXML private Text                  historianCountText;
    @FXML private ProgressBar           runningProgressBar;
    @FXML private ChoiceBox<Integer>    historianSamplingTimeChoiceBox;
    @FXML private ChoiceBox<Integer>    historianLimitChoiceBox;

    public HistorianToolbarController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HistorianToolbar.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registerListeners();
    }

    private void registerListeners() {
        //TODO
//        currentStateText.textProperty().bind(Historian.getInstance().statusProperty());
//        historianCountText.textProperty().bind(Historian.getInstance().countProperty().asString());
//        historicalTagsListView.itemsProperty().bind(Historian.getInstance().historicalTagListProperty());
//        historianSamplingTimeChoiceBox.valueProperty().bindBidirectional(Historian.getInstance().samplingTimeProperty().asObject());
//        historianLimitChoiceBox.valueProperty().bindBidirectional(Historian.getInstance().limitProperty().asObject());
//        playButton.disableProperty().bind(Historian.getInstance().runningProperty());
//        stopButton.disableProperty().bind(Historian.getInstance().runningProperty().not());
        playButton.setOnAction(value -> handleEvent("PLAY"));
        stopButton.setOnAction(value -> handleEvent("STOP"));
    }

    private void handleEvent(final String EVENT) {
        switch (EVENT){
            case "PLAY":
                runningProgressBar.setProgress(-1);
                break;
            case "STOP":
                runningProgressBar.setProgress(0);
                break;
        }
    }

}
