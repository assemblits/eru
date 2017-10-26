/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
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
