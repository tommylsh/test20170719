package com.maxim.marshal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd");
	
	public static String format(Date v) {
		return dateFormat.format(v);
	}


	public static Date parse(String v) throws ParseException {
		if(v == null || v.isEmpty()) {
			return null;
		}
		else {
			return dateFormat.parse(v);
		}
	}
}
