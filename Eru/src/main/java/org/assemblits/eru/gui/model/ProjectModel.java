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
package org.assemblits.eru.gui.model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.assemblits.eru.entities.*;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ProjectModel {

    private ObjectProperty<Project> project;
    private IntegerProperty id;
    private StringProperty name;
    private ObjectProperty<EruGroup> group;
    private ObservableList<Device> devices;
    private ObservableList<Connection> connections;
    private ObservableList<Tag> tags;
    private ObservableList<User> users;
    private ObservableList<Display> displays;

    public void load(Project project){
        this.project = new SimpleObjectProperty<>(project);
        this.id = new SimpleIntegerProperty(project.getId());
        this.name = new SimpleStringProperty(project.getName());
        this.group = new SimpleObjectProperty<>(project.getGroup());
        this.devices = FXCollections.observableList(project.getDevices());
        this.connections = FXCollections.observableList(project.getConnections());
        this.tags = FXCollections.observableList(project.getTags());
        this.users = FXCollections.observableList(project.getUsers());
        this.displays = FXCollections.observableList(project.getDisplays());
    }

}
