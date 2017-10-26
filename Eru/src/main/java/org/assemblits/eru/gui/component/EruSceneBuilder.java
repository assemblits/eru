/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.gui.component;

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
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.exception.FxmlFileReadException;
import org.assemblits.eru.jfx.scenebuilder.SceneFxmlManager;
import org.assemblits.eru.jfx.scenebuilder.library.CustomLibraryLoader;
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

    public EruSceneBuilder(CustomLibraryLoader customLibraryLoader, SceneFxmlManager sceneFxmlManager) {
        this.customLibraryLoader = customLibraryLoader;
        this.sceneFxmlManager = sceneFxmlManager;

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
            sceneFxmlManager.updateContent(sceneFxmlFile, editorController.getFxmlText());
        };

        editorController.getJobManager().revisionProperty().addListener(updateListener);
    }
}
