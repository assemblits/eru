package org.assemblits.eru.gui.controller;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.EruGroup;
import org.assemblits.eru.gui.component.*;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class CenterPaneController  {

    @FXML
    private UsersTableView usersTableView;
    @FXML
    private ConnectionsTableView connectionsTableView;
    @FXML
    private DevicesTableView devicesTableView;
    @FXML
    private TagsTableView tagsTableView;
    @FXML
    private DisplayTableView displayTableView;
    @FXML
    private AnchorPane tablesAnchorPane;

    public void setVisibleTable(EruGroup eruGroup) {
        tablesAnchorPane.getChildren().forEach(node -> node.setVisible(false));

        switch (eruGroup.getType()) {
            case DEVICE:
                devicesTableView.setVisible(true);
                break;
            case CONNECTION:
                connectionsTableView.setVisible(true);
                break;
            case TAG:
                tagsTableView.setVisible(true);
                break;
            case USER:
                usersTableView.setVisible(true);
                break;
            case DISPLAY:
                displayTableView.setVisible(true);
                break;
            case UNKNOWN:
                break;
        }
    }
}
