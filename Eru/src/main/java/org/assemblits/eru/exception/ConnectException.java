package org.assemblits.eru.exception;

/**
 * Created by marlontrujillo1080 on 10/31/17.
 */
public class ConnectException extends java.net.ConnectException {
    public ConnectException(String localizedMessage) {
        super(localizedMessage);
    }
}
