package com.eru.gui.controller;

import com.eru.entities.Display;
import com.eru.entities.TreeElementsGroup;
import com.eru.exception.FxmlFileReadException;
import com.eru.gui.component.*;
import com.eru.gui.model.ProjectModel;
import com.eru.scenebuilder.SceneBuilderStarter;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j
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
        DisplayTable displayTableView = new DisplayTable(this);
        centerPane.getChildren().addAll(usersTableView, connectionsTableView, devicesTableView, tagsTableView,
                displayTableView);
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
        log.info(String.format("Starting display builder for '%s'", display.getName()));

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
            log.error(e);
        }

        centerPane.getChildren().add(eruSceneBuilder);
    }
}
