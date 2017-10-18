package org.assemblits.eru.gui.model;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
public class ProjectListenerTest {

//    private ProjectListener projectListener;
//    private ProjectModel projectModel;
//
//    @Before
//    public void setUp() throws Exception {
//        Project project = new Project();
//        project.setId(0);
//        projectModel = ProjectModel.from(project);
//        Agency busAgency = new Agency();
//        projectListener = new ProjectListener(new Fieldbus(new BusDirector(), busAgency), new TagBus(busAgency));
//        projectListener.setProjectModel(projectModel);
//        projectListener.listen();
//    }
//
//    @Test
//    public void testDeviceListenConnection() throws Exception {
//        // [Given]
//        // Create device and connections
//        Device device = new Device();
//        Connection connection = new TcpConnection();
//        device.setEnabled(true);
//        device.setConnection(connection);
//
//        // Set device and connections to the project
//        projectModel.getConnections().add(connection);
//        projectModel.getDevices().add(device);
//
//        // [When]
//        connection.setConnected(true);
//
//        // [Check]
//        assertTrue("When connected, device should be added to the fieldbus.", projectListener.getFieldbus().contains(device));
//    }
//
//    @Test
//    public void testTagListenConnections() throws Exception {
//        // [Given]
//        Connection connection = new TcpConnection();
//
//        Device device = new Device();
//        device.setEnabled(true);
//        device.setConnection(connection);
//
//        Address address = new Address();
//        address.setOwner(device);
//        device.getAddresses().add(address);
//
//        Tag tag = new Tag();
//        tag.setType(Tag.Type.INPUT);
//        tag.setEnabled(true);
//        tag.setLinkedAddress(address);
//
//        projectModel.getTags().add(tag);
//
//        // [When]
//        connection.setConnected(true);
//
//        // [Check]
//        assertTrue("When connected, tag should be added to the tagbus.", projectListener.getTagsHandler().contains(tag));
//    }
}