package com.maxim.json;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;

public class CustomDateDeserializer extends JsonDeserializer<Date> {

    public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public synchronized Date deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        try {
            String text = parser.getText();
            return (text != null && !text.trim().equals("")) ? dateFormat
                    .parse(text) : null;
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
