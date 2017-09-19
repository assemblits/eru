package com.eru.gui.component;

import com.eru.entity.TreeElementsGroup;
import com.eru.entity.User;
import com.eru.gui.model.ProjectModel;
import javafx.beans.property.StringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.List;

public class UsersTableView extends EruTableView<User> {

    public UsersTableView() {
        TableColumn<User, String> groupColumn = new TableColumn<>("Group");
        TableColumn<User, String> userNameColumn = new TableColumn<>("Username");
        TableColumn<User, String> firstNameColumn = new TableColumn<>("First name");
        TableColumn<User, String> lastNameColumn = new TableColumn<>("Last name");
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
        TableColumn<User, Boolean> onlineColumn = new TableColumn<>("Online");

        groupColumn.setCellValueFactory(param -> param.getValue().groupNameProperty());
        groupColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        groupColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        userNameColumn.setCellValueFactory(param -> param.getValue().userNameProperty());
        userNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        userNameColumn.prefWidthProperty().bind(widthProperty().multiply(0.15));

        firstNameColumn.setCellValueFactory(param -> param.getValue().firstNameProperty());
        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        firstNameColumn.prefWidthProperty().bind(widthProperty().multiply(0.15));

        lastNameColumn.setCellValueFactory(param -> param.getValue().lastNameProperty());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        emailColumn.setCellValueFactory(param -> param.getValue().emailProperty());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        passwordColumn.setCellValueFactory(param -> param.getValue().passwordProperty());
        passwordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        passwordColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));

        onlineColumn.setCellValueFactory(param -> param.getValue().onlineProperty());
        onlineColumn.prefWidthProperty().bind(widthProperty().multiply(0.14));
        onlineColumn.setCellFactory(CheckBoxTableCell.forTableColumn(onlineColumn));

        getColumns().addAll(
                groupColumn,
                userNameColumn,
                firstNameColumn,
                lastNameColumn,
                emailColumn,
                passwordColumn,
                onlineColumn);

        setEditable(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @Override
    public void addNewItem() {
        User newUser = new User();
        newUser.setUserName("newUser");
        newUser.setGroupName("Users");
        items.add(newUser);
        getSelectionModel().clearSelection();
        getSelectionModel().select(newUser);

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

        // Check if a textToFilter is setted
        if (super.textToFilter != null) {
            setTextToFilter(textToFilter);
        }
        // *******************************************************************************
    }

    @Override
    public void setTextToFilter(StringProperty textToFilter) {
        textToFilter.addListener(observable ->
                filteredItems.setPredicate(user ->
                        (textToFilter.getValue() == null
                                || textToFilter.getValue().isEmpty()
                                || user.getUserName().startsWith(textToFilter.getValue())
                                || user.getGroupName().startsWith(textToFilter.getValue()))
                )
        );
    }

    @Override
    public TreeElementsGroup.Type getItemType() {
        return TreeElementsGroup.Type.USER;
    }

    @Override
    protected List<User> getItemsFromProjectModel(ProjectModel projectModel) {
        return projectModel.getUsers();
    }

}
