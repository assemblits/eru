package com.eru.persistence;

import com.eru.entities.Project;
import com.eru.entities.TreeElementsGroup;
import com.eru.util.JpaUtil;
import javafx.application.Platform;
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
                Project project = null;
                try {
                    updateMessage("Getting database Connection");
                    updateProgress(25, 100);
                    EntityManager entityManager = JpaUtil.getGlobalEntityManager();
                    updateMessage("Loading");
                    updateProgress(50, 100);
                    Dao<Project> dao = new Dao<>(entityManager, Project.class);
                    List<Project> entities = dao.findEntities();
                    if (entities == null || entities.isEmpty()) {
                        project = getNewProject();
                        dao.create(project);
                    } else {
                        project = entities.get(0);
                    }

                    updateProgress(100, 100);
                    updateMessage("Done");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return project;
            }
        };
    }

    private Project getNewProject() {
        Project newProject = new Project();
        newProject.setName("Project");

        TreeElementsGroup root = new TreeElementsGroup();
        root.setName("Project");
        root.setType(TreeElementsGroup.Type.ROOT);

        TreeElementsGroup connections = new TreeElementsGroup();
        connections.setName("Connections");
        connections.setType(TreeElementsGroup.Type.CONNECTION);
        connections.setParent(root);
        root.getChildren().add(connections);

        TreeElementsGroup devices = new TreeElementsGroup();
        devices.setName("Devices");
        devices.setType(TreeElementsGroup.Type.DEVICE);
        devices.setParent(root);
        root.getChildren().add(devices);

        TreeElementsGroup tags = new TreeElementsGroup();
        tags.setName("Tags");
        tags.setType(TreeElementsGroup.Type.TAG);
        tags.setParent(root);
        root.getChildren().add(tags);

        TreeElementsGroup users = new TreeElementsGroup();
        users.setName("Users");
        users.setType(TreeElementsGroup.Type.USER);
        users.setParent(root);
        root.getChildren().add(users);

        TreeElementsGroup displays = new TreeElementsGroup();
        displays.setName("Displays");
        displays.setType(TreeElementsGroup.Type.DISPLAY);
        displays.setParent(root);
        root.getChildren().add(displays);

        newProject.setGroup(root);

        return newProject;
    }
}

