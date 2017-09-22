package org.assemblits.eru.gui.controller;

import org.assemblits.eru.entities.Display;
import org.assemblits.eru.entities.TreeElementsGroup;
import org.assemblits.eru.exception.FxmlFileReadException;
import org.assemblits.eru.gui.component.*;
import org.assemblits.eru.gui.model.ProjectModel;
import org.assemblits.eru.jfx.scenebuilder.SceneBuilderStarter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CenterPaneController implements SceneBuilderStarter {

    private final BeanFactory beanFactory;
    @FXML
    private AnchorPane centerPane;

    public void initialize() {
        UsersTableView usersTableView = new UsersTableView();
        ConnectionsTableView connectionsTableView = new ConnectionsTableView();
        DevicesTableView devicesTableView = new DevicesTableView();
        TagsTableView tagsTableView = new TagsTableView();
        DisplayTableView displayTableViewView = new DisplayTableView();
        centerPane.getChildren().addAll(usersTableView, connectionsTableView, devicesTableView, tagsTableView,
                displayTableViewView);
        centerPane.getChildren().forEach(this::adjustToAnchorPaneAndHide);
    }

    public void setProjectModel(ProjectModel projectModel) {
        centerPane.getChildren().stream()
                .filter(node -> node instanceof EruTableView)
                .forEach(node -> ((EruTableView) node).setProjectModel(projectModel));
    }

    public void onTreeItemSelected(TreeElementsGroup.Type type) {
        setVisible(type);
    }

    private void setVisible(TreeElementsGroup.Type type) {
        centerPane.getChildren().stream().forEach(eruTableView -> eruTableView.setVisible(false));
        Optional<Node> table = centerPane.getChildren().stream().filter(node -> node instanceof EruTableView)
                .filter(node -> type.equals(((EruTableView) node).getItemType()))
                .findFirst();
        if (table.isPresent()) {
            table.get().setVisible(true);
        }
    }

    private void adjustToAnchorPaneAndHide(Node node) {
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        node.setVisible(false);
    }

    @Override
    public void startSceneBuilder(Display display) {
        log.info("Starting display builder for '{}'", display.getName());

        List<Node> oldSceneBuilders = centerPane.getChildren().stream()
                .filter(node -> node instanceof EruSceneBuilder).collect(Collectors.toList());
        centerPane.getChildren().removeAll(oldSceneBuilders);

        EruSceneBuilder eruSceneBuilder = beanFactory.getBean(EruSceneBuilder.class);
        try {
            eruSceneBuilder.init(display);
        } catch (FxmlFileReadException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error on Eru Scene Builder initialization.");
            alert.setContentText(e.getLocalizedMessage());
            log.error("Error starting scene builder", e);
        }

        centerPane.getChildren().add(eruSceneBuilder);
    }
}
