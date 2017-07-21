package com.marlontrujillo.eru.gui.login;

import com.marlontrujillo.eru.dolphin.ClientStartupService;
import com.marlontrujillo.eru.util.Commands;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;

import java.io.IOException;
import java.util.List;

import static com.marlontrujillo.eru.util.DolphinConstants.*;

/**
 * Created by mtrujillo on 10/6/2015.
 */
public class Login extends AnchorPane{
    private final Stage             owner;
    @FXML private TextField         usernameTextField;
    @FXML private PasswordField     passwordTextField;
    @FXML private Button            loginButton;
    @FXML private Button            cancelButton;
    @FXML private Label             informationLabel;
    private BooleanProperty         logged;
    private ClientPresentationModel presentationModel;
    private Stage                   stage;

    public Login(Stage owner) {
        try {
            this.owner = owner;
            presentationModel     = ClientStartupService.getInstance().getClientDolphin().findPresentationModelById(USER_PM_ID);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            logged                = new SimpleBooleanProperty(this, "logged", false);
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            registerListeners();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void registerListeners() {
        cancelButton.setOnAction(event -> owner.close());
        loginButton.setOnAction(event -> ClientStartupService.getInstance().getClientDolphin().send(Commands.CHECK_USER_ENTRY, new OnFinishedHandlerAdapter(){
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                if(!logged.getValue()){
                    usernameTextField.clear();
                    passwordTextField.clear();
                    informationLabel.setVisible(true);
                    informationLabel.setText("Invalid Information.");
                    usernameTextField.requestFocus();
                }
            }
        }));
        JFXBinder.bind("text").of(usernameTextField).to(ATT_USER_NAME).of(presentationModel);
        JFXBinder.bind("text").of(passwordTextField).to(ATT_USER_PASSWORD).of(presentationModel);
        JFXBinder.bind(ATT_USER_LOGGED).of(presentationModel).to("value").of(logged);
        loginButton.disableProperty().bind(logged);
        cancelButton.disableProperty().bind(logged);
        logged.addListener(observable -> {if(isLogged()) stage.close();});
    }

    public void showIn(Stage stage){
        this.stage = stage;
        stage.setScene(new Scene(this));
        stage.show();
    }

    public boolean isLogged() {
        return logged.get();
    }
    public BooleanProperty loggedProperty() {
        return logged;
    }
}
