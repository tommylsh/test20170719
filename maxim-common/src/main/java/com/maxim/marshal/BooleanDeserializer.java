package com.maxim.marshal;

import java.io.IOException;
import java.util.Collections;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

import com.maxim.exception.ValidationException;
import com.maxim.exception.ValidationException.Violation;

public class BooleanDeserializer extends JsonDeserializer<Boolean> {

	@Override
	public Boolean deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		String v = arg0.getText();
		if(v == null || v.isEmpty()) {
			return null;
		}
		
		else {
			if("Y".equals(v)) {
				return Boolean.TRUE;
			}
			else if("N".equals(v)) {
				return Boolean.FALSE;
			}
			throw new ValidationException(Collections.singletonList(new Violation("Invalid boolean value: "+v)));
		}
	}
}