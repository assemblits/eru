package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.gui.toolbars.tree.ProjectTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by mtrujillo on 8/31/2015.
 */
public class FrameController extends AnchorPane {

    @FXML private ProjectTree projectTree;
    @FXML private AnchorPane  leftPane;
    @FXML private AnchorPane  centerPane;
    @FXML private AnchorPane  bottomPane;
    @FXML private Label       statusLabel;

    public FrameController() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Frame.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void launchScadaButtonFired(ActionEvent actionEvent) {
    }

    @FXML
    private void ConnectDevicesButtonFired(ActionEvent actionEvent) {
        if (((ToggleButton) actionEvent.getSource()).isSelected()){
            App.getSingleton().execute(App.Action.CONNECT_MODBUS);
        } else {
            App.getSingleton().execute(App.Action.DISCONNECT_MODBUS);
        }
    }

    @FXML
    private void saveButtonFired(ActionEvent actionEvent) {
        App.getSingleton().execute(App.Action.SAVE);
    }

}
