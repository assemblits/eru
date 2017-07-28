package com.marlontrujillo.eru.gui.toolbars.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

/**
 * Created by mtrujillo on 7/27/17.
 */
public class ProjectTree extends TreeView<Group> {

    public ProjectTree() {
        createTreeRoots();
    }

    private void createTreeRoots() {
        Group rootGroup        = new Group("Project" , Group.Type.ROOT);
        Group connectionsGroup = new Group("Connections", Group.Type.CONNECTION);
        Group devicesGroup     = new Group("Devices", Group.Type.DEVICE);
        Group tagsGroup        = new Group("Tags", Group.Type.TAG);
        Group userGroup        = new Group("Users", Group.Type.USER);
        rootGroup.getObservableChildren().addAll(connectionsGroup, devicesGroup, tagsGroup, userGroup);

        TreeItem<Group> root        = new TreeItem<>(rootGroup);
        TreeItem<Group> connections = new TreeItem<>(connectionsGroup);
        TreeItem<Group> devices     = new TreeItem<>(devicesGroup);
        TreeItem<Group> tags        = new TreeItem<>(tagsGroup);
        TreeItem<Group> users       = new TreeItem<>(userGroup);
        root.getChildren().addAll(connections, devices, tags, users);
        root.setExpanded(true);
        this.setRoot(root);
        this.setEditable(true);
        this.setCellFactory(c -> new GroupTreeCell());
    }

    private TreeItem<Group> createTree(Group group) {
        TreeItem<Group> treeItem = new TreeItem<>(group);
        for (Group t : group.getObservableChildren()) {
            treeItem.getChildren().add(createTree(t));
        }
        treeItem.setGraphic(new ImageView(getClass().getResource("folder.png").toExternalForm()));
        return treeItem;
    }

    public void setContent(Group content){
        this.getRoot().getChildren().add(createTree(content));
    }
}
