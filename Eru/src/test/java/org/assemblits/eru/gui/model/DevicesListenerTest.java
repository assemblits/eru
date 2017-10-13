package org.assemblits.eru.gui.model;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.assemblits.eru.entities.Device;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
public class DevicesListenerTest {

    @Test
    public void testAddListener() throws Exception {
        // [given]
        DevicesListener devicesListener = Mockito.mock(DevicesListener.class);
        Device device = Mockito.mock(Device.class);
        ObservableList<Device> observableDevices = FXCollections.observableArrayList();

        // [when]
        observableDevices.addListener(devicesListener);
        observableDevices.add(device);

        // [then]
        ArgumentCaptor<ListChangeListener.Change> argument = ArgumentCaptor.forClass(ListChangeListener.Change.class);
        verify(devicesListener).onChanged(argument.capture());
        assertTrue(argument.getValue().next());
        assertTrue(argument.getValue().wasAdded());
    }

}