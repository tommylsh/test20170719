package com.maxim.validation;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CollectionPatternValidator implements ConstraintValidator<CollectionPattern, Collection<?>> {

	private String regexp;
	

	public void initialize(CollectionPattern collectionPattern) {
		regexp = collectionPattern.regexp();
	}

	
	public boolean isValid(Collection<?> collection, ConstraintValidatorContext constraintContext) {
		
		// skip for empty or null collection
		if(collection == null) {
			return true;
		}
		
		for (Iterator<?> itr = collection.iterator(); itr.hasNext();) {
			Object obj = itr.next();
			
			if(!Pattern.matches(regexp, obj.toString())) {
				return false;
			}
		}
		
		return true;
	}
}
