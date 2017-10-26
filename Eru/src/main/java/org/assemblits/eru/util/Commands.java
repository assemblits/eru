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

public class Commands {
    public static final String INITIALIZE_SYSTEM             = "engine.initialize.system";
    public static final String LOAD_DATABASE                 = "database.load";
    public static final String PAUSE_SYNCHRONIZATION         = "engine.synchronize.pause";
    public static final String PAUSE_ALL_SYNCHRONIZATIONS    = "engine.synchronize.all.pause";
    public static final String CONTINUE_SYNCHRONIZATION      = "engine.synchronize.continue";
    public static final String CONTINUE_ALL_SYNCHRONIZATIONS = "engine.synchronize.all.continue";
    public static final String INITIALIZE_PM                 = "engine.initialize.pm";
    public static final String SYNCHRONIZE_PM                = "engine.synchronize.pm";
    public static final String SYNCHRONIZE_SYSTEM_PM         = "engine.synchronize.system.pm";
    public static final String ACKNOWLEDGE_ALARMS            = "alarms.acknowledge";
    public static final String CHECK_USER_ENTRY              = "user.check.entry";
    public static final String START_COMMUNICATIONS          = "user.protocols.load";
    public static final String STOP_COMMUNICATIONS           = "user.protocols.stopDirector";
    public static final String BLOCK_COMMUNICATIONS          = "user.protocols.block";
    public static final String RELEASE_COMMUNICATIONS        = "user.protocols.release";
    public static final String START_HISTORIAN               = "user.export.load";
    public static final String STOP_HISTORIAN                = "user.export.stopDirector";
    public static final String START_ALARMING                = "user.alarming.load";
    public static final String STOP_ALARMING                 = "user.alarming.stopDirector";
    public static final String WRITE_TAG                     = "write.tag";

}
