package com.marlontrujillo.eru.gui.toolbars.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Created by mtrujillo on 7/27/17.
 */
public class ProjectTree extends TreeView<Group> {

    public ProjectTree() {
        this.setEditable(true);
        this.setCellFactory(c -> new GroupTreeCell());
    }

    public void setContent(Group group){
        this.setRoot(createTree(group));
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
