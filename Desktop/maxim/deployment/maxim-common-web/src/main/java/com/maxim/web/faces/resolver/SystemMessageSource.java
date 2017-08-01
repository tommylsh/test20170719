package com.maxim.web.faces.resolver;

import org.springframework.context.MessageSource;

public class SystemMessageSource {

    private MessageSource messageSource;
    private String systemName;

    public SystemMessageSource(MessageSource messageSource, String systemName) {
        this.messageSource = messageSource;
        this.systemName = systemName;
    }

    public MessageSource getMessageSource() {
        return this.messageSource;
    }

    public String getSystemName() {
        return this.systemName;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }
}
