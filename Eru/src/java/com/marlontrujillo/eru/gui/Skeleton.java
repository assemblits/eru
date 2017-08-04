package com.marlontrujillo.eru.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by mtrujillo on 8/4/17.
 */
public class Skeleton extends VBox{

    public Skeleton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Skeleton_i18n.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
