package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.comm.connection.Connection;
import javafx.collections.FXCollections;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;

import java.util.List;

/**
 * Created by mtrujillo on 8/7/17.
 */
public class ConnectionsTable extends EruTable<Connection> {
    public ConnectionsTable(List<Connection> connections) {
        this.setItems(FXCollections.observableList(connections));
        TableColumn<Connection, String> nameColumn = new TableColumn<>("Name");
        TableColumn<Connection, Boolean> enableNameColumn = new TableColumn<>("Enable");
        TableColumn<Connection, Integer> timeoutColumn = new TableColumn<>("Timeout");
        TableColumn<Connection, Integer> samplingColumn = new TableColumn<>("Sampling");
        TableColumn<Connection, Boolean> connectedColumn = new TableColumn<>("Connected");
        TableColumn<Connection, String> statusColumn = new TableColumn<>("Status");

        TableColumn<Connection, String> typeColumn = new TableColumn<>("Type");

        TableColumn<Connection, Boolean> serialPortColumn = new TableColumn<>("Port");
        TableColumn<Connection, Boolean> serialBitsPerSecondsColumn = new TableColumn<>("Bps");
        TableColumn<Connection, Boolean> serialDatabitsColumn = new TableColumn<>("Port");
        TableColumn<Connection, Boolean> serialParityColumn = new TableColumn<>("Port");
        TableColumn<Connection, Boolean> serialStopBitsColumn = new TableColumn<>("Port");
        TableColumn<Connection, Boolean> serialFrameEncodingColumn = new TableColumn<>("Port");
        TableColumn serialgroupColumn = new TableColumn<>("Port");
        serialgroupColumn.getColumns().addAll(serialPortColumn, serialBitsPerSecondsColumn, serialDatabitsColumn, serialParityColumn, serialStopBitsColumn, serialFrameEncodingColumn);

        TableColumn<Connection, Boolean> tcpHostnameColumn = new TableColumn<>("Port");
        TableColumn<Connection, Boolean> tcpportColumn = new TableColumn<>("Port");
        TableColumn tcpgroupColumn = new TableColumn<>("Tcp");
        tcpgroupColumn.getColumns().addAll(tcpHostnameColumn, tcpportColumn);

//        userNameColumn.setCellValueFactory(param -> param.getValue().userNameProperty());
//        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        userNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.25));
//
//        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
//        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        firstNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.20));
//
//        lastNameColumn.setCellValueFactory(param -> param.getValue().lastNameProperty());
//        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        lastNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
//
//        emailColumn.setCellValueFactory(param -> param.getValue().emailProperty());
//        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        emailColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
//
//        passwordColumn.setCellValueFactory(param -> param.getValue().passwordProperty());
//        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
//        passwordColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
//
//        onlineColumn.setCellValueFactory(param -> param.getValue().onlineProperty());
//        onlineColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
//        onlineColumn.setCellFactory(CheckBoxTableCell.forTableColumn(onlineColumn));
//
//        this.getColumns().addAll(userNameColumn, firstNameColumn, lastNameColumn, emailColumn, passwordColumn, onlineColumn);
        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void addNewItem() {
        User newUser = new User();
        this.getItems().add(new User());
        this.getSelectionModel().select(newUser);
    }
}
