package com.maxim.pos.common.exception;

import com.maxim.i18n.MessageSource;

public class PosException extends RuntimeException {

    private static final long serialVersionUID = -5409266695923414590L;

    protected String errorMessage;

    public PosException(MessageSource messageSource, Object[] args) {
        errorMessage = messageSource.getMessage(this.getClass().getSimpleName(), args);
    }

    public PosException(MessageSource messageSource) {
        errorMessage = messageSource.getMessage(this.getClass().getSimpleName(), null);
    }

    public PosException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }

}
