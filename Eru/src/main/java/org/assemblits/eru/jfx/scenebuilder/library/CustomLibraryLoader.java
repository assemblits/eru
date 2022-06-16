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
package org.assemblits.eru.jfx.scenebuilder.library;

import com.oracle.javafx.scenebuilder.kit.library.Library;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.jfx.JFXClassUtil;
import org.assemblits.eru.packages.ClassInfo;
import org.assemblits.eru.packages.PackageExplorer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.io.File.separator;
import static java.lang.String.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLibraryLoader {

    private static final List<String> DYNAMO_CLASSES_LOCATION = new ArrayList<String>() {{
        add(join("/", "", "org", "assemblits", "eru", "gui", "dynamo"));
    }};

    private final PackageExplorer packageExplorer;
    private final JFXClassUtil jfxClassUtil;
    private Library library;

    public void loadFromClassPath() {
        log.info("Loading custom components from classpath");
        Set<ClassInfo> classesInfo = packageExplorer.exploreAndGetClassesInfo(DYNAMO_CLASSES_LOCATION);

        List<EruComponent> eruComponents = classesInfo.stream()
                .filter(this::isJFXComponentClass)
                .map(classInfo -> {
                    EruComponent eruComponent = new EruComponent(classInfo.getName(), classInfo.getClazz());
                    log.debug("Component {} loaded", eruComponent.getName());
                    return eruComponent;
                }).collect(Collectors.toList());
        log.info("{} custom components loaded successfully", eruComponents.size());
        library = new EruLibrary(eruComponents);
    }

    public Library getLibrary() {
        if (library == null) {
            throw new IllegalStateException("Library should be loaded before");
        }
        return library;
    }

    private boolean isJFXComponentClass(ClassInfo dynamoClassFile) {
        if (!dynamoClassFile.getFile().isDirectory()) {
            return jfxClassUtil.isJFXComponentClass(dynamoClassFile.getName(), dynamoClassFile.getAbsolutePackage());
        }
        return false;
    }
}