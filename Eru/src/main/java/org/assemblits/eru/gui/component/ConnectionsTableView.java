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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javafx.util.StringConverter;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.SerialConnection;
import org.assemblits.eru.entities.TcpConnection;
import org.assemblits.eru.exception.ConnectException;

import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class ConnectionsTableView extends EruTableView<Connection> {

    public ConnectionsTableView() {
        TableColumn<Connection, Void> actionColumn = new TableColumn<>("Action");
        TableColumn<Connection, String> groupColumn = new TableColumn<>("Group");
        TableColumn<Connection, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Connection, String> typeColumn = new TableColumn<>("Type");
        TableColumn<Connection, Boolean> enabledColumn = new TableColumn<>("Enable");
        TableColumn<Connection, Integer> timeoutColumn = new TableColumn<>("Timeout");
        TableColumn<Connection, Integer> samplingColumn = new TableColumn<>("Sampling");
        TableColumn<Connection, Boolean> connectedColumn = new TableColumn<>("Connected");
        TableColumn<Connection, String> statusColumn = new TableColumn<>("Status");
        TableColumn<Connection, String> serialPortColumn = new TableColumn<>("Port");
        TableColumn<Connection, Integer> serialBitsPerSecondsColumn = new TableColumn<>("Bps");
        TableColumn<Connection, Integer> serialDatabitsColumn = new TableColumn<>("Databits");
        TableColumn<Connection, String> serialParityColumn = new TableColumn<>("Parity");
        TableColumn<Connection, Integer> serialStopBitsColumn = new TableColumn<>("Stop bits");
        TableColumn<Connection, String> serialFrameEncodingColumn = new TableColumn<>("Frame");
        TableColumn serialgroupColumn = new TableColumn<>("Serial Parameters");

        TableColumn<Connection, String> tcpHostnameColumn = new TableColumn<>("Hostname");
        TableColumn<Connection, Integer> tcpPortColumn = new TableColumn<>("Port");
        TableColumn tcpgroupColumn = new TableColumn<>("Tcp Parameters");

        // **** Columns Group **** //
        serialgroupColumn.getColumns().addAll(
                serialPortColumn,
                serialBitsPerSecondsColumn,
                serialDatabitsColumn,
                serialParityColumn,
                serialStopBitsColumn,
                serialFrameEncodingColumn);

        tcpgroupColumn.getColumns().addAll(
                tcpHostnameColumn,
                tcpPortColumn);

        // **** General Cells **** //
        actionColumn.setCellFactory(param -> new ConnectActionCell<>(
                index -> activateConnection(getItems().get(index)),
                index -> deactivateConnection(getItems().get(index))));

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        nameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        typeColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getClass().getSimpleName()));

        enabledColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        enabledColumn.setCellValueFactory((TableColumn.CellDataFeatures<Connection, Boolean> param) -> param.getValue().enabledProperty());
        enabledColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enabledColumn));

        timeoutColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        timeoutColumn.setCellValueFactory(param -> param.getValue().timeoutProperty().asObject());
        timeoutColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));

        samplingColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        samplingColumn.setCellValueFactory(param -> param.getValue().samplingTimeProperty().asObject());
        samplingColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));

        connectedColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        connectedColumn.setCellValueFactory(param -> param.getValue().connectedProperty());
        connectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(connectedColumn));

        statusColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
        statusColumn.setCellValueFactory(param -> param.getValue().statusProperty());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        serialPortColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).portProperty();
            }
            return cellValue;
        });
        serialPortColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serialPortColumn.setVisible(false);

        serialBitsPerSecondsColumn.setCellValueFactory(param -> {
            ObjectProperty<Integer> cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).bitsPerSecondsProperty().asObject();
            }
            return cellValue;
        });
        serialBitsPerSecondsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));
        serialBitsPerSecondsColumn.setVisible(false);

        serialDatabitsColumn.setCellValueFactory(param -> {
            ObjectProperty<Integer> cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).dataBitsProperty().asObject();
            }
            return cellValue;
        });
        serialDatabitsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));
        serialDatabitsColumn.setVisible(false);

        serialParityColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).parityProperty();
            }
            return cellValue;
        });
        serialParityColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serialParityColumn.setVisible(false);

        serialStopBitsColumn.setCellValueFactory(param -> {
            ObjectProperty<Integer> cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).stopsBitsProperty().asObject();
            }
            return cellValue;
        });
        serialStopBitsColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));
        serialStopBitsColumn.setVisible(false);

        serialFrameEncodingColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).frameEncodingProperty();
            }
            return cellValue;
        });
        serialFrameEncodingColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        serialFrameEncodingColumn.setVisible(false);

        // **** Tcp Cells **** //
        tcpHostnameColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof TcpConnection) {
                cellValue = ((TcpConnection) param.getValue()).hostnameProperty();
            }
            return cellValue;
        });
        tcpHostnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        tcpHostnameColumn.setVisible(false);

        tcpPortColumn.setCellValueFactory(param -> {
            ObjectProperty<Integer> cellValue = null;
            if (param.getValue() instanceof TcpConnection) {
                cellValue = ((TcpConnection) param.getValue()).portProperty().asObject();
            }
            return cellValue;
        });
        tcpPortColumn.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));
        tcpPortColumn.setVisible(false);

        this.getColumns().addAll(
                actionColumn,
                groupColumn,
                nameColumn,
                typeColumn,
                enabledColumn,
                timeoutColumn,
                samplingColumn,
                connectedColumn,
                statusColumn,
                serialgroupColumn,
                tcpgroupColumn);

        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        this.setTableMenuButtonVisible(true);
    }

    private void activateConnection(Connection connection){
        try {
            log.info("Connecting <{}>...", connection.getName());
            if (getItems().stream().anyMatch(Connection::isConnected)){
                throw new ConnectException("There are another active connection.");
            } else {
                connection.connect();
            }
        } catch (ConnectException ce) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(ce.getClass().getSimpleName());
            alert.setContentText(ce.getMessage());
            alert.show();
            log.error(ce.getMessage());
        }
    }

    private void deactivateConnection(Connection connection){
        connection.disconnect();
    }

    @Override
    public void addNewItem() {
        final String SERIAL = "Serial";
        final String TCP = "Tcp";
        ObservableList<String> choices = FXCollections.observableArrayList();
        choices.add(TCP);
        choices.add(SERIAL);

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Create new connection");

        // Set the button types.
        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        // Create the dialog pane.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField connectionNameTextField = new TextField();
        connectionNameTextField.setPromptText("Name");
        ChoiceBox<String> typeChoiceBox = new ChoiceBox<>(choices);
        typeChoiceBox.getSelectionModel().select(0);

        grid.add(new Label("Name:"), 0, 0);
        grid.add(connectionNameTextField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeChoiceBox, 1, 1);

        // Enable/Disable login button depending on whether a username was entered.
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        okButton.setDisable(true);
        connectionNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        // Set content.
        dialog.getDialogPane().setContent(grid);

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Pair<>(connectionNameTextField.getText(), typeChoiceBox.getSelectionModel().getSelectedItem());
            }
            return null;
        });

        // SHOW dialog and get result
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(results -> {
            switch (results.getValue()) {
                case SERIAL:
                    SerialConnection newSerialConnection = new SerialConnection();
                    newSerialConnection.setName(results.getKey());
                    newSerialConnection.setGroupName("Connections");
                    getItems().add(newSerialConnection);
                    this.getSelectionModel().clearSelection();
                    this.getSelectionModel().select(newSerialConnection);
                    break;
                case TCP:
                    TcpConnection newTcpConnection = new TcpConnection();
                    newTcpConnection.setName(results.getKey());
                    newTcpConnection.setGroupName("Connections");
                    getItems().add(newTcpConnection);
                    this.getSelectionModel().clearSelection();
                    this.getSelectionModel().select(newTcpConnection);
                    break;
            }
        });
    }

    public void setConnections(ObservableList<Connection> connections) {
        super.setItems(connections);
    }

    class ConnectActionCell<S> extends TableCell<S, Void> {
        private ToggleButton toggleButton;
        private ImageView connectImageView = new ImageView(new Image(getClass().getResource("/images/connect.png").toExternalForm()));
        private ImageView disconnectImageView = new ImageView(new Image(getClass().getResource("/images/disconnect.png").toExternalForm()));

        public ConnectActionCell(Consumer<Integer> selectedAction, Consumer<Integer> deselectedAction) {
            toggleButton = new ToggleButton();
            toggleButton.setGraphic(connectImageView);
            toggleButton.selectedProperty().addListener((observable, oldValue, isSelected) -> {
                if (isSelected) {
                    selectedAction.accept(getIndex());
                    toggleButton.setGraphic(disconnectImageView);
                } else {
                    deselectedAction.accept(getIndex());
                    toggleButton.setGraphic(connectImageView);
                }
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

        @Override
        public void commitEdit(Void newValue) {
            super.commitEdit(newValue);
        }
    }
}
