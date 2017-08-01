package com.maxim.validation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = PrecedingDateValidator.class)
@Documented
public @interface PrecedingDateValidation {

	String message() default "{Start Date must be before End Date}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	String precedingDate();
	
	String referencedDate();
}
