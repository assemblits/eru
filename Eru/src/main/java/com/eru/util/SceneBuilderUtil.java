package com.eru.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SceneBuilderUtil {

    public boolean isNotJavaFxComponent(@NonNull String className) {
        return !className.startsWith("java.") && !className.startsWith("javax.")
                && !className.startsWith("javafx.") && !className.startsWith("com.oracle.javafx.scenebuilder.")
                && !className.startsWith("com.javafx.");
    }

    public String getSimpleNameFromClassName(@NonNull String className) {
        return className.substring(className.lastIndexOf(".") + 1, className.length());
    }
}
