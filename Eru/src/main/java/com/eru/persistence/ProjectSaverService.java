package com.eru.persistence;

import com.eru.entities.Project;
import com.eru.gui.ApplicationContextHolder;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j;

@Log4j
public class ProjectSaverService extends Service<Project> {

    private Project project;

    @Override
    protected Task<Project> createTask() {
        return new Task<Project>() {
            @Override
            protected Project call() throws Exception {
                updateMessage("Collecting objects");
                updateProgress(76, 100);
                ProjectDao projectDao = ApplicationContextHolder.getApplicationContext().getBean(ProjectDao.class);
                log.debug("Saving " + getProject());
                Project updatedProject = projectDao.update(getProject());
                updateProgress(100, 100);
                return updatedProject;
            }
        };
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
