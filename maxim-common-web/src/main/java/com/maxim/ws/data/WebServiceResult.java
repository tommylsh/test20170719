package com.maxim.ws.data;

import java.io.Serializable;
import java.util.Collection;

import com.maxim.exception.ValidationException.Violation;

public class WebServiceResult implements Serializable {

	/**
	 * @author Steven
	 */
	private static final long serialVersionUID = 20130723L;

	private boolean completed = true;

	private String errorMsg;

	private Collection<Violation> violations;

	public WebServiceResult() {
	}

	public WebServiceResult(String error) {
		this.completed = false;
		this.errorMsg = error;
	}

	public WebServiceResult(Collection<Violation> violations) {
		this.errorMsg = "Validation Error";
		this.completed = false;
		this.violations = violations;
	}

	public boolean isCompleted() {
		return completed;
	}

	public Collection<Violation> getViolations() {
		return violations;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
