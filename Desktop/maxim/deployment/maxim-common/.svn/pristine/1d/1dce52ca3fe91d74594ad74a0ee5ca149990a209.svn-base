package com.maxim.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {

//    public static DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
    public static DateFormat DATE_FORMAT_2 = new SimpleDateFormat("yyyyMMdd");
//    public static DateFormat datetimeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    public static String format(Date date) {
    	DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        if (date != null) {
            return dateFormatter.format(date);
        } else {
            return "";
        }
    }

    public static String format(Date date, DateFormat dateFormat) {
    	DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        if (date != null) {
            if (dateFormat == null) {
                dateFormat = dateFormatter;
            }
            return dateFormat.format(date);
        } else {
            return "";
        }
    }
    
    public static String formatDateTime(Date date) {
    	DateFormat datetimeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (date != null) {
            return datetimeFormatter.format(date);
        } else {
            return "";
        }
    }
    
    public static Date parseAsDate(String text) {
    	DateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
        try {
            return dateFormatter.parse(text);
        } catch (ParseException e) {
            return null;
        }
    }
    
    public static Date parseAsDataTime(String text) {
    	DateFormat datetimeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            return datetimeFormatter.parse(text);
        } catch (ParseException e) {
            return null;
        }
    }

}
