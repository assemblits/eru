package com.eru.scenebuilder

import com.eru.entities.Display
import com.eru.preferences.EruPreferences
import spock.lang.Specification

import java.nio.file.Files

class SceneFxmlManagerTest extends Specification {

    def NEW_FXML_FILE_CONTENT = ''
    def localPath = ClassLoader.getSystemClassLoader().getResource('').path

    def eruPreferences = Mock(EruPreferences)
    def sceneFxmlManager = new SceneFxmlManager(eruPreferences)

    def 'should create scene fxml file with the formatted name and with the right content'() {
        given:
        eruPreferences.getApplicationDirectory() >> localPath
        when:
        def sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(new Display(1L, 'Test scene', 'group', false))
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
