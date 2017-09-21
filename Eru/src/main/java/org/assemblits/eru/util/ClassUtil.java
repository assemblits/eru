package org.assemblits.eru.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Optional;

import static java.io.File.separator;

@UtilityClass
public class ClassUtil {

    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return ClassUtil.class.getClassLoader().loadClass(className);
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
