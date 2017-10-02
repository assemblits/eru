package org.assemblits.eru.util

import spock.lang.Specification

class ClassUtilTest extends Specification {

    def 'makeClassNameFromCompiledClassName with invalid class name'() {
        when:
        def className = ClassUtil.makeClassNameFromCompiledClassName('Name')
        then:
        !className.ifPresent()
    }

    def 'makeClassNameFromCompiledClassName with $'() {
        when:
        def className = ClassUtil.makeClassNameFromCompiledClassName('Name$Noooooo.class')
        then:
        !className.ifPresent()
    }

    def 'makeClassNameFromCompiledClassName with valid class name'() {
        when:
        def className = ClassUtil.makeClassNameFromCompiledClassName('Name.class')
        then:
        className.get() == 'Name'
    }

    def 'loadClass should throw exception when class not found'() {
        when:
        ClassUtil.loadClass("A")
        then:
        thrown(ClassNotFoundException)
    }

    def 'loadClass should load existent class'() {
        when:
        def clazz = ClassUtil.loadClass('org.assemblits.eru.util.ClassUtilTest')
        then:
        clazz != null
    }
}
