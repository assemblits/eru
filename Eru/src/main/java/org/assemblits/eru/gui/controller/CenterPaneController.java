package org.assemblits.eru.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.EruType;
import org.assemblits.eru.exception.FxmlFileReadException;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.component.*;
import org.assemblits.eru.gui.model.ProjectModel;
import org.assemblits.eru.jfx.scenebuilder.SceneBuilderStarter;
import org.assemblits.eru.persistence.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CenterPaneController implements SceneBuilderStarter {

    @FXML
    private UsersTableView usersTableView;
    @FXML
    private ConnectionsTableView connectionsTableView;
    @FXML
    private DevicesTableView devicesTableView;
    @FXML
    private TagsTableView tagsTableView;
    @FXML
    private DisplayTableView displayTableViewView;
    @FXML
    private AnchorPane tablesAnchorPane;
    @Autowired
    private ProjectRepository projectRepository;

    public void populateTables(ProjectModel project){
        usersTableView.setUsers(project.getUsers());
        connectionsTableView.setConnections(project.getConnections());
        devicesTableView.setDevicesAndConnections(project.getDevices(), project.getConnections());
        tagsTableView.setTagsAndDevices(project.getTags(), project.getDevices());
        displayTableViewView.setDisplays(project.getDisplays());
        usersTableView.setOnEditCommit(() -> projectRepository.save(project.get()));
        connectionsTableView.setOnEditCommit(() -> projectRepository.save(project.get()));
        devicesTableView.setOnEditCommit(() -> projectRepository.save(project.get()));
        tagsTableView.setOnEditCommit(() -> projectRepository.save(project.get()));
        displayTableViewView.setOnEditCommit(() -> projectRepository.save(project.get()));
    }

    public void setVisibleTable(EruType type) {
        tablesAnchorPane.getChildren().forEach(table -> table.setVisible(false));
        switch (type) {
            case DEVICE:
                devicesTableView.setVisible(true);
                break;
            case CONNECTION:
                connectionsTableView.setVisible(true);
                break;
            case TAG:
                tagsTableView.setVisible(true);
                break;
            case USER:
                usersTableView.setVisible(true);
                break;
            case DISPLAY:
                displayTableViewView.setVisible(true);
                break;
            case UNKNOWN:
                // Nothing
                break;
        }
    }

    @Override
    public void startSceneBuilder(Display display) {
        log.info("Starting display builder for '{}'", display.getName());

        List<Node> oldSceneBuilders = tablesAnchorPane.getChildren().stream()
                .filter(node -> node instanceof EruSceneBuilder).collect(Collectors.toList());
        tablesAnchorPane.getChildren().removeAll(oldSceneBuilders);
        EruSceneBuilder eruSceneBuilder = ApplicationContextHolder.getApplicationContext().getBean(EruSceneBuilder.class);
        try {
            eruSceneBuilder.init(display);
        } catch (FxmlFileReadException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error on Eru Scene Builder initialization.");
            alert.setContentText(e.getLocalizedMessage());
            log.error("Error starting scene builder", e);
        }

        tablesAnchorPane.getChildren().add(eruSceneBuilder);
    }
}
