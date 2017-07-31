package com.marlontrujillo.eru.gui.toolbars.tree;

import com.marlontrujillo.eru.gui.App;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 7/27/17.
 */
public class ProjectTree extends TreeView<Group> implements Initializable {

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

        TreeItem<Group> r           = new TreeItem<>(rootGroup);
        TreeItem<Group> connections = new TreeItem<>(connectionsGroup);
        TreeItem<Group> devices     = new TreeItem<>(devicesGroup);
        TreeItem<Group> tags        = new TreeItem<>(tagsGroup);
        TreeItem<Group> users       = new TreeItem<>(userGroup);
        r.getChildren().addAll(connections, devices, tags, users);

        this.setRoot(r);
        this.getRoot().setExpanded(true);
        this.setEditable(true);
        this.setCellFactory(c -> new GroupTreeCell());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.setContent(App.getSingleton().getProject().getGroup());
    }

    private TreeItem<Group> createTree(Group group) {
        TreeItem<Group> treeItem = new TreeItem<>(group);
        for (Group t : group.getObservableChildren()) {
            treeItem.getChildren().add(createTree(t));
        }
        treeItem.setGraphic(new ImageView(getClass().getResource("folder.png").toExternalForm()));
        return treeItem;
    }

    private void setContent(Group content){
        this.getRoot().getChildren().add(createTree(content));
    }

}
