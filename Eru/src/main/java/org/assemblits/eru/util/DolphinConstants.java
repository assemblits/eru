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

public class DolphinConstants {
    public static final String TYPE_SYSTEM                  = "system";
    public static final String SYSTEM_PM_ID                 = "system.pm.id";
    public static final String ATT_SYSTEM_ID                = "system.id";
    public static final String ATT_SYSTEM_OK                = "system.ok";
    public static final String ATT_SYSTEM_STATUS            = "system.status";
    public static final String ATT_COMMUNICATIONS_CONNECTED = "system.protocols.connected";
    public static final String ATT_COMMUNICATIONS_BLOCKED   = "system.protocols.blocked";
    public static final String ATT_HISTORIAN_RUNNING        = "system.export.running";
    public static final String ATT_DATABASE_LOADED          = "system.database.loaded";

    public static final String TYPE_TO_WRITE                = "to_write";
    public static final String TAG_TO_WRITE_PM_ID           = "to_write.tag.pm.id";
    public static final String ATT_TO_WRITE_NAME            = "to_write.tag.name";
    public static final String ATT_TO_WRITE_NEWVALUE        = "to_write.tag.new_value";


    public static final String TYPE_TAG                     = "tag";
    public static final String TAGS_PM_ID                   = "tags.pm.id";
    public static final String ATT_TAG_VALUE                = "tag.value";

    public static final String TYPE_ALARM                   = "alarm";
    public static final String ATT_ALARM_DATE               = "alarm.date";
    public static final String ATT_ALARM_GROUP              = "alarm.group";
    public static final String ATT_ALARM_DESCRIPTION        = "alarm.description";
    public static final String ATT_ALARM_ACK                = "alarm.ack";

    public static final String TYPE_USER                    = "user.pm.id";
    public static final String USER_PM_ID                   = "user.pm.id";
    public static final String ATT_USER_NAME                = "user.name";
    public static final String ATT_USER_PASSWORD            = "user.password";
    public static final String ATT_USER_LOGGED              = "user.logged";

    public static String tagQualifier(String name, String propertyName) {
        return TYPE_TAG + "." + name + "." + propertyName;
    }

    public static String alarmQualifier(String name, String propertyName) {
        return TYPE_ALARM + "." + name + "." + propertyName;
    }

    public static String tagToWriteQualifier(String name, String propertyName) {
        return TYPE_TO_WRITE + "." + name + "." + propertyName;
    }
}
