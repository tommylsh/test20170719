package com.maxim.ws.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class JsonDeserializeException extends WebApplicationException {

	/**
	 * @author Steven
	 */
	private static final long serialVersionUID = 20130825L;

	private static final String ERROR_CODE = "T-0008";

	public JsonDeserializeException(String message) {
		super(Response.status(Response.Status.BAD_REQUEST).entity(message)
				.type(MediaType.TEXT_PLAIN).build());
	}
}
