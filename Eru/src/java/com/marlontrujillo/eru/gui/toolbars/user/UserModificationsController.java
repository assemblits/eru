package com.marlontrujillo.eru.gui.toolbars.user;

import com.marlontrujillo.eru.persistence.Container;
import com.marlontrujillo.eru.user.User;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.util.PSVAlert;
import groovy.lang.Closure;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mtrujillo on 9/4/2015.
 */
public class UserModificationsController extends AnchorPane {

    @FXML private TitledPane    userSettingsTitledPane;
    @FXML private TextField     usernameTextField;
    @FXML private PasswordField passwordTextField;
    @FXML private ChoiceBox     levelChoiceBox;
    @FXML private TextField     firstNameTextField;
    @FXML private TextField     lastNameTextField;
    @FXML private TextField     emailTextField;

    public UserModificationsController(final User userToModify) {
        if(userToModify != null){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("UserModifications.fxml"));
                fxmlLoader.setRoot(this);
                fxmlLoader.setController(this);
                fxmlLoader.load();
                transferUserToUI(userToModify);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public void handleSave(ActionEvent actionEvent) {
        LogUtil.logger.info("Saving users");
        usernameTextField.setStyle("-fx-border-color:transparent");
        passwordTextField.setStyle("-fx-border-color:transparent");
        levelChoiceBox.setStyle("-fx-border-color:transparent");
        firstNameTextField.setStyle("-fx-border-color:transparent");
        lastNameTextField.setStyle("-fx-border-color:transparent");
        emailTextField.setStyle("-fx-border-color:transparent");
        if (!isUserValid(usernameTextField.getText())) {
            showInformationAlertPane("Invalid Name.");
            usernameTextField.setStyle("-fx-border-color:lightcoral");
        } else if (!isPasswordValid(passwordTextField.getText())) {
            showInformationAlertPane("Invalid Password");
            passwordTextField.setStyle("-fx-border-color:lightcoral");
        } else if (levelChoiceBox.getValue() == null) {
            showInformationAlertPane("Select a Level");
            levelChoiceBox.setStyle("-fx-border-color:lightcoral");
        } else if (!isUserValid(firstNameTextField.getText())) {
            showInformationAlertPane("Invalid First Name");
            firstNameTextField.setStyle("-fx-border-color:lightcoral");
        } else if (!isUserValid(lastNameTextField.getText())) {
            showInformationAlertPane("Invalid Last Name");
            lastNameTextField.setStyle("-fx-border-color:lightcoral");
        } else if (!isEmailValid(emailTextField.getText())) {
            showInformationAlertPane("Invalid E-mail");
            emailTextField.setStyle("-fx-border-color:lightcoral");
        } else {

            //Save in Database


            Container.getInstance().getUsersAgent().send(new Closure(this) {
                void doCall(ObservableList<User> users) {
                    // Note: The agent is a form to organize the modifications of the users, but the modifications
                    // can be done on the FX thread, so they are organized to be done sequentially
                    Platform.runLater(() -> {
                        boolean userFounded = false;
                        for (User userInContainer : users) {
                            if (userInContainer.getUserName().equals(usernameTextField.getText())) {
                                userFounded = true;
                                transferUIToUser(userInContainer);
                            }
                        }

                        if (!userFounded) {
                            User newUser = new User();
                            newUser.setUserName(usernameTextField.getText());
                            transferUIToUser(newUser);
                            users.add(newUser);
                        }
                    });
                }
            });

            userSettingsTitledPane.setDisable(true);
        }
        LogUtil.logger.info("Users saved");
    }

    private void transferUIToUser(User user){
        user.setFirstName(firstNameTextField.getText());
        user.setLastName(lastNameTextField.getText());
        user.setEmail(emailTextField.getText());
        user.setPassword(passwordTextField.getText());
        user.setOnline(false);
    }

    private void transferUserToUI(final User userToModify) {
        usernameTextField.setText(userToModify.getUserName()    == null ? "" : userToModify.getUserName());
        passwordTextField.setText(userToModify.getPassword()    == null ? "" : userToModify.getPassword());
        firstNameTextField.setText(userToModify.getFirstName()  == null ? "" : userToModify.getFirstName());
        lastNameTextField.setText(userToModify.getLastName()    == null ? "" : userToModify.getLastName());
        emailTextField.setText(userToModify.getEmail()          == null ? "" : userToModify.getEmail());
    }

    // **** USER_STRING_PATTERN **** //
    private boolean isUserValid(String num) {
        String patternForUser = "[a-zA-Z]+";
        return patternTest(num, patternForUser);
    }

    private boolean isPasswordValid(String num) {
        String patternForPassword = "[a-zA-Z0-9]+"; // The user password could be letters and numbers
        return patternTest(num, patternForPassword);
    }

    private boolean isEmailValid(String num) {
        String patternForEmail = "([a-z0-9_.-]+[@][a-z0-9]+[.][a-z0-9]+)";
        return patternTest(num, patternForEmail);
    }

    private boolean patternTest(String str, String pattern) {
        boolean isOk;
        if (str != null) {
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(str);
            isOk = m.matches();
        } else {
            isOk = false;
        }
        return isOk;
    }

    private void showInformationAlertPane(final String information){
        PSVAlert alert = new PSVAlert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Information Alert");
        alert.setContentText(information);
        alert.show();
    }
}
