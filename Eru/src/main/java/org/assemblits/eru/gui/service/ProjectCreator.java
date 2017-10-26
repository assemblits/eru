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
package org.assemblits.eru.gui.service;

import org.assemblits.eru.entities.EruGroup;
import org.assemblits.eru.entities.EruType;
import org.assemblits.eru.entities.Project;

public class ProjectCreator {

    public Project defaultProject() {
        Project newProject = new Project();
        newProject.setName("Project");

        EruGroup root = new EruGroup();
        root.setName("Project");
        root.setType(EruType.UNKNOWN);

        EruGroup connections = new EruGroup();
        connections.setName("Connections");
        connections.setType(EruType.CONNECTION);
        connections.setParent(root);
        root.getChildren().add(connections);

        EruGroup devices = new EruGroup();
        devices.setName("Devices");
        devices.setType(EruType.DEVICE);
        devices.setParent(root);
        root.getChildren().add(devices);

        EruGroup tags = new EruGroup();
        tags.setName("Tags");
        tags.setType(EruType.TAG);
        tags.setParent(root);
        root.getChildren().add(tags);

        EruGroup users = new EruGroup();
        users.setName("Users");
        users.setType(EruType.USER);
        users.setParent(root);
        root.getChildren().add(users);

        EruGroup displays = new EruGroup();
        displays.setName("Displays");
        displays.setType(EruType.DISPLAY);
        displays.setParent(root);
        root.getChildren().add(displays);

        newProject.setGroup(root);

        return newProject;
    }
}
