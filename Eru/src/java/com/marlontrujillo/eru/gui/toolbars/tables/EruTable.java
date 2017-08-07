package com.marlontrujillo.eru.gui.toolbars.tables;

import javafx.scene.control.TableView;

/**
 * Created by mtrujillo on 8/7/17.
 */
public abstract class EruTable<T> extends TableView<T> {
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
}
