package com.maxim.web.faces.utils;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

public class JsfExceptionHandlerFactory extends ExceptionHandlerFactory {

	private ExceptionHandlerFactory parent;

	// this injection handles jsf
	public JsfExceptionHandlerFactory(ExceptionHandlerFactory parent) {
		this.parent = parent;
	}

	// create your own ExceptionHandler
	@Override
	public ExceptionHandler getExceptionHandler() {
		ExceptionHandler result = new JsfExceptionHandler(parent.getExceptionHandler());
		return result;
	}
}