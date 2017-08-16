package com.eru.exception;

/**
 * Created by mtrujillo on 8/15/17.
 */
public class TagLinkException extends RuntimeException {
    public TagLinkException() {
    }

    public TagLinkException(String message) {
        super(message);
    }

    public TagLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
