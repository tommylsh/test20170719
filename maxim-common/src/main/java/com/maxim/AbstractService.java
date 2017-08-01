package com.maxim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import com.maxim.data.DTO;
import com.maxim.data.Query;
import com.maxim.exception.UnsupportedActionException;
import com.maxim.exception.ValidationException;
import com.maxim.exception.ValidationException.Violation;
import com.maxim.process.Process;

/**
 * Abstract Service class
 * 
 * @author SPISTEV
 */
public abstract class AbstractService {

	protected static final Validator validator = Validation
			.buildDefaultValidatorFactory().getValidator();

	private Map<String, Process> processes = Collections.emptyMap();

	protected Process getProcess(String key) {
		return processes.get(key);
	}

	public void setProcesses(Map<String, Process> processes) {
		this.processes = processes;
	}

	protected DTO defaultProcess(Query query, String methodName) {
		Process process = getProcess(methodName+"_"+query.getClass().getSimpleName());

		if (process == null) {
			throw new UnsupportedActionException(query);
		}

		return process.process(query);
	}

	public void validate(Query query) {
		Set<ConstraintViolation<Query>> result = validator.validate(query);

		// passed
		if (result.isEmpty()) {
			return;
		}

		// violation occurred
		Collection<Violation> violations = new ArrayList<Violation>();
		for (ConstraintViolation<Query> violation : result) {
			violations.add(new Violation(violation.getLeafBean(), violation
					.getMessage(), violation.getPropertyPath().toString()
					+ ": " + violation.getInvalidValue()));
		}

		throw new ValidationException(violations);
	}

	/**
	 * Must be override by Service directly interfacing with external system 
	 */
	public String getSysId() {
		return "SYSTEM";
	}

	
}
