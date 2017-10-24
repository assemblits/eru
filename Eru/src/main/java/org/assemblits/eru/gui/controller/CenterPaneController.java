package org.assemblits.eru.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.EruGroup;
import org.assemblits.eru.exception.FxmlFileReadException;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.component.*;
import org.assemblits.eru.jfx.scenebuilder.SceneBuilderStarter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Getter
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

    @Override
    public void startSceneBuilder(Display display) {
        log.info("Starting display builder for '{}'", display.getName());
        List<Node> oldSceneBuilders = tablesAnchorPane
                .getChildren()
                .stream()
                .filter(node -> node instanceof EruSceneBuilder)
                .collect(Collectors.toList());
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

    public void setVisibleTable(EruGroup eruGroup) {
        tablesAnchorPane.getChildren().forEach(node -> node.setVisible(false));

        switch (eruGroup.getType()) {
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
                break;
        }
    }
}
