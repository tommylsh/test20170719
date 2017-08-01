package com.maxim.api.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
public class ResponseData implements Serializable {

    private static final long serialVersionUID = 20170301L;

    private boolean success = Boolean.TRUE;
    private String code;
    private String message;
    private Object data;

    public ResponseData() {
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