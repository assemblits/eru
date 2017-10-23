package org.assemblits.eru.gui.component;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class EruTableView<Item> extends TableView<Item> {

    EruTableView() {
        setDefaultContextMenu();
    }

    public abstract void addNewItem();

    public void addActionOnEditCommit(Runnable onEditCommit) {
        // To be sure the runnable will be executed after commit event finish, we add a quick delay
        int delayToRunOnEditCommit = 500;
        getColumns().forEach(column -> column.addEventHandler(TableColumn.editCommitEvent(), event -> {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(onEditCommit, delayToRunOnEditCommit, TimeUnit.MILLISECONDS);
            executor.shutdown();
        }));
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
