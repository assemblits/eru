package com.eru.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Created by mtrujillo on 8/31/2015.
 */
public class PSVAlert extends Alert {

    private Stage stage;

    public PSVAlert(AlertType alertType) {
        super(alertType);
        stage = (Stage) getDialogPane().getScene().getWindow();
//        stage.getScene().getStylesheets().add(PSVStageUtil.class.getResource("dark-style.css").toExternalForm());
        PSVStageUtil.setPSVTitleAndIcon(stage);
    }

    public PSVAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
    }
}
