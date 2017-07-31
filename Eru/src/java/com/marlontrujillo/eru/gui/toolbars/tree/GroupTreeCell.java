package com.marlontrujillo.eru.gui.toolbars.tree;

import com.marlontrujillo.eru.gui.App;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Created by mtrujillo on 7/28/17.
 */
public class GroupTreeCell extends TreeCell<Group> {

    private final Image rootIcon        = new Image(getClass().getResourceAsStream("project-icon.png"));
    private final Image connectionIcon  = new Image(getClass().getResourceAsStream("connection-icon.png"));
    private final Image deviceIcon      = new Image(getClass().getResourceAsStream("device-icon.png"));
    private final Image tagIcon         = new Image(getClass().getResourceAsStream("tag-icon.png"));
    private final Image userIcon        = new Image(getClass().getResourceAsStream("user-icon.png"));

    private final ContextMenu connectionsMenu = new ContextMenu();
    private final ContextMenu devicesMenu     = new ContextMenu();
    private final ContextMenu tagsMenu        = new ContextMenu();
    private final ContextMenu usersMenu       = new ContextMenu();

    public GroupTreeCell() {
        connectionsMenu.getItems().addAll(getMenuItemToAdd(Group.Type.CONNECTION), getMenuItemToRename());
        devicesMenu.getItems().addAll(getMenuItemToAdd(Group.Type.DEVICE), getMenuItemToRename());
        tagsMenu.getItems().addAll(getMenuItemToAdd(Group.Type.TAG), getMenuItemToRename());
        usersMenu.getItems().addAll(getMenuItemToAdd(Group.Type.USER), getMenuItemToRename());
    }

    @Override
    protected void updateItem(Group item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(null);
        setText(null);
        if (item != null){
            setText(item.getName());
            switch (item.getType()) {
                case ROOT:
                    setGraphic(new ImageView(rootIcon));
                    break;
                case CONNECTION:
                    setGraphic(new ImageView(connectionIcon));
                    if (getTreeItem().getParent()!= null) setContextMenu(connectionsMenu);
                    break;
                case DEVICE:
                    setGraphic(new ImageView(deviceIcon));
                    if (getTreeItem().getParent()!= null) setContextMenu(devicesMenu);
                    break;
                case TAG:
                    setGraphic(new ImageView(tagIcon));
                    if (getTreeItem().getParent()!= null) setContextMenu(tagsMenu);
                    break;
                case USER:
                    setGraphic(new ImageView(userIcon));
                    if (getTreeItem().getParent()!= null) setContextMenu(usersMenu);
                    break;
            }
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        App.getSingleton().showGroup(getItem());
    }

    private MenuItem getMenuItemToAdd(Group.Type type){
        MenuItem addMenuItem = new MenuItem("Add new " + type.name().toLowerCase());
        addMenuItem.setOnAction(event -> {
            Group newConnectionGroup = new Group("new " + type.name().toLowerCase(), type);
            getTreeItem().getValue().getObservableChildren().add(newConnectionGroup);
            getTreeItem().getChildren().add(new TreeItem<>(newConnectionGroup));
        });
        return addMenuItem;
    }

    private MenuItem getMenuItemToRename(){
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
}