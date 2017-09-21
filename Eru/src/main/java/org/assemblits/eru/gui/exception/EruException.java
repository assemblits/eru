package org.assemblits.eru.gui.exception;

public class EruException extends RuntimeException {
    public EruException(String message, Throwable e) {
        super(message, e);
    }
}
