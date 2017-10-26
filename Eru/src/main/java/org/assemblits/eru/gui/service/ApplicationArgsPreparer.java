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
