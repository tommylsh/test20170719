package com.maxim.marshal;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class BooleanSerializer extends JsonSerializer<Boolean> {

	@Override
	public void serialize(Boolean arg0, JsonGenerator arg1,
			SerializerProvider arg2) throws IOException,
			JsonProcessingException {
		if(Boolean.TRUE.equals(arg0)) {
			arg1.writeString("Y");
		}
		else if(Boolean.FALSE.equals(arg0)) {
				arg1.writeString("N");
		}
		else {
			arg1.writeString("");
		}
		
	}
}