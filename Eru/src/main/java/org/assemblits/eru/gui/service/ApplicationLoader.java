package org.assemblits.eru.gui.service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Project;
import org.assemblits.eru.gui.model.ProjectModel;
import org.assemblits.eru.jfx.scenebuilder.library.CustomLibraryLoader;
import org.assemblits.eru.persistence.ProjectRepository;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

@Slf4j
public class ApplicationLoader extends Service<ConfigurableApplicationContext> {

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
    protected Task<ConfigurableApplicationContext> createTask() {
        return new Task<ConfigurableApplicationContext>() {
            @Override
            protected ConfigurableApplicationContext call() throws Exception {
                ConfigurableApplicationContext applicationContext = startApplicationContext();
                try {
                    log.info("Starting application context");
                    updateMessage("Starting application context");
                    updateProgress(5, 100);
                    ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
                    ProjectRepository projectRepository = beanFactory.getBean(ProjectRepository.class);
                    CustomLibraryLoader customLibraryLoader = beanFactory.getBean(CustomLibraryLoader.class);
                    ProjectModel projectModel = beanFactory.getBean(ProjectModel.class);

                    log.info("Searching project");
                    updateMessage("Searching project");
                    updateProgress(75, 100);
                    Project project;
                    List<Project> projects = projectRepository.findAll();

                    if (projects.isEmpty()) {
                        log.info("No project found, creating a new one...");
                        updateMessage("No project found, creating a new one...");
                        project = projectCreator.defaultProject();
                        projectRepository.save(projectModel.get());
                    } else {
                        project = projects.get(0); // TODO: Project picker: Issue #86
                    }

                    log.info("Loading " + project.getName() + " project...");
                    updateMessage("Loading " + project.getName() + " project...");
                    projectModel.set(project);

                    log.info("Loading custom components");
                    updateMessage("Loading custom components");
                    updateProgress(85, 100);
                    customLibraryLoader.loadFromClassPath();

                    log.info("Application loaded successfully");
                    updateMessage("Application loaded successfully");
                    updateProgress(100, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return applicationContext;
            }
        };
    }

    private ConfigurableApplicationContext startApplicationContext() {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(appClass, appArgs);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(appObject);
        return applicationContext;
    }

}
