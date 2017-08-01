package com.maxim.exception;

import java.io.Serializable;

public class ParameterIsEmptyException extends BaseException {
    
    private static final long serialVersionUID = 20160201L;

    private static final String ERROR_CODE = "F-0015";

    public ParameterIsEmptyException(Serializable key) {
        super(ERROR_CODE, key);
    }
    
}