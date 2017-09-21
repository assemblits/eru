package org.assemblits.eru.jfx.scenebuilder.library;

import org.assemblits.eru.jfx.JFXClassUtil;
import org.assemblits.eru.packages.ClassInfo;
import org.assemblits.eru.packages.PackageExplorer;
import com.oracle.javafx.scenebuilder.kit.library.Library;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.io.File.separator;
import static java.lang.String.format;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLibraryLoader {

    private static final List<String> DYNAMO_CLASSES_LOCATION = new ArrayList<String>() {{
        add("%sorg%sassemblits%seru%sgui%sdynamo".replaceAll("%s", separator));
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