package com.linkedlogics.diameter.exception;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public class TransportException extends InternalException {


    public TransportException() {
    }

    public TransportException(double lowThreshold, double highThreshold, double value) {
    }

    public TransportException(String message) {
        super(message);
    }

    public TransportException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransportException(Throwable cause) {
        super(cause);
    }

}
