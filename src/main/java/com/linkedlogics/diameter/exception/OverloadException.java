package com.linkedlogics.diameter.exception;

/**
 * Created by shnovruzov on 25/5/2018.
 */
public class OverloadException extends InternalException {

    double lowThreshold;
    double highThreshold;
    double value;

    public OverloadException() {
    }

    public OverloadException(double lowThreshold, double highThreshold, double value) {
        this.lowThreshold = lowThreshold;
        this.highThreshold = highThreshold;
        this.value = value;
    }

    public OverloadException(String message) {
        super(message);
    }

    public OverloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public OverloadException(Throwable cause) {
        super(cause);
    }

    public double getLowThreshold() {
        return this.lowThreshold;
    }

    public double getHighThreshold() {
        return this.highThreshold;
    }

    public double getValue() {
        return this.value;
    }
}
