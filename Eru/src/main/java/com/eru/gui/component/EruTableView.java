package com.eru.gui.component;

import com.eru.entity.TreeElementsGroup;
import com.eru.gui.model.ProjectModel;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public abstract class EruTableView<Item> extends TableView<Item> {

    protected StringProperty textToFilter;
    protected ObservableList<Item> items;
    protected FilteredList<Item> filteredItems;

    EruTableView() {
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(event -> addNewItem());
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> deleteSelectedItems());
        contextMenu.getItems().add(addMenuItem);
        contextMenu.getItems().add(deleteMenuItem);
        addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, t.getScreenX(), t.getScreenY());
            }
        });
    }

    private void deleteSelectedItems() {
        final int CURRENT_INDEX = getSelectionModel().getSelectedIndex();
        items.removeAll(getSelectionModel().getSelectedItems());
        getSelectionModel().select(CURRENT_INDEX - 1);
    }

    public void setProjectModel(ProjectModel projectModel) {
        items = FXCollections.observableList(getItemsFromProjectModel(projectModel));
        filteredItems = new FilteredList<>(this.items, t -> true);
        setItems(filteredItems);
    }

    public abstract void addNewItem();

    public abstract void setTextToFilter(StringProperty textToFilter);

    public abstract TreeElementsGroup.Type getItemType();

    protected abstract List<Item> getItemsFromProjectModel(ProjectModel projectModel);
}
