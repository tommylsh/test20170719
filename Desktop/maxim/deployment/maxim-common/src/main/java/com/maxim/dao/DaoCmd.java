package com.maxim.dao;

import java.util.Collections;
import java.util.Map;

import org.hibernate.transform.ResultTransformer;

/**
 * Abstract Command Object representing SQL query
 * 
 * @author Steven
 */
public abstract class DaoCmd {

	private String queryKey;
	
	private String orderString;
	
	private String paramString;
	
	private Map<String, Object> statements = Collections.emptyMap();

	protected Map<String, Object> params = Collections.emptyMap();

	protected ResultTransformer transformer ;

	public DaoCmd(String queryKey) {
		super();
		this.queryKey = queryKey;
	}

	public DaoCmd(String queryKey, Map<String, Object> params) {
		super();
		this.queryKey = queryKey;
		this.params = params;
	}

	public String getQueryKey() {
		return queryKey;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String getOrderString() {
		return orderString;
	}

	public void setOrderString(String orderString) {
		this.orderString = orderString;
	}

	public Map<String, Object> getStatements() {
		return statements;
	}

	public void setStatements(Map<String, Object> statements) {
		this.statements = statements;
	}

	public String getParamString() {
		return paramString;
	}

	public void setParamString(String paramString) {
		this.paramString = paramString;
	}
	
	public ResultTransformer getTransformer() {
		return transformer;
	}

	public void setTransformer(ResultTransformer transformer) {
		this.transformer = transformer;
	}

	public void setDefaultEntityMapTransformer() {
		this.transformer = HibernateBaseDAO.ALIAS_TO_BEAN_NAME_MAP; 
	}



}
