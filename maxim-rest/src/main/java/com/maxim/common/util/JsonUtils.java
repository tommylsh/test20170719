package com.maxim.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.MessageFormat;

/**
 * Created by Lotic on 2017-04-27.
 */
public class JsonUtils {
    public static String bean2Json(Object bean){
        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(bean);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
    }
}
