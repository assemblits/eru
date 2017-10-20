package org.assemblits.eru.jfx.scenebuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Display;
import org.assemblits.eru.exception.FxmlFileWriteException;
import org.assemblits.eru.preferences.EruPreferences;
import org.springframework.stereotype.Component;

import java.io.*;

import static java.io.File.separator;

@Slf4j
@Component
@RequiredArgsConstructor
public class SceneFxmlManager {

    private static final String NEW_FXML_FILE_CONTENT = "";
    private static final String CHARSET_NAME = "utf-8";

    public File createSceneFxmlFile(Display display) {
        final EruPreferences eruPreferences = new EruPreferences();
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