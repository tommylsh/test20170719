package com.maxim.util.meta;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxim.util.LoggerHelper;
import com.maxim.util.NumberHelper;


public class DataColumn<T> {

    private static final int SCALE = 2;
    private static final DecimalFormat PERCENTAGE_FORMAT = new DecimalFormat(".0%");
    public static final Logger logger = LoggerFactory.getLogger(DataColumn.class);
    public static final int DEFAULT_CELL_WIDTH = 2632 * 2;
    public static final double BASE_LENGTH = 18;
    public static final double MULTIPLE = 3.0d;

    private String headerName;
    private String keyName;
    private Class<T> clazz;
    private DataType dataType;
    private boolean sequenceType = false;
    private int scale = SCALE;
    private DataColumnExtractor extractor;
    private int columnWidth = DEFAULT_CELL_WIDTH;

    public DataColumn() {
    }

    public DataColumn(String headerName, String keyName, Class<T> clazz) {
        this.headerName = headerName;
        this.keyName = keyName;
        this.clazz = clazz;
        if (String.class.equals(this.clazz)) {
            this.dataType = DataType.PRIMITIVE;
        } else if (Integer.class.equals(this.clazz)) {
            this.dataType = DataType.PRIMITIVE;
        } else if (Long.class.equals(this.clazz)) {
            this.dataType = DataType.PRIMITIVE;
        } else if (BigDecimal.class.equals(this.clazz)) {
            this.dataType = DataType.DECIMAL;
        } else if (Date.class.equals(this.clazz)) {
            this.dataType = DataType.DATE;
        }
    }

    public DataColumn(String headerName, boolean sequenceType) {
        this.headerName = headerName;
        this.sequenceType = sequenceType;
    }

    public DataColumn(String headerName, String keyName, Class<T> clazz, DataColumnExtractor extractor) {
        this(headerName, keyName, clazz);
        this.extractor = extractor;
    }

    public DataColumn(String headerName, String keyName, Class<T> clazz, DataType dataType) {
        this(headerName, keyName, clazz);
        this.dataType = dataType;
        if (DataType.PERCENTAGE.equals(this.dataType)) {
            PERCENTAGE_FORMAT.setMinimumIntegerDigits(scale);
        }
    }

    public DataColumn(String headerName, String keyName, Class<T> clazz, DataType dataType, int scale) {
        this(headerName, keyName, clazz, dataType);
        this.scale = scale;
    }

    public String format(Map<String, Object> element) {
        Object _value;
        try {
            _value = element.get(getKeyName());
            if (_value == null) {
                if (BigDecimal.class.equals(this.clazz)) {

                    return convertNumber(new BigDecimal(0.0));

                } else if (Integer.class.equals(this.clazz)) {
                    return new Integer(0).toString();
                } else if (Long.class.equals(this.clazz)) {
                    return new Integer(0).toString();
                } else {
                    return StringUtils.EMPTY;
                }
            } else {
                Object value = null;
                if (String.class.equals(this.clazz)) {
                    value = (String) _value;
                } else if (Integer.class.equals(this.clazz)) {
                    value = String.valueOf((Integer) _value);
                } else if (Long.class.equals(this.clazz)) {
                    value = String.valueOf((Long) _value);
                } else if (BigDecimal.class.equals(this.clazz)) {

                    value = convertNumber(_value);

                } else if (Date.class.equals(this.clazz)) {
                    if (DataType.DATE.equals(getDataType())) {
                        value = DateHelper.formatDate((Date) _value);
                    } else if (DataType.DATETIME.equals(getDataType())) {
                        value = DateHelper.formatDateTime((Date) _value);
                    } else if (DataType.TIME.equals(getDataType())) {
                        value = DateHelper.formatTime((Date) _value);
                    }
                }

                if (value != null) {
                    if (getExtractor() != null) {
                        value = (String) getExtractor().extract(element, value);
                    }

                    String stringValue = (String) value;
                    columnWidth = caculateColumnWidth(stringValue);

                    return stringValue;
                }
            }
            
            if (logger.isErrorEnabled()) {
                logger.error(LoggerHelper.formatErrorLog("Occuring error when formating dataColumn :%s", this));
            }
            throw new RuntimeException("Known data type [" + _value.getClass() + "]");
            
        } catch (Exception e) {
//            LogHelper.logError(logger, "Formatting DataColumn[%s] occured error: %s", this,  e);
            return "";
        }
    }

    private String convertNumber(Object _value) {
        String value = null;
        if (DataType.DECIMAL.equals(getDataType())) {
            value = String.valueOf(NumberHelper.updateDecimal((BigDecimal) _value));
        } else if (DataType.PERCENTAGE.equals(getDataType())) {
            value = PERCENTAGE_FORMAT.format(NumberHelper.updateDecimal((BigDecimal) _value).doubleValue());

            logger.error(LoggerHelper.formatErrorLog("PERCENTAGE type dataColumn value: %s", value));
        }
        return value;
    }

    private int caculateColumnWidth(String stringValue) {
        double rate = 1.0d;
        if ((stringValue.length() > BASE_LENGTH)) {
            rate = Math.min((stringValue.length() / BASE_LENGTH), (MULTIPLE));
        }
        return (int) (rate * DEFAULT_CELL_WIDTH);
    }

    @Override
    public String toString() {
        return "DataColumn [headerName=" + headerName + ", keyName=" + keyName + ", clazz=" + clazz + ", dataType="
                + dataType + ", extractor=" + extractor + "]";
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public boolean isSequenceType() {
        return sequenceType;
    }

    public void setSequenceType(boolean sequenceType) {
        this.sequenceType = sequenceType;
    }

    public DataColumnExtractor getExtractor() {
        return extractor;
    }

    public void setExtractor(DataColumnExtractor extractor) {
        this.extractor = extractor;
    }

    public int getColumnWidth() {
        return columnWidth;
    }

    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

}
