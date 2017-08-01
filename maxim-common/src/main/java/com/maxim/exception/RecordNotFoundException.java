package com.maxim.exception;

import java.io.Serializable;

/**
 * Whenever a Query is issue to a method which is not supported, this exception
 * will be thrown
 * 
 * @author SPISTEV
 */
public class RecordNotFoundException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130628L;

	private static final String ERROR_CODE = "T-0003";

	public RecordNotFoundException(Serializable key) {
		super(ERROR_CODE, key);
	}
}
