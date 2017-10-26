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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Display;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Data
public class DisplayTableView extends EruTableView<Display> {

    private Consumer<Display> onDisplayPreview;
    private Consumer<Display> onDisplayEdit;

    public DisplayTableView() {
        TableColumn<Display, Void> actionColumn = new TableColumn<>("Preview");
        TableColumn<Display, String> groupColumn = new TableColumn<>("Group");
        TableColumn<Display, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Display, Display.StageType> stageTypeColumn = new TableColumn<>("Stage type");
        TableColumn<Display, Boolean> initialDisplayColumn = new TableColumn<>("Initial");

        actionColumn.setCellFactory(param -> new PreviewActionCell<>(index -> onDisplayPreview.accept(getItems().get(index))));
        actionColumn.prefWidthProperty().bind(widthProperty().multiply(0.05));

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(widthProperty().multiply(0.35));

        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.prefWidthProperty().bind(widthProperty().multiply(0.35));

        stageTypeColumn.prefWidthProperty().bind(widthProperty().multiply(0.15));
        stageTypeColumn.setCellValueFactory(param -> param.getValue().stageTypeProperty());
        stageTypeColumn.setCellFactory(ChoiceBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(Display.StageType.values())
        ));

        initialDisplayColumn.setCellValueFactory(param -> param.getValue().initialDisplayProperty());
        initialDisplayColumn.setCellFactory(CheckBoxTableCell.forTableColumn(initialDisplayColumn));
        initialDisplayColumn.prefWidthProperty().bind(widthProperty().multiply(0.05));

        getColumns().addAll(
                actionColumn,
                groupColumn,
                nameColumn,
                stageTypeColumn,
                initialDisplayColumn
        );

        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        addGraphicEditorMenuItem();
    }

    public void setDisplays(ObservableList<Display> displays) {
        setItems(displays);
    }

    private void addGraphicEditorMenuItem() {
        final MenuItem displayEditor = new MenuItem("Edit graphic");
        displayEditor.setOnAction(event -> onDisplayEdit.accept(getSelectionModel().getSelectedItem()));
        getContextMenu().getItems().add(displayEditor);
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
            getItems().add(display);
            getSelectionModel().select(display);
        });
    }

    class PreviewActionCell<S> extends TableCell<S, Void> {
        private Button toggleButton;
        private ImageView buttonImageView = new ImageView(new Image(getClass().getResource("/images/show-display.png").toExternalForm()));

        public PreviewActionCell(Consumer<Integer> onButtonPressedAction) {
            toggleButton = new Button();

            toggleButton.setGraphic(buttonImageView);
            toggleButton.setOnAction(event -> {
                log.info("launching");
                onButtonPressedAction.accept(getIndex());
            });
            setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            setGraphic(null);
            if (!empty) {
                setGraphic(toggleButton);
            }
        }
    }
}