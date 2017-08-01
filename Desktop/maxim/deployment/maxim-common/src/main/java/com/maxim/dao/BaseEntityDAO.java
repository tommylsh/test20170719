package com.maxim.dao;

import java.util.List;

public interface BaseEntityDAO<T,K> extends BaseDAO {

	
	public T findByKey(K key );
	public T deleteByKey(K key );

	public List<T> findAll();
	
	public T update(T obj);
	public void delete(T obj);
	public void insert(T obj);
    public void mergerDelete(T obj);
    public void mergeInsert(T obj) ;


	


}
