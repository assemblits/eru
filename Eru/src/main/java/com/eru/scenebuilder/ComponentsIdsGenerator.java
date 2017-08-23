package com.eru.scenebuilder;

import com.eru.util.SceneBuilderUtil;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import lombok.AllArgsConstructor;

import java.util.Stack;
import java.util.UUID;

@AllArgsConstructor
public class ComponentsIdsGenerator {

    public void generateComponentsId(FXOMDocument fxomDocument) {
        Stack<FXOMObject> fxmlObjects = new Stack<>();
        fxmlObjects.push(fxomDocument.getFxomRoot());

        while (!fxmlObjects.isEmpty()) {
            FXOMObject fxmlObject = fxmlObjects.pop();
            Class<?> componentClass = ((FXOMInstance) fxmlObject).getDeclaredClass();
            String className = componentClass.getName();

            if (fxmlObject.getFxId() == null && SceneBuilderUtil.isNotJavaFxComponent(className)) {
                fxmlObject.setFxId(generateId());
            }
            fxmlObjects.addAll(fxmlObject.getChildObjects());
        }
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
