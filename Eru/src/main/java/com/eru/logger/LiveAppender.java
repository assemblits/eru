package com.eru.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * Created by mtrujillo on 2/13/2016.
 */
public class LiveAppender extends AppenderBase<ILoggingEvent> {

    private static volatile String lastLog = "";

    public static String getLastLog() {
        return lastLog;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        lastLog = eventObject.getFormattedMessage();
    }
}
