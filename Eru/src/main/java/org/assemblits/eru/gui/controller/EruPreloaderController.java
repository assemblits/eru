package org.assemblits.eru.gui.controller;

import org.assemblits.eru.gui.service.ApplicationLoader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EruPreloaderController {

    private final ApplicationLoader applicationLoader;
    @FXML
    private Label statusLabel;
    @FXML
    private ProgressBar progressBar;

    public void initialize() {
        progressBar.progressProperty().bind(applicationLoader.progressProperty());
        statusLabel.textProperty().bind(applicationLoader.messageProperty());
    }

}
