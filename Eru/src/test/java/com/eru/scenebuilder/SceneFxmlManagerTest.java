package com.eru.scenebuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

public class SceneFxmlManagerTest {

    private static final String NEW_FXML_FILE_CONTENT = "";

    private SceneFxmlManager sceneFxmlManager;

    @Before
    public void setUp() throws Exception {
        URL resource = ClassLoader.getSystemClassLoader().getResource("");
        System.setProperty("user.home", resource.getPath());

        sceneFxmlManager = new SceneFxmlManager();
    }

    @Test
    public void createSceneFxmlFileWithTheFormattedNameAndWithTheRightContent() throws Exception {
        File sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(new MyScene());

        Assert.assertEquals("test-scene.fxml", sceneFxmlFile.getName());
        Assert.assertEquals(NEW_FXML_FILE_CONTENT, getFileContent(sceneFxmlFile));
    }

    @Test
    public void testUpdateContent() throws Exception {
        File fxmlFile = new File(ClassLoader.getSystemClassLoader().getResource("scene-fxml/test-file.fxml").toURI());
        sceneFxmlManager.updateContent(fxmlFile, "hi :)");

        Assert.assertEquals("hi :)\n", getFileContent(fxmlFile));
    }

    private String getFileContent(File sceneFxmlFile) throws IOException {
        StringBuilder fileContent = new StringBuilder();
        Files.lines(sceneFxmlFile.toPath()).forEach(s -> fileContent.append(s).append("\n"));
        return fileContent.toString();
    }

    class MyScene implements EruScene {
        @Override
        public String getName() {
            return "Test scene";
        }
    }
}