package com.maxim.util;

import org.slf4j.Logger;

public class LoggerHelper {

    private static final String INFO = "[INFO]  ";
    private static final String ERROR = "[ERROR] ";
    private static final String WARN = "[WARN]  ";

    public static String formatInfoLog(String log, Object... args) {
        return formatLog(INFO, log, args);
    }

    public static String formatErrorLog(String log, Object... args) {
        return formatLog(ERROR, log, args);
    }

    public static String formatWarnLog(String log, Object... args) {
        return formatLog(WARN, log, args);
    }

    private static String formatLog(String levelType, String log, Object... args) {
        return String.format(new StringBuffer(levelType).append(log).toString(), args);
    }

    public static void logInfo(Logger logger, String log, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(formatInfoLog(log, args));
        }
    }

    public static void logError(Logger logger, String log, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(formatErrorLog(log, args));
        }
    }
    
    public static void logError(Throwable t,Logger logger, String log, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(formatErrorLog(log, args),t);
        }
    }

    public static void logWarn(Logger logger, String log, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(formatWarnLog(log, args));
        }
    }

}
