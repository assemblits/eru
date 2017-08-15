package com.eru.scenebuilder;

import com.eru.gui.App;
import lombok.extern.log4j.Log4j;

import java.io.*;

import static java.io.File.separator;
import static java.lang.String.format;

@Log4j
public class SceneFxmlManager {

    private static final String newFxmlFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<?import javafx.scene.layout.BorderPane?>\n" +
            "\n" +
            "<fx:root fx:id=\"root\" maxHeight=\"-Infinity\" maxWidth=\"-Infinity\" minHeight=\"-Infinity\" minWidth=\"-Infinity\" prefHeight=\"250.0\" prefWidth=\"500.0\" styleClass=\"-fx-base: #444444;\" type=\"javafx.scene.layout.BorderPane\" xmlns=\"http://javafx.com/javafx/8.0.112\" xmlns:fx=\"http://javafx.com/fxml/1\">\n" +
            "</fx:root>\n";
    private static final String CHARSET_NAME = "utf-8";

    public File createSceneFxmlFile(EruScene scene) {
        File fxmlFilesDirectory = new File(System.getProperty("user.home") + separator + "." + separator
                + App.NAME + separator + "displays");
        File fxmlFile = new File(fxmlFilesDirectory.getAbsolutePath() + separator + scene.getName() + ".fxml");
        if (!fxmlFile.exists()) {
            log.debug(format("Creating fxml file for scene <%s>", scene.getName()));
            try {
                if (fxmlFilesDirectory.mkdirs() && fxmlFile.createNewFile()) {
                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(fxmlFile), CHARSET_NAME))) {
                        writer.write(newFxmlFileContent);
                    }
                }
            } catch (IOException e) {
                //TODO
                e.printStackTrace();
            }
        }
        log.debug(format("fxml file for scene <%s> was created successfully in <%s>",
                scene.getName(), fxmlFile.getAbsolutePath()));
        return fxmlFile;
    }

    public void updateContent(File fxmlFile, String content) {
        log.debug(format("Updating <%s>", fxmlFile.getAbsolutePath()));
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(fxmlFile), CHARSET_NAME))) {
            writer.write(content);
        } catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
        log.debug(format("File <%s> updated successfully", fxmlFile.getAbsolutePath()));
    }
}