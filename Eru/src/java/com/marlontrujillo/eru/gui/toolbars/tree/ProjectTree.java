package com.marlontrujillo.eru.gui.toolbars.tree;

import com.marlontrujillo.eru.gui.App;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by mtrujillo on 7/27/17.
 */
public class ProjectTree extends TreeView<Group> {

    public ProjectTree() {
        this.setEditable(true);
        this.setCellFactory(c -> new GroupTreeCell());
        this.setRoot(createTree(App.getSingleton().getProject().getGroup()));
        this.getRoot().setExpanded(true);
    }

    private TreeItem<Group> createTree(Group group) {
        TreeItem<Group> treeItem = new TreeItem<>(group);
        for (Group t : group.getChildren()) {
            treeItem.getChildren().add(createTree(t));
        }
        return treeItem;
    }

}
