package com.maxim.pos.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.type.AnnotatedTypeMetadata;

@Configuration
@Conditional(LocalConfiguration.Condition.class)
@ImportResource(LocalConfiguration.filename)
public class LocalConfiguration {
	
	final  @Value("${mail.reportTempDirectory}") static String filename =  "file:c:\\UserData\\weblogic\\pos-core-local.xml" ;
	
    static class Condition implements ConfigurationCondition {
        @Override
        public ConfigurationPhase getConfigurationPhase() {
            return ConfigurationPhase.PARSE_CONFIGURATION;
        }
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            DefaultResourceLoader loader = new DefaultResourceLoader();
            Resource localResource = loader.getResource(filename);
            return localResource.exists();
        }
   }
}