package com.maxim.util;

public class MaskingUtil {

    private static final String MASKING_CHAR = "x";
    private static final int MIN_LENGTH = 8;
    private static final int START_POSITION = 5;

    public static String mask(String value) {
        if (value != null) {
            if (value.length() >= START_POSITION) {
                StringBuilder builder = new StringBuilder(value);
                int endPosition = value.length() > MIN_LENGTH ? 8 : value.length();
                builder.replace(START_POSITION, endPosition,
                        getMaskingString(MASKING_CHAR, endPosition - START_POSITION + 1));

                return builder.toString();
            }
            return value;
        } else {
            return null;
        }
    }

    private static String getMaskingString(String str, int length) {
        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < length; i++) {
            buffer.append(str);
        }

        return buffer.toString();
    }

}
