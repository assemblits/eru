package com.eru.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by mtrujillo on 9/2/2015.
 */
@Slf4j
public class LabelAppender extends AppenderBase<ILoggingEvent> {
    private static volatile StringProperty lastLog;

    public static void setObservableString(StringProperty observableString) {
        LabelAppender.lastLog = observableString;
    }

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
