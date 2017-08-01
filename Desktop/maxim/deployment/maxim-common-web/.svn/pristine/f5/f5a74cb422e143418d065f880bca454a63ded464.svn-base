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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

public class JsfMessageSourceELResolver extends ELResolver {

	protected static final String RESOURCE_BUNDLE_KEY = "msg";

	protected Locale defaultLocale;

	protected final Log logger = LogFactory.getLog(getClass());

	@Override
	public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
		if (base == null)
			return MessageSource.class;
		if (base instanceof MessageSource)
			return SystemMessageSource.class;
		if (base instanceof SystemMessageSource) {
			return String.class;
		}
		return null;
	}

	@Override
	public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
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
			return SystemMessageSource.class;
		}
		if (base instanceof SystemMessageSource) {
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
			return new SystemMessageSource((MessageSource) base, property.toString());
		}
		if (base instanceof SystemMessageSource) {
			elContext.setPropertyResolved(true);
			SystemMessageSource systemMessageSource = (SystemMessageSource) base;
			MessageSource messageSource = systemMessageSource.getMessageSource();
			String systemName = systemMessageSource.getSystemName();

			try {
				if (defaultLocale == null) {
					BeanFactory bf = getBeanFactory(elContext);
					com.maxim.i18n.MessageSource msgSource = bf
							.getBean(com.maxim.i18n.MessageSource.class);
					defaultLocale = msgSource.getDefaultLocale();
				}
			} catch (BeansException e) {
				defaultLocale = Locale.getDefault();
			}
			if (defaultLocale == null) {
				defaultLocale = Locale.getDefault();
			}

			String message = messageSource.getMessage(systemName + "." + property.toString(), null,
					null, defaultLocale);
			if (message != null) {
				return message;
			}
			return property.toString();
		}
		if (base == null) {
			String beanName = property.toString();
			BeanFactory bf = getBeanFactory(elContext);

			if (bf.containsBean(beanName)) {
				if (logger.isTraceEnabled()) {
					logger.trace("Successfully resolved variable '" + beanName
							+ "' in Spring BeanFactory");
				}
				elContext.setPropertyResolved(true);
				return bf.getBean(beanName);
			}
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
		if (base instanceof SystemMessageSource) {
			elContext.setPropertyResolved(true);
			return true;
		}
		return false;
	}

	@Override
	public void setValue(ELContext elContext, Object base, Object property, Object value)
			throws NullPointerException, PropertyNotFoundException, PropertyNotWritableException,
			ELException {
		if ((base == null) && (RESOURCE_BUNDLE_KEY.equals(property))) {
			throw new PropertyNotWritableException("The '" + RESOURCE_BUNDLE_KEY
					+ "' variable is not writable.");
		}
		if (base instanceof MessageSource)
			throw new PropertyNotWritableException("The MessageSource is not writable.");
		if (base instanceof SystemMessageSource)
			throw new PropertyNotWritableException("The SystemMessageSource is not writable.");
	}

	protected MessageSource getMessageSource() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
	}

	protected BeanFactory getBeanFactory(ELContext elContext) {
		return getWebApplicationContext(elContext);
	}

	protected WebApplicationContext getWebApplicationContext(ELContext elContext) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
	}

}
