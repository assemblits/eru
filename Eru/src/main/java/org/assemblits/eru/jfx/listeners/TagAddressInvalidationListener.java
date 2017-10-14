package org.assemblits.eru.jfx.listeners;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import lombok.extern.slf4j.Slf4j;
import org.assemblits.eru.entities.Tag;
import org.assemblits.eru.exception.TagLinkException;

import javax.script.ScriptException;

/**
 * Created by marlontrujillo1080 on 10/14/17.
 */
@Slf4j
public class TagAddressInvalidationListener implements InvalidationListener {
    private Tag tagToUpdate;

    public TagAddressInvalidationListener(Tag tagToUpdate) {
        this.tagToUpdate = tagToUpdate;
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            updateValueAndTimestamp();
            updateAlarmStatus();
            tagToUpdate.setStatus("OK");
        } catch (Exception e) {
            tagToUpdate.setStatus(e.getLocalizedMessage());
            log.error("Error updating tag", e);
        }
    }

    private void updateAlarmStatus() throws ScriptException {
        if (tagToUpdate.getAlarmEnabled()) {
            // TODO
        }
    }

    private void updateValueAndTimestamp() throws Exception {
        if ((tagToUpdate.getLinkedAddress() == null)) throw new TagLinkException(tagToUpdate + "has a no address");
        tagToUpdate.setValue(String.valueOf(tagToUpdate.getLinkedAddress().getCurrentValue()));
        tagToUpdate.setTimestamp(tagToUpdate.getLinkedAddress().getTimestamp());
    }
}