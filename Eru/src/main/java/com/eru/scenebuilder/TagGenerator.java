package com.eru.scenebuilder;

import com.eru.entities.Display;
import com.eru.entities.Tag;
import com.eru.gui.App;
import com.eru.scene.control.annotation.TagValue;
import com.eru.util.SceneBuilderUtil;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class TagGenerator {

    private ComponentIdGenerator idGenerator;

    public void generateTags(FXOMDocument fxomDocument, Display display) {
        Map<Class<?>, Integer> componentCount = countComponents(fxomDocument);
        generateTags(fxomDocument, display, componentCount);
    }

    private void generateTags(FXOMDocument fxomDocument, Display display, Map<Class<?>, Integer> componentCount) {
        Stack<FXOMObject> fxmlObjects = new Stack<>();
        fxmlObjects.push(fxomDocument.getFxomRoot());

        while (!fxmlObjects.isEmpty()) {
            FXOMObject fxmlObject = fxmlObjects.pop();
            Class<?> componentClass = ((FXOMInstance) fxmlObject).getDeclaredClass();
            String className = componentClass.getName();

            if (fxmlObject.getFxId() == null && SceneBuilderUtil.isNotJavaFxComponent(className)) {
                fxmlObject.setFxId(idGenerator.generateId());

                List<Field> tagFields = getComponentPropertyFields(componentClass);
                String simpleClassName = SceneBuilderUtil.getSimpleNameFromClassName(className);
                Integer thisComponentCount = componentCount.get(componentClass);

                List<Tag> tags = tagFields.stream()
                        .map(field -> createTag(display, fxmlObject, simpleClassName, thisComponentCount, field))
                        .collect(Collectors.toList());

//                App.getSingleton().getProject().getTags().addAll(tags);
//                App.getSingleton().execute(App.Action.SAVE_TO_DB);

            }
            fxmlObjects.addAll(fxmlObject.getChildObjects());
        }
    }

    private List<Field> getComponentPropertyFields(Class<?> componentClass) {
        return Arrays.stream(componentClass.getDeclaredFields())
                .filter(field -> field.getType().getName().endsWith("Property") && field.isAnnotationPresent(TagValue.class))
                .collect(Collectors.toList());
    }

    private Tag createTag(Display display, FXOMObject fxmlObject, String justClassName, Integer thisComponentCount, Field field) {
        Tag tag = new Tag();
        tag.setName(display.getName() + "_" + justClassName + "(" + thisComponentCount + ")_" + field.getName());
        tag.setGroupName("Tags");
        tag.setDisplay(display);
        tag.setFxId(fxmlObject.getFxId());
        tag.setComponentFieldName(field.getAnnotation(TagValue.class).value());
        return tag;
    }

    private Map<Class<?>, Integer> countComponents(FXOMDocument fxomDocument) {
        Stack<FXOMObject> fxmlObjects = new Stack<>();
        fxmlObjects.push(fxomDocument.getFxomRoot());
        Map<Class<?>, Integer> componentCount = new HashMap<>();

        while (!fxmlObjects.isEmpty()) {
            FXOMObject fxmlObject = fxmlObjects.pop();
            Class<?> componentClass = ((FXOMInstance) fxmlObject).getDeclaredClass();
            componentCount.merge(componentClass, 1, (a, b) -> a + b);
            fxmlObjects.addAll(fxmlObject.getChildObjects());
        }
        return componentCount;
    }
}
