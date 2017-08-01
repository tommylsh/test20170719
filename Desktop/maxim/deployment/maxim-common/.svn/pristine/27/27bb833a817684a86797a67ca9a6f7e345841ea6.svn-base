package com.maxim.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = CollectionPatternValidator.class)
@Documented
public @interface CollectionPattern {

	String message() default "{Element(s) does not compiles pattern}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	String regexp();
}
