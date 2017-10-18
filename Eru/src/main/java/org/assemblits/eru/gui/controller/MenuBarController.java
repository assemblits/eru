package org.assemblits.eru.gui.controller;

import javafx.application.Platform;
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

    private final EruPreferences eruPreferences;

    public void exitMenuItemSelected() {
        Platform.exit();
    }

    public void preferencesMenuItemSelected() {
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

    public void aboutMenuItemSelected() {
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
}
