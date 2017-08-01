package com.maxim.validation;

import java.util.Date;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.collections.BeanMap;

public class PrecedingDateValidator implements ConstraintValidator<PrecedingDateValidation, Object> {

	private String precedingDate;
	
	private String referencedDate;
	

	public void initialize(PrecedingDateValidation anno) {
		precedingDate = anno.precedingDate();
		referencedDate = anno.referencedDate();
	}


	public boolean isValid(Object object, ConstraintValidatorContext constraintContext) {
		
		// to be checked by @NotNull
		if(object == null) {
			return true;
		}
		

		BeanMap beanMap = new BeanMap(object);
		// Validate date
		Date startDate = (Date) beanMap.get(precedingDate);
		Date endDate = (Date) beanMap.get(referencedDate);
		
		// startDate > endDate ?
		if(startDate!= null && endDate != null && startDate.after(endDate)) {
			return false;
		}
	
		// passed
		return true;
	}

}
