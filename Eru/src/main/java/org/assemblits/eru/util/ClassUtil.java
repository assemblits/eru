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
