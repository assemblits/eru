package org.assemblits.eru.fieldbus.actors;

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
        Executor executor = Mockito.mock(Executor.class);
        director.getExecutors().add(executor);
        assertTrue("Director accepts communicators", director.getExecutors().contains(executor));
    }

    @Test
    public void testRemovingCommunicators() throws Exception {
        Executor mockedExecutor = Mockito.mock(Executor.class);
        director.getExecutors().add(mockedExecutor);
        director.getExecutors().remove(mockedExecutor);
        assertFalse("Director accepts communicators", director.getExecutors().contains(mockedExecutor));
    }

    @Test (timeout = 500)
    public void testStopping() throws Exception {
        director.start();
        director.interrupt();
        while (director.isAlive()){
            Thread.sleep(10);
        }
    }
}