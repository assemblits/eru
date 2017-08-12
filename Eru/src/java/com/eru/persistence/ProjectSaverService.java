package com.eru.persistence;

import com.eru.util.JpaUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.persistence.EntityManager;

/**
 * Created by mtrujillo on 7/30/17.
 */
public class ProjectSaverService extends Service<Project> {

    private Project project;

    @Override
    protected Task<Project> createTask() {
        return new Task<Project>() {
            @Override
            protected Project call() throws Exception {
                updateMessage("Getting database Connection");
                updateProgress(33, 100);
                EntityManager entityManager = JpaUtil.getGlobalEntityManager();
                updateMessage("Saving");
                updateProgress(76, 100);
                Dao<Project> dao = new Dao<>(entityManager, Project.class);
                System.out.println("Saving " + getProject());
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
