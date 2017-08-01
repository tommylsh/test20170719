package com.maxim.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SysParamsConstant {

    private static final Map<String, String> params = new ConcurrentHashMap<String, String>();

    public static String getParamValue(String paramName) {
        return params.get(paramName);
    }

    public static void setParams(Map<String, String> params) {
        SysParamsConstant.params.putAll(params);
    }
    
    public static Map<String, String> getParams(){
        return params;
    }

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm:ss";

    public static String getDateFormat() {
        String paramName = "DATE_FORMAT";
        return params.get(paramName) == null ? DATE_FORMAT : params.get(paramName);
    }

    public static String getDateTimeFormat() {
        String paramName = "DATE_TIME_FORMAT";
        return params.get(paramName) == null ? DATE_TIME_FORMAT : params.get(paramName);
    }

}
