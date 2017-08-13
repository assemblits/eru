package com.eru.logger;

import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by mtrujillo on 2/13/2016.
 */
public class LiveAppender extends WriterAppender {

    private static volatile String lastLog = "";

    @Override
    public void append(LoggingEvent event) {
        lastLog = getLayout().format(event);
    }

    public static String getLastLog() {
        return lastLog;
    }
}
