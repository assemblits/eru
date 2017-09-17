package com.eru.logger;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by mtrujillo on 9/2/2015.
 */
@Slf4j
public class TextAreaAppender extends AppenderBase<ILoggingEvent> {
    private static volatile TextArea textArea;

    public static void setTextArea(TextArea textArea) {
        TextAreaAppender.textArea = textArea;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        final String message = eventObject.getFormattedMessage();
        try {
            Platform.runLater(() -> {
                try {
                    if (textArea != null) {
                        if (textArea.getText().length() == 0) {
                            textArea.setText(message);
                        } else {
                            textArea.selectEnd();
                            textArea.insertText(textArea.getText().length(), message);
                        }
                    }
                } catch (final Throwable t) {
                    log.error("TextAreaAppender error:", t);
                }
            });
        } catch (final IllegalStateException ignored) {
        }
    }
}
