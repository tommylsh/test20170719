package com.maxim.web.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.helpers.LogLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.util.SystemPropertyUtils;
import org.springframework.web.util.ServletContextPropertyUtils;
import org.springframework.web.util.WebUtils;

public class WebUrlServlet extends HttpServlet {

    private static final long serialVersionUID = -7356191141729209663L;
    private static final Logger LOGGER = LoggerFactory.getLogger(WebUrlServlet.class);

    private static final String CONFIG_LOCATION_PARAM = "webConfigLocation";

    private String webPropsContent;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String location = config.getInitParameter(CONFIG_LOCATION_PARAM);

        if (location != null) {
            ServletContext servletContext = config.getServletContext();
            try {
                location = ServletContextPropertyUtils.resolvePlaceholders(location, servletContext);

                if (!ResourceUtils.isUrl(location)) {
                    location = WebUtils.getRealPath(servletContext, location);
                }

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Initializing Web Urls Properties from [ {0} ]", location);
                }

            } catch (FileNotFoundException ex) {
                throw new IllegalArgumentException("Invalid '" + CONFIG_LOCATION_PARAM + "' parameter: "
                        + ex.getMessage());
            }

            String resolvedLocation = SystemPropertyUtils.resolvePlaceholders(location);
            URL url;
            try {
                url = ResourceUtils.getURL(resolvedLocation);
                if (ResourceUtils.URL_PROTOCOL_FILE.equals(url.getProtocol()) && !ResourceUtils.getFile(url).exists()) {
                    throw new FileNotFoundException("web.properties config file [" + resolvedLocation + "] not found");
                }

                Properties props = doConfigure(url);
                if (props == null) {
                    LOGGER.warn("{0} properties is empty.", resolvedLocation);
                }
                StringBuilder sb = new StringBuilder();
                sb.append("var basePath=\"").append(servletContext.getContextPath()).append("\";");
                for (Entry<Object, Object> entity : props.entrySet()) {
                    String key = entity.getKey().toString();
                    key = StringUtils.replace(key, ".", "_");
                    Object value = entity.getValue();
                    sb.append("var ").append(key).append("=\"").append(value == null ? "" : value.toString()).append("\";");
                }
                webPropsContent = sb.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LOGGER.error("web.properties config file [ {0} ] not found", resolvedLocation);
            }

        } else {
            LOGGER.error("{0} is not set.", CONFIG_LOCATION_PARAM);
            throw new RuntimeException(String.format("%s is not set.", CONFIG_LOCATION_PARAM));
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/javascript;charset=UTF-8");
        resp.setContentLength(webPropsContent.length());
        resp.getWriter().println(webPropsContent);
    }

    private Properties doConfigure(URL configURL) {
        Properties props = new Properties();
        LogLog.debug("Reading configuration from URL " + configURL);
        InputStream istream = null;
        URLConnection uConn = null;
        try {
            uConn = configURL.openConnection();
            uConn.setUseCaches(false);
            istream = uConn.getInputStream();
            props.load(istream);
        } catch (Exception e) {
            if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LOGGER.error("Could not read configuration file from URL [" + configURL + "].", e);
            throw new RuntimeException("Could not read configuration file from URL [" + configURL + "].", e);
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (InterruptedIOException ignore) {
                    Thread.currentThread().interrupt();
                } catch (IOException ignore) {
                } catch (RuntimeException ignore) {
                }
            }
        }
        return props;
    }
}
