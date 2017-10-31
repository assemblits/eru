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
package org.assemblits.eru.jfx.scenebuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.exception.FxmlFileWriteException;
import org.assemblits.eru.preferences.EruPreferences;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;

import static java.io.File.separator;

@Slf4j
@Component
@RequiredArgsConstructor
public class SceneFxmlManager {

    private final EruPreferences eruPreferences;
    private static final String NEW_FXML_FILE_CONTENT = "";
    private static final String CHARSET_NAME = "utf-8";

    public File createSceneFxmlFile(Display display) {
        String fxmlFilesDirectoryPath = eruPreferences.getApplicationDirectory().getValue() + separator + "displays";

        String formattedSceneName = formatName(display);
        File fxmlFilesDirectory = new File(fxmlFilesDirectoryPath);
        File fxmlFile = new File(fxmlFilesDirectory.getAbsolutePath() + separator + formattedSceneName + ".fxml");
        if (!fxmlFile.exists()) {
            log.debug("Creating fxml file for display '{}'", display.getName());
            try {
                if (!fxmlFilesDirectory.exists()) {
                    fxmlFilesDirectory.mkdirs();
                }
                if (!fxmlFile.exists() && fxmlFile.createNewFile()) {
                    updateContent(fxmlFile, NEW_FXML_FILE_CONTENT);
                }

            } catch (IOException e) {
                log.error("Error creating fxml file for display '{}'", display.getName());
                throw new FxmlFileWriteException(e);
            }
        }
        log.debug("fxml file for display '{}' was created successfully in '{}'",
                display.getName(), fxmlFile.getAbsolutePath());
        return fxmlFile;
    }

    public void updateContent(File fxmlFile, String content) {
        log.debug("Updating '{}'", fxmlFile.getAbsolutePath());
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fxmlFile), CHARSET_NAME))) {
            writer.write(content);
        } catch (IOException e) {
            log.error("Error updating fxml file '{}'", fxmlFile.getAbsolutePath());
            throw new FxmlFileWriteException(e);
        }
        log.debug("File '{}' updated successfully", fxmlFile.getAbsolutePath());
    }

    private String formatName(Display display) {
        return display.getName().replaceAll("\\s", "-").toLowerCase();
    }

}