package com.maxim.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class DateUtil {
	
	public static Date trimTimePart(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static boolean dateEquals(Date date1, Date date2) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);

		if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
				&& cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)
				&& cal1.get(Calendar.DATE) == cal2.get(Calendar.DATE)) {
			return true;
		}

		return false;
	}

	/**
	 * Compare the date with startDate and endDate
	 * 
	 * @param startDate
	 * @param endDate
	 * @param date
	 * @return
	 */
	public static boolean compareDate(Date startDate, Date endDate, Date date) {
		date = getDate(date);
		if (date.compareTo(getDate(startDate)) >= 0
				&& date.compareTo(getDate(endDate)) <= 0) {
			return true;
		}
		return false;
	}

	public static boolean compareTate(Date startDate, Date endDate) {
		if (startDate.compareTo(endDate) >= 0) {
			return true;
		}
		return false;
	}

	public static Date getDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTime();
	}

	public static Date parse(String dateStr, String format) {
		if (dateStr == null || "".equals(dateStr)) {
			return null;
		}
		Date date = null;
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		try {
			date = dataFormat.parse(dateStr);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return date;
	}

	public static String format(Date date, String format) {
		SimpleDateFormat dataFormat = new SimpleDateFormat(format);
		return dataFormat.format(date);
	}
	
	public static java.sql.Date getSQLDate(java.util.Date date)
	{
		return new java.sql.Date(date.getTime());
	}
	public static Date getCurrentUtilDate()
	{
		return new Date();
	}
	public static java.sql.Date getCurrentDate()
	{
		return new java.sql.Date(System.currentTimeMillis());
	}
	public static String getCurrentDateString()
	{
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(getCurrentDate());
	}

	public static Timestamp getCurrentTimestamp()
	{
		return new Timestamp(System.currentTimeMillis());
	}
	
}
