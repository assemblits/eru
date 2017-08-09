package com.marlontrujillo.eru.gui.toolbars.tables;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Created by mtrujillo on 8/7/17.
 */
public abstract class EruTable<T> extends TableView<T> {

    protected StringProperty    textToFilter;
    protected ObservableList<T> items;
    protected FilteredList<T>   filteredItems;

    EruTable(List<T> items) {
        this.items = FXCollections.observableList(items);
        this.filteredItems = new FilteredList<>(this.items, t -> true);
        this.setItems(this.filteredItems);
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
