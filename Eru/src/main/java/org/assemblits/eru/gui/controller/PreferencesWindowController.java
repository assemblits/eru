package org.assemblits.eru.gui.controller;

import org.assemblits.eru.gui.Application.Theme;
import org.assemblits.eru.preferences.EruPreference;
import org.assemblits.eru.preferences.EruPreferences;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PreferencesWindowController {

    @FXML
    private ChoiceBox<Theme> themeChoiceBox;

    public void initialize() {
        final EruPreferences eruPreferences = new EruPreferences();
        themeChoiceBox.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themeChoiceBox.setValue(eruPreferences.getTheme().getValue());
        themeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ThemeChangeListener());
    }

    @RequiredArgsConstructor
    private static class ThemeChangeListener implements ChangeListener<Theme> {
        private final EruPreferences eruPreferences = new EruPreferences();
        @Override
        public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
            eruPreferences.getTheme().setValue(newValue);
        }
    }
}
