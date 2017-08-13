package com.eru.gui.tree;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Created by mtrujillo on 7/27/17.
 */
public class ProjectTree extends TreeView<TreeElementsGroup> {

    public ProjectTree() {
        this.setEditable(true);
        this.setCellFactory(c -> new GroupTreeCell());
    }

    public void setContent(TreeElementsGroup treeElementsGroup){
        if (this.getRoot() != null) this.getRoot().getChildren().clear();
        this.setRoot(createTree(treeElementsGroup));
        this.getRoot().setExpanded(true);
    }

    private TreeItem<TreeElementsGroup> createTree(TreeElementsGroup treeElementsGroup) {
        TreeItem<TreeElementsGroup> treeItem = new TreeItem<>(treeElementsGroup);
        for (TreeElementsGroup t : treeElementsGroup.getChildren()) {
            treeItem.getChildren().add(createTree(t));
        }
        return treeItem;
    }

}
