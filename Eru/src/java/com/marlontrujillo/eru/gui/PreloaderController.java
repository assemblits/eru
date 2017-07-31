package com.marlontrujillo.eru.gui;

import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Created by mtrujillo on 9/11/2015.
 */
public class PreloaderController extends BorderPane {

    @FXML private Label         statusLabel;
    @FXML private ProgressBar   progressBar;

    public PreloaderController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Preloader.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }
}

