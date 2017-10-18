package org.assemblits.eru.bus.actors;

import org.assemblits.eru.bus.context.Context;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by mtrujillo on 10/8/17.
 */
public class BusDirectorTest {

    private BusDirector busDirector;

    @Before
    public void setUp() throws Exception {
        busDirector = new BusDirector();
    }

    @Test
    public void testRunInOtherThread() throws Exception {
        Thread testThread = Thread.currentThread();
        busDirector.start();
        assertNotSame("Director is not started in other thread", testThread, busDirector);
    }

    @Test
    public void testAddingCommunicators() throws Exception {
//        Context context = Mockito.mock(Context.class);
//        busDirector.getContexts().add(context);
//        assertTrue("Director accepts communicators", busDirector.getContexts().contains(context));
    }

    @Test (timeout = 250)
    public void testStopping() throws Exception {
        busDirector.start();
        busDirector.interrupt();
        while (busDirector.isAlive()){
            Thread.sleep(10);
        }
    }
}