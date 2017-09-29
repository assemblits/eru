package org.assemblits.eru.gui.controller;

import org.assemblits.eru.entities.Project;
import org.assemblits.eru.gui.exception.EruException;
import org.assemblits.eru.gui.model.ProjectModel;
import org.assemblits.eru.preferences.EruPreferences;
import org.assemblits.eru.util.ProjectDynamicBehavior;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EruController {

    private final ConfigurableApplicationContext applicationContext;
    private final MenuBarController menuBarController;
    private final CenterPaneController centerPaneController;
    private final ProjectTreeController projectTreeController;
    private final ProjectDynamicBehavior projectDynamicBehavior;
    private final EruPreferences eruPreferences;
    private ProjectModel projectModel;

    public void startEru(Project project, Stage stage) {
        log.info("Starting Eru");

        Parent mainNode = loadMainScene();
        projectModel = ProjectModel.from(project);
        projectTreeController.populateTree(project.getGroup(), centerPaneController::onTreeItemSelected);
        centerPaneController.setProjectModel(projectModel);
        menuBarController.setProjectModel(projectModel);
        projectDynamicBehavior.setProjectModel(projectModel);

        stage.setScene(new Scene(mainNode));
        stage.setMaximized(true);
        stage.setTitle("Eru - The open JavaFX SCADA");
        stage.getScene().getStylesheets().add(eruPreferences.getTheme().getValue().getStyleSheetURL());
        eruPreferences.getTheme().addListener((observable, oldTheme, newTheme) ->  {
            stage.getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().add(newTheme.getStyleSheetURL());
        });
        stage.getIcons().add(new Image(getClass().getResource("/images/Logo256x256.png").toExternalForm()));
        stage.show();
    }

    private Parent loadMainScene() {
        try {
            FXMLLoader fxmlLoader = createFxmlLoader();
            fxmlLoader.setLocation(getClass().getResource("/views/Main.fxml"));
            return fxmlLoader.load();
        } catch (IOException e) {
            log.error("Error loading eru main screen");
            throw new EruException("Error loading eru main screen", e);
        }
    }

    private FXMLLoader createFxmlLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        return fxmlLoader;
    }
}
