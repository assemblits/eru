package com.eru.jfx

import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer
import org.junit.runner.RunWith
import org.mockito.Matchers
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

import static org.powermock.api.mockito.PowerMockito.mockStatic
import static org.powermock.api.mockito.PowerMockito.when

@RunWith(PowerMockRunner)
@PowerMockRunnerDelegate(Sputnik)
@PrepareForTest([JarExplorer])
class JFXClassUtilTest extends Specification {

    def jfxClassUtil = new JFXClassUtil()

    def 'isNotJavaFxComponent with null param'() {
        when:
        jfxClassUtil.isNotJavaFxComponent(null)
        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'className is null'
    }


    def 'isNotJavaFxComponent with java package'() {
        when:
        def result = jfxClassUtil.isNotJavaFxComponent('java.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with javax package'() {
        when:
        def result = jfxClassUtil.isNotJavaFxComponent('javax.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with JavaFx package'() {
        when:
        def result = jfxClassUtil.isNotJavaFxComponent('javafx.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with JavaFxSceneBuilder package'() {
        when:
        boolean result = jfxClassUtil.isNotJavaFxComponent('com.oracle.javafx.scenebuilder.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with com.javafx.component package'() {
        when:
        boolean result = jfxClassUtil.isNotJavaFxComponent('com.javafx.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with valid package package'() {
        when:
        boolean result = jfxClassUtil.isNotJavaFxComponent('com.eru.control')
        then:
        result
    }

    def 'makeClassNameFromCompiledClassName with null param'() {
        when:
        jfxClassUtil.isNotJavaFxComponent(null)
        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'className is null'
    }

    def 'isJFXComponentClass should return false for class which is not JFX component'() {
        given:
        mockStatic(JarExplorer)
        when(JarExplorer.instantiateWithFXMLLoader(Matchers.any(Class), Matchers.any(ClassLoader)))
                .thenThrow(new RuntimeException())
        when:
        def isJFXComponentClass = jfxClassUtil.isJFXComponentClass('NoJFX', 'com.eru.jfx.JFXClassUtilTest.NoJFX')
        then:
        !isJFXComponentClass
    }

    def 'isJFXComponentClass should return true for class which is JFX component'() {
        given:
        mockStatic(JarExplorer)
        when(JarExplorer.instantiateWithFXMLLoader(Matchers.any(Class), Matchers.any(ClassLoader)))
                .thenReturn(new Object())
        when:
        def isJFXComponentClass = jfxClassUtil.isJFXComponentClass('EruAlarm', 'com.eru.gui.dynamo.EruAlarm')
        then:
        isJFXComponentClass
    }

    def 'isJFXComponentClass should return false for null class name'() {
        when:
        def isJFXComponentClass = jfxClassUtil.isJFXComponentClass(null, 'com.eru.gui.dynamo.EruAlarm')
        then:
        !isJFXComponentClass
    }

    class NoJFX {

    }
}
