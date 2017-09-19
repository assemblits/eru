package com.eru.jfx;

import com.eru.util.ClassUtil;
import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer;
import javafx.scene.Node;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import java.lang.reflect.Modifier;

@Component
public class JFXClassUtil {

    public boolean isNotJavaFxComponent(@NonNull String className) {
        return !className.startsWith("java.") && !className.startsWith("javax.")
                && !className.startsWith("javafx.") && !className.startsWith("com.oracle.javafx.scenebuilder.")
                && !className.startsWith("com.javafx.");
    }

    public boolean isJFXComponentClass(String className, String classAbsolutePackage) {
        if (className != null && isNotJavaFxComponent(className)) {
            try {
                Class<?> jfxComponentClass = ClassUtil.loadClass(classAbsolutePackage);
                if (!Modifier.isAbstract(jfxComponentClass.getModifiers()) && Node.class.isAssignableFrom(jfxComponentClass)) {
                    JarExplorer.instantiateWithFXMLLoader(jfxComponentClass, this.getClass().getClassLoader());
                    return true;
                }
            } catch (Exception ignored) {
                return false;
            }
        }
        return false;
    }
}
