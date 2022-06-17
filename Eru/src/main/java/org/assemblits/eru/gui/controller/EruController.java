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

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.Project;
import org.assemblits.eru.exception.EruException;
import org.assemblits.eru.exception.FxmlFileReadException;
import org.assemblits.eru.gui.component.EruSceneBuilder;
import org.assemblits.eru.gui.model.ProjectModel;
import org.assemblits.eru.jfx.scenebuilder.SceneFxmlManager;
import org.assemblits.eru.logger.LabelAppender;
import org.assemblits.eru.persistence.ProjectRepository;
import org.assemblits.eru.preferences.EruPreferences;
import org.assemblits.eru.project.ProjectListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class EruController {

    private final ConfigurableApplicationContext applicationContext;
    private final CenterPaneController centerPaneController;
    private final ProjectTreeController projectTreeController;
    private final ProjectListener projectListener;
    private final LogLabelController logLabelController;
    private final ProjectRepository projectRepository;
    private final ProjectModel projectModel;
    private final SceneFxmlManager sceneFxmlManager;
    private final EruPreferences eruPreferences;

    public void startEru(Stage stage) {
        log.info("Starting Eru");
        setStage(stage);
        populateControllers();
        configureAutoSave();
        stage.show();
    }

    private void setStage(Stage stage) {
        try {
            FXMLLoader fxmlLoader = createFxmlLoader();
            fxmlLoader.setLocation(getClass().getResource("/views/Main.fxml"));
            Parent appRootNode = fxmlLoader.load();

            Scene eruScene = new Scene(appRootNode);
            eruScene.getStylesheets().add(eruPreferences.getTheme().getValue().getStyleSheetURL());
            eruPreferences.getTheme().addListener((observable, oldTheme, newTheme) ->  {
                log.info("Updating App Theme...");
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

    private void populateControllers(){
        centerPaneController.getUsersTableView().setUsers(projectModel.getUsers());
        centerPaneController.getConnectionsTableView().setConnections(projectModel.getConnections());
        centerPaneController.getDevicesTableView().setDevicesAndConnections(projectModel.getDevices(), projectModel.getConnections());
        centerPaneController.getTagsTableView().setTagsAndDevices(projectModel.getTags(), projectModel.getDevices());
        centerPaneController.getDisplayTableView().setDisplays(projectModel.getDisplays());
        centerPaneController.getDisplayTableView().setOnDisplayPreview(this::launchDisplayPreview);
        centerPaneController.getDisplayTableView().setOnDisplayEdit(this::launchDisplayEditor);

        projectTreeController.setRoot(projectModel.getGroup().getValue());
        projectTreeController.setOnSelectedItem(centerPaneController::setVisibleTable);

        projectListener.setProjectModel(projectModel);
        projectListener.listen();

        logLabelController.getLogeLabel().textProperty().bind(LabelAppender.lastLog);
    }

    private void configureAutoSave() {
        centerPaneController.getUsersTableView().addActionOnEditCommit(this::saveProject);
        centerPaneController.getConnectionsTableView().addActionOnEditCommit(this::saveProject);
        centerPaneController.getDevicesTableView().addActionOnEditCommit(this::saveProject);
        centerPaneController.getTagsTableView().addActionOnEditCommit(this::saveProject);
        centerPaneController.getDisplayTableView().addActionOnEditCommit(this::saveProject);
    }

    void saveProject(){
        log.info("Saving...");
        Project lastProject = projectRepository.findOne(1);
        lastProject.setName(projectModel.getName().getValue());
        lastProject.setGroup(projectModel.getGroup().getValue());
        lastProject.setConnections(projectModel.getConnections());
        lastProject.setDevices(projectModel.getDevices());
        lastProject.setDisplays(projectModel.getDisplays());
        lastProject.setTags(projectModel.getTags());
        lastProject.setUsers(projectModel.getUsers());
        lastProject = projectRepository.save(lastProject);

        boolean thereIsConnectionWhithoutPK = projectModel.getConnections()
                .stream()
                .anyMatch(conn -> conn.getId() == null || conn.getId() == 0);
        boolean thereIsDeviceWhithoutPK = projectModel.getDevices()
                .stream()
                .anyMatch(dev -> dev.getId() == null || dev.getId() == 0);
        boolean thereIsDisplayWhithoutPK = projectModel.getDisplays()
                .stream()
                .anyMatch(disp -> disp.getId() == null || disp.getId() == 0);
        boolean thereIsTagWhithoutPK = projectModel.getTags()
                .stream()
                .anyMatch(tag -> tag.getId() == null || tag.getId() == 0);
        boolean thereIsUserWhithoutPK = projectModel.getUsers()
                .stream()
                .anyMatch(user -> user.getId() == null || user.getId() == 0);

        if (thereIsConnectionWhithoutPK) {
            log.info("Updating connections PKs...");
            projectModel.getConnections().clear();
            projectModel.getConnections().setAll(lastProject.getConnections());
        }
        if (thereIsDeviceWhithoutPK) {
            log.info("Updating devices PKs...");
            projectModel.getDevices().clear();
            projectModel.getDevices().setAll(lastProject.getDevices());
        }
        if (thereIsDisplayWhithoutPK) {
            log.info("Updating Displays PKs...");
            projectModel.getDisplays().clear();
            projectModel.getDisplays().setAll(lastProject.getDisplays());
        }
        if (thereIsTagWhithoutPK) {
            log.info("Updating Tags PKs...");
            projectModel.getTags().clear();
            projectModel.getTags().setAll(lastProject.getTags());
        }
        if (thereIsUserWhithoutPK) {
            log.info("Updating users PKs...");
            projectModel.getUsers().clear();
            projectModel.getUsers().setAll(lastProject.getUsers());
        }
    }

    private void launchDisplayEditor(Display display){
        log.info("Starting display builder for '{}'", display.getName());
        List<Node> oldSceneBuilders = centerPaneController.getTablesAnchorPane()
                .getChildren()
                .stream()
                .filter(node -> node instanceof EruSceneBuilder)
                .collect(Collectors.toList());

        centerPaneController.getTablesAnchorPane().getChildren().removeAll(oldSceneBuilders);

        try {
            EruSceneBuilder eruSceneBuilder = applicationContext.getBeanFactory().getBean(EruSceneBuilder.class);
            eruSceneBuilder.init(display);
            centerPaneController.getTablesAnchorPane().getChildren().add(eruSceneBuilder);
        } catch (FxmlFileReadException e) {
            log.error("Error starting scene builder", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error on Eru Scene Builder initialization.");
            alert.setContentText(e.getMessage());
            alert.setContentText(e.getLocalizedMessage());
        }
    }

    private void launchDisplayPreview(Display display){
        try {
            log.info("Launching displays.");
            final File sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(display);
            final URL fxmlFileUrl = sceneFxmlFile.toURI().toURL();
            final Parent displayNode = FXMLLoader.load(fxmlFileUrl);
            final Scene SCADA_SCENE = new Scene(displayNode);
            final Stage SCADA_STAGE = new Stage(display.stageStyle());
            display.setFxNode(displayNode);
            SCADA_SCENE.setFill(Color.TRANSPARENT);
            SCADA_STAGE.setScene(SCADA_SCENE);
            SCADA_STAGE.setTitle(display.getName());
            SCADA_STAGE.show();
        } catch (Exception e) {
            log.error("Error launching display", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error in SCADA launching process.");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private FXMLLoader createFxmlLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setControllerFactory(applicationContext::getBean);
        return fxmlLoader;
    }
}
