package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.user.User;
import javafx.beans.property.StringProperty;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

/**
 * Created by mtrujillo on 8/1/17.
 */
public class UserTable extends EruTable<User> {

    public UserTable(List<User> users) {
        super(users);
        TableColumn<User, String> groupColumn = new TableColumn<>("Group");
        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last name");
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
        TableColumn<User, Boolean> onlineColumn = new TableColumn<>("Online");

        groupColumn.setCellValueFactory(param -> param.getValue().groupProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));

        userNameColumn.setCellValueFactory(param -> param.getValue().userNameProperty());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));

        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));

        lastNameColumn.setCellValueFactory(param -> param.getValue().lastNameProperty());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));

        emailColumn.setCellValueFactory(param -> param.getValue().emailProperty());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));

        passwordColumn.setCellValueFactory(param -> param.getValue().passwordProperty());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));

        onlineColumn.setCellValueFactory(param -> param.getValue().onlineProperty());
        onlineColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));
        onlineColumn.setCellFactory(CheckBoxTableCell.forTableColumn(onlineColumn));

        this.getColumns().addAll(
                groupColumn,
                userNameColumn,
                firstNameColumn,
                lastNameColumn,
                emailColumn,
                passwordColumn,
                onlineColumn);

        this.setEditable(true);
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void addNewItem() {
        User newUser = new User();
        this.getItems().add(new User());
        this.getSelectionModel().clearSelection();
        this.getSelectionModel().select(newUser);
    }

    @Override
    public void addListenerToFilterTable(StringProperty textToFilter) {
        textToFilter.addListener(observable ->
                this.filteredList.setPredicate(user ->
                        (textToFilter.getValue() == null
                                || textToFilter.getValue().isEmpty()
                                || user.getUserName().startsWith(textToFilter.getValue())
                                || user.getGroup().startsWith(textToFilter.getValue()))
                ));
    }

}
