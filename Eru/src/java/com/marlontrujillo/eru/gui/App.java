package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.comm.FieldBusCommunicator;
import com.marlontrujillo.eru.dolphin.ServerStartupService;
import com.marlontrujillo.eru.gui.toolbars.tree.Group;
import com.marlontrujillo.eru.logger.LabelAppender;
import com.marlontrujillo.eru.persistence.Project;
import com.marlontrujillo.eru.persistence.ProjectLoaderService;
import com.marlontrujillo.eru.persistence.ProjectSaverService;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by mtrujillo on 8/31/2015.
 *
 */

public class App extends Application {

    private static App singleton;

    public enum Action {SHOW_GROUP, DELETE_GROUP, SAVE_TO_DB, UPDATE_PROJECT_IN_GUI, CONNECT_MODBUS, DISCONNECT_MODBUS}

    private Project         project;
    private Stage           stage;
    private FrameController frame;
    private StringProperty  status = new SimpleStringProperty();

    public App() {
        App.singleton = this;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        this.frame = new FrameController();
        LabelAppender.setObservableString(status);
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
        stage.setScene(new Scene(this.frame, 800, 400));
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }

    public static App getSingleton() {
        return singleton;
    }

    public void showGroup(Group item) {
        System.out.println("Project: " + project);
        System.out.println("Have to show " + item.getName() + " group of " + item.getType());
    }

    public void execute(Action action){
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
                this.frame.getProjectTree().setContent(project.getGroup());
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
        }
    }

    public String getStatus() {
        return status.get();
    }
    public StringProperty statusProperty() {
        return status;
    }

    public Project getProject() {
        return project;
    }
}
