package com.eru.persistence;

import com.eru.entities.Project;
import com.eru.util.JpaUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.log4j.Log4j;

import javax.persistence.EntityManager;

@Log4j
public class ProjectSaverService extends Service<Project> {

    private Project project;

    @Override
    protected Task<Project> createTask() {
        return new Task<Project>() {
            @Override
            protected Project call() throws Exception {
                updateMessage("Getting database Connection...");
                updateProgress(33, 100);
                EntityManager entityManager = JpaUtil.getGlobalEntityManager();
                updateMessage("Collecting objects...");
                updateProgress(76, 100);
                Dao<Project> dao = new Dao<>(entityManager, Project.class);
                log.debug("Saving " + getProject());
                Project updatedProject = dao.update(getProject());
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
