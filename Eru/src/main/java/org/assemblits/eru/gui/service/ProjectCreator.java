package org.assemblits.eru.gui.service;

import org.assemblits.eru.entities.*;

import java.util.Arrays;
import java.util.Collections;

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
