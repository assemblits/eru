package com.marlontrujillo.eru.gui;

import com.marlontrujillo.eru.comm.FieldBusCommunicator;
import com.marlontrujillo.eru.dolphin.ServerStartupService;
import com.marlontrujillo.eru.util.PSVAlert;
import com.marlontrujillo.eru.util.PSVStageUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Created by mtrujillo on 8/31/2015.
 *
 */

public class App extends Application {

    private File file;
    private Stage stage;
    private Scene preloaderScene;
    private Scene designerScene;
    private Scene scadaScene;
    private App   singleton;


    public App() {
        assert singleton == null;
        singleton = this;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        PSVStageUtil.setPSVDesignerTitleAndIcon(stage);
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
        PreloaderController preloaderController = new PreloaderController(PreloaderController.Type.SERVER);
        preloaderController.loadedProperty().addListener(observable -> launchDesigner());
        preloaderController.failedProperty().addListener(observable -> {
            PSVAlert alert = new PSVAlert(Alert.AlertType.ERROR);
            alert.setHeaderText("You cannot open two instances of Power Scene Viewer.");
            alert.showAndWait();
            stage.close();
        });
        preloaderController.start();
        stage.setScene(new Scene(preloaderController, 500, 250));
        stage.show();
    }

    private void launchDesigner(){
        try {
            Parent designerFrame = FXMLLoader.load(DesignerController.class.getResource("Designer.fxml"));
            designerScene = new Scene(designerFrame, 800, 400);
            stage.setScene(designerScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void launchSCADA(){
//        try {
//            if (!FieldBusCommunicator.getInstance().isStarted()) {
//                FieldBusCommunicator.getInstance().start();
//            }
//            ScadaApp scadaApp = new ScadaApp();
//            scadaApp.setConnectToLocalHost(true);
//            scadaApp.start(primaryStage);
//        } catch (Exception e) {
//            LogUtil.logger.error("Unable to launch SCADA: " + e.getMessage(), e);
//        }
    }

    public static void main(String[] args){
        launch(args);
    }

    public App getSingleton() {
        return singleton;
    }
}
