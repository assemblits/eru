package com.eru.gui;

import com.eru.entities.Display;
import com.eru.entities.Project;
import com.eru.entities.TreeElementsGroup;
import com.eru.gui.about.About;
import com.eru.gui.preferences.EruPreferences;
import com.eru.persistence.ProjectLoaderService;
import com.eru.persistence.ProjectSaverService;
import com.eru.scenebuilder.SceneFxmlManager;
import com.eru.util.TagLinksManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mtrujillo on 8/23/17.
 */
@Log4j
public class EruController {

    public enum ScadaAction {
        LAUNCH,
        STOP
    }

    public enum DBAction {
        LOAD,
        SAVE
    }

    public enum PopupAction {
        SHOW_PREFERENCES,
        SHOW_ABOUT
    }

    private final SimpleObjectProperty<TreeElementsGroup> selectedTreeItemProperty
            = new SimpleObjectProperty<>(null);

    private final SimpleObjectProperty<Project> project
            = new SimpleObjectProperty<>();

    private final TagLinksManager tagLinksManager = new TagLinksManager(this);

    public EruController() {
    }

    public void performScadaAction(ScadaAction scadaAction){
        switch (scadaAction) {
            case LAUNCH:
                try {
                    log.info("Launching tags.");
                    tagLinksManager.linkToConnections();
                    log.info("Launching displays.");
                    final Display mainDisplay = this.project.get().getDisplays().stream().filter(display -> display.getName().equals("Main")).findAny().get();
                    final SceneFxmlManager sceneFxmlManager = new SceneFxmlManager();
                    final File sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(mainDisplay);
                    final URL fxmlFileUrl = sceneFxmlFile.toURI().toURL();
                    final Parent mainNode = FXMLLoader.load(fxmlFileUrl);
                    final Scene SCADA_SCENE = new Scene(mainNode);
                    final Stage SCADA_STAGE = new Stage();
                    SCADA_STAGE.setScene(SCADA_SCENE);
                    SCADA_STAGE.show();
                    tagLinksManager.linkToScada(mainNode);
                } catch (Exception e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText("Error in SCADA launching process.");
                    alert.setContentText("There is no Main display created.");
                    alert.show();
                    log.error(e);
                    e.printStackTrace();
                }
                break;
            case STOP:
                tagLinksManager.unlinkFromConnections();
                break;
        }
    }

    public void performPopupAction(PopupAction popupAction){
        switch (popupAction) {
            case SHOW_PREFERENCES:
                Stage preferencesStage = new Stage();
                preferencesStage.setScene(new Scene(new EruPreferences()));
                preferencesStage.showAndWait();
                break;
            case SHOW_ABOUT:
                Stage aboutStage = new Stage();
                aboutStage.setScene(new Scene(new About()));
                aboutStage.showAndWait();
                break;
        }
    }

    public void performDBAction(DBAction dbAction){
        switch (dbAction) {
            case LOAD:
                log.info("Loading " + getProject());
                final Stage plsStage            = new Stage(StageStyle.TRANSPARENT);
                final Preloader preloaderWindow = new Preloader();
                final ProjectLoaderService pls  = new ProjectLoaderService();
                preloaderWindow.getProgressBar().progressProperty().bind(pls.progressProperty());
                preloaderWindow.getStatusLabel().textProperty().bind(pls.messageProperty());
                pls.setOnSucceeded(event -> {
                    this.project.set((Project) event.getSource().getValue());
                    plsStage.close();
                    log.info("Loaded");
                });
                pls.start();
                plsStage.setScene(new Scene(preloaderWindow));
                plsStage.show();
                break;
            case SAVE:
                log.info("Saving " + getProject());
                ProjectSaverService pss = new ProjectSaverService();
                pss.setProject(this.project.get());
                pss.setOnSucceeded(event -> {
                    this.project.set((Project) event.getSource().getValue());
                    log.info("Saved");
                });
                pss.start();
                break;
        }
    }

    public Project getProject() {
        return project.get();
    }

    public SimpleObjectProperty<Project> projectProperty() {
        return project;
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public TreeElementsGroup getSelectedTreeItemProperty() {
        return selectedTreeItemProperty.get();
    }

    public SimpleObjectProperty<TreeElementsGroup> selectedTreeItemProperty() {
        return selectedTreeItemProperty;
    }

    public void setSelectedTreeItemProperty(TreeElementsGroup selectedTreeItemProperty) {
        this.selectedTreeItemProperty.set(selectedTreeItemProperty);
    }
}
