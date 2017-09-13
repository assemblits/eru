package com.eru.gui;

import com.eru.gui.controller.EruController;
import com.eru.gui.controller.EruPreloaderController;
import com.eru.gui.service.ApplicationLoader;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Log4j
@SpringBootApplication
@EnableJpaRepositories("com.eru")
@ComponentScan(value = "com.eru")
@EntityScan("com.eru")
public class Application extends javafx.application.Application {

    public static final String NAME = "eru";

    public enum Theme {
        DEFAULT {
            @Override
            public String toString() {
                return "prefs.theme.default";
            }
        },
        DARK {
            @Override
            public String toString() {
                return "prefs.theme.dark";
            }
        }
    }

    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private EruController eruController;

    @Override
    public void start(Stage stage) throws Exception {
        ApplicationLoader applicationLoader = new ApplicationLoader(this, getClass(), getApplicationParameters());
        Preloader preloaderWindow = loadPreloader(applicationLoader);

        applicationLoader.setOnSucceeded(event -> {
            ApplicationLoader.Result loadResult = (ApplicationLoader.Result) event.getSource().getValue();

            ApplicationContextHolder.setApplicationContext(applicationContext);
            applicationContext = loadResult.getApplicationContext();
            eruController.startEru(loadResult.getProject(), stage);
        });

        preloaderWindow.start(stage);
        applicationLoader.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        applicationContext.close();
    }

    private Preloader loadPreloader(ApplicationLoader applicationLoader) {
        return new Preloader() {
            @Override
            public void start(Stage primaryStage) throws Exception {
                FXMLLoader loader = new FXMLLoader();
                loader.setController(new EruPreloaderController(applicationLoader));
                loader.setLocation(getClass().getResource("/views/Preloader.fxml"));
                Parent preLoader = loader.load();

                primaryStage.setScene(new Scene(preLoader));
                primaryStage.show();
            }
        };
    }

    private String[] getApplicationParameters(){
        final Parameters parametersObject = getParameters();
        return parametersObject.getRaw().toArray(new String[0]);
    }
}
