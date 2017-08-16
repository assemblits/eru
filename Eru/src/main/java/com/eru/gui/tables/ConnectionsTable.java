package com.eru.gui.tables;

import com.eru.comm.connection.Connection;
import com.eru.comm.connection.SerialConnection;
import com.eru.comm.connection.TcpConnection;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

/**
 * Created by mtrujillo on 8/7/17.
 */
public class ConnectionsTable extends EruTable<Connection> {

    public ConnectionsTable(List<Connection> items) {
        super(items);

        // **** Columns **** //
        TableColumn<Connection, String> groupColumn         = new TableColumn<>("Group");
        TableColumn<Connection, String> nameColumn          = new TableColumn<>("Name");
        TableColumn<Connection, String> typeColumn          = new TableColumn<>("Type");
        TableColumn<Connection, Boolean> enabledColumn      = new TableColumn<>("Enable");
        TableColumn<Connection, Integer> timeoutColumn      = new TableColumn<>("Timeout");
        TableColumn<Connection, Integer> samplingColumn     = new TableColumn<>("Sampling");
        TableColumn<Connection, Boolean> connectedColumn    = new TableColumn<>("Connected");
        TableColumn<Connection, String> statusColumn        = new TableColumn<>("Status");

        TableColumn<Connection, String> serialPortColumn            = new TableColumn<>("Port");
        TableColumn<Connection, Integer> serialBitsPerSecondsColumn = new TableColumn<>("Bps");
        TableColumn<Connection, Integer> serialDatabitsColumn       = new TableColumn<>("Databits");
        TableColumn<Connection, String> serialParityColumn          = new TableColumn<>("Parity");
        TableColumn<Connection, Integer> serialStopBitsColumn       = new TableColumn<>("Stop bits");
        TableColumn<Connection, String> serialFrameEncodingColumn   = new TableColumn<>("Frame");
        TableColumn serialgroupColumn                               = new TableColumn<>("Serial Parameters");
        serialgroupColumn.getColumns().addAll(serialPortColumn, serialBitsPerSecondsColumn, serialDatabitsColumn, serialParityColumn, serialStopBitsColumn, serialFrameEncodingColumn);

        TableColumn<Connection, String> tcpHostnameColumn   = new TableColumn<>("Hostname");
        TableColumn<Connection, Integer> tcpPortColumn      = new TableColumn<>("Port");
        TableColumn tcpgroupColumn                          = new TableColumn<>("Tcp Parameters");
        tcpgroupColumn.getColumns().addAll(tcpHostnameColumn, tcpPortColumn);


        // **** General Cells **** //
        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.1));

        nameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        typeColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getClass().getSimpleName()));

        enabledColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        enabledColumn.setCellValueFactory(param -> param.getValue().enabledProperty());
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

        // **** Serial Cells **** //
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
                return object !=null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        }));
        tcpPortColumn.setVisible(false);

        // **** General **** //
        this.getColumns().addAll(
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
            switch (results.getValue()){
                case SERIAL:
                    SerialConnection newSerialConnection = new SerialConnection();
                    newSerialConnection.setName(results.getKey());
                    newSerialConnection.setGroupName("Connections");
                    this.items.add(newSerialConnection);
                    this.getSelectionModel().clearSelection();
                    this.getSelectionModel().select(newSerialConnection);
                    break;
                case TCP:
                    TcpConnection newTcpConnection = new TcpConnection();
                    newTcpConnection.setName(results.getKey());
                    newTcpConnection.setGroupName("Connections");
                    this.items.add(newTcpConnection);
                    this.getSelectionModel().clearSelection();
                    this.getSelectionModel().select(newTcpConnection);
                    break;
            }
        });

        // *******************************************************************************
        // Implemented to solve : https://javafx-jira.kenai.com/browse/RT-32091
        // When a new object is added to the table, a new filteredList has to be created
        // and the items updated, because the filteredList is non-editable. So, despite the
        // filtered List is setted to the tableview, a list is used in the background. The
        // filtered list is only used to be able to filter using the textToFilter.
        //
        //Wrap ObservableList into FilteredList
        super.filteredItems = new FilteredList<>(this.items);
        super.setItems(this.filteredItems);

        // Check if a textToFilter is setted
        if (super.textToFilter != null){
            setTextToFilter(textToFilter);
        }
        // *******************************************************************************
    }

    @Override
    public void setTextToFilter(StringProperty textToFilter) {
        textToFilter.addListener(observable ->
                this.filteredItems.setPredicate(connection ->
                        (textToFilter.getValue() == null
                        || textToFilter.getValue().isEmpty()
                        || connection.getName().startsWith(textToFilter.getValue())
                        || connection.getGroupName().startsWith(textToFilter.getValue()))
        ));
    }

}