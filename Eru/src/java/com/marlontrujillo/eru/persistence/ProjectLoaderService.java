package com.marlontrujillo.eru.persistence;

import com.marlontrujillo.eru.util.JpaUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by mtrujillo on 7/30/17.
 */
public class ProjectLoaderService extends Service<Project> {

    @Override
    protected Task<Project> createTask() {
        return new Task<Project>() {
            @Override
            protected Project call() throws Exception {
                updateMessage("Getting database Connection");
                updateProgress(33, 100);
                EntityManager entityManager = JpaUtil.getGlobalEntityManager();
                updateMessage("Loading");
                updateProgress(76, 100);
                Dao<Project> dao = new Dao<>(entityManager, Project.class);
                List<Project> entities = dao.findEntities();
                updateProgress(100, 100);
                return entities.isEmpty() ? new Project() : entities.get(0);
            }
        };
    }
}

