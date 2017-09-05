package com.eru.gui.controller;

import com.eru.gui.Application.Theme;
import com.eru.preferences.EruPreferences;
import com.eru.preferences.EruPreferencesRecord;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PreferencesController implements ChangeListener<Theme> {

    @FXML
    private ChoiceBox<Theme> themeChoiceBox;
    private final EruPreferences eruPreferences;

    public void initialize() {
        themeChoiceBox.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themeChoiceBox.setValue(eruPreferences.getEruPreferencesRecord().getTheme());
        themeChoiceBox.getSelectionModel().selectedItemProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Theme> observable, Theme oldValue, Theme newValue) {
        EruPreferencesRecord eruPreferencesRecord = eruPreferences.getEruPreferencesRecord();
        // Update preferences
        eruPreferencesRecord.setTheme(newValue);
        // Update UI
//            preferences.refreshToolTheme();
    }
}
