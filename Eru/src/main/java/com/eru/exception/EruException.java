package com.eru.exception;

public class EruException extends RuntimeException {
    public EruException(String message, Throwable e) {
        super(message, e);
    }
}
