package com.maxim.exception;


/**
 * Whenever a Query is issue to a method which is not supported, this exception
 * will be thrown
 * 
 * @author SPISTEV
 */
public class UnknownUserException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130628L;

	private static final String ERROR_CODE = "T-0004";

	public UnknownUserException() {
		super(ERROR_CODE);
	}
	
	public UnknownUserException(String userId) {
		super(ERROR_CODE, userId);
	}
}
