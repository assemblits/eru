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

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Getter;
import org.assemblits.eru.entities.EruGroup;
import org.assemblits.eru.entities.EruType;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Getter
@Component
public class ProjectTreeController {

    @FXML
    private TreeView<EruGroup> projectTree;

    public void setRoot(EruGroup eruGroup){
        projectTree.setRoot(createTree(eruGroup));
        projectTree.getRoot().setExpanded(true);
    }

    public void setOnSelectedItem(Consumer<EruGroup> onSelectedItem){
        projectTree.setCellFactory(c -> {
            GroupTreeCell groupTreeCell = new GroupTreeCell();
            groupTreeCell.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
                if (isSelected) {
                    onSelectedItem.accept(groupTreeCell.getItem());
                }
            });
            return groupTreeCell;
        });
    }

    private TreeItem<EruGroup> createTree(EruGroup eruGroup) {
        TreeItem<EruGroup> treeItem = new TreeItem<>(eruGroup);
        for (EruGroup t : eruGroup.getChildren()) {
            treeItem.getChildren().add(createTree(t));
        }
        return treeItem;
    }

    private class GroupTreeCell extends TreeCell<EruGroup> {

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
            connectionsMenu.getItems().addAll(getMenuItemToAdd(EruType.CONNECTION), getMenuItemToRename(), getMenuItemToRemove());
            devicesMenu.getItems().addAll(getMenuItemToAdd(EruType.DEVICE), getMenuItemToRename(), getMenuItemToRemove());
            tagsMenu.getItems().addAll(getMenuItemToAdd(EruType.TAG), getMenuItemToRename(), getMenuItemToRemove());
            usersMenu.getItems().addAll(getMenuItemToAdd(EruType.USER), getMenuItemToRename(), getMenuItemToRemove());
            displaysMenu.getItems().addAll(getMenuItemToAdd(EruType.DISPLAY), getMenuItemToRename(), getMenuItemToRemove());
        }

        @Override
        protected void updateItem(EruGroup item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(null);
            setText(null);
            if (item != null) {
                setText(item.getName());
                switch (item.getType()) {
                    case DEVICE:
                        setGraphic(new ImageView(deviceIcon));
                        if (getTreeItem().getParent() != null) setContextMenu(devicesMenu);
                        break;
                    case CONNECTION:
                        setGraphic(new ImageView(connectionIcon));
                        if (getTreeItem().getParent() != null) setContextMenu(connectionsMenu);
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
                    case UNKNOWN:
                        setGraphic(new ImageView(rootIcon));
                        break;
                }
            }
        }

        @Override
        public void startEdit() {
            super.startEdit();
        }

        private MenuItem getMenuItemToAdd(EruType type) {
            MenuItem addMenuItem = new MenuItem("New group");
            addMenuItem.setOnAction(event -> {
                EruGroup newEruGroup = new EruGroup();
                newEruGroup.setName("new " + type + " group");
                newEruGroup.setType(type);
                newEruGroup.setParent(getItem());

                getItem().getChildren().add(newEruGroup);
                getTreeItem().getChildren().add(new TreeItem<>(newEruGroup));
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
                        EruGroup parentEruGroup = getItem().getParent();
                        parentItem.getChildren().remove(getTreeItem());
                        parentEruGroup.getChildren().remove(getItem());
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
