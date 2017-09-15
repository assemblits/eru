package com.eru.gui.controller;

import com.eru.entities.Project;
import com.eru.gui.exception.EruException;
import com.eru.gui.model.ProjectModel;
import com.eru.preferences.EruPreferences;
import com.eru.util.TagLinksManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j
@Component
@RequiredArgsConstructor
public class EruController {

    private final ConfigurableApplicationContext applicationContext;
    private final ProjectTreeController projectTreeController;
    private final CenterPaneController centerPaneController;
    private final MenuBarController menuBarController;
    private final TagLinksManager tagLinksManager;
    private ProjectModel projectModel;

    public void startEru(Project project, Stage stage, EruPreferences eruPreferences) {
        log.info("Starting Eru");
        Parent parent = loadMainScene();

        projectModel = ProjectModel.from(project);
        projectTreeController.populateTree(project.getGroup(), centerPaneController::onTreeItemSelected);
        centerPaneController.setProjectModel(projectModel);
        menuBarController.setProjectModel(projectModel);
        tagLinksManager.setProjectModel(projectModel);

        stage.setScene(new Scene(parent));
        stage.getScene().getStylesheets().add(getClass().getResource("/theme/"+eruPreferences.getTheme()+".css").toExternalForm());
        stage.setMaximized(true);
        stage.setTitle("Eru 2.0");
        stage.show();
    }

    public void refreshTheme(){

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
