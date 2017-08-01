package com.maxim.exception;


import java.io.Serializable;

/**
 * Whenever a Query is issue to a method which is not supported, this exception
 * will be thrown
 *
 * @author SPISTEV
 */
public class UnknownException extends BaseException {


    /**
     *
     */
    private static final long serialVersionUID = 130628L;

    private static final String ERROR_CODE = "T-9999";

    public UnknownException(Throwable cause) {
        super(ERROR_CODE, cause);
    }

    public UnknownException(Serializable additionalContext, Throwable cause) {
        super(ERROR_CODE, additionalContext, cause);
    }

}
