package com.eru.gui;

import com.eru.comm.CommunicationsManager;
import com.eru.entities.Connection;
import com.eru.entities.Device;
import com.eru.comm.member.ModbusDeviceCommunicator;
import com.eru.dolphin.ServerStartupService;
import com.eru.gui.about.About;
import com.eru.gui.tables.*;
import com.eru.entities.TreeElementsGroup;
import com.eru.entities.Project;
import com.eru.persistence.ProjectLoaderService;
import com.eru.persistence.ProjectSaverService;
import com.eru.util.DatabaseIdentifier;
import com.eru.util.JpaUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;


/**
 * Created by mtrujillo on 8/31/2015.
 */

public class App extends Application {

    private static App singleton;
    private Project project;
    private Stage stage;
    private Skeleton skeleton;
    private EruTable table;
    private DatabaseIdentifier databaseIdentifier;

    public App() {
        App.singleton = this;
        databaseIdentifier = new DatabaseIdentifier(JpaUtil.getGlobalEntityManager());
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static App getSingleton() {
        return singleton;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.skeleton = new Skeleton();
        launchPreloader();
    }

    private void launchPreloader() {
        Preloader preloaderWindow = new Preloader();
        ProjectLoaderService pls = new ProjectLoaderService();
        preloaderWindow.getProgressBar().progressProperty().bind(pls.progressProperty());
        preloaderWindow.getStatusLabel().textProperty().bind(pls.messageProperty());
        pls.setOnSucceeded(event -> {
            this.project = (Project) event.getSource().getValue();
            this.skeleton.getUsedDatabaseText().setText(databaseIdentifier.getDatabaseProductName());
            execute(Action.UPDATE_PROJECT_IN_GUI);
            launchApp();
        });
        pls.start();
        stage.setScene(new Scene(preloaderWindow, 500, 250));
        stage.show();
    }

    private void launchApp() {
        stage.setScene(new Scene(this.skeleton, 900, 500));
        stage.show();
    }

    public void showGroup(TreeElementsGroup selectedTreeElementsGroup) {
        this.skeleton.getMainPane().getChildren().clear();
        switch (selectedTreeElementsGroup.getType()) {
            case ROOT:
                break;
            case CONNECTION:
                this.table = new ConnectionsTable(this.project.getConnections());
                break;
            case DEVICE:
                this.table = new DeviceTable(this.project.getDevices());
                break;
            case TAG:
                this.table = new TagTable(this.project.getTags());
                break;
            case USER:
                this.table = new UserTable(this.project.getUsers());
                break;
        }
        if (this.table != null) {
            AnchorPane.setTopAnchor(table, 0.0);
            AnchorPane.setBottomAnchor(table, 0.0);
            AnchorPane.setRightAnchor(table, 0.0);
            AnchorPane.setLeftAnchor(table, 0.0);
            this.skeleton.getMainPane().getChildren().add(table);
            this.table.setTextToFilter(this.skeleton.getSearchTextField().textProperty());
            this.skeleton.getSearchTextField().setText(selectedTreeElementsGroup.getName());
        }
    }

    public void execute(Action action) {
        try {
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
                    System.out.println(this.project.getDevices());
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
                case CONNECT:
                    try {
                        // Run connections
                        this.project.getConnections().forEach(Connection::connect);

                        // Activate script engine in tags TODO: Find a better place to this
//                        this.project.getTags().forEach(tag -> EngineScriptUtil.getInstance().loadTag(tag));

                        // Link tags each other
//                        this.project.getTags().forEach(tag -> TagUtil.installLink(tag, project.getTags()));

                        // Start Historian
//                        Historian.getInstance().start();

                        // Start Alarming
//                        Alarming.getInstance().start();

                        // Create ModbusDeviceCommunicators to update the device with the connections in a good way
                        this.project.getDevices().stream().
                                filter(Device::getEnabled).
                                forEach(enabledDevice ->
                                        CommunicationsManager.getInstance().
                                                getCommunicators().
                                                add(new ModbusDeviceCommunicator(enabledDevice)));

                        // Start communications with all communicators
                        CommunicationsManager.getInstance().start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case DISCONNECT:
                    try {
                        // Link tags each other
//                        this.project.getTags().forEach(TagUtil::removeLink);

                        CommunicationsManager.getInstance().stop();

                        // Stop connections
                        this.project.getConnections().forEach(Connection::discconnect);

                        // Stop Historian
//                        Historian.getInstance().stop();

                        // Stop Alarming
//                        Alarming.getInstance().stop();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case SHOW_ABOUT:
                    Stage aboutStage = new Stage();
                    aboutStage.setScene(new Scene(new About()));
                    aboutStage.showAndWait();
                    break;
                case EXIT_APP:
                    if (CommunicationsManager.getInstance().isRunning()) {
                        CommunicationsManager.getInstance().stop();
                    }

                    if (ServerStartupService.getInstance().isRunning()) {
                        ServerStartupService.getInstance().stop();
                    }

                    JpaUtil.getGlobalEntityManager().close();
                    Platform.exit();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Project getProject() {
        return project;
    }

    public enum Action {
        SHOW_GROUP,
        DELETE_GROUP,
        SAVE_TO_DB,
        UPDATE_PROJECT_IN_GUI,
        ADD_TABLE_ITEM,
        DELETE_TABLE_ITEM,
        SELECT_ALL_TABLE_ITEMS,
        UNSELECT_ALL_TABLE_ITEMS,
        CONNECT,
        DISCONNECT,
        SHOW_ABOUT,
        EXIT_APP
    }
}
