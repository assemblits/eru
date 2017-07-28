package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.logger.LabelAppender;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 8/31/2015.
 */
public class FrameController implements Initializable {

    @FXML private AnchorPane leftPane;
    @FXML private AnchorPane centerPane;
    @FXML private AnchorPane bottomPane;
    @FXML private Label      statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LabelAppender.setLabel(statusLabel);
    }

    @FXML
    private void launchScadaButtonFired(ActionEvent actionEvent) {
        App.launchSCADA();
    }

    @FXML
    private void ConnectDevicesButtonFired(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()){
            App.connect();
        } else {
            App.disconnect();
        }
    }

    @FXML
    private void saveButtonFired(ActionEvent actionEvent) {
        App.save();
    }

}
