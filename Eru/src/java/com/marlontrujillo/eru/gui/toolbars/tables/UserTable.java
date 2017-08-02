package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.user.User;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Created by mtrujillo on 8/1/17.
 */
public class UserTable extends TableView<User> {

    public UserTable() {
        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last name");
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
        TableColumn<User, Boolean> onlineColumn = new TableColumn<>("Online");

        userNameColumn.setCellValueFactory(param -> param.getValue().userNameProperty());
        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(param -> param.getValue().lastNameProperty());
        emailColumn.setCellValueFactory(param -> param.getValue().emailProperty());
        passwordColumn.setCellValueFactory(param -> param.getValue().passwordProperty());
        onlineColumn.setCellValueFactory(param -> param.getValue().onlineProperty());
    }

}
