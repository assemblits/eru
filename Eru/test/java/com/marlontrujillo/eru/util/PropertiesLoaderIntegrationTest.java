package com.marlontrujillo.eru.util;

import com.marlontrujillo.eru.exception.LoadPropertiesFileException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class PropertiesLoaderIntegrationTest {

    private PropertiesLoader propertiesLoader;

    @Before
    public void setUp() throws Exception {
        propertiesLoader = new PropertiesLoader();
    }

    @Test(expected = LoadPropertiesFileException.class)
    public void loadPropertiesThrowExceptionWhenFileDoesntExist() throws Exception {
        propertiesLoader.loadPropertiesAsMap("nonExistentFile");
    }

    @Test
    public void loadPropertiesReturnPropertiesWhenFileExist() throws Exception {
        Map<String, String> test = propertiesLoader.loadPropertiesAsMap("test");
        Assert.assertFalse(test.isEmpty());
        Assert.assertEquals("value1", test.get("key1"));
        Assert.assertEquals("value2", test.get("key2"));
    }
}