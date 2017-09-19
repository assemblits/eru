package com.eru.gui.component;

import com.eru.entities.Display;
import com.eru.entities.TreeElementsGroup;
import com.eru.gui.ApplicationContextHolder;
import com.eru.gui.model.ProjectModel;
import com.eru.scenebuilder.SceneBuilderStarter;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class DisplayTable extends EruTableView<Display> {

    public DisplayTable() {
        TableColumn<Display, String> groupColumn = new TableColumn<>("Group");
        TableColumn<Display, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Display, Display.StageType> stageTypeColumn = new TableColumn<>("Stage type");
        TableColumn<Display, Boolean> initialDisplayColumn = new TableColumn<>("Initial");

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(widthProperty().multiply(0.25));

        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.prefWidthProperty().bind(widthProperty().multiply(0.25));

        stageTypeColumn.prefWidthProperty().bind(widthProperty().multiply(0.25));
        stageTypeColumn.setCellValueFactory(param -> param.getValue().stageTypeProperty());
        stageTypeColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(Display.StageType.values())
        ));

        initialDisplayColumn.setCellValueFactory(param -> param.getValue().initialDisplayProperty());
        initialDisplayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(initialDisplayColumn));
        initialDisplayColumn.prefWidthProperty().bind(widthProperty().multiply(0.25));

        getColumns().addAll(
                groupColumn,
                nameColumn,
                stageTypeColumn,
                initialDisplayColumn
                );

        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        addGraphicEditorMenuItem();
    }

    private void addGraphicEditorMenuItem() {
        final MenuItem displayEditor = new MenuItem("Edit graphic");
        final SceneBuilderStarter sceneBuilderStarter = ApplicationContextHolder.getApplicationContext().
                getBean(SceneBuilderStarter.class);
        displayEditor.setOnAction(event ->
                sceneBuilderStarter.startSceneBuilder(getSelectionModel().getSelectedItem()));
        contextMenu.getItems().add(displayEditor);
    }

    @Override
    public void addNewItem() {
        getSelectionModel().clearSelection();

        Dialog<Display> dialog = new Dialog<>();
        dialog.setTitle("Create new display");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField displayName = new TextField();
        displayName.setPromptText("Name");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(displayName, 1, 0);

        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);
        displayName.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                Display newDisplay = new Display();
                newDisplay.setGroupName("Displays");
                newDisplay.setName(displayName.getText());
                return newDisplay;
            }
            return null;
        });

        Optional<Display> result = dialog.showAndWait();


        result.ifPresent(display -> {
            items.add(display);
            getSelectionModel().select(display);
        });

        // *******************************************************************************
        // Implemented to solve : https://javafx-jira.kenai.com/browse/RT-32091
        // When a new object is added to the table, a new filteredList has to be created
        // and the items updated, because the filteredList is non-editable. So, despite the
        // filtered List is setted to the tableview, a list is used in the background. The
        // filtered list is only used to be able to filter using the textToFilter.
        //
        //Wrap ObservableList into FilteredList
        super.filteredItems = new FilteredList<>(items);
        super.setItems(filteredItems);

        if (super.textToFilter != null) {
            setTextToFilter(textToFilter);
        }
    }

    @Override
    public void setTextToFilter(StringProperty textToFilter) {
        textToFilter.addListener(observable ->
                filteredItems.setPredicate(user ->
                        (textToFilter.getValue() == null
                                || textToFilter.getValue().isEmpty()
                                || user.getName().startsWith(textToFilter.getValue())
                                || user.getGroupName().startsWith(textToFilter.getValue()))
                )
        );
    }

    @Override
    public TreeElementsGroup.Type getItemType() {
        return TreeElementsGroup.Type.DISPLAY;
    }

    @Override
    protected List<Display> getItemsFromProjectModel(ProjectModel projectModel) {
        return projectModel.getDisplays();
    }
}
