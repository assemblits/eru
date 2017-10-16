package org.assemblits.eru.comm.actors;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

/**
 * Created by mtrujillo on 10/8/17.
 */
public class DirectorTest {

    private Director director;

    @Before
    public void setUp() throws Exception {
        director = new Director();
    }

    @Test
    public void testRunInOtherThread() throws Exception {
        Thread testThread = Thread.currentThread();
        director.start();
        assertNotSame("Director is not started in other thread", testThread, director);
    }

    @Test
    public void testAddingCommunicators() throws Exception {
        Context context = Mockito.mock(Context.class);
        director.getContexts().add(context);
        assertTrue("Director accepts communicators", director.getContexts().contains(context));
    }

    @Test (timeout = 250)
    public void testStopping() throws Exception {
        director.start();
        director.interrupt();
        while (director.isAlive()){
            Thread.sleep(10);
        }
    }
}