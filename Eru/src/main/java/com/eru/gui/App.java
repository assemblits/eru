package com.eru.gui;

import com.eru.entities.TreeElementsGroup;
import com.eru.exception.FxmlFileReadException;
import com.eru.gui.menubar.MenuBar;
import com.eru.gui.scenebuilder.EruSceneBuilder;
import com.eru.gui.tables.*;
import com.eru.gui.tree.ProjectTree;
import com.eru.scenebuilder.EruScene;
import com.eru.scenebuilder.SceneBuilderStarter;
import com.eru.scenebuilder.SceneFxmlManager;
import com.eru.util.JpaUtil;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;


/**
 * Created by mtrujillo on 8/31/2015.
 */
@Log4j
public class App extends Application implements SceneBuilderStarter, EruMainScreenStarter {

    public static final String NAME = "eru";

    private Stage stage;
    private EruController eruController;
    private Skeleton skeleton;
    private MenuBar menubar;
    private ProjectTree projectTree;
    private ConnectionsTable connectionsTable;
    private DeviceTable deviceTable;
    private TagTable tagTable;
    private UserTable userTable;
    private DisplayTable displayTable;

    public App() {
        this.eruController = new EruController();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.eruController.performDBAction(EruController.DBAction.LOAD);
        this.eruController.projectProperty().addListener(observable -> startEruScreen());
        this.eruController.selectedTreeItemProperty().addListener((observable, oldValue, newValue) -> updateSkeletonCenterPane(newValue));
    }

    private void updateSkeletonCenterPane(TreeElementsGroup selectedTreeElementsGroup) {
        this.skeleton.getCenterPane().getChildren().clear();
        switch (selectedTreeElementsGroup.getType()) {
            case ROOT:
                break;
            case CONNECTION:
                this.skeleton.getCenterPane().getChildren().add(this.connectionsTable);
                break;
            case DEVICE:
                this.skeleton.getCenterPane().getChildren().add(this.deviceTable);
                break;
            case TAG:
                this.skeleton.getCenterPane().getChildren().add(this.tagTable);
                break;
            case USER:
                this.skeleton.getCenterPane().getChildren().add(this.userTable);
                break;
            case DISPLAY:
                this.skeleton.getCenterPane().getChildren().add(this.displayTable);
                break;
        }
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

    @Override
    public void startEruScreen() {
        log.info(String.format("Starting eru screen"));

        this.skeleton = new Skeleton();
        this.menubar = new MenuBar(eruController);
        this.projectTree = new ProjectTree(eruController);

        this.connectionsTable = new ConnectionsTable(eruController);
        this.connectionsTable.setTextToFilter(skeleton.getSearchTextField().textProperty());

        this.deviceTable = new DeviceTable(eruController);
        this.deviceTable.setTextToFilter(skeleton.getSearchTextField().textProperty());

        this.tagTable = new TagTable(eruController);
        this.tagTable.setTextToFilter(skeleton.getSearchTextField().textProperty());

        this.userTable = new UserTable(eruController);
        this.userTable.setTextToFilter(skeleton.getSearchTextField().textProperty());

        this.displayTable = new DisplayTable(eruController, this);
        this.displayTable.setTextToFilter(skeleton.getSearchTextField().textProperty());

        this.skeleton.getTopPane().getChildren().add(menubar);
        this.skeleton.getLeftPane().getChildren().add(projectTree);

        Rectangle2D bounds = getScreenBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());

        stage.setScene(new Scene(skeleton, bounds.getWidth(), bounds.getHeight()));
        stage.show();
    }

    private Rectangle2D getScreenBounds() {
        Screen screen = Screen.getPrimary();
        return screen.getVisualBounds();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        JpaUtil.getGlobalEntityManager().close();
    }
}
