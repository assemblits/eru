package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.gui.toolbars.connections.FieldConnToolbarController;
import com.marlontrujillo.eru.gui.toolbars.historian.HistorianToolbarController;
import com.marlontrujillo.eru.gui.toolbars.device.DeviceToolbar;
import com.marlontrujillo.eru.gui.toolbars.tag.TagToolbarController;
import com.marlontrujillo.eru.gui.toolbars.user.UsersToolbarController;
import com.marlontrujillo.eru.comm.FieldBusCommunicator;
import com.marlontrujillo.eru.logger.LabelAppender;
import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.persistence.Container;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 8/31/2015.
 */
public class DesignerController implements Initializable {

    @FXML private Label   statusLabel;
    @FXML private Tab     userTab;
    @FXML private TabPane centralTabPane;
    @FXML private Tab     devicesTab;
    @FXML private Tab     tagTab;
    @FXML private Tab     fieldConnectionsTab;
    @FXML private Tab     historianTab;

    private TagToolbarController        tagToolbar;
    private DeviceToolbar               deviceToolbar;
    private UsersToolbarController      usersToolbar;
    private FieldConnToolbarController  fieldConnToolbar;
    private HistorianToolbarController  historianToolbar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Container.getInstance().getDatabaseLoadedAgent().getInstantVal()){
            tagToolbar         = new TagToolbarController(centralTabPane);
            deviceToolbar      = new DeviceToolbar(centralTabPane);
            usersToolbar       = new UsersToolbarController(centralTabPane);
            fieldConnToolbar   = new FieldConnToolbarController(centralTabPane);
            historianToolbar   = new HistorianToolbarController();
            devicesTab.setContent(deviceToolbar);
            userTab.setContent(usersToolbar);
            tagTab.setContent(tagToolbar);
            fieldConnectionsTab.setContent(fieldConnToolbar);
            historianTab.setContent(historianToolbar);
            LabelAppender.setLabel(statusLabel);
        } else {
            LogUtil.logger.error("The designer interface cannot start because the Container is not ready.");
        }
    }

    public void launchScadaButtonFired(ActionEvent actionEvent) {
        App.launchSCADA();
    }

    public void ConnectDevicesButtonFired(ActionEvent actionEvent) throws Exception {
        if (((ToggleButton) actionEvent.getSource()).isSelected()){
            FieldBusCommunicator.getInstance().start();
        } else {
            FieldBusCommunicator.getInstance().stop();
        }
    }

    public void saveButtonFired(ActionEvent actionEvent) {
        Container.getInstance().saveDatabase();
    }
}
