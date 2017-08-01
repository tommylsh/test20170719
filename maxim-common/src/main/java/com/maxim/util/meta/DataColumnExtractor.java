package com.maxim.util.meta;

import java.util.Map;

public interface DataColumnExtractor {

    public String extract(Map<String, Object> element, Object value);

}
