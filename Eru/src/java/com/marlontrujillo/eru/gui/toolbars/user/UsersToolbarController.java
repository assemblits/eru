package com.marlontrujillo.eru.gui.toolbars.user;

import com.marlontrujillo.eru.persistence.Container;
import com.marlontrujillo.eru.user.User;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.util.PSVAlert;
import groovy.lang.Closure;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author mtrujillo
 */
public class UsersToolbarController extends AnchorPane implements Initializable {

    @FXML private ListView<User>    userList;
    @FXML private Button            deleteUserButton;
    @FXML private Button            editUserButton;
    @FXML private Button            newUserButton;
    private TabPane                 externalTabPane;

    /* ********** Constructor ********** */
    public UsersToolbarController(TabPane externalTabPane) {
        assert externalTabPane != null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UsersToolbar.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            this.externalTabPane = externalTabPane;
            registerListeners();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    /* ********** Initialization ********** */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            userList.setItems(Container.getInstance().getUsersAgent().getVal());
        } catch (InterruptedException e) {
            LogUtil.logger.error("User toolbar cannot get the users from the agent." , e);
        }
    }

    private void registerListeners() {
        deleteUserButton.setOnAction(value -> handleUserSelection("DELETE_USER"));
        editUserButton.setOnAction(value -> handleUserSelection("EDIT_USER"));
        newUserButton.setOnAction(value -> handleUserSelection("NEW_USER"));
    }

    /* ********** Methods ********** */
    private void handleUserSelection(final String PROPERTY) {
        switch (PROPERTY){
            case "EDIT_USER":
                if(userList.getSelectionModel().getSelectedItem() != null){
                    insertNewModificationTab(userList.getSelectionModel().getSelectedItem());
                }
                break;
            case "NEW_USER":
                User newUser = createNewUSer();
                if(newUser != null){
                    insertNewModificationTab(newUser);
                }
                break;
            case "DELETE_USER":
                final User selectedUser = userList.getSelectionModel().getSelectedItem();
                final String USERNAME    = selectedUser.getUserName();
                if (selectedUser != null) {
                    PSVAlert alert = new PSVAlert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText("The user " + USERNAME + " will be delete. Are you sure?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK){
                        userList.getItems().remove(selectedUser);
                        Container.getInstance().getUsersAgent().send(new Closure(this) {
                            void doCall(ObservableList<User> users) {
                                users.remove(selectedUser);
                            }
                        });
                    }
                }
                break;
        }
    }

    private void insertNewModificationTab(User userToModify){
        final String tabID          = userToModify.getUserName();
        Boolean tabAlreadyExists    = false;

        for (Tab tab : externalTabPane.getTabs()){
            if(tab.getText().equals(tabID)){
                tabAlreadyExists = true;
                externalTabPane.getSelectionModel().select(tab);
            }
        }
        if(!tabAlreadyExists){
            UserModificationsController modificationsController = new UserModificationsController(userToModify);
            Tab newModificationTab = new Tab(userToModify.getUserName());
            newModificationTab.setContent(modificationsController);
            newModificationTab.setOnCloseRequest(eventHandler -> {
                PSVAlert closeAlert = new PSVAlert(Alert.AlertType.CONFIRMATION);
                closeAlert.setHeaderText("Do you want to close? Press OK to close or click title bar 'X' for cancel");
                Optional<ButtonType> result = closeAlert.showAndWait();
                if (result.get() != ButtonType.OK) {
                    eventHandler.consume();
                }
            });
            externalTabPane.getTabs().add(newModificationTab);
            externalTabPane.getSelectionModel().select(newModificationTab);
        }
    }

    private User createNewUSer(){
        // Create name selection dialog pane
        Dialog<User> newUserDialog = new Dialog<>();
        newUserDialog.setTitle("New User");
        newUserDialog.setHeaderText("Please, enter a new name and press OK (or click title bar 'X' for cancel)");
        newUserDialog.setResizable(true);
        final String DEFAULT_USER_NAME = "NEW_USER - " + System.currentTimeMillis();
        Label nameLabel = new Label("Name:");
        TextField nameTextField = new TextField(DEFAULT_USER_NAME);
        GridPane grid = new GridPane();
        grid.add(nameLabel, 1, 1);
        grid.add(nameTextField, 2, 1);
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        newUserDialog.getDialogPane().setContent(grid);
        newUserDialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        newUserDialog.setResultConverter(param -> {
            if (param == buttonTypeOk) {
                return new User(nameTextField.getText());
            }
            return null;

        });

        Optional<User> result = newUserDialog.showAndWait();
        if(result.isPresent()){
            return result.get();
        } else {
            return null;
        }
    }

}
