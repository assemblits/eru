package com.marlontrujillo.eru.persistence;

import com.marlontrujillo.eru.gui.toolbars.tree.Group;
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
                Project project = null;
                try {
                    updateMessage("Getting database Connection");
                    updateProgress(33, 100);
                    EntityManager entityManager = JpaUtil.getGlobalEntityManager();
                    updateMessage("Loading");
                    updateProgress(76, 100);
                    Dao<Project> dao = new Dao<>(entityManager, Project.class);
                    List<Project> entities = dao.findEntities();
                    if(entities == null || entities.isEmpty()){
                        project = getNewProject();
                        dao.create(project);
                    } else {
                        project = entities.get(0);
                    }

                    updateProgress(100, 100);
                } catch (Exception e){
                    e.printStackTrace();
                }

                return project;
            }
        };
    }

    private Project getNewProject() {
        Project newProject = new Project();
        newProject.setName("Project");

        Group root = new Group();
        root.setName("Project");
        root.setType(Group.Type.ROOT);

        Group connections = new Group();
        connections.setName("Connections");
        connections.setType(Group.Type.CONNECTION);
        connections.setParent(root);
        root.getChildren().add(connections);

        Group devices = new Group();
        devices.setName("Devices");
        devices.setType(Group.Type.DEVICE);
        devices.setParent(root);
        root.getChildren().add(devices);

        Group tags = new Group();
        tags.setName("Tags");
        tags.setType(Group.Type.TAG);
        tags.setParent(root);
        root.getChildren().add(tags);

        Group users = new Group();
        users.setName("Users");
        users.setType(Group.Type.USER);
        users.setParent(root);
        root.getChildren().add(users);

        newProject.setGroup(root);

        return newProject;
    }
}

