package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.comm.FieldBusCommunicator;
import com.marlontrujillo.eru.dolphin.ServerStartupService;
import com.marlontrujillo.eru.gui.toolbars.tables.ConnectionsTable;
import com.marlontrujillo.eru.gui.toolbars.tables.EruTable;
import com.marlontrujillo.eru.gui.toolbars.tables.UserTable;
import com.marlontrujillo.eru.gui.toolbars.tree.Group;
import com.marlontrujillo.eru.persistence.Project;
import com.marlontrujillo.eru.persistence.ProjectLoaderService;
import com.marlontrujillo.eru.persistence.ProjectSaverService;
import com.marlontrujillo.eru.util.JpaUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * Created by mtrujillo on 8/31/2015.
 *
 */

public class App extends Application {

    private static App singleton;

    public enum Action {
        SHOW_GROUP,
        DELETE_GROUP,
        SAVE_TO_DB,
        UPDATE_PROJECT_IN_GUI,
        ADD_TABLE_ITEM,
        DELETE_TABLE_ITEM,
        SELECT_ALL_TABLE_ITEMS,
        UNSELECT_ALL_TABLE_ITEMS,
        CONNECT_MODBUS,
        DISCONNECT_MODBUS,
        EXIT_APP
    }

    private Project     project;
    private Stage       stage;
    private Skeleton    skeleton;
    private EruTable    table;

    public App() {
        App.singleton = this;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.skeleton = new Skeleton();
        launchPreloader();
    }

    @Override
    public void stop() throws Exception {
        if (FieldBusCommunicator.getInstance().isStarted()){
            FieldBusCommunicator.getInstance().stop();
        }
        ServerStartupService.getInstance().stopServer();
    }

    public void launchPreloader(){
        PreloaderController preloaderWindow = new PreloaderController();
        ProjectLoaderService pls = new ProjectLoaderService();
        preloaderWindow.getProgressBar().progressProperty().bind(pls.progressProperty());
        preloaderWindow.getStatusLabel().textProperty().bind(pls.messageProperty());
        pls.setOnSucceeded(event -> {
            project = (Project) event.getSource().getValue();
            execute(Action.UPDATE_PROJECT_IN_GUI);
            launchApp();
        });
        pls.start();
        stage.setScene(new Scene(preloaderWindow, 500, 250));
        stage.show();
    }

    private void launchApp(){
        stage.setScene(new Scene(this.skeleton, 900, 500));
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

    public static App getSingleton() {
        return singleton;
    }

    public void showGroup(Group selectedGroup) {
        this.skeleton.getMainPane().getChildren().clear();
        switch (selectedGroup.getType()) {
            case ROOT:
                break;
            case CONNECTION:
                this.table = new ConnectionsTable(this.project.getConnections());
                break;
            case DEVICE:
                break;
            case TAG:
                break;
            case USER:
                this.table = new UserTable(this.project.getUsers());
                break;
        }
        AnchorPane.setTopAnchor(table, 0.0);
        AnchorPane.setBottomAnchor(table, 0.0);
        AnchorPane.setRightAnchor(table, 0.0);
        AnchorPane.setLeftAnchor(table, 0.0);
        this.skeleton.getMainPane().getChildren().add(table);
        this.table.setTextToFilter(this.skeleton.getSearchTextField().textProperty());
        this.skeleton.getSearchTextField().setText(selectedGroup.getName());
    }

    public void execute(Action action){
        try{
            switch (action) {
                case SHOW_GROUP:
                    break;
                case DELETE_GROUP:
                    break;
                case SAVE_TO_DB:
                    ProjectSaverService pss = new ProjectSaverService();
                    pss.setProject(this.project);
                    pss.setOnSucceeded(event -> {
                        project = (Project) event.getSource().getValue();
                        execute(Action.UPDATE_PROJECT_IN_GUI);
                    });
                    pss.start();
                    break;
                case UPDATE_PROJECT_IN_GUI:
                    this.skeleton.getProjectTree().setContent(project.getGroup());
                    break;
                case ADD_TABLE_ITEM:
                    if (this.table != null) this.table.addNewItem();
                    break;
                case DELETE_TABLE_ITEM:
                    if (this.table != null) this.table.deleteSelectedItems();
                    break;
                case SELECT_ALL_TABLE_ITEMS:
                    System.out.println(this.project.getUsers());
                    if (this.table != null) this.table.selectAllItems();
                    break;
                case UNSELECT_ALL_TABLE_ITEMS:
                    if (this.table != null) this.table.unselectAllItems();
                    break;
                case CONNECT_MODBUS:
                    try {
                        FieldBusCommunicator.getInstance().start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DISCONNECT_MODBUS:
                    try {
                        FieldBusCommunicator.getInstance().stop();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case EXIT_APP:
                    JpaUtil.getGlobalEntityManager().close();
                    Platform.exit();
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
