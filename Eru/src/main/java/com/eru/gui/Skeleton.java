package com.eru.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by mtrujillo on 8/4/17.
 */
public class Skeleton extends VBox {

    @FXML
    private StackPane topPane;
    @FXML
    private AnchorPane leftPane;
    @FXML
    private AnchorPane centerPane;
    @FXML
    private TextField searchTextField;
    @FXML
    private Label leftStatusLabel;
    @FXML
    private Label rightStatusLabel;

    public Skeleton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Skeleton.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public StackPane getTopPane() {
        return topPane;
    }

    public AnchorPane getLeftPane() {
        return leftPane;
    }

    public AnchorPane getCenterPane() {
        return centerPane;
    }

    public TextField getSearchTextField() {
        return searchTextField;
    }

    public Label getLeftStatusLabel() {
        return leftStatusLabel;
    }

    public Label getRightStatusLabel() {
        return rightStatusLabel;
    }
}
