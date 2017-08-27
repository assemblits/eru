package com.eru.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class SceneBuilderUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void isNotJavaFxComponentWithNullParam() throws Exception {
        SceneBuilderUtil.isNotJavaFxComponent(null);
    }

    @Test
    public void isNotJavaFxComponentWithJavaPackage() throws Exception {
        boolean result = SceneBuilderUtil.isNotJavaFxComponent("java.component");
        Assert.assertFalse(result);
    }

    @Test
    public void isNotJavaFxComponentWithJavaxPackage() throws Exception {
        boolean result = SceneBuilderUtil.isNotJavaFxComponent("javax.component");
        Assert.assertFalse(result);
    }

    @Test
    public void isNotJavaFxComponentWithJavaFxPackage() throws Exception {
        boolean result = SceneBuilderUtil.isNotJavaFxComponent("javafx.component");
        Assert.assertFalse(result);
    }

    @Test
    public void isNotJavaFxComponentWithJavaFxSceneBuilderPackage() throws Exception {
        boolean result = SceneBuilderUtil.isNotJavaFxComponent("com.oracle.javafx.scenebuilder.component");
        Assert.assertFalse(result);
    }

    @Test
    public void isNotJavaFxComponentWithComJavaFxSceneBuilderPackage() throws Exception {
        boolean result = SceneBuilderUtil.isNotJavaFxComponent("com.javafx.component");
        Assert.assertFalse(result);
    }

    @Test
    public void isNotJavaFxComponentWithValidPackage() throws Exception {
        boolean result = SceneBuilderUtil.isNotJavaFxComponent("com.eru.control");
        Assert.assertTrue(result);
    }

    @Test
    public void getSimpleNameFromClassName() throws Exception {
        String name = SceneBuilderUtil.getSimpleNameFromClassName("com.test.Name");
        Assert.assertEquals("Name", name);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSimpleNameFromClassNameWithNullParam() throws Exception {
        SceneBuilderUtil.getSimpleNameFromClassName(null);
    }

    @Test
    public void makeClassNameFromCompiledClassNameWithInvalidClassName() throws Exception {
        Optional<String> className = SceneBuilderUtil.makeClassNameFromCompiledClassName("Name");
        Assert.assertFalse(className.isPresent());
    }

    @Test
    public void makeClassNameFromCompiledClassNameWith$() throws Exception {
        Optional<String> className = SceneBuilderUtil.makeClassNameFromCompiledClassName("Name$Noooooo.class");
        Assert.assertFalse(className.isPresent());
    }

    @Test
    public void makeClassNameFromCompiledClassNameWithValidClassName() throws Exception {
        Optional<String> className = SceneBuilderUtil.makeClassNameFromCompiledClassName("Name.class");
        Assert.assertEquals("Name", className.get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void makeClassNameFromCompiledClassNameWithNullParam() throws Exception {
        SceneBuilderUtil.isNotJavaFxComponent(null);
    }

}