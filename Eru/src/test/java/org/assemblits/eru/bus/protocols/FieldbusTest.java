package org.assemblits.eru.bus.protocols;

/**
 * Created by marlontrujillo1080 on 10/16/17.
 */
public class FieldbusTest {

//    private Fieldbus fieldbus;
//
//    @Before
//    public void setUp() throws Exception {
//        BusDirector busDirector = new BusDirector();
//        Agency busAgency = new Agency();
//        fieldbus = new Fieldbus(busDirector, busAgency);
//    }
//
//    @Test
//    public void testLinksContainerOnConnection() throws Exception {
//        Device device = Mockito.mock(Device.class);
//        Context doThis = Mockito.mock(Context.class);
//        Context doThat = Mockito.mock(Context.class);
//
//        fieldbus.add(device, doThis);
//        fieldbus.add(device, doThat);
//
//        assertEquals("Contexts must be added to the Links Container", fieldbus.getBusAgency().getContracts(device).size(), 2);
//    }
//
//    @Test
//    public void testLinksContainerOnDisconnection() throws Exception {
//        Device device = Mockito.mock(Device.class);
//        Context doThis = Mockito.mock(Context.class);
//        Context doThat = Mockito.mock(Context.class);
//
//        fieldbus.add(device, doThis);
//        fieldbus.add(device, doThat);
//        fieldbus.remove(device);
//
//        assertTrue("Contexts must be removed from the Links Container", fieldbus.getBusAgency().getContracts(device).isEmpty());
//    }
//
//    @Test
//    public void testDirectorOnConnection() throws Exception {
//        Device device = Mockito.mock(Device.class);
//        Context context = Mockito.mock(Context.class);
//
//        fieldbus.add(device, context);
//
//        assertTrue("Context must be added to the Director", fieldbus.getBusDirector().getContexts().contains(context));
//    }
//
//    @Test
//    public void testDirectorOnDisconnection() throws Exception {
//        Device device = Mockito.mock(Device.class);
//        Context context = Mockito.mock(Context.class);
//
//        fieldbus.add(device, context);
//        fieldbus.remove(device);
//
//        assertFalse("Context must be removed from Director", fieldbus.getBusDirector().getContexts().contains(context));
//    }
//
//    @Test
//    public void testIfContains() throws Exception {
//        Device device = Mockito.mock(Device.class);
//        Context context = Mockito.mock(Context.class);
//
//        assertFalse(fieldbus.contains(device));
//
//        fieldbus.add(device, context);
//
//        assertTrue(fieldbus.contains(device));
//
//        fieldbus.remove(device);
//
//        assertFalse(fieldbus.contains(device));
//    }
}