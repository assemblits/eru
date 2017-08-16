package com.eru.gui;

import com.eru.comm.CommunicationsManager;
import com.eru.comm.member.ModbusDeviceCommunicator;
import com.eru.dolphin.ServerStartupService;
import com.eru.entities.Connection;
import com.eru.entities.Device;
import com.eru.entities.Project;
import com.eru.entities.TreeElementsGroup;
import com.eru.exception.FxmlFileReadException;
import com.eru.gui.about.About;
import com.eru.gui.scenebuilder.EruSceneBuilder;
import com.eru.gui.tables.*;
import com.eru.logger.LogUtil;
import com.eru.persistence.ProjectLoaderService;
import com.eru.persistence.ProjectSaverService;
import com.eru.scenebuilder.EruScene;
import com.eru.scenebuilder.SceneBuilderStarter;
import com.eru.scenebuilder.SceneFxmlManager;
import com.eru.util.DatabaseIdentifier;
import com.eru.util.EngineScriptUtil;
import com.eru.util.JpaUtil;
import com.eru.util.TagUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;


/**
 * Created by mtrujillo on 8/31/2015.
 */
@Log4j
public class App extends Application implements SceneBuilderStarter, EruMainScreenStarter {

    public static final String NAME = "eru";

    private static App singleton;
    private Project project;
    private Stage stage;
    private Skeleton skeleton;
    private EruTable table;
    private Scene eruScene;
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
        skeleton = new Skeleton();
        launchPreloader();
    }

    private void launchPreloader() {
        Preloader preloaderWindow = new Preloader();
        ProjectLoaderService pls = new ProjectLoaderService();
        preloaderWindow.getProgressBar().progressProperty().bind(pls.progressProperty());
        preloaderWindow.getStatusLabel().textProperty().bind(pls.messageProperty());
        pls.setOnSucceeded(event -> {
            project = (Project) event.getSource().getValue();
            skeleton.getUsedDatabaseText().setText(databaseIdentifier.getDatabaseProductName());
            eruScene = new Scene(skeleton, 900, 500);
            execute(Action.UPDATE_PROJECT_IN_GUI);
            displayMainEruScreen();
        });
        pls.start();
        stage.setScene(new Scene(preloaderWindow, 500, 250));
        stage.show();
    }

    private void displayMainEruScreen() {
        stage.setScene(eruScene);
        stage.show();
    }

    public void showGroup(TreeElementsGroup selectedTreeElementsGroup) {
        skeleton.getMainPane().getChildren().clear();
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
            case DISPLAY:
                this.table = new DisplayTable(this.project.getDisplays(), this);
                break;
        }
        if (this.table != null) {
            AnchorPane.setTopAnchor(table, 0.0);
            AnchorPane.setBottomAnchor(table, 0.0);
            AnchorPane.setRightAnchor(table, 0.0);
            AnchorPane.setLeftAnchor(table, 0.0);
            skeleton.getMainPane().getChildren().add(table);
            table.setTextToFilter(skeleton.getSearchTextField().textProperty());
            skeleton.getSearchTextField().setText(selectedTreeElementsGroup.getName());
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
                    skeleton.getProjectTree().setContent(project.getGroup());
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
                        // START STUFF
                        this.project.getConnections().forEach(Connection::connect);
                        this.project.getDevices().forEach(device -> device.setStatus(device.getConnection().isConnected() ? "CONNECTED" : "NOT CONNECTED"));
                        this.project.getTags().forEach(tag -> EngineScriptUtil.getInstance().loadTag(tag));
                        this.project.getTags().forEach(tag -> TagUtil.installLink(tag, project.getTags()));

                        // COMMUNICATION MANAGER
                        this.project.getDevices().stream().
                                filter(Device::getEnabled).
                                filter(device -> device.getConnection() != null).
                                forEach(enabledAndConnectedDevice -> CommunicationsManager.getInstance().getCommunicators().add(new ModbusDeviceCommunicator(enabledAndConnectedDevice)));
                        CommunicationsManager.getInstance().start();
                        this.skeleton.getLeftStatusLabel().setText("Connected");
                    } catch (Exception e) {
                        LogUtil.logger.error(e);
                    }
                    break;
                case DISCONNECT:
                    try {
                        // COMMUNICATION MANAGER
                        CommunicationsManager.getInstance().stop();

                        // STOP STUFF
                        this.project.getTags().forEach(TagUtil::removeLink);
                        this.project.getConnections().forEach(Connection::discconnect);
                        this.project.getDevices().forEach(device -> device.setStatus(device.getConnection().isConnected() ? "CONNECTED" : "NOT CONNECTED"));
                        this.skeleton.getLeftStatusLabel().setText("Disconnected");
                    } catch (InterruptedException e) {
                        LogUtil.logger.error(e);
                    }
                    break;
                case SHOW_ABOUT:
                    Stage aboutStage = new Stage();
                    aboutStage.setScene(new Scene(new About()));
                    aboutStage.showAndWait();
                    break;
                case EXIT_APP:
                    this.project.getConnections().forEach(Connection::discconnect);
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

    @Override
    public void startSceneBuilder(EruScene scene) {
        log.info(String.format("Starting scene builder for '%s'", scene.getName()));
        SceneFxmlManager sceneFxmlManager = new SceneFxmlManager();
        EruSceneBuilder eruSceneBuilder = new EruSceneBuilder(scene, sceneFxmlManager, this);
        try {
            eruSceneBuilder.init();
        } catch (FxmlFileReadException e) {
            //TODO Display dialog with error
            log.error("Error on Eru Scene Builder init", e);
        }
        Rectangle2D bounds = getScreenBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());

        stage.setScene(new Scene(eruSceneBuilder, bounds.getWidth(), bounds.getHeight()));
        stage.show();
    }

    private Rectangle2D getScreenBounds() {
        Screen screen = Screen.getPrimary();
        return screen.getVisualBounds();
    }

    @Override
    public void startEruScreen() {
        displayMainEruScreen();
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
