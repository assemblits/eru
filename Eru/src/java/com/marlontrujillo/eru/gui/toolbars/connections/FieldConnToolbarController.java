package com.marlontrujillo.eru.gui.toolbars.connections;

import com.marlontrujillo.eru.comm.connection.Connection;
import com.marlontrujillo.eru.comm.connection.SerialConnection;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.persistence.Container;
import groovy.lang.Closure;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 09/10/2014.
 */
public class FieldConnToolbarController extends AnchorPane implements Initializable {

    @FXML private TableView<Connection> connectionsTableView;
    @FXML private Button            playButton;
    @FXML private Button            stopButton;
    @FXML private Button            setupButton;
    @FXML private Text              currentSerialStateText;
    @FXML private Text              currentTcpStateText;
    @FXML private ProgressBar       runningProgressBar;
    private TabPane                 externalTabPane;

    public FieldConnToolbarController(TabPane externalTabPane) {
        this.externalTabPane = externalTabPane;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FieldConn.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        adjustTableSize();
        fillTable();
    }

    public void deleteConnectionButtonFired(ActionEvent actionEvent) {
        try{
            Container.getInstance().getConnectionsAgent().send(new Closure(this) {
                void doCall(ObservableList<Connection> conns) {
                    Platform.runLater(() -> {
                        conns.remove(connectionsTableView.getSelectionModel().getSelectedItem());
                    });
                }
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void editConnectionButtonFired(ActionEvent actionEvent) {
        insertNewModificationTab(connectionsTableView.getSelectionModel().getSelectedItem());
    }

    public void newConnectionButtonFired(ActionEvent actionEvent) {
        insertNewModificationTab(new SerialConnection());
    }

    private void insertNewModificationTab(Connection connectionToModify) {
        Boolean tabAlreadyExists    = false;

        for (Tab tab : externalTabPane.getTabs()){
            if(tab.getId().equals(Integer.toString(connectionToModify.getId()))){
                tabAlreadyExists = true;
                externalTabPane.getSelectionModel().select(tab);
            }
        }
        if(!tabAlreadyExists){
            FieldConnModificationsController fcmc  = new FieldConnModificationsController(connectionToModify);
            Tab tab = new Tab(connectionToModify.getName());
            fcmc.getNameTextField().textProperty().addListener((observable, oldValue, newValue) -> {
                tab.setText(newValue);
            });
            tab.setId(Integer.toString(connectionToModify.getId()));
            tab.setContent(fcmc);
            externalTabPane.getTabs().add(tab);
            externalTabPane.getSelectionModel().select(tab);
        }
    }

    private void adjustTableSize(){
        connectionsTableView.getColumns().get(0).prefWidthProperty().bind(connectionsTableView.widthProperty().multiply(0.28));
        connectionsTableView.getColumns().get(1).prefWidthProperty().bind(connectionsTableView.widthProperty().multiply(0.70));
    }

    private void fillTable(){
        try {
            connectionsTableView.setItems(Container.getInstance().getConnectionsAgent().getVal());
        } catch (InterruptedException e) {
            LogUtil.logger.error(" Field connections toolbar cannot load devices from agent.", e);
        }
    }

}