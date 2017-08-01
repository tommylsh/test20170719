package com.maxim.marshal;

import java.io.IOException;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class ClassDeserializer extends JsonDeserializer<Class<?>> {

	@Override
	public Class<?> deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		String v = arg0.getText();

		if (v == null || v.isEmpty()) {
			return null;
		} else {
			try {
				return Class.forName(v);
			} catch (Exception e) {
				throw new IOException("Unknown class: " + v);
			}
		}
	}
}