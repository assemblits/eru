package com.eru.gui.controller;

import com.eru.gui.Application.Theme;
import com.eru.gui.ApplicationContextHolder;
import com.eru.preferences.EruPreferences;
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

    private final EruPreferences eruPreferences;
    @FXML
    private ChoiceBox<Theme> themeChoiceBox;

    public void initialize() {
        themeChoiceBox.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themeChoiceBox.setValue(eruPreferences.getTheme());
        themeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ThemeListener(eruPreferences));
    }

    @RequiredArgsConstructor
    private static class ThemeListener implements ChangeListener<Theme> {
        private final EruPreferences eruPreferences;
        @Override
        public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
            eruPreferences.setTheme(newValue);
        }
    }
}
