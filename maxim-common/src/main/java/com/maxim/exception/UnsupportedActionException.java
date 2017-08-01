package com.maxim.exception;

import com.maxim.data.Query;

/**
 * Whenever a Query is issue to a method which is not supported, this exception
 * will be thrown
 * 
 * @author SPISTEV
 */
public class UnsupportedActionException extends BaseException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130628L;

	private static final String ERROR_CODE = "T-0002";

	public UnsupportedActionException(Query queryAsked) {
		super(ERROR_CODE, queryAsked);
	}
}
