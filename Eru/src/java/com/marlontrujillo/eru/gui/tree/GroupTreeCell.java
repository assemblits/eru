package com.marlontrujillo.eru.gui.tree;

import com.marlontrujillo.eru.gui.App;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Optional;

/**
 * Created by mtrujillo on 7/28/17.
 */
public class GroupTreeCell extends TreeCell<TreeElementsGroup> {

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
        connectionsMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.CONNECTION), getMenuItemToRename(), getMenuItemToRemove());
        devicesMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.DEVICE), getMenuItemToRename(), getMenuItemToRemove());
        tagsMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.TAG), getMenuItemToRename(), getMenuItemToRemove());
        usersMenu.getItems().addAll(getMenuItemToAdd(TreeElementsGroup.Type.USER), getMenuItemToRename(), getMenuItemToRemove());
    }

    @Override
    protected void updateItem(TreeElementsGroup item, boolean empty) {
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

    private MenuItem getMenuItemToAdd(TreeElementsGroup.Type type){
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

    private MenuItem getMenuItemToRemove(){
        MenuItem removeMenuItem = new MenuItem("Remove");
        removeMenuItem.setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Deleting " + getItem() + " group of " + getItem().getParent() + ". Are you sure");
            alert.setContentText(null);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                try{
                    TreeItem parentItem = getTreeItem().getParent();
                    TreeElementsGroup parentTreeElementsGroup = getItem().getParent();

                    System.out.println("TreeElementsGroup " + parentTreeElementsGroup + " contains " + getItem() + "?:" + parentTreeElementsGroup.getChildren().contains(getItem()));


                    boolean itemRemoved = parentItem.getChildren().remove(getTreeItem());
                    boolean groupRemoved = parentTreeElementsGroup.getChildren().remove(getItem());

                    System.out.println("Remove result: " + itemRemoved + groupRemoved);
                } catch (Exception e){
                    e.printStackTrace();
                }
            } else {
                // ok np
            }
        });
        return removeMenuItem;
    }
}