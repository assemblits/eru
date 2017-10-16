package org.assemblits.eru.gui.model;

import org.assemblits.eru.comm.actors.Director;
import org.assemblits.eru.comm.bus.Fieldbus;
import org.assemblits.eru.entities.Connection;
import org.assemblits.eru.entities.Device;
import org.assemblits.eru.entities.Project;
import org.assemblits.eru.entities.TcpConnection;
import org.assemblits.eru.jfx.links.LinksContainer;
import org.assemblits.eru.tag.TagBus;
import org.junit.Before;
import org.junit.Test;

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
        LinksContainer linksContainer = new LinksContainer();
        projectListener = new ProjectListener(new Fieldbus(new Director(), linksContainer), new TagBus(linksContainer));
        projectListener.setProjectModel(projectModel);
        projectListener.listen();
    }

    @Test
    public void testDeviceListenConnection() throws Exception {
        // [Given]
        // Create device and connections
        Device device = new Device();
        Connection connection = new TcpConnection();
        device.setEnabled(true);
        device.setConnection(connection);

        // Set device and connections to the project
        projectModel.getConnections().add(connection);
        projectModel.getDevices().add(device);

        // [When]
        connection.setConnected(true);

        // [Check]
        assertTrue("When connected, device should be added to the fieldbus.", projectListener.getFieldbus().contains(device));
    }
}