package com.eru.gui.service;

import com.eru.entity.Project;
import com.eru.persistence.ProjectRepository;
import com.eru.jfx.scenebuilder.library.CustomLibraryLoader;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@Slf4j
public class ApplicationLoader extends Service<ApplicationLoader.Result> {

    private final Object appObject;
    private final Class<?> appClass;
    private final String[] appArgs;
    private ProjectCreator projectCreator;

    public ApplicationLoader(@NonNull Object appObject, @NonNull Class<?> appClass, @NonNull String[] appArgs) {
        this.appObject = appObject;
        this.appClass = appClass;
        this.appArgs = appArgs;
        projectCreator = new ProjectCreator();
    }

    @Override
    protected Task<Result> createTask() {
        return new Task<Result>() {
            @Override
            protected Result call() throws Exception {
                log.info("Starting application load");
                updateMessage("Starting application");
                updateProgress(5, 100);

                ConfigurableApplicationContext applicationContext = startApplicationContext();
                ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
                ProjectRepository projectRepository = beanFactory.getBean(ProjectRepository.class);
                CustomLibraryLoader customLibraryLoader = beanFactory.getBean(CustomLibraryLoader.class);

                Project project;
                updateMessage("Loading project");
                updateProgress(75, 100);
                List<Project> projects = projectRepository.findAll();

                if (projects.isEmpty()) {
                    log.info("There is no project created, creating a new one");
                    project = projectCreator.defaultProject();
                    projectRepository.save(project);
                } else {
                    project = projects.get(0);
                }

                updateMessage("Loading custom components");
                updateProgress(85, 100);

                customLibraryLoader.loadFromClassPath();

                updateProgress(100, 100);
                updateMessage("Done");

                log.info("Application loaded successfully");
                return new Result(applicationContext, project);
            }
        };
    }

    private ConfigurableApplicationContext startApplicationContext() {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(appClass, appArgs);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(appObject);
        return applicationContext;
    }

    @Value
    public class Result {
        private final ConfigurableApplicationContext applicationContext;
        private final Project project;
    }
}
