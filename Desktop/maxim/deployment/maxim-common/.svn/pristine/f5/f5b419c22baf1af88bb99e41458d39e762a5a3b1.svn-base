package com.maxim.util.meta;

import java.util.Map;

import com.maxim.util.HttpUtil;

public class HttpCommonResponse {

    private int statusCode;
    private String responseContent;

    public HttpCommonResponse() {
    }

    public HttpCommonResponse(int statusCode, String responseContent) {
        this.statusCode = statusCode;
        this.responseContent = responseContent;
    }

    public boolean isSuccessStatus() {
        return (200 == statusCode);
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Object> getJsonResult() throws Exception {
        return HttpUtil.mapper.readValue(responseContent, Map.class);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseContent() {
        return responseContent;
    }

    public void setResponseContent(String responseContent) {
        this.responseContent = responseContent;
    }

}