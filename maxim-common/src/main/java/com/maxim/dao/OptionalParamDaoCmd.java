package com.maxim.dao;


/**
 * Abstract Command Object representing SQL query with Optional Params
 * 
 * @author SPISTEV
 */
public abstract class OptionalParamDaoCmd extends DaoCmd {

	private static final String optFlagSuffix = "Set";

	public OptionalParamDaoCmd(String queryKey) {
		super(queryKey);
	}

	protected void setOptionalParam(String paramName, Object value,
			Object defaultValue) {
		if (value == null) {
			params.put(paramName + optFlagSuffix,0);
			params.put(paramName, defaultValue);
		} else {
			params.put(paramName + optFlagSuffix, 1);
			params.put(paramName, value);
		}
	}
}
