package com.marlontrujillo.eru.gui.toolbars.connections;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.connection.SerialConnection;
import com.marlontrujillo.eru.comm.connection.TcpConnection;
import com.marlontrujillo.eru.persistence.Container;
import groovy.lang.Closure;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by mtrujillo on 19/05/17.
 */
public class FieldConnModificationsController extends AnchorPane {

    @FXML private TextField nameTextField;
    @FXML private CheckBox enableCheckBox;
    @FXML private TextField timeoutTextField;
    @FXML private TextField samplingTimeTextField;
    @FXML private ChoiceBox<String> connectionTypeChoiceBox;
    @FXML private TextField serialPortTextField;
    @FXML private TextField bitsPerSecondTextField;
    @FXML private TextField databitsTextField;
    @FXML private ChoiceBox<String> parityChoiceBox;
    @FXML private ChoiceBox<String> stopBitsChoiceBox;
    @FXML private ChoiceBox<String> frameEncodingChoiceBox;
    @FXML private VBox specificConnectionVBox;
    @FXML private VBox tcpConnectionVBox;
    @FXML private VBox serialConnectionVBox;
    @FXML private TextField hostnameTextField;
    @FXML private TextField tcpPortTextField;

    public FieldConnModificationsController(Connection connectionToModify) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FieldConnModifications.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            registerListeners();
            transferConnectionToUI(connectionToModify);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void transferConnectionToUI(Connection connectionToModify) {
        nameTextField.setText(connectionToModify.getName());
        enableCheckBox.setSelected(connectionToModify.isEnabled());
        timeoutTextField.setText(String.valueOf(connectionToModify.getTimeout()));
        samplingTimeTextField.setText(String.valueOf(connectionToModify.getSamplingTime()));
        if(connectionToModify instanceof SerialConnection){
            SerialConnection actualConnection = (SerialConnection) connectionToModify;
            connectionTypeChoiceBox.getSelectionModel().select("Serial");
            serialPortTextField.setText(actualConnection.getPort());
            bitsPerSecondTextField.setText(String.valueOf(actualConnection.getBitsPerSeconds()));
            databitsTextField.setText(String.valueOf(actualConnection.getDataBits()));
            parityChoiceBox.getSelectionModel().select(actualConnection.getParity());
            stopBitsChoiceBox.getSelectionModel().select(Integer.toString(actualConnection.getStopsBits()));
            frameEncodingChoiceBox.getSelectionModel().select(actualConnection.getFrameEncoding());
        } else {
            connectionTypeChoiceBox.getSelectionModel().select("TCP");
            TcpConnection actualConnection = (TcpConnection) connectionToModify;
            hostnameTextField.setText(actualConnection.getHostname());
            tcpPortTextField.setText(String.valueOf(actualConnection.getPort()));
        }
    }

    private void transferUIToConnection(Connection connectionToModify){
        connectionToModify.setName(nameTextField.getText());
        connectionToModify.setEnabled(enableCheckBox.isSelected());
        connectionToModify.setTimeout(Integer.parseInt(timeoutTextField.getText()));
        connectionToModify.setSamplingTime(Integer.parseInt(samplingTimeTextField.getText()));
        if(connectionToModify instanceof SerialConnection){
            SerialConnection actualConnection = (SerialConnection) connectionToModify;
            actualConnection.setPort(serialPortTextField.getText());
            actualConnection.setBitsPerSeconds(Integer.parseInt(bitsPerSecondTextField.getText()));

            actualConnection.setDataBits(Integer.parseInt(databitsTextField.getText()));
            actualConnection.setParity(parityChoiceBox.getValue());
            actualConnection.setStopsBits(Integer.parseInt(stopBitsChoiceBox.getValue()));
            actualConnection.setFrameEncoding(frameEncodingChoiceBox.getValue());
        } else {
            TcpConnection actualConnection = (TcpConnection) connectionToModify;
            actualConnection.setHostname(hostnameTextField.getText());
            actualConnection.setPort(Integer.parseInt(tcpPortTextField.getText()));
        }
    }

    private void registerListeners() {
        connectionTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.equals("TCP")){
                specificConnectionVBox.getChildren().clear();
                specificConnectionVBox.getChildren().add(tcpConnectionVBox);
            } else {
                specificConnectionVBox.getChildren().clear();
                specificConnectionVBox.getChildren().add(serialConnectionVBox);
            }
        });
    }

    public void handleSaveButtonAction(ActionEvent actionEvent) {
        try{
            Container.getInstance().getConnectionsAgent().send(new Closure(this) {
                void doCall(ObservableList<Connection> conns) {
                    Connection connectionToSave = null;
                    for (Connection containerConn : conns) {
                        if (containerConn.getName().equals(nameTextField.getText())) {
                            if (containerConn instanceof SerialConnection && connectionTypeChoiceBox.getSelectionModel().getSelectedItem().equals("TCP")){
                                connectionToSave = new SerialConnection();
                            } else if (containerConn instanceof TcpConnection && connectionTypeChoiceBox.getSelectionModel().getSelectedItem().equals("Serial")){
                                connectionToSave = new TcpConnection();
                            } else {
                                connectionToSave = containerConn;
                            }
                            break;
                        }
                    }
                    if (connectionToSave == null) {
                        if (connectionTypeChoiceBox.getSelectionModel().getSelectedItem().equals("TCP")){
                            connectionToSave = new TcpConnection();
                            conns.add(connectionToSave);
                        } else {
                            connectionToSave = new SerialConnection();
                            conns.add(connectionToSave);
                        }
                    }
                    transferUIToConnection(connectionToSave);
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public TextField getNameTextField() {
        return nameTextField;
    }
}
