package com.maxim.common.util;

import java.util.regex.Pattern;

public interface Constant {

    String DATE_PATTERN = "yyyy-MM-dd";
    String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    String USER_SESSION_ATTRIBUTE = "activeUser";
    String EXCEPTION_ATTRIBUTE = "exception";

    String SLASHES_SEPARATOR = "/";
    String PERIOD_SEPARATOR = ".";
    String COMMA_SEPARATOR = ",";
    String COLON_SEPARATOR = ":";

    String SPACE = " ";
    String EMPTY = "";

    String REMOTE_VALIDATION_KEY = "valid";

    Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");
    Pattern REGISTRY_SPLIT_PATTERN = Pattern.compile("\\s*[|;]+\\s*");
    Pattern SEMICOLON_SPLIT_PATTERN = Pattern.compile("\\s*[;]+\\s*");

    Pattern DATE_FORMAT_PATTERN = Pattern.compile("^\\d{4}(-)\\d{2}(-)\\d{2}(\\s)\\d{2}(:)\\d{2}(:)\\d{2}$", Pattern.CASE_INSENSITIVE);

}
