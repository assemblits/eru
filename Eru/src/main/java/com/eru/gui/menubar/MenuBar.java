package com.eru.gui.menubar;

import com.eru.gui.EruController;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

/**
 * Created by mtrujillo on 8/23/17.
 */
public class MenuBar extends javafx.scene.control.MenuBar {

    private final EruController eruController;

    public MenuBar(EruController eruController) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuBar.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            // Load successful
            this.eruController = eruController;
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    private void saveMenuItemSelected(ActionEvent event) {
        this.eruController.performDBAction(EruController.DBAction.SAVE);
    }

    @FXML
    private void exitMenuItemSelected(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void aboutMenuItemSelected(ActionEvent event) {
        this.eruController.performPopupAction(EruController.PopupAction.SHOW_ABOUT);
    }

    @FXML
    private void connectMenuItemSelected(ActionEvent event) {
        this.eruController.performConnectionAction(EruController.ConnectionAction.CONNECT);
    }

    @FXML
    private void disconnectMenuItemSelected(ActionEvent event) {
        this.eruController.performConnectionAction(EruController.ConnectionAction.DISCONNECT);
    }

    @FXML private void preferencesMenuItemSelected(ActionEvent event){
        this.eruController.performPopupAction(EruController.PopupAction.SHOW_PREFERENCES);
    }
}
