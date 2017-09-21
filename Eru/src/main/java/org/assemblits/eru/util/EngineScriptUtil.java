package org.assemblits.eru.util;



import org.assemblits.eru.entities.Tag;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by Marlon on 23/11/2014.
 */
public class EngineScriptUtil {
    private static final EngineScriptUtil   ourInstance         = new EngineScriptUtil();
    private final  ScriptEngineManager      scriptEngineManager = new ScriptEngineManager();
    private final ScriptEngine              scriptEngine;

    public static EngineScriptUtil getInstance() {
        return ourInstance;
    }

    private EngineScriptUtil() {
        scriptEngine = scriptEngineManager.getEngineByName("JavaScript");
    }

    public ScriptEngine getScriptEngine() {
        return scriptEngine;
    }

    public void loadTag(Tag tag){
        if (!scriptEngineManager.getBindings().containsKey(tag.getName())) {
            scriptEngineManager.getBindings().put(tag.getName(), tag);
        }
    }
}
