package com.maxim.ws;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.maxim.ws.exception.JsonDeserializeException;

@Provider
@Consumes({MediaType.APPLICATION_JSON, "text/json"})
@Produces({MediaType.APPLICATION_JSON, "text/json"})
public class ExceptionWrappedJsonProvider extends JacksonJsonProvider {
	
	@Override
	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException {
		
		try {
		return super.readFrom(type, genericType, annotations, mediaType, httpHeaders,
				entityStream);
		} catch(IOException e) {
			throw new JsonDeserializeException(e.getMessage());
		}
	}
	
}
