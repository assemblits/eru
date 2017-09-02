package com.eru.gui.preferences;

import com.eru.gui.App.Theme;
import com.eru.preferences.EruPreferences;
import com.eru.preferences.EruPreferencesRecord;
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
public class EruPreferencesWindows extends AnchorPane {

    @FXML
    private ChoiceBox<Theme> themeChoiceBox;

    public EruPreferencesWindows() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EruPreferencesWindows.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            initialize();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void initialize() {
        final EruPreferences eruPreferences = EruPreferences.getInstance();
        final EruPreferencesRecord eruPreferencesRecord = eruPreferences.getEruPreferencesRecord();

        // Theme
        themeChoiceBox.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themeChoiceBox.setValue(eruPreferencesRecord.getTheme());
        themeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ThemeListener());
    }

    private static class ThemeListener implements ChangeListener<Theme> {

        @Override
        public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
            final EruPreferences eruPreferences = EruPreferences.getInstance();
            final EruPreferencesRecord eruPreferencesRecord = eruPreferences.getEruPreferencesRecord();
            // Update preferences
            eruPreferencesRecord.setTheme(newValue);
            // Update UI
//            preferences.refreshToolTheme();
        }
    }
}
