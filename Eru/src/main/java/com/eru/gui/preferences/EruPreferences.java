package com.eru.gui.preferences;

import com.eru.gui.App.Theme;
import com.eru.util.Preferences;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by mtrujillo on 8/16/17.
 */
public class EruPreferences extends AnchorPane {

    @FXML
    private ChoiceBox<Theme> themeChoiceBox;

    public EruPreferences() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EruPreferences.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            initialize();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void initialize() {
        final Preferences preferences = Preferences.getInstance();

        // Theme
        themeChoiceBox.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themeChoiceBox.setValue(Theme.valueOf(preferences.getTheme()));
        themeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ThemeListener());
    }

    private static class ThemeListener implements ChangeListener<Theme> {

        @Override
        public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
            final Preferences preferences = Preferences.getInstance();
            // Update preferences
            preferences.setTheme(newValue);
            // Update UI
//            preferences.refreshToolTheme();
        }
    }
}
