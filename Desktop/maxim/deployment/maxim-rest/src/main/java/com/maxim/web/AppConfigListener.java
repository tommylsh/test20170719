package com.maxim.web;

import com.maxim.common.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoaderListener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppConfigListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfigListener.class);

    private static final String APP_CONFIG_LOCATION_PARAM = "appConfigLocation";
    private static final String ROOT_WEB_APP_CONTEXT_ATTRIBUTE = "app";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        SpringContext.setApplicationContext(ContextLoaderListener.getCurrentWebApplicationContext());
        // String location = servletContext.getInitParameter(APP_CONFIG_LOCATION_PARAM);
        // PropertiesLoader.init(location);
        // Properties properties = PropertiesLoader.getProperties();
        // servletContext.setAttribute(ROOT_WEB_APP_CONTEXT_ATTRIBUTE, properties);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}
