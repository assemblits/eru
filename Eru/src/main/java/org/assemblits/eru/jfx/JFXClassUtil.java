/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.jfx;

import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer;
import javafx.scene.Node;
import lombok.NonNull;
import org.assemblits.eru.util.ClassUtil;
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
