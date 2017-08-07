package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.gui.toolbars.tree.ProjectTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by mtrujillo on 8/4/17.
 */
public class Skeleton extends VBox{

    @FXML
    private ProjectTree projectTree;
    @FXML
    private AnchorPane mainPane;

    public Skeleton() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Skeleton.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML private void saveMenuItemSelected(ActionEvent event){
        App.getSingleton().execute(App.Action.SAVE_TO_DB);
    }

    @FXML private void exitMenuItemSelected(ActionEvent event){
        App.getSingleton().execute(App.Action.EXIT_APP);
    }

    @FXML private void addMenuItemSelected(ActionEvent event){
        App.getSingleton().execute(App.Action.ADD_TABLE_ITEM);
    }

    @FXML private void deleteMenuItemSelected(ActionEvent event){
        App.getSingleton().execute(App.Action.DELETE_TABLE_ITEM);
    }

    @FXML private void selectAllMenuItemSelected(ActionEvent event){
        App.getSingleton().execute(App.Action.SELECT_ALL_TABLE_ITEMS);
    }

    @FXML private void unselectAllMenuItemSelected(ActionEvent event){
        App.getSingleton().execute(App.Action.UNSELECT_ALL_TABLE_ITEMS);
    }

    public ProjectTree getProjectTree() {
        return projectTree;
    }

    public AnchorPane getMainPane() {
        return mainPane;
    }
}
