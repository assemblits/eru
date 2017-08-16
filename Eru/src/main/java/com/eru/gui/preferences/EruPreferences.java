package com.eru.gui.preferences;

import com.eru.util.Preferences;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by mtrujillo on 8/16/17.
 */
public class EruPreferences extends AnchorPane {

    private Preferences preferences;

    public EruPreferences() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EruPreferences.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();

            this.preferences = Preferences.getInstance();

        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
