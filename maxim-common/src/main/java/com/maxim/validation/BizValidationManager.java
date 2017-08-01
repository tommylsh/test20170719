package com.maxim.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.maxim.data.DTO;
import com.maxim.data.ICollectionDTO;
import com.maxim.exception.ValidationException;
import com.maxim.exception.ValidationException.Violation;

public class BizValidationManager {

	public static enum Strategy {
		EXIT_ON_FAIL, COMPLETE_ALL
	};

	private Strategy strategy = Strategy.COMPLETE_ALL;

	private List<BizValidator> validators = new ArrayList<BizValidator>();

	public BizValidationManager() {
	}

	/**
	 * Validate the dto with registered validators, throw ValidationException
	 */
	public void validate(DTO dto) {
		if(Strategy.EXIT_ON_FAIL.equals(strategy)) {
			exitOnFailValidate(dto);
		}
		else {
			completeAllValidate(dto);
		}
	}

	/**
	 * Depends on DTO type, use different validate method
	 * @param validator
	 * @param dto
	 * @return
	 */
	private Collection<Violation> validate(BizValidator validator, DTO dto) {
		if(dto == null) {
			return Collections.emptyList();
		}
		else if(dto.getClass().isAssignableFrom(ICollectionDTO.class)) {
			return validator.validateCollection((ICollectionDTO) dto);
		}
		else {
			return validator.validate(dto);
		}
	}
	
	private void completeAllValidate(DTO dto) {
		Collection<Violation> allViolations = new ArrayList<Violation>();
		for (BizValidator validator : validators) {
			Collection<Violation> violations = validate(validator, dto);
			if (!violations.isEmpty()) {
				allViolations.addAll(violations);
			}
		}
		
		// if any violation
		if(!allViolations.isEmpty()) {
			throw new ValidationException(allViolations);
		}
	}
	
	private void exitOnFailValidate(DTO dto) {
		for (BizValidator validator : validators) {
			Collection<Violation> violations = validate(validator, dto);
			if (!violations.isEmpty()) {
				throw new ValidationException(violations);
			}
		}
	}

	public void setValidators(List<BizValidator> validators) {
		this.validators = validators;
	}

	public Strategy getStrategy() {
		return strategy;
	}

	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

}
