package com.marlontrujillo.eru.gui.toolbars.device;

import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.comm.device.Device;
import com.marlontrujillo.eru.persistence.Container;
import com.marlontrujillo.eru.util.FXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DeviceToolbar extends AnchorPane implements Initializable{

    /* ********** Fields ********** */
    @FXML private TableView<Device> devicesTableView;
    @FXML private Button            editDeviceButton;
    @FXML private Button            newDeviceButton;
    @FXML private Button            deleteSelectedDevButton;
    private TabPane                 externalTabPane;
    private Device                  selectedDevice;

    /* ********** Constructor ********** */
    public DeviceToolbar(TabPane externalTabPane) {
        assert externalTabPane != null;
        this.externalTabPane = externalTabPane;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DevicesToolbar.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
            registerListener();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }


    /* ********** Initialization ********** */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        devicesTableView.getColumns().get(0).prefWidthProperty().bind(devicesTableView.widthProperty().multiply(0.40));
        devicesTableView.getColumns().get(1).prefWidthProperty().bind(devicesTableView.widthProperty().multiply(0.58));

        try {
            devicesTableView.setItems(Container.getInstance().getDevicesAgent().getVal());
        } catch (Exception e){
            LogUtil.logger.error("Devices toolbar cannot get the users from the agent." , e);
        }

    }

    private void registerListener() {
        editDeviceButton.setOnAction(value -> handleUserSelection("EDIT_DEVICE"));
        newDeviceButton.setOnAction(value -> handleUserSelection("NEW_DEVICE"));
        deleteSelectedDevButton.setOnAction(value -> handleUserSelection("DELETE_DEVICE"));
        devicesTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> selectedDevice=newValue);
    }

    /* ********** Private Methods ********** */
    private void handleUserSelection(final String PROPERTY) {
        switch (PROPERTY) {
            case "EDIT_DEVICE": //TODO: Aquí hay que cambiar esto a doble click en la tabla
                if(selectedDevice == null) return;
                insertNewModificationTab(selectedDevice);
                break;
            case "NEW_DEVICE":
                insertNewModificationTab(new Device());
                break;
            case "DELETE_DEVICE":
                if(devicesTableView.isFocused()){
                    Device selectedDevice = devicesTableView.getSelectionModel().getSelectedItem();
//                    Container.getInstance().getDevicesAgent().send(new Closure(this) {
//                        void doCall(ObservableList<Device> devs) {
//                            Platform.runLater(() -> devs.remove(selectedDevice));
//                        }
//                    });
                    Container.getInstance().getDevicesAgent().getInstantVal().remove(selectedDevice);
                }
                break;
        }
    }

    private void insertNewModificationTab(Device deviceToModify) {
        Boolean tabAlreadyExists    = false;
        for (Tab tab : externalTabPane.getTabs()){
            if(tab.getId().equals(Integer.toString(deviceToModify.getId()))){
                tabAlreadyExists = true;
                externalTabPane.getSelectionModel().select(tab);
            }
        }
        if(!tabAlreadyExists){
            DeviceModifications modificationUI = new DeviceModifications(deviceToModify);
            FXUtils.injectFXMLTo(modificationUI);
            externalTabPane.getTabs().add(modificationUI);
            externalTabPane.getSelectionModel().select(modificationUI);
        }
    }
}
