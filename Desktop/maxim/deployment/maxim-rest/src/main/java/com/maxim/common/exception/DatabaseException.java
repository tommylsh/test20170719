package com.maxim.common.exception;

import java.io.Serializable;

public class DatabaseException extends AbstractException {

    private static final long serialVersionUID = 1L;

    private static final String ERROR_CODE = "F-0002";

    public DatabaseException(Serializable detailMessage) {
        super(ERROR_CODE, detailMessage);
    }

    public DatabaseException(Serializable detailMessage, Throwable cause) {
        super(ERROR_CODE, detailMessage, cause);
    }

}
