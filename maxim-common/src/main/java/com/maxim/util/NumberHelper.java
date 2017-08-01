package com.maxim.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;

public class NumberHelper {

    private static int DEFAULT_AMOUNT_SCALE = 2;

    public static BigDecimal updateDecimal(BigDecimal value, int scale) {
        return value.setScale(scale, RoundingMode.HALF_UP);
    }
    
    public static BigDecimal updateDecimal(BigDecimal value) {
        return updateDecimal(value, DEFAULT_AMOUNT_SCALE);
    }
    
    public static BigDecimal trimNull(BigDecimal value) {
        if (value == null) {
            return new BigDecimal(0);
        } else {
            return value;
        }
    }
    
    public static Long trimNull(Long value) {
        if (value == null) {
            return new Long(0);
        } else {
            return value;
        }
    }

    public static boolean isNumberic(String investPaymentDeductDateValue) {
        if (StringUtils.isEmpty(investPaymentDeductDateValue)) {
            return false;
        }
        
        try {
            Double.parseDouble(investPaymentDeductDateValue);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static String format(double value) {
        DecimalFormat df = new DecimalFormat("0");
        return df.format(value);
    }
    
}
