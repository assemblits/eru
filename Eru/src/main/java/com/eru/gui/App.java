package com.eru.gui;

import com.eru.entities.Display;
import com.eru.entities.TreeElementsGroup;
import com.eru.exception.FxmlFileReadException;
import com.eru.gui.menubar.MenuBar;
import com.eru.gui.scenebuilder.EruSceneBuilder;
import com.eru.gui.tables.*;
import com.eru.gui.tree.ProjectTree;
import com.eru.scenebuilder.SceneBuilderStarter;
import com.eru.scenebuilder.SceneFxmlManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static com.sun.javafx.application.LauncherImpl.launchApplication;


@Log4j
@EntityScan("com.eru")
@SpringBootApplication
@EnableJpaRepositories("com.eru")
@ComponentScan(value = "com.eru")
public class App extends Application implements SceneBuilderStarter, EruMainScreenStarter {

    public static final String NAME = "eru";
    private static String[] savedArgs;

    public enum Theme {
        DEFAULT {
            @Override
            public String toString() {
                return "prefs.theme.default";
            }
        },
        DARK {
            @Override
            public String toString() {
                return "prefs.theme.dark";
            }
        }
    }

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
    private ConfigurableApplicationContext applicationContext;

    public App() {
        this.eruController = new EruController();
    }

    public static void main(String[] args) {
        savedArgs = args;
        launchApplication(App.class, args);
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
    public void startSceneBuilder(Display display) {
        log.info(String.format("Starting display builder for '%s'", display.getName()));
        SceneFxmlManager sceneFxmlManager = new SceneFxmlManager();
        EruSceneBuilder eruSceneBuilder = new EruSceneBuilder(display, sceneFxmlManager);
        try {
            eruSceneBuilder.init();
        } catch (FxmlFileReadException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error on Eru Scene Builder initialization.");
            alert.setContentText(e.getLocalizedMessage());
            log.error(e);
        }

        this.skeleton.getCenterPane().getChildren().add(eruSceneBuilder);
    }

    @Override
    public void startEruScreen() {
        log.info("Starting eru screen");

        if(this.skeleton == null){
            this.skeleton = new Skeleton();
        }
        this.menubar = new MenuBar(eruController);
        this.projectTree = new ProjectTree(eruController);

        this.skeleton.getTopPane().getChildren().clear();
        this.skeleton.getTopPane().getChildren().add(menubar);
        this.skeleton.getLeftPane().getChildren().clear();
        this.skeleton.getLeftPane().getChildren().add(projectTree);

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

        if (!stage.isShowing()){
            stage.setScene(new Scene(skeleton));
            stage.setMaximized(true);
            stage.show();
        }
    }

    @Override
    public void init() throws Exception {
        applicationContext = SpringApplication.run(getClass(), savedArgs);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(this);
        ApplicationContextHolder.setApplicationContext(applicationContext);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
    }
}
