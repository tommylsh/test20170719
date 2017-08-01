package com.maxim.pos.common.config;

import java.util.Enumeration;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.maxim.util.EncryptionUtil;

public class EncryptablePropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {

    public static final Logger logger = LoggerFactory.getLogger(EncryptablePropertyPlaceholderConfigurer.class);

    private static final String AESKEY_KEY = "aesKey";
//    private static final String JDBC_PASSWORD_KEY = "connection.password";

    protected void processProperties(ConfigurableListableBeanFactory beanFactory, Properties props)
            throws BeansException {
        try {
            String aesKey = props.getProperty(AESKEY_KEY);
            
            logger.info("onSetup aesKey is {}", aesKey);
            
            @SuppressWarnings("unchecked")
			Enumeration<String> namesEnum = (Enumeration<String>) props.propertyNames();
            while ( namesEnum.hasMoreElements())
            {
            	String name = namesEnum.nextElement();
            	String value = props.getProperty(name);
            	if (value.startsWith(SecurityConfig.AES_PREFIX))
            	{
            		value = EncryptionUtil.aesDecrypt(value.substring(SecurityConfig.AES_PREFIX.length()), aesKey);
            		
            		props.setProperty(name, value);
            	}

            }

//
//            String jdbc_password_key = props.getProperty(JDBC_PASSWORD_KEY);
//
//            if (jdbc_password_key != null) {
//                props.setProperty(JDBC_PASSWORD_KEY, EncryptionUtil.decrypt(jdbc_password_key, aesKey));
//            }

            super.processProperties(beanFactory, props);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BeanInitializationException(e.getMessage());
        }
    }
    
    public static void main(String[] args) throws Exception
    {
    	System.out.println(EncryptionUtil.aesEncrypt("P@assw0rd", "90206f7a4fc149b592a14b7629caad5e"));
    	System.out.println(EncryptionUtil.aesEncrypt("2x39adc6vsdi", "90206f7a4fc149b592a14b7629caad5e"));
    	
    	System.out.println(EncryptionUtil.decrypt("5LdjcG4AydcB6m0ScF49ew==", "90206f7a4fc149b592a14b7629caad5e"));
    	System.out.println(EncryptionUtil.decrypt("avkc6hUYhDsW3bszfpJcew==", "90206f7a4fc149b592a14b7629caad5e"));
    	
    }
}