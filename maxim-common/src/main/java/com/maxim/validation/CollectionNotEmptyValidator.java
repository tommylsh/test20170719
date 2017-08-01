package com.maxim.validation;

import java.util.Collection;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CollectionNotEmptyValidator implements ConstraintValidator<CollectionNotEmpty, Collection<?>> {


	public void initialize(CollectionNotEmpty collectionNotEmpty) {
	}


	public boolean isValid(Collection<?> collection, ConstraintValidatorContext constraintContext) {
		
		// also as @NotNull
		if(collection == null || collection.isEmpty()) {
			return false;
		}
		
		return true;
	}

}
