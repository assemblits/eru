package com.eru.gui.component;

import com.eru.entities.Display;
import com.eru.exception.FxmlFileReadException;
import com.eru.scenebuilder.ComponentsIdsGenerator;
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
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.URL;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EruSceneBuilder extends VBox {

    private final SceneFxmlManager sceneFxmlManager;
    private final CustomLibraryLoader customLibraryLoader;
    private File sceneFxmlFile;
    private EditorController editorController;
    private ChangeListener<Number> updateListener;
    private ComponentsIdsGenerator componentsIdsGenerator;

    public EruSceneBuilder(CustomLibraryLoader customLibraryLoader, SceneFxmlManager sceneFxmlManager) {
        this.customLibraryLoader = customLibraryLoader;
        this.sceneFxmlManager = sceneFxmlManager;
        componentsIdsGenerator = new ComponentsIdsGenerator();

        AnchorPane.setTopAnchor(this, 0.0);
        AnchorPane.setBottomAnchor(this, 0.0);
        AnchorPane.setRightAnchor(this, 0.0);
        AnchorPane.setLeftAnchor(this, 0.0);
        setFillWidth(true);
    }

    public void init(Display display) {
        log.debug("Instantiating Eru Scene Builder for {}", display.getName());
        sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(display);
        editorController = new EditorController();
        editorController.setLibrary(customLibraryLoader.getLibrary());

        HierarchyTreeViewController componentTree = new HierarchyTreeViewController(editorController);
        ContentPanelController canvas = new ContentPanelController(editorController);
        InspectorPanelController propertyTable = new InspectorPanelController(editorController);
        LibraryPanelController palette = new LibraryPanelController(editorController);

        setFxmlTextAndLocation();

        SplitPane leftPane = new SplitPane();
        leftPane.setOrientation(Orientation.VERTICAL);
        leftPane.getItems().addAll(palette.getPanelRoot(), componentTree.getPanelRoot());
        leftPane.setDividerPositions(0.5, 0.5);

        SplitPane.setResizableWithParent(leftPane, Boolean.FALSE);
        SplitPane.setResizableWithParent(propertyTable.getPanelRoot(), Boolean.FALSE);

        SplitPane content = new SplitPane();
        content.getItems().addAll(leftPane, canvas.getPanelRoot(), propertyTable.getPanelRoot());
        content.setDividerPositions(0.2, 0.8);
        SplitPane.setResizableWithParent(content, Boolean.TRUE);

        getChildren().add(content);
        startChangeListener();
    }

    private void setFxmlTextAndLocation() {
        try {
            URL fxmlFileUrl = sceneFxmlFile.toURI().toURL();
            String fxmlText = FXOMDocument.readContentFromURL(fxmlFileUrl);
            editorController.setFxmlTextAndLocation(fxmlText, fxmlFileUrl);
        } catch (IOException e) {
            log.error("Error trying to read '{}'", sceneFxmlFile.getAbsoluteFile());
            throw new FxmlFileReadException(e);
        }
    }

    private void startChangeListener() {
        updateListener = (observable, oldValue, newValue) -> {
            componentsIdsGenerator.generateComponentsId(editorController.getFxomDocument());
            sceneFxmlManager.updateContent(sceneFxmlFile, editorController.getFxmlText());
        };

        editorController.getJobManager().revisionProperty().addListener(updateListener);
    }
}
