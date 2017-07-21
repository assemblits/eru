package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.dolphin.ServerStartupService;
import javafx.concurrent.Service;
import com.marlontrujillo.eru.dolphin.ClientStartupService;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    public enum Type {SERVER, CLIENT}

    @FXML private Label         statusLabel;
    @FXML private ProgressBar   progressBar;
    private Service<Void>       startupService;
    private BooleanProperty     loaded;
    private BooleanProperty     failed;

    public PreloaderController(Type type) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Preloader.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            loaded = new SimpleBooleanProperty(false);
            failed = new SimpleBooleanProperty(false);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        switch (type) {
            case SERVER:
                startupService = ServerStartupService.getInstance();
                break;
            case CLIENT:
                startupService = ClientStartupService.getInstance();
                break;
        }
        statusLabel.textProperty().bind(startupService.messageProperty());
        progressBar.progressProperty().bind(startupService.progressProperty());
        startupService.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 1) {
                loaded.setValue(true);
            }
        });
        startupService.setOnFailed(event -> failedProperty().setValue(true));
    }

    public void start(){
        switch (startupService.getState()){
            case READY:
            case SCHEDULED:
                startupService.start();
                break;
            case RUNNING:
                break;
            case SUCCEEDED:
                break;
            case CANCELLED:
                break;
            case FAILED:
                break;
        }
    }

    public boolean isLoaded() {
        return loaded.get();
    }
    public ReadOnlyBooleanProperty loadedProperty() {
        return loaded;
    }

    public boolean isFailed() {
        return failed.get();
    }
    public BooleanProperty failedProperty() {
        return failed;
    }

    public void setURLToConnect(String URLToConnect) {
        ClientStartupService.getInstance().setURLToConnect(URLToConnect);
    }
}

