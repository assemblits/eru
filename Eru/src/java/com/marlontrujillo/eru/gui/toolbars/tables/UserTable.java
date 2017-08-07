package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.user.User;
import javafx.collections.FXCollections;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.util.List;

/**
 * Created by mtrujillo on 8/1/17.
 */
public class UserTable extends EruTable<User> {

    public UserTable(List<User> items) {
        this.setItems(FXCollections.observableArrayList(items));
        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last name");
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
        TableColumn<User, Boolean> onlineColumn = new TableColumn<>("Online");

        userNameColumn.setCellValueFactory(param -> param.getValue().userNameProperty());
        userNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.25));    // type column size

        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
        firstNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.20));

        lastNameColumn.setCellValueFactory(param -> param.getValue().lastNameProperty());
        lastNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        emailColumn.setCellValueFactory(param -> param.getValue().emailProperty());
        emailColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        passwordColumn.setCellValueFactory(param -> param.getValue().passwordProperty());
        passwordColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        onlineColumn.setCellValueFactory(param -> param.getValue().onlineProperty());
        onlineColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
        onlineColumn.setCellFactory(CheckBoxTableCell.forTableColumn(onlineColumn));

        this.getColumns().addAll(userNameColumn, firstNameColumn, lastNameColumn, emailColumn, passwordColumn, onlineColumn);
    }

    @Override
    public void addNewItem() {
        User newUser = new User();
        this.getItems().add(new User());
        this.getSelectionModel().select(newUser);
    }

}
