package com.maxim.rest;

import com.maxim.api.model.ResponseData;
import com.maxim.common.exception.AbstractException;
import com.maxim.common.exception.UnknownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionMapper.class);

    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof WebApplicationException) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ex.getMessage())
                    .type(MediaType.TEXT_PLAIN)
                    .build();
        }
        LOGGER.error("Request failed.", ex);
        AbstractException exception = ex instanceof AbstractException ? (AbstractException) ex : new UnknownException(ex.getMessage());
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ResponseData().setSuccess(Boolean.FALSE).setCode(exception.getErrorCode()).setMessage(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}
