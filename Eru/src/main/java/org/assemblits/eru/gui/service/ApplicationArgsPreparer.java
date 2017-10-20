package org.assemblits.eru.gui.service;

import org.assemblits.eru.preferences.EruPreferences;

import java.util.Arrays;

public class ApplicationArgsPreparer {

    public String[] prepare(String[] savedArgs) {
        final EruPreferences eruPreferences = new EruPreferences();
        final boolean isProjectDirectoryArgPresent = Arrays.stream(savedArgs).anyMatch(arg -> arg.contains("project.directory"));
        if (!isProjectDirectoryArgPresent) {
            savedArgs = Arrays.copyOf(savedArgs, savedArgs.length + 1);
            savedArgs[savedArgs.length - 1] = "--project.directory=" + eruPreferences.getApplicationDirectory().getValue();
        }
        return savedArgs;
    }
}
