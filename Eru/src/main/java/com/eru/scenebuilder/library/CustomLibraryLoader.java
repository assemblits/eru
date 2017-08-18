package com.eru.scenebuilder.library;

import com.oracle.javafx.scenebuilder.kit.library.Library;
import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer;
import javafx.scene.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

import static java.io.File.separator;
import static java.lang.String.format;


@Log4j
public class CustomLibraryLoader {

    private static final List<String> DYNAMO_CLASSES_LOCATION = new ArrayList<String>() {{
        add("%scom%seru%sscene%scontrol".replaceAll("%s", separator));
        add("%seu%shansolo".replaceAll("%s", separator));
    }};

    private static final List<String> TOP_PACKAGES = new ArrayList<String>() {{
        add("main");
        add("target");
        add("classes");
    }};

    public Library loadFromClassPath() {
        Set<ClassInfo> classesInfo = scanClassPathAndGetClassesInfo();

        List<EruComponent> eruComponents = classesInfo.stream()
                .filter(this::isJFXComponentClass)
                .map(classInfo -> {
                    EruComponent eruComponent = new EruComponent(classInfo.getName(), classInfo.getClazz());
                    log.info(format("Component '%s' loaded", eruComponent.getName()));
                    return eruComponent;
                }).collect(Collectors.toList());

        return new EruLibrary(eruComponents);

    }

    private Set<ClassInfo> scanClassPathAndGetClassesInfo() {
        Stack<File> directories = new Stack<>();
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
                    String className = makeClassName(file.getName());
                    if (className != null) {
                        String canonicalPackage = getCanonicalPackage(className, file);
                        try {
                            classesInfo.add(new ClassInfo(className, canonicalPackage, file, loadClass(canonicalPackage)));
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
            if (className != null && !className.startsWith("java.") && !className.startsWith("javax.")
                    && !className.startsWith("javafx.") && !className.startsWith("com.oracle.javafx.scenebuilder.")
                    && !className.startsWith("com.javafx.")) {
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

    private String makeClassName(String entryName) {
        String result;
        if (!entryName.endsWith(".class")) {
            result = null;
        } else if (entryName.contains("$")) {
            result = null;
        } else {
            int endIndex = entryName.length() - 6;
            result = entryName.substring(0, endIndex).replace(separator, ".");
        }

        return result;
    }

    @Data
    @AllArgsConstructor
    private static class ClassInfo {
        String name;
        String absolutePackage;
        File file;
        Class<?> clazz;
    }
}