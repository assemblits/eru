package com.eru.jfx.scenebuilder;

import com.eru.jfx.JFXClassUtil;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Stack;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ComponentsIdsGenerator {

    private final JFXClassUtil jfxClassUtil;

    public void generateComponentsId(@NonNull FXOMDocument fxomDocument) {
        Stack<FXOMObject> fxmlObjects = new Stack<>();
        fxmlObjects.push(fxomDocument.getFxomRoot());

        while (!fxmlObjects.isEmpty()) {
            FXOMObject fxmlObject = fxmlObjects.pop();
            Class<?> componentClass = ((FXOMInstance) fxmlObject).getDeclaredClass();
            String className = componentClass.getName();

            if (fxmlObject.getFxId() == null && jfxClassUtil.isNotJavaFxComponent(className)) {
                fxmlObject.setFxId(generateId());
            }
            fxmlObjects.addAll(fxmlObject.getChildObjects());
        }
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
