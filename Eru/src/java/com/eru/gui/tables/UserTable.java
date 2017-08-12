package com.eru.gui.tables;

import com.eru.user.User;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
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
        TableColumn<User, String> groupColumn = new TableColumn<>("TreeElementsGroup");
        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last name");
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
        TableColumn<User, Boolean> onlineColumn = new TableColumn<>("Online");

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.14));

        userNameColumn.setCellValueFactory(param -> param.getValue().userNameProperty());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

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
        newUser.setUserName("newUser");
        newUser.setGroupName("Users");
        this.items.add(newUser);
        this.getSelectionModel().clearSelection();
        this.getSelectionModel().select(newUser);

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
                this.filteredItems.setPredicate(user ->
                        (textToFilter.getValue() == null
                                || textToFilter.getValue().isEmpty()
                                || user.getUserName().startsWith(textToFilter.getValue())
                                || user.getGroupName().startsWith(textToFilter.getValue()))
                )
        );
    }

}
