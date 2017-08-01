package com.maxim.exception;

import java.io.Serializable;

public class StatusValidationException extends BaseException{
    private static final long serialVersionUID = 6126871746041996748L;
    private static final String ERROR_CODE = "F-0016";

    public StatusValidationException(Serializable key) {
        super(ERROR_CODE, key);
    }
}
