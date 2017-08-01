package com.maxim.common.exception;

import java.io.Serializable;

public class BizException extends AbstractException {

    private static final long serialVersionUID = 1L;

    private static final String ERROR_CODE = "F-0003";

    public BizException(Serializable detailMessage) {
        super(ERROR_CODE, detailMessage);
    }

    public BizException(Serializable detailMessage, Throwable cause) {
        super(ERROR_CODE, detailMessage, cause);
    }

}
