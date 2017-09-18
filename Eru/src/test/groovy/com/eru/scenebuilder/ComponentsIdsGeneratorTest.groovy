package com.eru.scenebuilder

import com.eru.gui.dynamo.EruGauge
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance
import spock.lang.Specification

class ComponentsIdsGeneratorTest extends Specification {

    def componentsIdsGenerator = new ComponentsIdsGenerator()
    def fxomDocument = Mock(FXOMDocument)

    def 'should throw illegal argument exception when null fxomDocument is provided'() {
        when:
        componentsIdsGenerator.generateComponentsId(null)
        then:
        def exception = thrown(IllegalArgumentException)
        exception.message == 'fxomDocument is null'
    }

    def 'should assign id to eru components without ids'() {
        given:
        def eruComponent = Mock(FXOMInstance)
        def eruComponent1 = Mock(FXOMInstance)
        def eruComponent2 = Mock(FXOMInstance)
        when:
        fxomDocument.getFxomRoot() >> eruComponent
        eruComponent.getDeclaredClass() >> EruGauge
        eruComponent.getChildObjects() >> [eruComponent1, eruComponent2]
        eruComponent1.getDeclaredClass() >> EruGauge
        eruComponent1.getChildObjects() >> []
        eruComponent2.getDeclaredClass() >> EruGauge
        eruComponent2.getChildObjects() >> []
        componentsIdsGenerator.generateComponentsId(fxomDocument)
        then:
        1 * eruComponent.setFxId(_)
        1 * eruComponent1.setFxId(_)
        1 * eruComponent2.setFxId(_)
    }

    def 'should not assign id to eru components with ids'() {
        given:
        def eruComponent = Mock(FXOMInstance)
        when:
        fxomDocument.getFxomRoot() >> eruComponent
        eruComponent.getDeclaredClass() >> EruGauge
        eruComponent.getFxId() >> 'id'
        eruComponent.getChildObjects() >> []
        componentsIdsGenerator.generateComponentsId(fxomDocument)
        then:
        0 * eruComponent.setFxId(_)
    }
}
