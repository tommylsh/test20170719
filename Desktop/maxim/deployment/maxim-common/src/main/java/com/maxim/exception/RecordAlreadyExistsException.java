package com.maxim.exception;

import java.io.Serializable;

/**
 * Whenever a Query is issue to a method which is not supported, this exception
 * will be thrown
 * 
 * @author CPPPAA
 */
public class RecordAlreadyExistsException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130628L;

	private static final String ERROR_CODE = "F-0013";

	public RecordAlreadyExistsException(Serializable key) {
		super(ERROR_CODE, key);
	}
}
