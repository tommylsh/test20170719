package com.maxim.marshal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class DatetimeSerializer extends JsonSerializer<Date> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	public void serialize(Date arg0, JsonGenerator arg1, SerializerProvider arg2)
			throws IOException, JsonProcessingException {
		arg1.writeString(dateFormat.format(arg0));
	}


}