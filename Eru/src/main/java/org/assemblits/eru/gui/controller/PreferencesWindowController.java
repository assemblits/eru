/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.RequiredArgsConstructor;
import org.assemblits.eru.gui.Application.Theme;
import org.assemblits.eru.preferences.EruPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class PreferencesWindowController {

    @FXML
    private ChoiceBox<Theme> themeChoiceBox;
    @FXML
    private TextField databaseUrlTextField;
    @FXML
    private TextField databaseUsernameTextField;
    @FXML
    private PasswordField databasePasswordField;
    @Autowired
    private EruPreferences eruPreferences;

    public void initialize() {
        themeChoiceBox.getItems().setAll(Arrays.asList(Theme.class.getEnumConstants()));
        themeChoiceBox.setValue(eruPreferences.getTheme().getValue());
        themeChoiceBox.setOnAction(event -> eruPreferences.getTheme().setValue(themeChoiceBox.getSelectionModel().getSelectedItem()));

        databaseUrlTextField.setText(eruPreferences.getEruDatabaseURL().get());
        databaseUrlTextField.setOnAction(event -> eruPreferences.getEruDatabaseURL().set(databaseUrlTextField.getText()));

        databaseUsernameTextField.setText(eruPreferences.getEruDatabaseUsername().get());
        databaseUsernameTextField.setOnAction(event -> eruPreferences.getEruDatabaseUsername().set(databaseUsernameTextField.getText()));

        databasePasswordField.setText(eruPreferences.getEruDatabasePassword().get());
        databasePasswordField.setOnAction(event -> eruPreferences.getEruDatabasePassword().set(databasePasswordField.getText()));
    }

}
