package com.eru.util

import spock.lang.Specification

class SceneBuilderUtilTest extends Specification {

    def 'isNotJavaFxComponent with null param'() {
        when:
        SceneBuilderUtil.isNotJavaFxComponent(null)
        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'className is null'
    }


    def 'isNotJavaFxComponent with java package'() {
        when:
        def result = SceneBuilderUtil.isNotJavaFxComponent('java.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with javax package'() {
        when:
        def result = SceneBuilderUtil.isNotJavaFxComponent('javax.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with JavaFx package'() {
        when:
        def result = SceneBuilderUtil.isNotJavaFxComponent('javafx.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with JavaFxSceneBuilder package'() {
        when:
        boolean result = SceneBuilderUtil.isNotJavaFxComponent('com.oracle.javafx.scenebuilder.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with com.javafx.component package'() {
        when:
        boolean result = SceneBuilderUtil.isNotJavaFxComponent('com.javafx.component')
        then:
        !result
    }

    def 'isNotJavaFxComponent with valid package package'() {
        when:
        boolean result = SceneBuilderUtil.isNotJavaFxComponent('com.eru.control')
        then:
        result
    }

    def 'getSimpleNameFromClassName'() {
        when:
        def name = SceneBuilderUtil.getSimpleNameFromClassName('com.test.Name')
        then:
        name == 'Name'
    }

    def 'getSimpleNameFromClassName with null param'() {
        when:
        SceneBuilderUtil.getSimpleNameFromClassName(null)
        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'className is null'
    }


    def 'makeClassNameFromCompiledClassName with invalid class name'() {
        when:
        def className = SceneBuilderUtil.makeClassNameFromCompiledClassName('Name')
        then:
        !className.ifPresent()
    }

    def 'makeClassNameFromCompiledClassName with $'() {
        when:
        def className = SceneBuilderUtil.makeClassNameFromCompiledClassName('Name$Noooooo.class')
        then:
        !className.ifPresent()
    }

    def 'makeClassNameFromCompiledClassName with valid class name'() {
        when:
        def className = SceneBuilderUtil.makeClassNameFromCompiledClassName('Name.class')
        then:
        className.get() == 'Name'
    }

    def 'makeClassNameFromCompiledClassName with null param'() {
        when:
        SceneBuilderUtil.isNotJavaFxComponent(null)
        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'className is null'
    }
}
