package org.assemblits.eru.util;

import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.net.URL;

/**
 * Created by mtrujillo on 10/11/2015.
 */
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
