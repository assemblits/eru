package com.eru.gui.scenebuilder;

import com.eru.exception.FxmlFileReadException;
import com.eru.gui.EruMainScreenStarter;
import com.eru.scenebuilder.EruScene;
import com.eru.scenebuilder.SceneFxmlManager;
import com.eru.scenebuilder.library.CustomLibraryLoader;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.treeview.HierarchyTreeViewController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static java.lang.String.format;

@Log4j
public class EruSceneBuilder extends VBox {

    private final SceneFxmlManager sceneFxmlManager;
    private final EruScene scene;
    private final File sceneFxmlFile;
    private EditorController editorController;
    private ChangeListener<Number> updateListener;
    private EruMainScreenStarter eruMainScreenStarter;

    public EruSceneBuilder(EruScene scene, SceneFxmlManager sceneFxmlManager, EruMainScreenStarter eruMainScreenStarter) {
        log.debug(format("Instantiating Eru Scene Builder for %s", scene.getName()));
        this.scene = scene;
        this.sceneFxmlManager = sceneFxmlManager;
        this.eruMainScreenStarter = eruMainScreenStarter;
        sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(scene);
    }

    public void init() {
        editorController = new EditorController();
        editorController.setLibrary(CustomLibraryLoader.getInstance().getLibrary());

        HierarchyTreeViewController componentTree = new HierarchyTreeViewController(editorController);
        ContentPanelController canvas = new ContentPanelController(editorController);
        InspectorPanelController propertyTable = new InspectorPanelController(editorController);
        LibraryPanelController palette = new LibraryPanelController(editorController);

        setFxmlTextAndLocation();

        MenuBar menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(this.widthProperty());
        Menu fileMenu = new Menu("File");
        MenuItem goBackToEruMenu = new MenuItem("Go back to Eru");
        fileMenu.getItems().addAll(goBackToEruMenu);
        menuBar.getMenus().addAll(fileMenu);

        goBackToEruMenu.setOnAction(event -> eruMainScreenStarter.startEruScreen());

        SplitPane leftPane = new SplitPane();
        leftPane.setOrientation(Orientation.VERTICAL);
        leftPane.getItems().addAll(palette.getPanelRoot(), componentTree.getPanelRoot());
        leftPane.setDividerPositions(0.5, 0.5);

        SplitPane.setResizableWithParent(leftPane, Boolean.FALSE);
        SplitPane.setResizableWithParent(propertyTable.getPanelRoot(), Boolean.FALSE);

        SplitPane content = new SplitPane();
        content.getItems().addAll(leftPane, canvas.getPanelRoot(), propertyTable.getPanelRoot());
        content.setDividerPositions(0.21036789297658862, 0.7963210702341137);
        SplitPane.setResizableWithParent(content, Boolean.TRUE);

        this.getChildren().addAll(menuBar, content);
        startChangeListener();
    }

    private void setFxmlTextAndLocation() {
        try {
            URL fxmlFileUrl = sceneFxmlFile.toURI().toURL();
            String fxmlText = FXOMDocument.readContentFromURL(fxmlFileUrl);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlFileUrl);
        } catch (IOException e) {
            log.error(format("Error trying to read <%s>", sceneFxmlFile.getAbsoluteFile()));
            throw new FxmlFileReadException(e);
        }
    }

    private void startChangeListener() {
        updateListener = (observable, oldValue, newValue) ->
                sceneFxmlManager.updateContent(sceneFxmlFile, editorController.getFxmlText());
        editorController.getJobManager().revisionProperty().addListener(updateListener);
    }
}
