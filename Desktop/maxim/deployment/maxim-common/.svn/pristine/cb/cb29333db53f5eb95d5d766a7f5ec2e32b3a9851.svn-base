package com.maxim.marshal;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class DateDeserializer extends JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		String v = arg0.getText();

		if (v == null || v.isEmpty()) {
			return null;
		} else {
			try {
				return DateFormatter.parse(v);
			} catch (ParseException e) {
				return null;
			}
		}
	}
}