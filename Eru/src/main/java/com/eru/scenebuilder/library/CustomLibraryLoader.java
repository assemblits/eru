package com.eru.scenebuilder.library;

import com.eru.util.SceneBuilderUtil;
import com.oracle.javafx.scenebuilder.kit.library.Library;
import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer;
import javafx.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static java.io.File.separator;
import static java.lang.String.format;

@Log4j
@Component
public class CustomLibraryLoader {

    private static final List<String> DYNAMO_CLASSES_LOCATION = new ArrayList<String>() {{
        add("%scom%seru%sgui%sdynamo".replaceAll("%s", separator));
    }};
    private static final List<String> TOP_PACKAGES = new ArrayList<String>() {{
        add("main");
        add("target");
        add("classes");
    }};
    private Library library;

    public void loadFromClassPath() {
        log.info("Loading custom components from classpath");
        Set<ClassInfo> classesInfo = scanClassPathAndGetClassesInfo();

        List<EruComponent> eruComponents = classesInfo.stream()
                .filter(this::isJFXComponentClass)
                .map(classInfo -> {
                    EruComponent eruComponent = new EruComponent(classInfo.getName(), classInfo.getClazz());
                    log.debug(format("Component '%s' loaded", eruComponent.getName()));
                    return eruComponent;
                }).collect(Collectors.toList());
        log.info(format("%d custom components loaded successfully", eruComponents.size()));
        library = new EruLibrary(eruComponents);
    }

    public Library getLibrary() {
        if (library == null) {
            throw new IllegalStateException("Library should be loaded before");
        }
        return library;
    }

    private Set<ClassInfo> scanClassPathAndGetClassesInfo() {
        Stack<File> directories = new Stack<>();
        log.debug(format("Scanning packages %s", DYNAMO_CLASSES_LOCATION));
        for (String location : DYNAMO_CLASSES_LOCATION) {
            directories.push(new File(getClass().getResource(location).getPath()));
        }
        Set<ClassInfo> classesInfo = new HashSet<>();
        while (!directories.isEmpty()) {
            File directory = directories.pop();
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    directories.push(file);
                } else {
                    Optional<String> className = SceneBuilderUtil.makeClassNameFromCompiledClassName(file.getName());
                    if (className.isPresent()) {
                        String canonicalPackage = getCanonicalPackage(className.get(), file);
                        try {
                            classesInfo.add(new ClassInfo(className.get(), canonicalPackage, file, loadClass(canonicalPackage)));
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

    private boolean isJFXComponentClass(ClassInfo dynamoClassFile) {
        if (!dynamoClassFile.getFile().isDirectory()) {
            String className = dynamoClassFile.getName();
            if (className != null && SceneBuilderUtil.isNotJavaFxComponent(className)) {
                try {
                    Class<?> jfxComponentClass = loadClass(dynamoClassFile.getAbsolutePackage());
                    if (!Modifier.isAbstract(jfxComponentClass.getModifiers()) && Node.class.isAssignableFrom(jfxComponentClass)) {
                        JarExplorer.instantiateWithFXMLLoader(jfxComponentClass, this.getClass().getClassLoader());
                        return true;

                    }
                } catch (Exception ignored) {
                    return false;
                }
            }
        }
        return false;

    }

    private Class<?> loadClass(String className) throws ClassNotFoundException {
        return CustomLibraryLoader.class.getClassLoader().loadClass(className);
    }

    @Value
    @AllArgsConstructor
    private static class ClassInfo {
        String name;
        String absolutePackage;
        File file;
        Class<?> clazz;
    }
}