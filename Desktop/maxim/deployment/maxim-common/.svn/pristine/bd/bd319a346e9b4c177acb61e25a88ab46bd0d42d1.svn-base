package com.maxim.util.meta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

    public static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static DateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public static String formatDate(Date date) {
        if (date != null) {
            return DATE_FORMAT.format(date);
        } else {
            return "";
        }
    }

    public static String formatDateTime(Date date) {
        if (date != null) {
            return DATETIME_FORMAT.format(date);
        } else {
            return "";
        }
    }

    public static String formatTime(Date date) {
        if (date != null) {
            return TIME_FORMAT.format(date);
        } else {
            return "";
        }
    }

    public static Date parseAsDate(String text) {
        try {
            return DATE_FORMAT.parse(text);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parseAsDateTime(String text) {
        try {
            return DATETIME_FORMAT.parse(text);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parseAsTime(String text) {
        try {
            return TIME_FORMAT.parse(text);
        } catch (Exception e) {
            return null;
        }
    }

}
