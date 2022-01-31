package com.linkedlogics.diameter.exception;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public class IllegalDiameterStateException extends Exception {

    public IllegalDiameterStateException() {
    }

    public IllegalDiameterStateException(String message) {
        super(message);
    }

    public IllegalDiameterStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalDiameterStateException(Throwable cause) {
        super(cause);
    }
}
