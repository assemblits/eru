package org.assemblits.eru.gui.model;

import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Project;
import org.assemblits.eru.entities.TcpConnection;
import org.assemblits.eru.gui.service.ProjectCreator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
public class ProjectListenerTest {

    private ProjectListener projectListener;
    private ProjectModel projectModel;

    @Before
    public void setUp() throws Exception {
        Project project = new Project();
        project.setId(0);
        projectModel = ProjectModel.from(project);
//        projectListener = new ProjectListener();
        projectListener.setProjectModel(projectModel);
    }

    @Test
    public void testDeviceListenConnection() throws Exception {
        Device device = new Device();
        Connection connection = new TcpConnection();
        device.setConnection(connection);

        projectModel.getConnections().add(connection);
        projectModel.getDevices().add(device);

        connection.setConnected(true);

    }
}