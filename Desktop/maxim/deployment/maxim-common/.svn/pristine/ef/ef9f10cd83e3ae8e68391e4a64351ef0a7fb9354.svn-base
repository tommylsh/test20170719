package com.maxim.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxim.util.meta.HttpCommonRequest;
import com.maxim.util.meta.HttpCommonResponse;

public class HttpUtil {

    private static final int REQUEST_TIMEOUT = 30000;

    public static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    public static ObjectMapper mapper = new ObjectMapper();

    public static HttpCommonResponse jsonRequest(HttpCommonRequest request) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(request.getUrl());
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(REQUEST_TIMEOUT)
                .setConnectionRequestTimeout(REQUEST_TIMEOUT).setSocketTimeout(REQUEST_TIMEOUT).build();
        post.setConfig(requestConfig);

        post.setHeader("Content-type", "application/json; charset=utf-8");
        post.setHeader("Accept", "application/json");

        Map<String, Object> jsonParams = request.getParams();
        if (jsonParams != null) {
            String jsonString = mapper.writeValueAsString(jsonParams);
            post.setEntity(new StringEntity(jsonString, "utf-8"));
        }

        HttpResponse response = client.execute(post);

        int statusCode = response.getStatusLine().getStatusCode();
        InputStream stream = response.getEntity().getContent();
        String content = getContent(stream);

        LoggerHelper.logInfo(logger, "Response content: \n%s", content);

        return new HttpCommonResponse(statusCode, content);
    }

    public static HttpCommonResponse doGetRequest(HttpCommonRequest request) throws Exception {
        HttpGet get = new HttpGet(request.getUrl());

        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(get);

        int statusCode = response.getStatusLine().getStatusCode();
        InputStream stream = response.getEntity().getContent();
        String content = getContent(stream);

        LoggerHelper.logInfo(logger, "Response content: \n%s", content);

        return new HttpCommonResponse(statusCode, content);
    }

    public static HttpCommonResponse doPostRequest(HttpCommonRequest request) throws Exception {
        HttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(request.getUrl());

        Map<String, Object> params = request.getParams();
        if (params != null) {
            List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

            for (Entry<String, Object> entry : params.entrySet()) {
                parameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
            }

            post.setEntity(new UrlEncodedFormEntity(parameters, "utf-8"));
        }

        HttpResponse response = client.execute(post);
        int statusCode = response.getStatusLine().getStatusCode();
        InputStream stream = response.getEntity().getContent();
        String content = getContent(stream);

        return new HttpCommonResponse(statusCode, content);
    }

    public static String getContent(InputStream stream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(stream);
        int length = 0;
        byte[] buffer = new byte[1024 * 10];

        StringBuffer stringBuffer = new StringBuffer();
        while ((length = bis.read(buffer)) != -1) {
            stringBuffer.append(new String(buffer, 0, length));
        }

        bis.close();

        return stringBuffer.toString();
    }

}
