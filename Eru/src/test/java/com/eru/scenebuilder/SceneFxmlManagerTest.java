package com.eru.scenebuilder;

import com.eru.entities.Display;
import com.eru.preferences.EruPreferences;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SceneFxmlManagerTest {

    private static final String NEW_FXML_FILE_CONTENT = "";

    private SceneFxmlManager sceneFxmlManager;
    @Mock
    private EruPreferences eruPreferences;

    @Before
    public void setUp() throws Exception {
        URL resource = ClassLoader.getSystemClassLoader().getResource("");
        when(eruPreferences.getApplicationDirectory()).thenReturn(resource.getPath());

        sceneFxmlManager = new SceneFxmlManager(eruPreferences);
    }

    @Test
    public void createSceneFxmlFileWithTheFormattedNameAndWithTheRightContent() throws Exception {
        File sceneFxmlFile = sceneFxmlManager.createSceneFxmlFile(new Display(1L, "Test scene", "group", false));

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
}