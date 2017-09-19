package com.eru.scenebuilder

import com.eru.entities.Display
import com.eru.preferences.EruPreference
import com.eru.preferences.EruPreferences
import spock.lang.Specification

import java.nio.file.Files

class SceneFxmlManagerTest extends Specification {

    def NEW_FXML_FILE_CONTENT = ''
    def localPath = ClassLoader.getSystemClassLoader().getResource('').path

    def applicationDirectory = Mock(EruPreference)
    def eruPreferences = Mock(EruPreferences)
    def sceneFxmlManager = new SceneFxmlManager(eruPreferences)

    def 'should create scene fxml file with the formatted name and with the right content'() {
        given:
        applicationDirectory.getValue() >> localPath
        eruPreferences.getApplicationDirectory() >> applicationDirectory
        when:
        def display = new Display();
        display.setId(1L);
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
