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
package org.assemblits.eru.gui.component;

import javafx.collections.ListChangeListener;
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

        // Add a trigger on cells edition commit
        getColumns().forEach(column -> column.addEventHandler(TableColumn.editCommitEvent(), event -> {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(onEditCommit, delayToRunOnEditCommit, TimeUnit.MILLISECONDS);
            executor.shutdown();
        }));

        // Add a trigger when a new item is added or removed
        getItems().addListener((ListChangeListener<Item>) c -> {
            while (c.next()) {
                if (c.wasPermutated()) {
                    // Nothing
                } else if (c.wasUpdated()) {
                    // Nothing
                } else { // An item was added or removed
                    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                    executor.schedule(onEditCommit, delayToRunOnEditCommit, TimeUnit.MILLISECONDS);
                    executor.shutdown();
                }
            }
        });
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
