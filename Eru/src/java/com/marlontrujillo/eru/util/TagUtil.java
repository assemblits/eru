package com.marlontrujillo.eru.util;

import com.marlontrujillo.eru.logger.LogUtil;
import com.marlontrujillo.eru.tag.Tag;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import javax.script.ScriptException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by mtrujillo on 9/9/2015.
 */
public class TagUtil {

    public static final Map<Tag, List<InvalidationListener>> TAG_LINK_MAP = new HashMap<>();

    public static void installLink(final Tag tag, final List<Tag> allTags){
        switch (tag.getTagType()) {
            case INPUT:
                if (tag.getAddress() != null) {
                    // Create installLink
                    AddressChangeListener link = new AddressChangeListener(tag);

                    // Link
                    tag.getAddress().timestampProperty().addListener(link);

                    // Register
                    registerLink(tag, link);
                } else {
                    final String errorMSG = "Tag with ID:" + tag.getName() + " is an INPUT_TAG but has no Address configured. Cannot installLink.";
                    LogUtil.logger.error(errorMSG);
                    tag.setStatus(errorMSG);
                }
                break;
            case MASK:
                // Find tag source in the tag list
                allTags.stream().filter(t -> t.getName().equals(tag.getTagSourceName())).forEach(tagSourceFinded -> {
                    // Create installLink
                    TagCurrentValueListenerForMaskUpdating link = new TagCurrentValueListenerForMaskUpdating(tag, tagSourceFinded);

                    // Link
                    tagSourceFinded.valueProperty().addListener(link);

                    // Register
                    registerLink(tagSourceFinded, link);
                });
                break;
            case MATH:
                if (tag.getScript() != null && !tag.getScript().isEmpty()) {
                    try {
                        // Set first value
                        tag.setValue(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tag.getScript())));

                        // Find tags in the script
                        allTags.stream().filter(t -> tag.getScript().contains(t.getName())).forEach(tagInScript -> {
                            //Create Link
                            TagCurrentValueListenerForScriptUpdating link = new TagCurrentValueListenerForScriptUpdating(tag, tagInScript);

                            // Link
                            tagInScript.valueProperty().addListener(link);

                            // Register
                            registerLink(tagInScript, link);
                        });
                    } catch (ScriptException e) {
                        LogUtil.logger.error("There is something wrong with the " + tag.getName() + " script.", e);
                    }
                } else {
                    final String errorMSG = "Tag with ID:" + tag.getName() + " cannot evaluate math script because the script is null";
                    LogUtil.logger.error(errorMSG);
                    tag.setStatus(errorMSG);
                }
                break;
            case STATUS:
                if (tag.getScript() != null && !tag.getScript().isEmpty()) {
                    // Find tag source in the tag list
                    allTags.stream().filter(t -> t.getName().equals(tag.getTagSourceName())).forEach(tagSource -> {

                        // Create Map number:statusName
                        Map<String, String> statusMap = new HashMap<>();
                        StringTokenizer scriptTokenizer = new StringTokenizer(tag.getScript() == null ? "" : tag.getScript(), ",");
                        while (scriptTokenizer.hasMoreElements()) {
                            String pair = scriptTokenizer.nextElement().toString();
                            StringTokenizer pairTokenizer = new StringTokenizer(pair, "=");
                            while (pairTokenizer.hasMoreElements()) {
                                try {
                                    String value = pairTokenizer.nextElement().toString();
                                    String status = pairTokenizer.nextElement().toString();
                                    statusMap.put(value, status);
                                } catch (Exception e) {
                                    final String errorMSG = "Script for status is not \"number=status\"";
                                    tag.setStatus(errorMSG);
                                    statusMap.clear();
                                    LogUtil.logger.error(errorMSG, e);
                                    return;
                                }

                            }
                        }

                        //Create Link
                        TagCurrentValueListenerForStatusUpdating link = new TagCurrentValueListenerForStatusUpdating(tag, tagSource, statusMap);

                        // Link
                        tagSource.valueProperty().addListener(link);

                        // Register
                        registerLink(tagSource, link);
                    });
                } else {
                    final String errorMSG = "Tag with ID:" + tag.getName() + " is a STATUS_TAG but the script is empty or null. Cannot set on.";
                    LogUtil.logger.error(errorMSG);
                    tag.setStatus(errorMSG);
                }
                break;
            case OUTPUT:
                break;
            case LOGICAL:
                break;
        }
    }

    public static void removeLink(Tag tag) {
        if (TAG_LINK_MAP.keySet().contains(tag)){
            for(InvalidationListener link : TAG_LINK_MAP.get(tag)){
                if(link instanceof AddressChangeListener){
                    tag.getAddress().timestampProperty().removeListener(link);
                } else {
                    tag.timestampProperty().removeListener(link);
                }
            }
        }
        TAG_LINK_MAP.clear();
    }

    private static void registerLink(Tag tag, InvalidationListener link){
        if(TAG_LINK_MAP.keySet().contains(tag)){
            TAG_LINK_MAP.get(tag).add(link);
        } else {
            TAG_LINK_MAP.put(tag, new ArrayList<>(Arrays.asList(link)));
        }
    }

}

class AddressChangeListener implements InvalidationListener{
    private Tag tagToUpdate;

    public AddressChangeListener(Tag tagToUpdate) {
        this.tagToUpdate = tagToUpdate;
    }

    @Override
    public void invalidated(Observable observable) {
        if((tagToUpdate.getAddress() != null) && (observable != null)){
            tagToUpdate.setValue(String.valueOf(tagToUpdate.getAddress().getCurrentValue()));
            tagToUpdate.setTimestamp(tagToUpdate.getAddress().getTimestamp());
            if (tagToUpdate.getAlarmEnabled()) {
                try {
                    tagToUpdate.setAlarmed(Boolean.parseBoolean(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getAlarmScript()))));
                } catch (ScriptException e) {
                    final String errorMSG = "Error in alarm script." + e.getLocalizedMessage();
                    tagToUpdate.setStatus(errorMSG);
                }
            }
        } else {
            tagToUpdate.setStatus("Null-Address");
        }
    }
}

class TagCurrentValueListenerForMaskUpdating implements InvalidationListener{
    private final Tag tagToUpdate;
    private final Tag tagToListen;

    public TagCurrentValueListenerForMaskUpdating(Tag tagToUpdate, Tag tagToListen) {
        this.tagToUpdate = tagToUpdate;
        this.tagToListen = tagToListen;
    }

    @Override
    public void invalidated(Observable observable) {
        tagToUpdate.setValue(
                String.valueOf(
                        (Integer.getInteger(tagToListen.getValue())) & tagToUpdate.getMask()
                )
        );
        tagToUpdate.setTimestamp(tagToListen.getTimestamp());
        if (tagToUpdate.getAlarmEnabled()) {
            try {
                tagToUpdate.setAlarmed(Boolean.parseBoolean(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getAlarmScript()))));
            } catch (ScriptException e) {
                final String errorMSG = "Error in alarm script." + e.getLocalizedMessage();
                tagToUpdate.setStatus(errorMSG);
            }
        }
    }
}

class TagCurrentValueListenerForScriptUpdating implements InvalidationListener {
    private final Tag           tagToUpdate;
    private final Tag           tagToListen;

    public TagCurrentValueListenerForScriptUpdating(Tag tagToUpdate, Tag tagToListen) {
        this.tagToUpdate    = tagToUpdate;
        this.tagToListen   = tagToListen;
    }

    @Override
    public void invalidated(Observable observable) {
        if(observable != null && tagToUpdate.getScript() != null && !tagToUpdate.getScript().isEmpty() && tagToListen != null){
            try {
                tagToUpdate.setValue(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getScript())));
                tagToUpdate.setTimestamp(tagToListen.getTimestamp() == null ? new Timestamp(System.currentTimeMillis()) : tagToListen.getTimestamp());
                if (tagToUpdate.getAlarmEnabled()) {
                    try {
                        tagToUpdate.setAlarmed(Boolean.parseBoolean(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getAlarmScript()))));
                    } catch (ScriptException e) {
                        final String errorMSG = "Error in alarm script." + e.getLocalizedMessage();
                        tagToUpdate.setStatus(errorMSG);
                    }
                }
            } catch (ScriptException e) {
                final String errorMSG = "Error in tag script." + e.getLocalizedMessage();
                tagToUpdate.setStatus(errorMSG);
            }
        } else {
            tagToUpdate.setStatus("Error: Null-Script");
        }
    }
}

class TagCurrentValueListenerForStatusUpdating implements InvalidationListener{
    private final Tag tagToUpdate;
    private final Tag tagToListen;
    private final Map<String, String> statusMap;

    public TagCurrentValueListenerForStatusUpdating(Tag tagToUpdate, Tag tagToListen, Map<String, String> statusMap) {
        this.tagToUpdate    = tagToUpdate;
        this.tagToListen    = tagToListen;
        this.statusMap      = statusMap;
    }

    @Override
    public void invalidated(Observable observable) {
        try {
            if( (observable != null)){
                if(statusMap.containsKey(tagToListen.getValue())){
                    tagToUpdate.setValue(statusMap.get(tagToListen.getValue()));
                    tagToUpdate.setTimestamp(tagToListen.getTimestamp());
                    if (tagToUpdate.getAlarmEnabled()) {
                        try {
                            tagToUpdate.setAlarmed(Boolean.parseBoolean(String.valueOf(EngineScriptUtil.getInstance().getScriptEngine().eval(tagToUpdate.getAlarmScript()))));
                        } catch (ScriptException e) {
                            final String errorMSG = "Error in alarm script." + e.getLocalizedMessage();
                            tagToUpdate.setStatus(errorMSG);
                        }
                    }
                } else {
                    tagToUpdate.setValue("????");
                    tagToUpdate.setStatus("Status unknown");
                    tagToUpdate.setTimestamp(tagToListen.getTimestamp());
                }
            } else {
                tagToUpdate.setStatus("Null-Source");
            }
        } catch (Exception e){
            LogUtil.logger.error(e);
        }
    }
}