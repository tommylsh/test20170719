package com.maxim.dao;

import java.util.List;

/**
 * Generic DAO interface
 * 
 * @author SPISTEV
 * 
 */
public interface DAO {

	/**
	 * Execute query for getting a particular entity
	 * 
	 * @param cmd
	 * @param clazz
	 * @return entities who match the query
	 */
	public <T> T getSingle(DaoCmd cmd, Class<?> T);

	/**
	 * Execute query for listing entities
	 * 
	 * @param cmd
	 * @param clazz
	 * @return entities who match the query
	 */
	public <T> List<T> getList(DaoCmd cmd, Class<?> T);

	/**
	 * Update an entity
	 * 
	 * @param obj
	 */
	public void update(Object obj);

	/**
	 * Execute
	 * 
	 */
	public void execute(DaoCmd cmd);


}
