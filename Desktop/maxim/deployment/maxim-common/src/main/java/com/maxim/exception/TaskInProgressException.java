package com.maxim.exception;

import java.io.Serializable;

/**
 * Async task is running while another async task is being triggered
 * 
 * @author CPPPAA
 */
public class TaskInProgressException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130628L;

	private static final String ERROR_CODE = "F-0020";

	public TaskInProgressException(Serializable key) {
		super(ERROR_CODE, key);
	}
}
