package com.eru.logger;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import lombok.extern.log4j.Log4j;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by mtrujillo on 9/2/2015.
 */
@Log4j
public class TextAreaAppender extends WriterAppender {
    private static volatile TextArea textArea;

    @Override
    public void append(LoggingEvent event) {
        final String message = this.layout.format(event);
        // Append formatted message to text area using the Thread.
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
        } catch (final IllegalStateException e) {
            // ignore case when the platform hasn't yet been iniitialized
        }
    }

    public static void setTextArea(TextArea textArea) {
        TextAreaAppender.textArea = textArea;
    }
}
