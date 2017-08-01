package com.maxim.generator;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class VelocityGeneratorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityGeneratorTest.class);

    private VelocityEngine engine;

    @Before
    public void setUp() throws Exception {
        engine = new VelocityEngine();
        engine.init(PropertiesLoaderUtils.loadProperties("velocity.properties"));
    }

    @Test
    public void testVelocityGenerator() throws Exception {
        Properties props = PropertiesLoaderUtils.loadProperties("velocity-generator.properties");
        String entityName = props.getProperty("entityClassName");

        Class<?> clazz = Class.forName(entityName);
        String entitySimpleName = clazz.getSimpleName();

        String rootPath = new File("").getAbsolutePath() + "/src/test/java/";

        String dao = rootPath + String.format("com/maxim/pos/core/persistence/%sDao.java", entitySimpleName);
        String service = rootPath + String.format("com/maxim/pos/core/service/%sService.java", entitySimpleName);
        String serviceImpl = rootPath + String.format("com/maxim/pos/core/service/%sServiceImpl.java", entitySimpleName);
        String controller = rootPath + String.format("com/maxim/pos/web/controller/%sController.java", entitySimpleName);
        String queryCriteria = rootPath + String.format("com/maxim/pos/core/value/%sQueryCriteria.java", entitySimpleName);
        String dataModelQuery = rootPath + String.format("com/maxim/pos/web/datamodel/%sDataModelQuery.java", entitySimpleName);

        String i18n = rootPath + String.format("%s_i18n.properties", entitySimpleName);
        String faces = rootPath + String.format("%s-list.xhtml", entitySimpleName);
        String query = rootPath + String.format("%s-query.xml", entitySimpleName);

        Map<Object, Object> context = new HashMap<>();
        context.putAll(props);
        context.put("entitySimpleName", entitySimpleName);
        context.put("fieldList", getFieldList(clazz));

        VelocityContext velocityContext = new VelocityContext(context);

        merge(engine.getTemplate("Dao.vm"), velocityContext, dao);
        merge(engine.getTemplate("Service.vm"), velocityContext, service);
        merge(engine.getTemplate("ServiceImpl.vm"), velocityContext, serviceImpl);
        merge(engine.getTemplate("Controller.vm"), velocityContext, controller);
        merge(engine.getTemplate("QueryCriteria.vm"), velocityContext, queryCriteria);
        merge(engine.getTemplate("DataModelQuery.vm"), velocityContext, dataModelQuery);

        merge(engine.getTemplate("I18n.vm"), velocityContext, i18n);
        merge(engine.getTemplate("Faces.vm"), velocityContext, faces);
        merge(engine.getTemplate("Query.vm"), velocityContext, query);

    }

    private List<String> getFieldList(Class<?> clazz) {
        List<String> fieldList = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Class<?> type = field.getType();
            if ((type.isPrimitive() && !"long".equals(type.getName()))
                    || isWrapper(type)
                    || type.isEnum()) {
                fieldList.add(field.getName());
            }
        }
        return fieldList;
    }

    private boolean isWrapper(Class<?> type) {
        return type.isAssignableFrom(Integer.class)
                || type.isAssignableFrom(Long.class)
                || type.isAssignableFrom(BigDecimal.class)
                || type.isAssignableFrom(String.class)
                || type.isAssignableFrom(Date.class);
    }

    private void merge(Template template, VelocityContext context, String path) throws Exception {

        File file = new File(path);
        if (!file.getParentFile().exists()) {
            boolean flag = file.getParentFile().mkdirs();
            LOGGER.info("Create Directory {} => {}", file.getParentFile().getPath(), flag);
        }

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(path);
            template.merge(context, writer);
            writer.flush();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    static abstract class PropertiesLoaderUtils {
        public static Properties loadProperties(String resource) throws IOException {
            Properties props = new Properties();
            fillProperties(props, resource);
            return props;
        }

        public static void fillProperties(Properties props, String resource) throws IOException {
            props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(resource));
        }
    }

}
