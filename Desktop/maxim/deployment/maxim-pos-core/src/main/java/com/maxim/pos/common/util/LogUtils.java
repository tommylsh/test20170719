package com.maxim.pos.common.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxim.pos.common.enumeration.PollSchemeType;

/**
 * LOG
 * 
 * @author Lotic
 *
 */
public class LogUtils {

	public static final Logger LOGGER = LoggerFactory.getLogger(LogUtils.class);
	public static final Logger SALES_REALTIME_LOGGER = LoggerFactory.getLogger(PollSchemeType.SALES_REALTIME.name());
	public static final Logger SALES_EOD_LOGGER = LoggerFactory.getLogger(PollSchemeType.SALES_EOD.name());
	public static final Logger MASTER_LOGGER = LoggerFactory.getLogger(PollSchemeType.MASTER.name());

	private static final ThreadLocal<Logger> CURRENT_THREAD_LOGGER = new ThreadLocal<Logger>();

	public static void printObject(Logger logger,Object object){
		ObjectMapper mapper = new ObjectMapper();
		try {
			printLog(logger,mapper.writeValueAsString(object));
		} catch (IOException e) {
			printException("object convert to json excepiton",e);
		}
	}

	/**
	 * get Exception Stack Ttrace String
	 * 
	 * @param t
	 * @return
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			t.printStackTrace(pw);
			return sw.toString();
		} finally {
			pw.close();
		}
	}

	public static void setCurrentThreadLogger(Logger logger) {

		CURRENT_THREAD_LOGGER.set(logger);

	}

	public static Logger getCurrentThreadLogger() {
		Object obj = CURRENT_THREAD_LOGGER.get();
		if (obj != null) {
			return (Logger) obj;
		}
		return null;
	}
	
	

	public static void printLog(Logger logger, String format, Object... arguments) {
//		if (getCurrentThreadLogger() != null) {
//			logger =getCurrentThreadLogger();
//		}
		if (logger == null) {
			logger =getCurrentThreadLogger();
		}
		if (logger == null) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(format, arguments);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(format, arguments);
			}
		}

	}

	public static void printLog(String format, Object... arguments) {
		printLog(null, format, arguments);
//		if (getCurrentThreadLogger() != null) {
//			getCurrentThreadLogger().info(format, arguments);
//		}
//		if (LOGGER.isInfoEnabled()) {
//			LOGGER.info(format, arguments);
//		}
	}

	public static void printLog(String message) {

		if (getCurrentThreadLogger() != null) {
			getCurrentThreadLogger().info(message);
		} else {
			LOGGER.info(message);
		}
	}

	public static void printException(String message, Exception exception) {
		if (getCurrentThreadLogger() != null) {
			getCurrentThreadLogger().error(message, exception);
		} else {
			LOGGER.error(message, exception);
		}

	}

	public static void printException(Logger logger, String message, Exception exception) {
		if (getCurrentThreadLogger() != null) {
			logger =getCurrentThreadLogger();
		}
		if (logger == null) {
			printException(message, exception);
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(message, exception);
			}
		}

	}

	public static void printException(Logger logger, String format, Object... arguments) {
//		if (getCurrentThreadLogger() != null) {
//			logger =getCurrentThreadLogger();
//		}
		if (logger == null) {
			if (getCurrentThreadLogger() != null) {
				getCurrentThreadLogger().error(format, arguments);
			} else {
				LOGGER.error(format, arguments);
			}
		} else {
			if (logger.isErrorEnabled()) {
				logger.error(format, arguments);
			}
		}

	}


}