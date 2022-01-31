package com.linkedlogics.diameter.exception;

import com.linkedlogics.diameter.object.Avp;

/**
 * Created by shnovruzov on 15/5/2018.
 */
public class AvpException extends Exception{

    protected Avp avp;

    public AvpException(Avp avp) {
        this.avp = avp;
    }

    public AvpException(String message, Avp avp) {
        super(message);
        this.avp = avp;
    }

    public AvpException(String message, Throwable cause, Avp avp) {
        super(message, cause);
        this.avp = avp;
    }

    public AvpException(Throwable cause, Avp avp) {
        super(cause);
        this.avp = avp;
    }

    public AvpException() {
    }

    public AvpException(String message) {
        super(message);
    }

    public AvpException(String message, Throwable cause) {
        super(message, cause);
    }

    public AvpException(Throwable cause) {
        super(cause);
    }

    public Avp getAvp() {
        return this.avp;
    }
}
