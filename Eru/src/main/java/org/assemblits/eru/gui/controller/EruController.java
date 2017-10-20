package org.assemblits.eru.gui.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.gui.exception.EruException;
import org.assemblits.eru.gui.model.ProjectListener;
import org.assemblits.eru.gui.model.ProjectModel;
import org.assemblits.eru.persistence.ProjectRepository;
import org.assemblits.eru.preferences.EruPreferences;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EruController {

    private final ConfigurableApplicationContext applicationContext;
    private final CenterPaneController centerPaneController;
    private final ProjectTreeController projectTreeController;
    private final ProjectListener projectListener;
    private final ProjectRepository projectRepository;
    private final ProjectModel projectModel;

    public void startEru(Stage stage) {
        log.info("Starting Eru");
        setStage(stage);
        populateControllers();
        stage.show();
    }

    private void populateControllers(){
        centerPaneController.getUsersTableView().setUsers(projectModel.getUsers());
        centerPaneController.getUsersTableView().setOnEditCommit(() -> projectRepository.save(projectModel.get()));
        centerPaneController.getConnectionsTableView().setConnections(projectModel.getConnections());
        centerPaneController.getConnectionsTableView().setOnEditCommit(() -> projectRepository.save(projectModel.get()));
        centerPaneController.getDevicesTableView().setDevicesAndConnections(projectModel.getDevices(), projectModel.getConnections());
        centerPaneController.getDevicesTableView().setOnEditCommit(() -> projectRepository.save(projectModel.get()));
        centerPaneController.getTagsTableView().setTagsAndDevices(projectModel.getTags(), projectModel.getDevices());
        centerPaneController.getTagsTableView().setOnEditCommit(() -> projectRepository.save(projectModel.get()));
        centerPaneController.getDisplayTableViewView().setDisplays(projectModel.getDisplays());
        centerPaneController.getDisplayTableViewView().setOnEditCommit(() -> projectRepository.save(projectModel.get()));

        projectTreeController.setRoot(projectModel.getGroup().getValue());
        projectTreeController.setOnSelectedItem(centerPaneController::setVisibleTable);
        projectListener.setProjectModel(projectModel);
        projectListener.listen();
    }

    private void setStage(Stage stage) {
        try {
            EruPreferences eruPreferences = new EruPreferences();
            FXMLLoader fxmlLoader = createFxmlLoader();
            fxmlLoader.setLocation(getClass().getResource("/views/Main.fxml"));
            Parent appRootNode = fxmlLoader.load();

            Scene eruScene = new Scene(appRootNode);
            eruScene.getStylesheets().add(eruPreferences.getTheme().getValue().getStyleSheetURL());
            eruPreferences.getTheme().addListener((observable, oldTheme, newTheme) ->  {
                eruScene.getStylesheets().clear();
                eruScene.getStylesheets().add(newTheme.getStyleSheetURL());
            });
            stage.setMaximized(true);
            stage.setTitle("Eru - The open JavaFX SCADA");
            stage.getIcons().add(new Image(getClass().getResource("/images/Logo256x256.png").toExternalForm()));
            stage.setScene(eruScene);
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
