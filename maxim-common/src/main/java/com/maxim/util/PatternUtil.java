package com.maxim.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public abstract class PatternUtil {

    /**
     * @param pattern Using comma(,) as a separator e.g. '(.*).csv,(.*).txt'
     * @return Pattern List
     */
    public static List<Pattern> buildPatterns(String pattern) {
        if (pattern != null && pattern.trim().length() != 0) {
            List<Pattern> list = new ArrayList<Pattern>();
            String[] tokens = pattern.split("\\s*[,]\\s*");
            for (String token : tokens) {
                list.add(Pattern.compile(token.trim()));
            }
            return Collections.unmodifiableList(list);
        }
        return Collections.emptyList();
    }

    /**
     * @param pattern Using comma(,) as a separator e.g. '(.*).csv,(.*).txt'
     * @param input   The character sequence to be matched
     * @return true|false
     */
    public static boolean match(String pattern, CharSequence input) {
        return match(buildPatterns(pattern), input);
    }

    /**
     * @param patterns Pattern List
     * @param input    The character sequence to be matched
     * @return true|false
     */
    public static boolean match(List<Pattern> patterns, CharSequence input) {
        if (patterns != null && !patterns.isEmpty()) {
            for (Pattern pattern : patterns) {
                if (pattern.matcher(input).matches()) {
                    return true;
                }
            }
        }
        return false;
    }

}
