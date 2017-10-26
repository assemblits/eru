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

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

public class FXUtils {

    public static void injectFXMLTo(Object controllerObject){
        final URL FXML_URL = getURLOfFXML(controllerObject);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(FXML_URL);
            fxmlLoader.setRoot(controllerObject);
            fxmlLoader.setController(controllerObject);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static URL getURLOfFXML(Object controllerObject){
        final Class objectClass = controllerObject.getClass();
        final String className  = objectClass.getSimpleName();
        return objectClass.getResource(className + ".fxml");
    }
}
