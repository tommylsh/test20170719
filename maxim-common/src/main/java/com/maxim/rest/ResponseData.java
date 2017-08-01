package com.maxim.rest;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class ResponseData implements Serializable {

    private static final long serialVersionUID = 20170301L;

    private boolean success = Boolean.TRUE;
    private String code;
    private String message;
    private Object data;

    public enum CODE {

        SUCCESS("1", "Process Success"),
        FAILURE("10", "WS Process Failure"),
        APP_CONFIG_ERROR("20", "App Config [REAL_TIME_RS] Error"),
        EXIST_EMPTY_DATA("21", "Exist empty data"),
        NO_DATA_FOUND("22", "No Data Found");

        private final String value;
        private final String reasonPhrase;

        CODE(String value, String reasonPhrase) {
            this.value = value;
            this.reasonPhrase = reasonPhrase;
        }

        public String getValue() {
            return value;
        }

        public String getReasonPhrase() {
            return reasonPhrase;
        }

    }

    public ResponseData() {
    }

    public ResponseData(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseData(String code, String message, Object data, boolean success) {
        this(code, message, data);
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public ResponseData setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getCode() {
        return code;
    }

    public ResponseData setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseData setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseData setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ResponseData{");
        sb.append("success=").append(success);
        sb.append(", code='").append(code).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
