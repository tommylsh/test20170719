package com.maxim.exception;


/**
 * Whenever a Query is issue to a method which is not supported, this exception
 * will be thrown
 * 
 * @author SPISTEV
 */
public class PrivilegeException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130628L;

	private static final String ERROR_CODE = "F-0006";

	public PrivilegeException(String userId, String action) {
		super(ERROR_CODE, "[" + userId + "," + action + "]");
	}
}
