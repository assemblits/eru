package com.eru.gui.tables;

import com.eru.gui.EruController;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

/**
 * Created by mtrujillo on 8/7/17.
 */
public abstract class EruTable<T> extends TableView<T> {

    protected StringProperty    textToFilter;
    protected ObservableList<T> items;
    protected FilteredList<T>   filteredItems;
    protected EruController     eruController;

    EruTable(List<T> items) {
        this.items = FXCollections.observableList(items);
        this.filteredItems = new FilteredList<>(this.items, t -> true);
        this.setItems(this.filteredItems);

        // Always fit the Anchor Pane parent
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);

        // Menu Context
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(event -> this.addNewItem());
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> deleteSelectedItems());
        contextMenu.getItems().add(addMenuItem);
        contextMenu.getItems().add(deleteMenuItem);
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if(t.getButton() == MouseButton.SECONDARY){
                contextMenu.show(this , t.getScreenX() , t.getScreenY());
            }
        });
    }

    public void deleteSelectedItems() {
        final int CURRENT_INDEX = this.getSelectionModel().getSelectedIndex();
        this.getItems().removeAll(this.getSelectionModel().getSelectedItems());
        this.getSelectionModel().select(CURRENT_INDEX -1);
    }

    public void selectAllItems() {
        this.getSelectionModel().selectAll();
    }

    public void unselectAllItems() {
        this.getSelectionModel().clearSelection();
    }

    public abstract void addNewItem();

    public abstract void setTextToFilter(StringProperty textToFilter);
}
