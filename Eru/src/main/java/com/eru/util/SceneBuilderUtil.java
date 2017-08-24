package com.eru.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Optional;

import static java.io.File.separator;

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

    public Optional<String> makeClassNameFromCompiledClassName(@NonNull String className) {
        String result;
        if (!className.endsWith(".class")) {
            result = null;
        } else if (className.contains("$")) {
            result = null;
        } else {
            int endIndex = className.length() - 6;
            result = className.substring(0, endIndex).replace(separator, ".");
        }
        return Optional.ofNullable(result);
    }
}
