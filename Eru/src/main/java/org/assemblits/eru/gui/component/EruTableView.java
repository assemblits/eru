package org.assemblits.eru.gui.component;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public abstract class EruTableView<Item> extends TableView<Item> {

    EruTableView() {
        setDefaultContextMenu();
    }

    public abstract void addNewItem();

    public void setOnEditCommit(Runnable onEditCommit) {
        getColumns().forEach(column -> column.setOnEditCommit(event -> onEditCommit.run()));
    }

    private void deleteSelectedItems() {
        final int CURRENT_INDEX = getSelectionModel().getSelectedIndex();
        getItems().removeAll(getSelectionModel().getSelectedItems());
        getSelectionModel().select(CURRENT_INDEX - 1);
    }

    private void setDefaultContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, t.getScreenX(), t.getScreenY());
            }
        });
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(event -> addNewItem());
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> deleteSelectedItems());
        contextMenu.getItems().add(addMenuItem);
        contextMenu.getItems().add(deleteMenuItem);
        setContextMenu(contextMenu);
    }
}
