package com.maxim.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class JsonUtils {

    public static final String EMPTY_STRING = " ";
    public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
            .setDateFormat(DATETIME_PATTERN)
            .create();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static void toJson(Object obj, Writer writer) {
        gson.toJson(obj, writer);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(Reader reader, Class<T> classOfT) {
        return gson.fromJson(reader, classOfT);
    }
    public static <T> T fromJson(Reader reader, Type typeOfT) {
        return gson.fromJson(reader, typeOfT);
    }

    static class NullStringToEmptyAdapterFactory implements TypeAdapterFactory {
        @Override
        @SuppressWarnings("unchecked")
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

    static class StringAdapter extends TypeAdapter<String> {
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return EMPTY_STRING;
            }
            return reader.nextString();
        }

        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null || "".equals(value)) {
                writer.value(EMPTY_STRING);
                return;
            }
            writer.value(value);
        }
    }

}