package org.assemblits.eru.gui.model;

import org.assemblits.eru.entities.*;
import org.assemblits.eru.fieldbus.actors.Director;
import org.assemblits.eru.contract.Agency;
import org.assemblits.eru.project.ProjectActivities;
import org.assemblits.eru.project.ProjectListener;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
public class ProjectListenerTest {

    private ProjectListener projectListener;

    private ProjectModel projectModel;
    private ProjectActivities projectActivities;

    @Before
    public void setUp() throws Exception {
        Project project = new Project();
        project.setId(0);
        projectModel = new ProjectModel();
        projectActivities = new ProjectActivities(new Agency(), new Director());
        projectListener = new ProjectListener(projectActivities);
        projectModel.load(project);
        projectListener.setProjectModel(projectModel);
        projectListener.listen();
    }

    @Test
    public void testDeviceReadingExecutorIsInDirector() throws Exception {
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
        assertTrue("When connected, the reading contract must be accepted",
                projectActivities.getAgency().findAgentByClient(device).getContracts().get(0).isAccepted());
    }

    @Test
    public void testDeviceReadingExecutorIsNotInDirector() throws Exception {
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
        connection.setConnected(false);

        // [Check]
        assertNull("When disconnected, the reading contract and agent must be removed.",
                projectActivities.getAgency().findAgentByClient(device));
    }

    @Test
    public void testTagListenConnections() throws Exception {
        // [Given]
        Connection connection = new TcpConnection();


        Device device = new Device();
        device.setEnabled(true);
        device.setConnection(connection);

        Address address = new Address();
        address.setOwner(device);
        device.getAddresses().add(address);

        Tag tag = new Tag();
        tag.setType(Tag.Type.INPUT);
        tag.setEnabled(true);
        tag.setLinkedAddress(address);

        projectModel.getConnections().add(connection);
        projectModel.getDevices().add(device);
        projectModel.getTags().add(tag);

        // [When]
        connection.setConnected(true);

        // [Check]
        assertTrue("When connected, the tag contract must be accepted",
                projectActivities.getAgency().findAgentByClient(tag).getContracts().get(0).isAccepted());
    }

    @Test
    public void testTagStopListenConnections() throws Exception {
        // [Given]
        Connection connection = new TcpConnection();


        Device device = new Device();
        device.setEnabled(true);
        device.setConnection(connection);

        Address address = new Address();
        address.setOwner(device);
        device.getAddresses().add(address);

        Tag tag = new Tag();
        tag.setType(Tag.Type.INPUT);
        tag.setEnabled(true);
        tag.setLinkedAddress(address);

        projectModel.getConnections().add(connection);
        projectModel.getDevices().add(device);
        projectModel.getTags().add(tag);

        // [When]
        connection.setConnected(true);
        connection.setConnected(false);

        // [Check]
        assertNull("When connected, the tag contract must be accepted",
                projectActivities.getAgency().findAgentByClient(tag));
    }
}