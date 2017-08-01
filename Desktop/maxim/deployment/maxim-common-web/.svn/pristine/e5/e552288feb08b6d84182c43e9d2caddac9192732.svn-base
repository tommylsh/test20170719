package com.maxim.filter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourcesFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesFilter.class);

    private static final String RESOURCE_CHARSET = "UTF-8";
    private static final String DEFAULT_MINE_TYPE = "application/octet-stream";
    private static final String RESOURCE_PACKAGE_PATH = "/assets";
    private String resourcePath = "/assets";

    private static final Map<String, String> MINE_TYPE_MAP;

    static {
        MINE_TYPE_MAP = new HashMap<String, String>();
        MINE_TYPE_MAP.put("js", "application/javascript;charset=" + RESOURCE_CHARSET);
        MINE_TYPE_MAP.put("css", "text/css;charset=" + RESOURCE_CHARSET);
        MINE_TYPE_MAP.put("gif", "image/gif");
        MINE_TYPE_MAP.put("jpg", "image/jpeg");
        MINE_TYPE_MAP.put("jpeg", "image/jpeg");
        MINE_TYPE_MAP.put("png", "image/png");
    }

    public String getResourcePath() {
        return resourcePath;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String resPath = filterConfig.getInitParameter("resourcePath");
        if (resPath != null) {
            if (!resPath.startsWith("/")) {
                resPath = "/" + resPath;
            }
            resourcePath = resPath;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        HttpServletResponse rep = (HttpServletResponse) response;

        if (path.startsWith(getResourcePath() + "/")) {

            path = path.substring(getResourcePath().length());

            try {
                URL resource = this.getClass().getResource(RESOURCE_PACKAGE_PATH + path);
                if (resource == null) { // 404
                    rep.sendError(HttpServletResponse.SC_NOT_FOUND);
                } else {
                    InputStream inputStream = readResource(resource);
                    if (inputStream != null) {
                        String ext = FilenameUtils.getExtension(path).toLowerCase();
                        String contentType = MINE_TYPE_MAP.get(ext);
                        if (contentType == null) {
                            contentType = DEFAULT_MINE_TYPE;
                        }
                        rep.setContentType(contentType);

                        ServletOutputStream outputStream = rep.getOutputStream();
                        try {
                            int size = IOUtils.copy(inputStream, outputStream);
                            rep.setContentLength(size);
                        } finally {
                            IOUtils.closeQuietly(inputStream);
                            IOUtils.closeQuietly(outputStream);
                        }
                    } else {
                        rep.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                }

            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                rep.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            LOGGER.error("MUST set url-pattern=\"" + resourcePath + "/*\"!!");
            rep.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

    @SuppressWarnings("restriction")
    private InputStream readResource(URL originUrl) throws Exception {
        InputStream inputStream = null;
        URLConnection urlConnection = originUrl.openConnection();
        if (urlConnection instanceof JarURLConnection) {
            inputStream = readJarResource((JarURLConnection) urlConnection);
        } else if (urlConnection instanceof sun.net.www.protocol.file.FileURLConnection) {
            File originFile = new File(originUrl.getPath());
            if (originFile.isFile()) {
                inputStream = originUrl.openStream();
            }
        } else {
            inputStream = urlConnection.getInputStream();
        }
        return inputStream;
    }

    private InputStream readJarResource(JarURLConnection jarConnection) throws Exception {
        InputStream inputStream = null;
        JarFile jarFile = jarConnection.getJarFile();
        if (!jarConnection.getJarEntry().isDirectory()) {
            inputStream = jarFile.getInputStream(jarConnection.getJarEntry());
        }
        return inputStream;
    }

}
