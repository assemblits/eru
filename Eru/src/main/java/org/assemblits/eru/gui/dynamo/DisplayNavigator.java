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
package org.assemblits.eru.gui.dynamo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.jfx.scenebuilder.SceneFxmlManager;

import java.io.File;
import java.net.URL;

public class DisplayNavigator extends Button implements Dynamo {

    private Display display;

    private StringProperty displayName = new SimpleStringProperty(this, "displayName", "");

    private Stage displayStage;

    @Override
    public void fire() {
        super.fire();
        try {
            switch (display.getStageType()) {
                case REPLACE:
                    displayStage = (Stage) this.getScene().getWindow();
                    break;
                case NEW:
                    displayStage = new Stage(StageStyle.DECORATED);
                    break;
            }
            final SceneFxmlManager sceneFxmlManager = new SceneFxmlManager();
            final File sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(display);
            final URL fxmlFileUrl = sceneFxmlFile.toURI().toURL();
            final Parent displayNode = FXMLLoader.load(fxmlFileUrl);
            final Scene SCADA_SCENE = new Scene(displayNode);
            display.setFxNode(displayNode);
            displayStage.setScene(SCADA_SCENE);
            displayStage.setTitle(display.getName());
            displayStage.show();
        } catch (Exception e) {
            this.setTextFill(Color.RED);
        }
    }

    public Display getDisplay() {
        return display;
    }
    public void setDisplay(Display display) {
        this.display = display;
    }

    public String getDisplayName() {
        return displayName.get();
    }
    public StringProperty displayNameProperty() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName.set(displayName);
    }

    public Stage getDisplayStage() {
        return displayStage;
    }
    public void setDisplayStage(Stage displayStage) {
        this.displayStage = displayStage;
    }
}
