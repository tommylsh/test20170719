package com.maxim.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServiceMethod {
	
	boolean log() default true;
	
	boolean auditTrail() default true;
	
	boolean validate() default true;
}
