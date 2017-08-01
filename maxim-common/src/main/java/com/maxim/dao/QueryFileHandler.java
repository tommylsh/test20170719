package com.maxim.dao;

import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maxim.util.URLWrapper;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

@Component("queryFileHandler")
public class QueryFileHandler {

    private static final Logger logger = LoggerFactory.getLogger(QueryFileHandler.class);

    @Autowired
    private URLWrapper urlWrapper;

    protected XMLConfiguration queryFile;

    @PostConstruct
    public void onCreate() throws ConfigurationException {
        setQueryFileURL(urlWrapper.getUrl());
    }

    protected void setQueryFileURL(URL queryFileURL) throws ConfigurationException {
        queryFile = new XMLConfiguration();
        queryFile.setDelimiterParsingDisabled(true);
        queryFile.load(queryFileURL);
    }

    public String formatQuery(String queryKey, String queryString, Map<String, Object> params) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_22);
        
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate(queryKey, queryString);
        cfg.setTemplateLoader(stringLoader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Template template = null;
        StringWriter writer = new StringWriter();
        try {
            template = cfg.getTemplate(queryKey);
            template.process(params, writer);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("processTemplate error: " + e);
            throw new RuntimeException(e);
        }
        return writer.toString();
    }

    public XMLConfiguration getQueryFile() {
        return queryFile;
    }

}
