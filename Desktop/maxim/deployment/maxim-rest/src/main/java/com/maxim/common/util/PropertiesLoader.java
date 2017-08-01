package com.maxim.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;
import java.util.Set;

public abstract class PropertiesLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesLoader.class);

    private static final Properties properties = new Properties();

    public static void init(String... resources) {
        loadProperties(resources);
    }

    private static void loadProperties(String... resources) {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        for (String location : resources) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Loading properties file from: {}", location);
            }
            try {
                PropertiesLoaderUtils.fillProperties(properties, resourceLoader.getResource(location));
            } catch (IOException e) {
                LOGGER.warn("Could not load properties from path: {}", location, e);
            }
        }
    }

    public static Set<String> getKeys() {
        return properties.stringPropertyNames();
    }

    public static String getValue(String key) {
        return properties.getProperty(key);
    }

    public static Properties getProperties() {
        return properties;
    }
}
