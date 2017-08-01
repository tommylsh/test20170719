package com.maxim.common.exception;

import java.io.Serializable;

public class ValidationException extends AbstractException {

    private static final long serialVersionUID = 20160809L;

    private static final String ERROR_CODE = "F-0004";

    public ValidationException(Serializable detailMessage) {
        super(ERROR_CODE, detailMessage);
    }

    public ValidationException(Serializable detailMessage, Throwable cause) {
        super(ERROR_CODE, detailMessage, cause);
    }

}
