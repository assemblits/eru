package org.assemblits.eru.gui.component;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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


/*
TODO:
    - Agregar un extractor a la lista para detectar cambios de actualizacion en el ListChangeListener
    - Eliminar el public abstract TreeElementsGroup.Type getItemType(); metodo que agrega dependencia.
    - Ver si es necesario el metodo getItemsFromProjectModel
    - Refactor el metodo setProjectModel a algo mas generico como setItems<Items>
 */
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

    private void setAutoSaveFeature(){
        items.addListener((ListChangeListener<Item>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    System.out.println("Added:");
                    c.getAddedSubList().forEach(System.out::println);
                    System.out.println();
                }
                if (c.wasRemoved()) {
                    System.out.println("Removed:");
                    c.getRemoved().forEach(System.out::println);
                    System.out.println();
                }
                if (c.wasUpdated()) {
                    System.out.println("Updated:");
//                    data.subList(c.getFrom(), c.getTo()).forEach(System.out::println);
                    System.out.println();
                }
            }
        });
    }

    private void deleteSelectedItems() {
        final int CURRENT_INDEX = getSelectionModel().getSelectedIndex();
        items.removeAll(getSelectionModel().getSelectedItems());
        getSelectionModel().select(CURRENT_INDEX - 1);
    }

    public void setProjectModel(ProjectModel projectModel) {
        switch (getItemType()) {
            case ROOT:
                break;
            case CONNECTION:
                break;
            case DEVICE:
                break;
            case TAG:
                break;
            case USER:
                break;
            case DISPLAY:
                break;
        }
        items = FXCollections.observableList(getItemsFromProjectModel(projectModel));
        filteredItems = new FilteredList<>(this.items, t -> true);
        setItems(filteredItems);
        setAutoSaveFeature();
    }

    public abstract void addNewItem();

    public abstract TreeElementsGroup.Type getItemType();

    protected abstract List<Item> getItemsFromProjectModel(ProjectModel projectModel);
}
