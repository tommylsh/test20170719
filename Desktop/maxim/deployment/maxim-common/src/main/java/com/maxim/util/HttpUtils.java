package com.maxim.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.maxim.enums.MediaType;
import com.maxim.enums.RequestMethod;
import com.maxim.enums.StatusCode;

public abstract class HttpUtils {

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String CONTENT_TYPE = "Content-Type";

    /**
     * Send a get request
     *
     * @param url HTTP request URL
     * @return Response as string
     * @throws IOException
     */
    public static String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * Send a get request
     *
     * @param url     HTTP request URL
     * @param headers HTTP request header
     * @return Response as string
     * @throws IOException
     */
    public static String get(String url, Map<String, String> headers) throws IOException {
        return request(RequestMethod.GET, url, null, headers, null, null);
    }

    /**
     * Send a post request
     *
     * @param url  HTTP request URL
     * @param body HTTP request body
     * @return Response as string
     * @throws IOException
     */
    public static String post(String url, String body) throws IOException {
        return post(url, body, null);
    }

    /**
     * Post a form with parameters
     *
     * @param url    HTTP request URL
     * @param params Map with parameters/values
     * @return Response as string
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params) throws IOException {
        return post(url, params, null);
    }

    /**
     * Post a form with parameters
     *
     * @param url     HTTP request URL
     * @param params  Map with parameters/values
     * @param headers HTTP request header
     * @return Response as string
     * @throws IOException
     */
    public static String post(String url, Map<String, String> params, Map<String, String> headers) throws IOException {
        // set content type
        if (headers == null) {
            headers = new HashMap<>();
        }
        headers.put(CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED.getType());

        // parse parameters
        String body = "";
        if (params != null) {
            boolean first = true;
            for (String param : params.keySet()) {
                if (first) {
                    first = false;
                } else {
                    body += "&";
                }
                String value = params.get(param);
                body += URLEncoder.encode(param, DEFAULT_ENCODING) + "=";
                body += URLEncoder.encode(value, DEFAULT_ENCODING);
            }
        }

        return post(url, body, headers);
    }

    /**
     * Send a post request
     *
     * @param url     HTTP request URL
     * @param body    HTTP request body
     * @param headers HTTP request header
     * @return Response as string
     * @throws IOException
     */
    public static String post(String url, String body, Map<String, String> headers) throws IOException {
        return request(RequestMethod.POST, url, body, headers, null, null);
    }

    /**
     * Send a put request
     *
     * @param url  HTTP request URL
     * @param body HTTP request body
     * @return Response as string
     * @throws IOException
     */
    public static String put(String url, String body) throws IOException {
        return put(url, body, null);
    }

    /**
     * Send a put request
     *
     * @param url     HTTP request URL
     * @param body    HTTP request body
     * @param headers HTTP request header
     * @return Response as string
     * @throws IOException
     */
    public static String put(String url, String body, Map<String, String> headers) throws IOException {
        return request(RequestMethod.PUT, url, body, headers, null, null);
    }

    /**
     * Send a delete request
     *
     * @param url HTTP request URL
     * @return Response as string
     * @throws IOException
     */
    public static String delete(String url) throws IOException {
        return delete(url, null);
    }

    /**
     * Send a delete request
     *
     * @param url     HTTP request URL
     * @param headers HTTP request header
     * @return Response as string
     * @throws IOException
     */
    public static String delete(String url, Map<String, String> headers) throws IOException {
        return request(RequestMethod.DELETE, url, null, headers, null, null);
    }

    /**
     * Append query parameters to given url
     *
     * @param url    HTTP request URL
     * @param params Map with query parameters
     * @return URL with query parameters appended
     * @throws IOException
     */
    public static String appendQueryParams(String url, Map<String, String> params) throws IOException {
        String fullUrl = url;
        if (params != null) {
            boolean first = (fullUrl.indexOf('?') == -1);
            for (String param : params.keySet()) {
                if (first) {
                    fullUrl += '?';
                    first = false;
                } else {
                    fullUrl += '&';
                }
                String value = params.get(param);
                fullUrl += URLEncoder.encode(param, DEFAULT_ENCODING) + '=';
                fullUrl += URLEncoder.encode(value, DEFAULT_ENCODING);
            }
        }

        return fullUrl;
    }

    /**
     * Retrieve the query parameters from given url
     *
     * @param url URL containing query parameters
     * @return Map with query parameters
     * @throws IOException
     */
    public static Map<String, String> getQueryParams(String url) throws IOException {
        Map<String, String> params = new HashMap<>();
        int start = url.indexOf('?');
        while (start != -1) {
            // read parameter name
            int equals = url.indexOf('=', start);
            String param = equals != -1 ? url.substring(start + 1, equals) : url.substring(start + 1);

            // read parameter value
            String value = "";
            if (equals != -1) {
                start = url.indexOf('&', equals);
                if (start != -1) {
                    value = url.substring(equals + 1, start);
                } else {
                    value = url.substring(equals + 1);
                }
            }

            params.put(URLDecoder.decode(param, DEFAULT_ENCODING), URLDecoder.decode(value, DEFAULT_ENCODING));
        }

        return params;
    }

    /**
     * Returns the url without query parameters
     *
     * @param url URL containing query parameters
     * @return URL without query parameters
     * @throws IOException
     */
    public static String removeQueryParams(String url) throws IOException {
        int q = url.indexOf('?');
        if (q != -1) {
            return url.substring(0, q);
        } else {
            return url;
        }
    }

    /**
     * Read an input stream into a string
     *
     * @param in InputStream
     * @return String
     * @throws IOException
     */
    public static String streamToString(InputStream in) throws IOException {
        StringBuilder out = new StringBuilder();
        byte[] b = new byte[4096];
        for (int n; (n = in.read(b)) != -1; ) {
            out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    /**
     * Send a request
     *
     * @param method    HTTP request method
     * @param url       HTTP request URL
     * @param body      HTTP request body
     * @param mediaType HTTP request Content-Type
     * @return Response as string
     * @throws IOException
     */
    public static String request(RequestMethod method, String url, String body, MediaType mediaType) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, mediaType.getType());
        return request(method, url, body, headers, mediaType, null);
    }

    public static String jasonRequest(RequestMethod method, String url, Object obj) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE, MediaType.APPLICATION_JSON.getType());
        return request(method, url, null, headers, MediaType.APPLICATION_JSON, obj);
    }

    /**
     * Send a request
     *
     * @param method  HTTP request method
     * @param url     HTTP request URL
     * @param body    HTTP request body
     * @param headers HTTP request header
     * @return Response as string
     * @throws IOException
     */
    public static String request(RequestMethod method, String url, String body, Map<String, String> headers, MediaType mediaType, Object obj) throws IOException {
        // connection
        URL u = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) u.openConnection();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        // method
        if (method != null) {
            conn.setRequestMethod(method.toString());
        }

        // headers
        if (headers != null) {
            for (String key : headers.keySet()) {
                conn.addRequestProperty(key, headers.get(key));
            }
        }

        // body
        if (MediaType.APPLICATION_JSON.equals(mediaType))
        {
	        if (obj != null) {
	            conn.setDoOutput(true);
	            OutputStreamWriter ow = new OutputStreamWriter(conn.getOutputStream());
	            JsonUtils.toJson(obj, ow);
	            ow.flush();
	            ow.close();
	        }
	    }
        else
        {
	        if (body != null) {
	            conn.setDoOutput(true);
	            OutputStream os = conn.getOutputStream();
	            os.write(body.getBytes());
	            os.flush();
	            os.close();
	        }
	     }

        // response
        InputStream is = conn.getInputStream();
        String response = streamToString(is);
        is.close();

        // handle redirects
        if (conn.getResponseCode() == StatusCode.MOVED_PERMANENTLY.getStatusCode()) {
            String location = conn.getHeaderField("Location");
            return request(method, location, body, headers, mediaType, obj);
        }

        return response;
    }

}
