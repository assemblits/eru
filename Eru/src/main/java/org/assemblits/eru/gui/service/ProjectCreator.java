package org.assemblits.eru.gui.service;

import org.assemblits.eru.entities.Project;
import org.assemblits.eru.entities.TreeElementsGroup;

public class ProjectCreator {

    public Project defaultProject() {
        Project newProject = new Project();
        newProject.setName("Project");

        TreeElementsGroup root = new TreeElementsGroup();
        root.setName("Project");
        root.setType(TreeElementsGroup.Type.ROOT);

        TreeElementsGroup connections = new TreeElementsGroup();
        connections.setName("Connections");
        connections.setType(TreeElementsGroup.Type.CONNECTION);
        connections.setParent(root);
        root.getChildren().add(connections);

        TreeElementsGroup devices = new TreeElementsGroup();
        devices.setName("Devices");
        devices.setType(TreeElementsGroup.Type.DEVICE);
        devices.setParent(root);
        root.getChildren().add(devices);

        TreeElementsGroup tags = new TreeElementsGroup();
        tags.setName("Tags");
        tags.setType(TreeElementsGroup.Type.TAG);
        tags.setParent(root);
        root.getChildren().add(tags);

        TreeElementsGroup users = new TreeElementsGroup();
        users.setName("Users");
        users.setType(TreeElementsGroup.Type.USER);
        users.setParent(root);
        root.getChildren().add(users);

        TreeElementsGroup displays = new TreeElementsGroup();
        displays.setName("Displays");
        displays.setType(TreeElementsGroup.Type.DISPLAY);
        displays.setParent(root);
        root.getChildren().add(displays);

        newProject.setGroup(root);

        return newProject;
    }
}
