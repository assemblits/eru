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
package org.assemblits.eru.packages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.util.ClassUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PackageExplorer {

    private static final List<String> TOP_PACKAGES = new ArrayList<String>() {{
        add("main");
        add("target");
        add("classes");
    }};

    public Set<ClassInfo> exploreAndGetClassesInfo(Collection<String> packages) {
        Stack<File> directories = new Stack<>();
        log.debug("Scanning packages {}", packages);
        for (String location : packages) {
            directories.push(new File(getClass().getResource(location).getPath()));
        }
        Set<ClassInfo> classesInfo = new HashSet<>();
        while (!directories.isEmpty()) {
            File directory = directories.pop();
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    directories.push(file);
                } else {
                    Optional<String> className = ClassUtil.makeClassNameFromCompiledClassName(file.getName());
                    if (className.isPresent()) {
                        String canonicalPackage = getCanonicalPackage(className.get(), file);
                        try {
                            classesInfo.add(new ClassInfo(className.get(), canonicalPackage, file,
                                    ClassUtil.loadClass(canonicalPackage)));
                        } catch (ClassNotFoundException ignore) {
                        }
                    }
                }
            }
        }
        return classesInfo;
    }

    private String getCanonicalPackage(String className, File file) {
        StringBuilder stringBuilder = new StringBuilder();
        while (file.getParentFile() != null && !TOP_PACKAGES.contains(file.getParentFile().getName())) {
            stringBuilder.insert(0, ".").insert(0, file.getParentFile().getName());
            file = file.getParentFile();
        }
        stringBuilder.append(className);
        return stringBuilder.toString();
    }
}
