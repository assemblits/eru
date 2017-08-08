package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.connection.SerialConnection;
import com.marlontrujillo.eru.comm.connection.TcpConnection;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by mtrujillo on 8/7/17.
 */
public class ConnectionsTable extends EruTable<Connection> {

    public ConnectionsTable(List<Connection> connections) {
        this.setItems(FXCollections.observableList(connections));


        // **** Columns **** //
        TableColumn<Connection, String> nameColumn          = new TableColumn<>("Name");
        TableColumn<Connection, String> typeColumn          = new TableColumn<>("Type");
        TableColumn<Connection, Boolean> enableNameColumn   = new TableColumn<>("Enable");
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
        nameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        typeColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));
        typeColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getClass().getSimpleName()));

        enableNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.05));
        enableNameColumn.setCellValueFactory(param -> param.getValue().enabledProperty());
        enableNameColumn.setCellFactory(CheckBoxTableCell.forTableColumn(enableNameColumn));

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

        statusColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));
        statusColumn.setCellValueFactory(param -> param.getValue().statusProperty());
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // **** Serial Cells **** //
        serialPortColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));
        serialPortColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).portProperty();
            }
            return cellValue;
        });
        serialPortColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        serialBitsPerSecondsColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.06));
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

        serialDatabitsColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
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

        serialParityColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        serialParityColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).parityProperty();
            }
            return cellValue;
        });
        serialParityColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        serialStopBitsColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
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

        serialFrameEncodingColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        serialFrameEncodingColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof SerialConnection) {
                cellValue = ((SerialConnection) param.getValue()).frameEncodingProperty();
            }
            return cellValue;
        });
        serialFrameEncodingColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        // **** Tcp Cells **** //
        tcpHostnameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
        tcpHostnameColumn.setCellValueFactory(param -> {
            StringProperty cellValue = null;
            if (param.getValue() instanceof TcpConnection) {
                cellValue = ((TcpConnection) param.getValue()).hostnameProperty();
            }
            return cellValue;
        });
        tcpHostnameColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        tcpPortColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.07));
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


        // **** General **** //
        this.getColumns().addAll(
                nameColumn,
                typeColumn,
                enableNameColumn,
                timeoutColumn,
                samplingColumn,
                connectedColumn,
                statusColumn,
                serialgroupColumn,
                tcpgroupColumn);

        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void addNewItem() {
        final String SERIAL = "Serial";
        final String TCP = "Tcp";
        List<String> choices = new ArrayList<>();
        choices.add(TCP);
        choices.add(SERIAL);


        ChoiceDialog<String> dialog = new ChoiceDialog<>(SERIAL, choices);
        dialog.setTitle("Type Selection");
        dialog.setContentText("Select the comm type:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(selectedType -> {
            switch (selectedType){
                case SERIAL:
                    SerialConnection newSerialConnection = new SerialConnection();
                    this.getItems().add(newSerialConnection);
                    this.getSelectionModel().clearSelection();
                    this.getSelectionModel().select(newSerialConnection);
                    break;
                case TCP:
                    TcpConnection newTcpConnection = new TcpConnection();
                    this.getItems().add(newTcpConnection);
                    this.getSelectionModel().clearSelection();
                    this.getSelectionModel().select(newTcpConnection);
                    break;
            }
        });
    }
}