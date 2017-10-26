/******************************************************************************
 * Copyright (c) 2017 Assemblits contributors                                 *
 *                                                                            *
 * This file is part of Eru The open JavaFX SCADA by Assemblits Organization. *
 *                                                                            *
 * Eru The open JavaFX SCADA is free software: you can redistribute it        *
 * and/or modify it under the terms of the GNU General Public License         *
 *  as published by the Free Software Foundation, either version 3            *
 *  of the License, or (at your option) any later version.                    *
 *                                                                            *
 * Eru is distributed in the hope that it will be useful,                     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU General Public License for more details.                               *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.            *
 ******************************************************************************/
package org.assemblits.eru.util;

import org.assemblits.eru.entities.Tag;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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
