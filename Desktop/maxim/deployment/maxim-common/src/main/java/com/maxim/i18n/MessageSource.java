package com.maxim.i18n;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;

import com.maxim.util.LoggerHelper;

public class MessageSource extends ResourceBundleMessageSource {

    public static Logger logger = LoggerFactory.getLogger(MessageSource.class);

    private String language = Locale.ENGLISH.toString();
    private Locale defaultLocale = Locale.US;

    public MessageSource() {
        LoggerHelper.logInfo(logger, "Initializing %s", "MessageSource");
    }

    public String getMessage(String code) {
        return getMessage(code, null);
    }

    public String getMessage(String code, Object[] args) {
        try {
            return getMessage(code, args, defaultLocale);
        } catch (NoSuchMessageException e) {
            return code;
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        for (Locale locale : Locale.getAvailableLocales()) {
            if (locale.toString().equalsIgnoreCase(language)) {
                this.language = language;
                this.defaultLocale = locale;
                break;
            }
        }
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

}
