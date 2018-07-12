package org.assemblits.eru.jfx.scenebuilder

import org.assemblits.eru.entities.Display
import org.assemblits.eru.preferences.EruPreference
import org.assemblits.eru.preferences.EruPreferences
import spock.lang.Specification

import java.nio.file.Files

class SceneFxmlManagerTest extends Specification {

    def NEW_FXML_FILE_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "\n" +
            "<?import javafx.scene.layout.Pane?>\n" +
            "\n" +
            "\n" +
            "<Pane maxHeight=\"-Infinity\" maxWidth=\"-Infinity\" minHeight=\"-Infinity\" minWidth=\"-Infinity\" prefHeight=\"400.0\" prefWidth=\"600.0\" xmlns=\"http://javafx.com/javafx/8.0.151\" xmlns:fx=\"http://javafx.com/fxml/1\" />\n";
    def localPath = ClassLoader.getSystemClassLoader().getResource('').path

    def applicationDirectory = Mock(EruPreference)
    def eruPreferences = new EruPreferences()
    def sceneFxmlManager = new SceneFxmlManager(eruPreferences)

    def 'should create scene fxml file with the formatted name and with the right content'() {
        given:
        applicationDirectory.getValue() >> localPath
        eruPreferences.getApplicationDirectory() >> applicationDirectory
        when:
        def display = new Display();
        display.setId(1);
        display.setName("Test Scene")
        display.setGroupName("group")
        display.setInitialDisplay(false)
        display.setStageType(Display.StageType.REPLACE)
        def sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(display)
        then:
        sceneFxmlFile.name == 'test-scene.fxml'
        getFileContent(sceneFxmlFile) == NEW_FXML_FILE_CONTENT
    }

    def 'should update the fxml file content'() {
        given:
        def fxmlFile = new File(ClassLoader.getSystemClassLoader().getResource('scene-fxml/test-file.fxml').toURI())
        when:
        sceneFxmlManager.updateContent(fxmlFile, 'hi :)')
        then:
        getFileContent(fxmlFile) == 'hi :)\n'
    }

    def getFileContent(File sceneFxmlFile) throws IOException {
        def fileContent = new StringBuilder()
        Files.lines(sceneFxmlFile.toPath()).forEach {
            line -> fileContent.append(line).append('\n')
        }
        return fileContent.toString()
    }
}
