package com.eru.util;

import com.eru.exception.TagLinkException;
import com.eru.entities.Tag;
import com.eru.gui.EruController;
import com.eru.gui.erget.Display;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import lombok.extern.log4j.Log4j;

import javax.script.ScriptException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by mtrujillo on 9/9/2015.
 */
@Log4j
public class TagLinksManager {

    public static final Map<String, String> DYNAMO_ID_TAG_ID = new HashMap<>();

    private final EruController eruController;
    private final Map<Tag, List<TagLink>> TAG_LINK_MAP = new HashMap<>();

    public TagLinksManager(EruController eruController) {
        this.eruController = eruController;
    }

    public void linkToConnections() {
        this.eruController.getProject().getTags().forEach(this::installUpdaterLink);
    }

    public void unlinkFromConnections() {
        this.eruController.getProject().getTags().forEach(this::removeUpdaterLink);
    }

    private void installUpdaterLink(Tag tag) {
        log.debug("Installing updater linkToConnections to " + tag.getName());
        TagLink link;
        switch (tag.getType()) {
            case INPUT:
                link = new AddressChangeLink(tag);
                tag.getLinkedAddress().timestampProperty().addListener(link);
                registerLink(tag, link);
                break;
            case MASK:
                link = new TagCurrentValueLinkForMaskUpdating(tag.getLinkedTag(), tag);
                tag.getLinkedTag().valueProperty().addListener(link);
                registerLink(tag.getLinkedTag(), link);
                break;
            case MATH:
                eruController.getProject().getTags().stream().filter(t -> tag.getScript().contains(t.getName())).forEach(tagInScript -> {            // Find tags in the script
                    TagLink scriptUpdating = new TagCurrentValueLinkForScriptUpdating(tagInScript, tag);
                    tagInScript.valueProperty().addListener(scriptUpdating);
                    registerLink(tagInScript, scriptUpdating);
                });
                break;
            case STATUS:
                Map<String, String> statusMap   = new HashMap<>();                                                      // Create Map value:statusName
                StringTokenizer scriptTokenizer = new StringTokenizer(tag.getScript() == null ? "" : tag.getScript(), ",");
                while (scriptTokenizer.hasMoreElements()) {
                    String pair = scriptTokenizer.nextElement().toString();
                    StringTokenizer pairTokenizer = new StringTokenizer(pair, "=");
                    while (pairTokenizer.hasMoreElements()) {
                        String value = pairTokenizer.nextElement().toString();
                        String status = pairTokenizer.nextElement().toString();
                        statusMap.put(value, status);
                    }
                }
                link= new TagCurrentValueLinkForStatusUpdating(tag.getLinkedTag(), tag, statusMap);
                tag.getLinkedTag().valueProperty().addListener(link);
                registerLink(tag.getLinkedTag(), link);
                break;
            case OUTPUT:
                break;
            case LOGICAL:
                break;
        }
    }

    private void registerLink(Tag tag, TagLink link){
        if(TAG_LINK_MAP.keySet().contains(tag)){
            TAG_LINK_MAP.get(tag).add(link);
        } else {
            TAG_LINK_MAP.put(tag, new ArrayList<>(Arrays.asList(link)));
        }
    }

    private void removeUpdaterLink(Tag tag) {
        for(TagLink link : TAG_LINK_MAP.get(tag)){
            log.debug("Removing updater linkToConnections to " + tag.getName() + " with linkToConnections " + link);
            if(link instanceof AddressChangeLink){
                tag.getLinkedAddress().timestampProperty().removeListener(link);
            } else {
                tag.timestampProperty().removeListener(link);
            }
        }
        TAG_LINK_MAP.remove(tag);
    }

    public void linkToScada(Node anchorPane) {
        for (String displayID : DYNAMO_ID_TAG_ID.keySet()){
            Display extractedDisplay = (com.eru.gui.erget.Display) anchorPane.lookup("#".concat(displayID));
            eruController.getProject().getTags()
                    .stream()
                    .filter(tag -> tag.getId() == Integer.valueOf(extractedDisplay.getCurrentValueTagID()))
                    .forEach(tag -> tag.valueProperty().addListener((observable, oldValue, newValue) -> extractedDisplay.setCurrentText(newValue)));
        }
    }
}

@Log4j
abstract class TagLink implements InvalidationListener {
    Tag tagToListen;
    Tag tagToUpdate;

    TagLink(Tag tagToListen, Tag tagToUpdate) {
        this.tagToListen = tagToListen;
        this.tagToUpdate = tagToUpdate;
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            updateValueAndTimestamp();
            updateAlarmStatus();
            tagToUpdate.setStatus("OK");
        } catch (Exception e){
            tagToUpdate.setStatus(e.getLocalizedMessage());
            log.error(e);
        }
    }

    private void updateAlarmStatus() throws ScriptException {
        if (tagToUpdate.getAlarmEnabled()) {
            tagToUpdate.setAlarmed(Boolean.parseBoolean(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getAlarmScript()))));
        }
    }
    protected abstract void updateValueAndTimestamp() throws Exception;
}

class AddressChangeLink extends TagLink {

    AddressChangeLink(Tag tagToUpdate) {
        super(null, tagToUpdate);
    }

    @Override
    protected void updateValueAndTimestamp() throws Exception {
        if ((tagToUpdate.getLinkedAddress() == null)) throw new TagLinkException(tagToUpdate + "has a no address");
        tagToUpdate.setValue(String.valueOf(tagToUpdate.getLinkedAddress().getCurrentValue()));
        tagToUpdate.setTimestamp(tagToUpdate.getLinkedAddress().getTimestamp());
    }
}

class TagCurrentValueLinkForMaskUpdating extends TagLink {
    TagCurrentValueLinkForMaskUpdating(Tag tagToListen, Tag tagToUpdate) {
        super(tagToListen, tagToUpdate);
    }
    @Override
    protected void updateValueAndTimestamp() throws Exception {
        tagToUpdate.setValue(String.valueOf( (Integer.getInteger(tagToListen.getValue())) & tagToUpdate.getMask() ));
        tagToUpdate.setTimestamp(tagToListen.getTimestamp());
    }
}

class TagCurrentValueLinkForScriptUpdating extends TagLink {
    TagCurrentValueLinkForScriptUpdating(Tag tagToListen, Tag tagToUpdate) {
        super(tagToListen, tagToUpdate);
    }
    @Override
    protected void updateValueAndTimestamp() throws Exception {
        if (tagToUpdate.getScript() == null || tagToUpdate.getScript().isEmpty() || tagToListen == null) throw new TagLinkException(tagToUpdate + "has a null script");
        tagToUpdate.setValue(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getScript())));
        tagToUpdate.setTimestamp(tagToListen.getTimestamp() == null ? new Timestamp(System.currentTimeMillis()) : tagToListen.getTimestamp());
    }
}

class TagCurrentValueLinkForStatusUpdating extends TagLink {
    private final Map<String, String> statusMap;
    TagCurrentValueLinkForStatusUpdating(Tag tagToListen, Tag tagToUpdate, Map<String, String> statusMap) {
        super(tagToListen, tagToUpdate);
        this.statusMap      = statusMap;
    }
    @Override
    protected void updateValueAndTimestamp() throws Exception {
        tagToUpdate.setValue(statusMap.get(tagToListen.getValue()));
        tagToUpdate.setTimestamp(tagToListen.getTimestamp());
    }
}