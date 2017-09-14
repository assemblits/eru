package com.eru.gui.controller;

import com.eru.gui.Application.Theme;
import com.eru.gui.ApplicationContextHolder;
import com.eru.preferences.PreferencesController;
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
        final PreferencesController preferencesController = ApplicationContextHolder.getApplicationContext().getBean(PreferencesController.class);
        themeChoiceBox.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themeChoiceBox.setValue(preferencesController.getGlobalPreferences().getTheme());
        themeChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ThemeListener());
    }

    private static class ThemeListener implements ChangeListener<Theme> {
        @Override
        public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
            final PreferencesController preferencesController = ApplicationContextHolder.getApplicationContext().getBean(PreferencesController.class);
            preferencesController.getGlobalPreferences().setTheme(newValue);
            preferencesController.save();
        }
    }
}
