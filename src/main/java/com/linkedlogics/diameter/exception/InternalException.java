package com.linkedlogics.diameter.exception;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public class InternalException extends Exception {

    public InternalException() {
    }

    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }

    public InternalException(Throwable cause) {
        super(cause);
    }
}
