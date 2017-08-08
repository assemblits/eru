package com.marlontrujillo.eru.gui.toolbars.tables;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;

import java.util.List;

/**
 * Created by mtrujillo on 8/7/17.
 */
public abstract class EruTable<T> extends TableView<T> {

    private StringProperty      textToFilter;
    protected FilteredList<T>   filteredList;
    private SortedList<T>       sortedList; // Implemented to solve : https://javafx-jira.kenai.com/browse/RT-32091

    EruTable(List<T> items) {
        this.filteredList   = new FilteredList<>(FXCollections.observableList(items));
        this.sortedList     = new SortedList<>(filteredList);
        this.setItems(this.sortedList);
//        this.setItems(FXCollections.observableList(items));
    }

    public abstract void addNewItem();

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

    public abstract void addListenerToFilterTable(StringProperty textToFilter);
}
