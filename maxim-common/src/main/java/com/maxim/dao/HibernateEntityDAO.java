package com.maxim.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateEntityDAO<T,K> extends HibernateBaseDAO implements BaseEntityDAO<T,K> {

	protected Class<T> dtoClass = null ;
	protected Class<K> keyClass = null ;
	
    protected HibernateEntityDAO(Class<T> dtoClass, Class<K> keyClass) {
        this.dtoClass = dtoClass;
        this.keyClass = keyClass;
    }	
    
	@SuppressWarnings("unchecked")
	protected HibernateEntityDAO() {
    	Type genericSuperclass = this.getClass().getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type type = pt.getActualTypeArguments()[0];
            dtoClass = (Class<T>) type;
            type = pt.getActualTypeArguments()[1];
            keyClass = (Class<K>) type;
        }
//        this.typeParameterClass = typeParameterClass;
    }  
	
	@SuppressWarnings("unchecked")
	protected Class<T> getDataClass()
	{
		Class<T> dataClass = null;
    	Type genericSuperclass = this.getClass().getGenericSuperclass();

        if (genericSuperclass instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericSuperclass;
            Type type = pt.getActualTypeArguments()[0];
            dataClass = (Class<T>) type;
        }
        
        return dataClass;
    }
    
    
    public T save(T obj) {
        return entityManager.merge(obj);
    }


	@Override
    public T update(T obj) {
        return entityManager.merge(obj);
    }
	@Override
    public void insert(T obj) {
		entityManager.persist(obj);
    }
	@Override
    public void mergeInsert(T obj) {
		T newObj = entityManager.merge(obj);
        entityManager.persist(newObj);
    }
	@Override
    public void delete(T obj) {
        entityManager.remove(obj);
    }	
	@Override
    public void mergerDelete(T obj) {
		T newObj = entityManager.merge(obj);
        entityManager.remove(newObj);
    }
    
	@Override
	public T findByKey(K key) {
        return entityManager.find(dtoClass, key);
	}
	
	@Override
	public T deleteByKey(K key) {
		T obj = this.findByKey(key);
        entityManager.remove(obj);
        return obj;
	}
	
	
	
	@Override
	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		
		Session session = entityManager.unwrap(org.hibernate.Session.class);
		//Session session = (Session) entityManager.getDelegate();
    	return session.createCriteria(dtoClass).list();
	}
	
	
	
	// Get List by Query Key with Params
	public List<T> getEntityListByQueryKey(String queryKey)
	{
        return getEntityListByQueryKey(queryKey, null, null, null, null, null, null);
	}
	public List<T> getEntityListByQueryKey(String queryKey, Integer startFrom, Integer maxResult)
	{
        return getEntityListByQueryKey(queryKey, null, startFrom, maxResult, null, null, null);
	}
	public List<T> getEntityListByQueryKey(String queryKey, Map<String, Object> params)
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;
        
        return getEntityListByQueryKey(queryKey, null, startFrom, maxResult, params, null, null);
	}
	public List<T> getEntityListByQueryKey(String queryKey, Map<String, Object> params, Integer startFrom, Integer maxResult)
	{
        return getEntityListByQueryKey(queryKey, null, startFrom, maxResult, params, null, null);
	}
	
	
 	public List<T> getEntityListByQueryKey(String queryKey, String addString, Integer startFrom, Integer maxResult, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer) 
    {
        Query query = createQueryByQueryKey(queryKey, addString, params, stmts, transformer);

        return getEntityList(query, startFrom, maxResult);
    }


	// Get List (By Query Object)
	public List<T> getEntityList(Query query, Integer startFrom, Integer maxResult) 
    {
		return super.getList(query, startFrom, maxResult, dtoClass);
    }


}
