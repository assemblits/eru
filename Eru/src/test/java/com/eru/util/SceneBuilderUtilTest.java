package com.eru.util;

import org.junit.Assert;
import org.junit.Test;

public class SceneBuilderUtilTest {

    @Test(expected = IllegalArgumentException.class)
    public void isNotJavaFxComponentWithNullArgument() throws Exception {
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
    public void getSimpleNameFromClassNameWithNullArgument() throws Exception {
        SceneBuilderUtil.getSimpleNameFromClassName(null);
    }

}