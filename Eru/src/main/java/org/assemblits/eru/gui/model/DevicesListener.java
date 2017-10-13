package org.assemblits.eru.gui.model;

import javafx.collections.ListChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Device;
import org.springframework.stereotype.Component;

/**
 * Created by marlontrujillo1080 on 10/13/17.
 */
@Component
@Slf4j
public class DevicesListener implements ListChangeListener<Device> {

    @Override
    public void onChanged(Change<? extends Device> c) {
        while (c.next()) {
            if (c.wasPermutated()) {
                for (int i = c.getFrom(); i < c.getTo(); ++i) {
                    log.info("Devices from "+c.getFrom()+" to "+c.getTo()+" changed.");
                }
            } else if (c.wasUpdated()) {
                log.info("Devices updated. ");
            } else {
                for (Device removedDevice : c.getRemoved()) {
                    log.info(removedDevice.getName() + " removed from project.");
                }
                for (Device addedDevice : c.getAddedSubList()) {
                    log.info(addedDevice.getName() + " added to the project.");
                }
            }
        }
    }
}
