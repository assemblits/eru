package org.assemblits.eru.gui.service;

import org.assemblits.eru.preferences.EruPreferences;

import java.util.Arrays;

public class ApplicationArgsPreparer {

    public String[] prepare(String[] savedArgs, EruPreferences eruPreferences) {
        boolean isProjectDirectoryArgPresent = Arrays.stream(savedArgs)
                .filter(arg -> arg.contains("project.directory")).findFirst().isPresent();
        if (!isProjectDirectoryArgPresent) {
            savedArgs = Arrays.copyOf(savedArgs, savedArgs.length + 1);
            savedArgs[savedArgs.length - 1] = "--project.directory=" + eruPreferences.getApplicationDirectory().getValue();
        }
        return savedArgs;
    }
}
