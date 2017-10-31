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
package org.assemblits.eru.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LabelAppender extends AppenderBase<ILoggingEvent> {
    public static volatile StringProperty lastLog;

    @Override
    protected void append(ILoggingEvent eventObject) {
        final String message = eventObject.getFormattedMessage();
        try {
            Platform.runLater(() -> {
                try {
                    if (lastLog != null) {
                        lastLog.setValue(message);
                    }
                } catch (final Throwable t) {
                    log.error("LabelAppender error:", t);
                }
            });
        } catch (final IllegalStateException ignore) {
        }
    }
}
