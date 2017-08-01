package com.maxim.marshal;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class ClassSerializer extends JsonSerializer<Class<?>> {
	@Override
	public void serialize(Class<?> arg0, JsonGenerator arg1,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		arg1.writeString(arg0.getName());
	}
}