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

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.assemblits.eru.preferences.EruPreferences;
import org.controlsfx.dialog.Wizard;
import org.controlsfx.dialog.WizardPane;

import java.io.File;

import static java.lang.String.format;

public class StartUpWizard extends Wizard {

    private static final String WELCOME_MESSAGE = "Welcome and thanks for using Eru.\n\n" +
            "Before startDirector using the application\n " +
            "we need to set up a couple of things.\n\n" +
            "Click Next to continue.";
    private static final String SETUP_DIRECTORY_PAGE_MESSAGE = "You can select the directory in which all the application\n" +
            "files are going to be stored or click Finish to use the \n" +
            "default '%s' directory.";

    private static final String SETUP_WIZARD_CSS = "views/styles/WizardPage1.css";


    private final File[] directory = new File[1];
    private final EruPreferences eruPreferences;

    public StartUpWizard(Stage stage, EruPreferences eruPreferences) {
        this.eruPreferences = eruPreferences;
        setTitle("Welcome to Eru");

        GridPane page1Grid = new GridPane();
        page1Grid.setVgap(10);
        page1Grid.setHgap(10);

        page1Grid.add(new Label(WELCOME_MESSAGE), 0, 0);

        WizardPane page1 = new WizardPane() {
            @Override
            public void onEnteringPage(Wizard wizard) {
                wizard.setTitle("Welcome to Eru");
            }
        };
        page1.getStylesheets().clear();
        page1.getStylesheets().add(SETUP_WIZARD_CSS);
        page1.setContent(page1Grid);

        GridPane page2Grid = new GridPane();
        page2Grid.setVgap(10);
        page2Grid.setHgap(10);

        page2Grid.add(new Label(format(SETUP_DIRECTORY_PAGE_MESSAGE, eruPreferences.getApplicationDirectory().getValue())),
                0, 0);
        Button button = new Button("Change directory");

        button.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select the project folder");
            directory[0] = chooser.showDialog(stage);
        });

        page2Grid.add(button, 0, 1);
        final WizardPane page2 = new WizardPane() {
            @Override
            public void onEnteringPage(Wizard wizard) {
                wizard.setTitle("Project Folder");
            }
        };
        page2.getStylesheets().clear();
        page2.getStylesheets().add(SETUP_WIZARD_CSS);
        page2.setContent(page2Grid);

        setFlow(new Wizard.LinearFlow(page1, page2));
    }

    public void startWizard() {
        showAndWait().ifPresent(result -> {
            if (result == ButtonType.FINISH) {
                if (directory[0] != null) {
                    eruPreferences.getApplicationDirectory().setValue(directory[0].getPath());
                }
                eruPreferences.getApplicationConfigured().setValue(true);
            } else if (result == ButtonType.CANCEL) {
                Platform.exit();
            }
        });
    }
}
