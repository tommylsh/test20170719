package com.maxim.pos.common.service;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SpringBeanUtil implements ApplicationListener<ContextRefreshedEvent> {

    public final static Logger log = Logger.getLogger(SpringBeanUtil.class);

    public static ApplicationContext context;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        context = event.getApplicationContext();
    }

}
