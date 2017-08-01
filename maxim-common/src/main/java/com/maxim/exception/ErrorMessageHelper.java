package com.maxim.exception;

import java.util.ResourceBundle;

public final class ErrorMessageHelper {

	private static final ResourceBundle errorRes = ResourceBundle.getBundle("errorDesc");

	public static String getErrorMessage(BaseException exception) {
		return errorRes.getString(exception.getErrorCode());
	}
	
	public static String getErrorMessage(String errorCode) {
		return errorRes.getString(errorCode);
	}
}
