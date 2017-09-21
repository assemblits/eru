package org.assemblits.eru.gui.controller;

import org.assemblits.eru.entities.TreeElementsGroup;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ProjectTreeController {

    @FXML
    private TreeView<TreeElementsGroup> projectTree;

    public void initialize() {
        projectTree.setEditable(true);

        AnchorPane.setTopAnchor(projectTree, 0.0);
        AnchorPane.setBottomAnchor(projectTree, 0.0);
        AnchorPane.setRightAnchor(projectTree, 0.0);
        AnchorPane.setLeftAnchor(projectTree, 0.0);
    }

    public void populateTree(TreeElementsGroup treeElementsGroup, Consumer<TreeElementsGroup.Type> selectItem) {
        setContent(treeElementsGroup);
        projectTree.setCellFactory(c -> {
            GroupTreeCell groupTreeCell = new GroupTreeCell();
            groupTreeCell.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
                if (isSelected) {
                    selectItem.accept(groupTreeCell.getItem().getType());
                }
            });
            return groupTreeCell;
        });
    }

    private void setContent(TreeElementsGroup treeElementsGroup) {
        if (projectTree.getRoot() != null) projectTree.getRoot().getChildren().clear();
        projectTree.setRoot(createTree(treeElementsGroup));
        projectTree.getRoot().setExpanded(true);
    }

    private TreeItem<TreeElementsGroup> createTree(TreeElementsGroup treeElementsGroup) {
        TreeItem<TreeElementsGroup> treeItem = new TreeItem<>(treeElementsGroup);
        for (TreeElementsGroup t : treeElementsGroup.getChildren()) {
            treeItem.getChildren().add(createTree(t));
        }
        return treeItem;
    }

    private class GroupTreeCell extends TreeCell<TreeElementsGroup> {

        private final Image rootIcon = new Image(getClass().getResourceAsStream("/images/project-icon.png"));
        private final Image connectionIcon = new Image(getClass().getResourceAsStream("/images/connection-icon.png"));
        private final Image deviceIcon = new Image(getClass().getResourceAsStream("/images/device-icon.png"));
        private final Image tagIcon = new Image(getClass().getResourceAsStream("/images/tag-icon.png"));
        private final Image userIcon = new Image(getClass().getResourceAsStream("/images/user-icon.png"));
        private final Image displayIcon = new Image(getClass().getResourceAsStream("/images/display-icon.png"));

        private final ContextMenu connectionsMenu = new ContextMenu();
        private final ContextMenu devicesMenu = new ContextMenu();
        private final ContextMenu tagsMenu = new ContextMenu();
        private final ContextMenu usersMenu = new ContextMenu();
        private final ContextMenu displaysMenu = new ContextMenu();

        public GroupTreeCell() {
            connectionsMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.CONNECTION), getMenuItemToRename(), getMenuItemToRemove());
            devicesMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.DEVICE), getMenuItemToRename(), getMenuItemToRemove());
            tagsMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.TAG), getMenuItemToRename(), getMenuItemToRemove());
            usersMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.USER), getMenuItemToRename(), getMenuItemToRemove());
            displaysMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.DISPLAY), getMenuItemToRename(), getMenuItemToRemove());
        }

        @Override
        protected void updateItem(TreeElementsGroup item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if (item != null) {
                setText(item.getName());
                switch (item.getType()) {
                    case ROOT:
                        setGraphic(new ImageView(rootIcon));
                        break;
                    case CONNECTION:
                        setGraphic(new ImageView(connectionIcon));
                        if (getTreeItem().getParent() != null) setContextMenu(connectionsMenu);
                        break;
                    case DEVICE:
                        setGraphic(new ImageView(deviceIcon));
                        if (getTreeItem().getParent() != null) setContextMenu(devicesMenu);
                        break;
                    case TAG:
                        setGraphic(new ImageView(tagIcon));
                        if (getTreeItem().getParent() != null) setContextMenu(tagsMenu);
                        break;
                    case USER:
                        setGraphic(new ImageView(userIcon));
                        if (getTreeItem().getParent() != null) setContextMenu(usersMenu);
                        break;
                    case DISPLAY:
                        setGraphic(new ImageView(displayIcon));
                        if (getTreeItem().getParent() != null) setContextMenu(displaysMenu);
                        break;
                }
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
        }

        private MenuItem getMenuItemToAdd(TreeElementsGroup.Type type) {
            MenuItem addMenuItem = new MenuItem("New group");
            addMenuItem.setOnAction(event -> {
                TreeElementsGroup newTreeElementsGroup = new TreeElementsGroup();
                newTreeElementsGroup.setName("new " + type.name().toLowerCase() + " group");
                newTreeElementsGroup.setType(type);
                newTreeElementsGroup.setParent(getItem());

                getItem().getChildren().add(newTreeElementsGroup);
                getTreeItem().getChildren().add(new TreeItem<>(newTreeElementsGroup));
            });
            return addMenuItem;
        }

        private MenuItem getMenuItemToRename() {
            MenuItem renameMenuItem = new MenuItem("Rename");
            renameMenuItem.setOnAction(event -> {
                TextInputDialog newNameTextInputDialog = new TextInputDialog("");
                newNameTextInputDialog.setContentText("New name:");
                newNameTextInputDialog.setHeaderText("Replace name " + getItem().getName());
                newNameTextInputDialog.setTitle("Rename");
                newNameTextInputDialog.showAndWait().ifPresent(s -> getItem().setName(s));
                updateItem(getItem(), isEmpty());
            });
            return renameMenuItem;
        }

        private MenuItem getMenuItemToRemove() {
            MenuItem removeMenuItem = new MenuItem("Remove");
            removeMenuItem.setOnAction(event -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Deleting " + getItem() + " group of " + getItem().getParent() + ". Are you sure");
                alert.setContentText(null);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    try {
                        TreeItem parentItem = getTreeItem().getParent();
                        TreeElementsGroup parentTreeElementsGroup = getItem().getParent();

                        System.out.println("TreeElementsGroup " + parentTreeElementsGroup + " contains " + getItem() + "?:" + parentTreeElementsGroup.getChildren().contains(getItem()));


                        boolean itemRemoved = parentItem.getChildren().remove(getTreeItem());
                        boolean groupRemoved = parentTreeElementsGroup.getChildren().remove(getItem());

                        System.out.println("Remove result: " + itemRemoved + groupRemoved);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // ok np
                }
            });
            return removeMenuItem;
        }
    }

}
