package com.maxim.validation;

public class DefaultPattern {
	
	// Fundamental classes
	public static final String FREE_TEXT_CLASS = "[A-Za-z0-9 _,=.!+-\\\\(\\\\)/\\\\]";
	
	// Default Patterns
	public static final String SEASON = "[WS]\\d{2}";
	
	public static final String PORT = "[A-Z]{3}";
	
	public static final String CARRIER = "[A-Z]{2}";
	
	public static final String FLT_NUM = "\\d{3,4}";
	
	public static final String PRODUCT = "[A-Z0-9]{2}";
			
	public static final String CONTRACT = "[A-Z0-9]{3}";
	
	public static final String CCY = "[A-Z]{3}";

}
