package com.marlontrujillo.eru.exception;

import static java.lang.String.format;

public class LoadPropertiesFileException extends RuntimeException {

    public LoadPropertiesFileException(String message) {
        super(message);
    }

    public LoadPropertiesFileException(String message, Throwable cause) {
        super(message, cause);
    }
}
