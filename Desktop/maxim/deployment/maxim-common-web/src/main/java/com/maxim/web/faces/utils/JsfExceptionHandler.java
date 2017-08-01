package com.maxim.web.faces.utils;

import java.util.Iterator;

import javax.faces.FacesException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsfExceptionHandler extends ExceptionHandlerWrapper {

	public static final Logger logger = LoggerFactory.getLogger(JsfExceptionHandler.class);
	private ExceptionHandler wrapped;

	public JsfExceptionHandler(ExceptionHandler wrapped) {
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handle() throws FacesException {
		// Iterate over all unhandeled exceptions
		Iterator i = getUnhandledExceptionQueuedEvents().iterator();
		while (i.hasNext()) {
			ExceptionQueuedEvent event = (ExceptionQueuedEvent) i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

			// obtain throwable object
			Throwable t = context.getException();

			// here you do what ever you want with exception
			try {
				// log error
				logger.error("Serious error happened!", t);
				// redirect to error view etc....
			} finally {
				// after exception is handeled, remove it from queue
				i.remove();
			}
		}
		// let the parent handle the rest
		getWrapped().handle();
	}
}