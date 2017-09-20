package com.eru.jfx.scenebuilder.library;


import com.oracle.javafx.scenebuilder.kit.library.BuiltinLibrary;
import com.oracle.javafx.scenebuilder.kit.library.Library;
import com.oracle.javafx.scenebuilder.kit.library.LibraryItem;
import com.oracle.javafx.scenebuilder.kit.library.util.JarExplorer;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EruLibrary extends Library {
    private static final String CUSTOM_SECTION = "Eru Controls";

    public EruLibrary(Collection<EruComponent> customComponents) {
        classLoaderProperty.set(EruLibrary.class.getClassLoader());

        getItems().setAll(BuiltinLibrary.getLibrary().getItems());

        List<LibraryItem> customLibraryItems = customComponents.stream().map(component ->
                new LibraryItem(component.getName(), CUSTOM_SECTION, JarExplorer.makeFxmlText(component.getKlass()),
                        null, this))
                .collect(Collectors.toList());
        getItems().addAll(customLibraryItems);
    }

    private static int compareSections(String s1, String s2) {
        final boolean isCustom1 = CUSTOM_SECTION.equals(s1);
        final boolean isCustom2 = CUSTOM_SECTION.equals(s2);
        if (isCustom1) {
            return isCustom2 ? 0 : 1;
        }
        if (isCustom2) {
            return -1;
        }
        return BuiltinLibrary.getLibrary().getSectionComparator().compare(s1, s2);
    }

    @Override
    public Comparator<String> getSectionComparator() {
        return EruLibrary::compareSections;
    }
}