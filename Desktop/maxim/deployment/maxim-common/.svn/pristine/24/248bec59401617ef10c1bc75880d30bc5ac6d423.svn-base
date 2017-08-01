package com.maxim.json;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class CustomDateSerializer extends JsonSerializer<Date> {

    public DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public synchronized void serialize(Date value, JsonGenerator generator,
            SerializerProvider seriProvider) throws IOException,
            JsonProcessingException {
        generator.writeString(dateFormat.format(value));
    }
}
