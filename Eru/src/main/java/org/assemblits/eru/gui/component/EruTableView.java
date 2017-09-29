package org.assemblits.eru.gui.component;

import javafx.beans.InvalidationListener;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.assemblits.eru.entities.TreeElementsGroup;
import org.assemblits.eru.gui.ApplicationContextHolder;
import org.assemblits.eru.gui.controller.SearchBarController;
import org.assemblits.eru.gui.model.ProjectModel;

import java.util.List;

public abstract class EruTableView<Item> extends TableView<Item> {

    ObservableList<Item> items;
    FilteredList<Item> filteredItems;
    ContextMenu contextMenu;

    EruTableView() {
        createContextMenu();
        addDefaultMenuItems();
        listenSearchBar();
    }

    private void createContextMenu(){
        this.contextMenu = new ContextMenu();
        addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
            if (t.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(this, t.getScreenX(), t.getScreenY());
            }
        });
    }

    private void addDefaultMenuItems(){
        MenuItem addMenuItem = new MenuItem("Add");
        addMenuItem.setOnAction(event -> addNewItem());
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> deleteSelectedItems());
        contextMenu.getItems().add(addMenuItem);
        contextMenu.getItems().add(deleteMenuItem);
    }

    private void listenSearchBar(){
        final SearchBarController searchBarController = ApplicationContextHolder.getApplicationContext().getBean(SearchBarController.class);
        final StringProperty searchBarText = searchBarController.getSearchTextField().textProperty();
        final InvalidationListener searchBarTextListener = textProperty -> {
            if (textProperty == null) return;
            filteredItems.setPredicate(item ->
                    searchBarText.getValue() == null ||
                            searchBarText.isEmpty().get() ||
                            item.toString().contains(searchBarText.getValue()));
        };

        searchBarController.getSearchTextField().textProperty().addListener(searchBarTextListener);
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

    public abstract TreeElementsGroup.Type getItemType();

    protected abstract List<Item> getItemsFromProjectModel(ProjectModel projectModel);
}
