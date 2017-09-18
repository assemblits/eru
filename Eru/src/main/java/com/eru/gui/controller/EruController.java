package com.eru.gui.controller;

import com.eru.entities.Project;
import com.eru.gui.Application;
import com.eru.gui.exception.EruException;
import com.eru.gui.model.ProjectModel;
import com.eru.preferences.EruPreferences;
import com.eru.util.TagLinksManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private final ProjectTreeController projectTreeController;
    private final CenterPaneController centerPaneController;
    private final MenuBarController menuBarController;
    private final TagLinksManager tagLinksManager;
    private final EruPreferences eruPreferences;
    private ProjectModel projectModel;
    private Stage stage;

    public void startEru(Project project, Stage stage) {
        log.info("Starting Eru");
        Parent parent = loadMainScene();

        this.stage = stage;
        this.projectModel = ProjectModel.from(project);
        this.projectTreeController.populateTree(project.getGroup(), centerPaneController::onTreeItemSelected);
        this.centerPaneController.setProjectModel(projectModel);
        this.menuBarController.setProjectModel(projectModel);
        this.tagLinksManager.setProjectModel(projectModel);

        stage.setScene(new Scene(parent));
        stage.setMaximized(true);
        stage.setTitle("Eru 2.0");
        refreshTheme();
        eruPreferences.getTheme().addListener(observable -> refreshTheme());
        stage.show();
    }

    public void refreshTheme(){
        final Application.Theme NEW_THEME = eruPreferences.getTheme().getValue();
        log.info("Refreshing Theme to " + NEW_THEME);
        this.stage.getScene().getStylesheets().clear();
        this.stage.getScene().getStylesheets().add(getClass().getResource("/theme/"+NEW_THEME+".css").toExternalForm());
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
