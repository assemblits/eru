package com.eru.scenebuilder;

import com.eru.entities.Display;
import com.eru.exception.FxmlFileWriteException;
import com.eru.gui.Application;
import com.eru.preferences.EruPreferences;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.io.*;

import static java.io.File.separator;
import static java.lang.String.format;

@Log4j
@Component
@RequiredArgsConstructor
public class SceneFxmlManager {

    private static final String NEW_FXML_FILE_CONTENT = "";
    private static final String CHARSET_NAME = "utf-8";

    private final EruPreferences eruPreferences;

    public File createSceneFxmlFile(Display display) {
        String fxmlFilesDirectoryPath = eruPreferences.getApplicationDirectory() + separator + "displays";

        String formattedSceneName = formatName(display);
        File fxmlFilesDirectory = new File(fxmlFilesDirectoryPath);
        File fxmlFile = new File(fxmlFilesDirectory.getAbsolutePath() + separator + formattedSceneName + ".fxml");
        if (!fxmlFile.exists()) {
            log.debug(format("Creating fxml file for display '%s'", display.getName()));
            try {
                if (!fxmlFilesDirectory.exists()) {
                    fxmlFilesDirectory.mkdirs();
                }
                if (!fxmlFile.exists() && fxmlFile.createNewFile()) {
                    updateContent(fxmlFile, NEW_FXML_FILE_CONTENT);
                }

            } catch (IOException e) {
                log.error(format("Error creating fxml file for display '%s'", display.getName()));
                throw new FxmlFileWriteException(e);
            }
        }
        log.debug(format("fxml file for display '%s' was created successfully in '%s'",
                display.getName(), fxmlFile.getAbsolutePath()));
        return fxmlFile;
    }

    public void updateContent(File fxmlFile, String content) {
        log.debug(format("Updating '%s'", fxmlFile.getAbsolutePath()));
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fxmlFile), CHARSET_NAME))) {
            writer.write(content);
        } catch (IOException e) {
            log.error(format("Error updating fxml file '%s'", fxmlFile.getAbsolutePath()));
            throw new FxmlFileWriteException(e);
        }
        log.debug(format("File '%s' updated successfully", fxmlFile.getAbsolutePath()));
    }

    private String formatName(Display display) {
        return display.getName().replaceAll("\\s", "-").toLowerCase();
    }

}