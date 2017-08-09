package com.marlontrujillo.eru.gui.toolbars.tables;

import com.marlontrujillo.eru.comm.device.Device;
import javafx.beans.property.StringProperty;

import java.util.List;

/**
 * Created by mtrujillo on 8/8/17.
 */
public class DeviceTable extends EruTable<Device> {

    public DeviceTable(List<Device> items) {
        super(items);
    }

    @Override
    public void addNewItem() {

    }

    @Override
    public void setTextToFilter(StringProperty textToFilter) {

    }
}
