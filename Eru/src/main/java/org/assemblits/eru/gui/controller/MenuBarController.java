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

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.gui.exception.EruException;
import org.assemblits.eru.preferences.EruPreferences;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuBarController {

    private final ApplicationContext applicationContext;
    private final EruController eruController;

    @FXML
    private void exitMenuItemSelected() {
        Platform.exit();
    }

    @FXML
    private void preferencesMenuItemSelected() {
        final EruPreferences eruPreferences = new EruPreferences();
        Stage preferencesStage = new Stage();
        preferencesStage.setTitle("Preferences");
        preferencesStage.setScene(new Scene(loadNode("/views/Preferences.fxml")));
        preferencesStage.getScene().getStylesheets().add(eruPreferences.getTheme().getValue().getStyleSheetURL());
        eruPreferences.getTheme().addListener((observable, oldTheme, newTheme) ->  {
            preferencesStage.getScene().getStylesheets().clear();
            preferencesStage.getScene().getStylesheets().add(newTheme.getStyleSheetURL());
        });
        preferencesStage.showAndWait();
    }

    @FXML
    private void aboutMenuItemSelected() {
        Stage aboutStage = new Stage();
        aboutStage.setScene(new Scene(loadNode("/views/About.fxml")));
        aboutStage.showAndWait();
    }

    private Parent loadNode(String fxmlViewName) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(fxmlViewName));
            fxmlLoader.setControllerFactory(applicationContext::getBean);
            return fxmlLoader.load();
        } catch (IOException e) {
            String errorMessage = format("Error loading %s screen", fxmlViewName);
            log.error(errorMessage);
            throw new EruException(errorMessage, e);
        }
    }

    @FXML
    private void saveMenuItemSelected(ActionEvent actionEvent) {
        eruController.saveProject();
    }
}
