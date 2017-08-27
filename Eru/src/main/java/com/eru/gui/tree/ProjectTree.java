package com.eru.gui.tree;

import com.eru.entities.TreeElementsGroup;
import com.eru.gui.EruController;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;

/**
 * Created by mtrujillo on 7/27/17.
 */
public class ProjectTree extends TreeView<TreeElementsGroup> {

    private final EruController eruController;

    public ProjectTree(EruController eruController) {
        this.eruController = eruController;
        this.setEditable(true);
        this.setCellFactory(c -> {
            GroupTreeCell groupTreeCell = new GroupTreeCell();
            groupTreeCell.selectedProperty().addListener((observable, wasSelected, isSelected) ->{
                if(isSelected) eruController.setSelectedTreeItemProperty(groupTreeCell.getItem());
            });
            return groupTreeCell;
        });

        setContent(this.eruController.getProject().getGroup());
        this.eruController.projectProperty().addListener((observable, oldValue, newValue) -> setContent(newValue.getGroup()));

        // Always fit the Anchor Pane parent
        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
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
