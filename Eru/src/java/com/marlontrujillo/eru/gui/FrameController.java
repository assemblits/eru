package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.gui.toolbars.tree.ProjectTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

/**
 * Created by mtrujillo on 8/31/2015.
 */
public class FrameController {

    public ProjectTree projectTree;
    public AnchorPane leftPane;
    public AnchorPane centerPane;
    public AnchorPane bottomPane;
    public Label      statusLabel;

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
