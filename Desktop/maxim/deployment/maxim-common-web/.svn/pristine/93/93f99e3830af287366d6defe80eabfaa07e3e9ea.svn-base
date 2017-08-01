package com.maxim.web.faces.resolver;

import java.beans.FeatureDescriptor;
import java.util.Iterator;
import java.util.Locale;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotFoundException;
import javax.el.PropertyNotWritableException;
import javax.faces.context.FacesContext;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.jsf.FacesContextUtils;

public class MessageSourceELResolver extends ELResolver {

    static final String RESOURCE_BUNDLE_KEY = "msg";

    @Override
    public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
        if (base == null)
            return MessageSource.class;
        if (base instanceof MessageSource) {
            return String.class;
        }
        return null;
    }

    @Override
    public Iterator<FeatureDescriptor> getFeatureDescriptors(
            ELContext elContext, Object base) {
        return null;
    }

    @Override
    public Class<?> getType(ELContext elContext, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if ((base == null) && (RESOURCE_BUNDLE_KEY.equals(property))) {
            elContext.setPropertyResolved(true);
            return MessageSource.class;
        }
        if (base instanceof MessageSource) {
            elContext.setPropertyResolved(true);
            return String.class;
        }
        return null;
    }

    @Override
    public Object getValue(ELContext elContext, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if ((base == null) && (RESOURCE_BUNDLE_KEY.equals(property))) {
            elContext.setPropertyResolved(true);
            return getMessageSource();
        }
        if (base instanceof MessageSource) {
            elContext.setPropertyResolved(true);
            MessageSource messageSource = (MessageSource) base;
            String message = messageSource.getMessage(property.toString(),
                    null, null, getLocale());
            if (message != null) {
                return message;
            }
            return property.toString();
        }

        return null;
    }

    @Override
    public boolean isReadOnly(ELContext elContext, Object base, Object property)
            throws NullPointerException, PropertyNotFoundException, ELException {
        if ((base == null) && (RESOURCE_BUNDLE_KEY.equals(property))) {
            elContext.setPropertyResolved(true);
            return true;
        }
        if (base instanceof MessageSource) {
            elContext.setPropertyResolved(true);
            return true;
        }
        return false;
    }

    @Override
    public void setValue(ELContext elContext, Object base, Object property,
            Object value) throws NullPointerException,
            PropertyNotFoundException, PropertyNotWritableException,
            ELException {
        if ((base == null) && (RESOURCE_BUNDLE_KEY.equals(property))) {
            throw new PropertyNotWritableException("The '"
                    + RESOURCE_BUNDLE_KEY + "' variable is not writable.");
        }
        if (base instanceof MessageSource)
            throw new PropertyNotWritableException(
                    "The MessageSource is not writable.");

    }

    protected Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    protected MessageSource getMessageSource() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }

}
