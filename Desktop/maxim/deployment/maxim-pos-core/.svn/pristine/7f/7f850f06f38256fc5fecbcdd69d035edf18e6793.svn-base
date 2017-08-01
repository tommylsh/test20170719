package com.maxim.pos.common.util;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.map.ObjectMapper;

public class HelperUtils {
	public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	public static String beanToJson(Object obj) {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(obj);
		} catch (Exception e) {
			LogUtils.printException(MessageFormat.format("HelperUtils.beanToJson Line {}  Exception", 15), e);
		}
		return null;
	}

	public static String getFormatDate() {

		return SIMPLE_DATE_FORMAT.format(new Date());
	}

	public static String getFormatDate(Date date) {

		return SIMPLE_DATE_FORMAT.format(date);
	}

}
