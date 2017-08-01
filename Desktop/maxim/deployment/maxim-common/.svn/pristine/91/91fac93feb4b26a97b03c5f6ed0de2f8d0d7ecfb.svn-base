package com.maxim.marshal;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class DateSerializer extends JsonSerializer<Date> {
	@Override
	public void serialize(Date arg0, JsonGenerator arg1, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		arg1.writeString(DateFormatter.format(arg0));
	}
}