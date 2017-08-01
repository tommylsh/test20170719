package com.maxim.dao;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ejb.QueryImpl;
import org.hibernate.transform.BasicTransformerAdapter;
import org.hibernate.transform.ResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.exception.DatabaseException;

@Transactional
public class HibernateBaseDAO implements BaseDAO {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected QueryFileHandler queryFileHandler;

    public static final String HQL = "hql";
    public static final String SQL = "sql";

    public static final String START_FROM_KEY = "startFrom";
    public static final String MAX_RESULT_KEY = "maxResult";


    public Object getSingle(String queryType, String sql) {
       return getSingle(queryType, sql, null, null, null);
    }    
    
    public Object getSingleByQueryKey(String queryKey, Map<String, Object> params) {
        Query query = createQueryByQueryKey(queryKey, null, params, null, null);
        
        return getSingle(query);
    }
    public Object getSingleByQueryKey(String queryKey, String addString, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer) {
        Query query = createQueryByQueryKey(queryKey, addString, params, stmts, transformer);
        
        return getSingle(query);
    }
    public Object getSingle(String queryType, String sql, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer) {
        Query query = createQuery(queryType, sql, params, stmts, transformer);
         
        return getSingle(query);
    }
     
 	public Object getSingle(Query query)
    {
         try {
             return query.getSingleResult();
         } catch (NoResultException e) {
             return null;
         }
     }


	public List<?>getList(String queryType, String sql) 
	{
		return getList(queryType, sql, null, null,null,null,null);
	}
	public List<?> getList(String queryType, String sql, ResultTransformer transformer) 
	{
		return getList(queryType, sql, null, null, null, null, transformer);
	}

	
	// Get List by SQL with Params
	public List<?> getList(String queryType, String sql, Map<String, Object> params) 
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;

		return getList(queryType, sql, startFrom, maxResult, params, null, null);
	}
	public List<Map<String, Object>> getMapList(String queryType, String sql, Map<String, Object> params) 
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;

		return getMapList(queryType, sql, startFrom, maxResult, params, null);
	}
	public List<?> getList(String queryType, String sql, Map<String, Object> params, ResultTransformer transformer) 
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;

		return getList(queryType, sql, startFrom, maxResult, params, null, transformer);
	}

	// Get List by Query Key with Params
	public List<?> getListByQueryKey(String queryKey)
	{
        return getListByQueryKey(queryKey, null, null, null, null, null, null);
	}
	public List<?> getListByQueryKey(String queryKey, Integer startFrom, Integer maxResult)
	{
        return getListByQueryKey(queryKey, null, startFrom, maxResult, null, null, null);
	}
	
	public <T> List<T> getListByQueryKey(String queryKey, Map<String, Object> params, Class<T> clazz)
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;
        
        return (List<T>) getListByQueryKey(queryKey, null, startFrom, maxResult, params, null, null,clazz);
	}

	public List<?> getListByQueryKey(String queryKey, Map<String, Object> params)
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;
        
        return getListByQueryKey(queryKey, null, startFrom, maxResult, params, null, null);
	}
	public List<?> getListByQueryKey(String queryKey, Map<String, Object> params, Integer startFrom, Integer maxResult)
	{
        return getListByQueryKey(queryKey, null, startFrom, maxResult, params, null, null);
	}
	public List<Map<String, Object>> getMapListByQueryKey(String queryKey, Map<String, Object> params)
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;
        
        return getMapListByQueryKey(queryKey, null, startFrom, maxResult, params, null);
	}
	public List<?> getListByQueryKey(String queryKey, Map<String, Object> params, ResultTransformer transformer)
	{
        Integer startFrom = (Integer) params.get(START_FROM_KEY) ;
        Integer maxResult = (Integer) params.get(MAX_RESULT_KEY) ;
        
        return getListByQueryKey(queryKey, null, startFrom, maxResult, params, null, transformer);
	}

	// getListByQueryKey (Full)
	public List<Map<String, Object>> getMapListByQueryKey(String queryKey, String addString, Integer startFrom, Integer maxResult, Map<String, Object> params, Map<String, Object> stmts) 
    {
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) getListByQueryKey(queryKey, addString, startFrom, maxResult, params, stmts, ALIAS_TO_ENTITY_LINKED_MAP);
        
		return list;
    }
 	public List<?> getListByQueryKey(String queryKey, String addString, Integer startFrom, Integer maxResult, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer) 
    {
        Query query = createQueryByQueryKey(queryKey, addString, params, stmts, transformer);

        return getList(query, startFrom, maxResult);
    }
    
 	
 	@SuppressWarnings("unchecked")
	public <T> List<T> getListByQueryKey(String queryKey, String addString, Integer startFrom, Integer maxResult, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer, Class<T> clazz) 
    {
        Query query = createQueryByQueryKey(queryKey, addString, params, stmts, transformer);

        return (List<T>) getList(query, startFrom, maxResult);
    }
    
	
	// getList By SQL (Full)
	public List<Map<String, Object>> getMapList(String queryType, String sql, Integer startFrom, Integer maxResult, Map<String, Object> params, Map<String, Object> stmts) 
    {
        @SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) getList(queryType, sql, startFrom, maxResult, params, stmts, ALIAS_TO_ENTITY_LINKED_MAP);
        
		return list;
    }
	public List<?> getList(String queryType, String sql, Integer startFrom, Integer maxResult, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer) 
    {
        Query query = createQuery(queryType, sql, params, stmts, transformer);

        return getList(query, startFrom, maxResult);
    }
	public <E> List<E> getList(String queryType, String sql, Integer startFrom, Integer maxResult, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer, Class<?> E) 
    {
        Query query = createQuery(queryType, sql, params, stmts, transformer);

        return getList(query, startFrom, maxResult, E);
    }
    
	// Get List (By Query Object)
	@SuppressWarnings("unchecked")
	public <E> List<E> getList(Query query, Integer startFrom, Integer maxResult, Class<?> E) 
    {
        if (startFrom != null && startFrom.intValue() < 0) {
            throw new IllegalArgumentException("startFrom should be equal to 0 or larger than 0");
        }
        
        if (maxResult != null && maxResult.intValue() <= 0) {
            throw new IllegalArgumentException("maxResult should larger than 0");
        }
        
        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }
        if (startFrom != null) {
            query.setFirstResult(startFrom);
        }
        return query.getResultList();
    }
    
	public List<?> getList(Query query, Integer startFrom, Integer maxResult) 
    {
        if (startFrom != null && startFrom.intValue() < 0) {
            throw new IllegalArgumentException("startFrom should be equal to 0 or larger than 0");
        }
        
        if (maxResult != null && maxResult.intValue() <= 0) {
            throw new IllegalArgumentException("maxResult should larger than 0");
        }
        
        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }
        if (startFrom != null) {
            query.setFirstResult(startFrom);
        }
        return query.getResultList();
    }
    

    protected Query createQueryByQueryKey(String queryKey) {
        return createQueryByQueryKey(queryKey, null, null, null, null);
    }

    protected Query createQueryByQueryKey(String queryKey, Map<String, Object> params) {
        return createQueryByQueryKey(queryKey, null, params, null, null);
    }

    protected Query createQueryByQueryKey(String queryKey, String addString, Map<String, Object> params) {
        return createQueryByQueryKey(queryKey, addString, params, null, null);
    }

    protected Query createQueryByQueryKey(String queryKey, String addString, Map<String, Object> params, ResultTransformer transformer) {
        return createQueryByQueryKey(queryKey, addString, params, null, transformer);
    }

    
    protected Query createQueryByQueryKey(String queryKey, String addString, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer) {
    	
      String queryType = queryFileHandler.getQueryFile().getString(queryKey + "[@type]");
      if (queryType == null) {
          queryType = HQL;
      }
      
      String queryString = processTemplate(queryKey, params);

      if (addString != null)
      {
	    if (StringUtils.isNotBlank(addString.toString())) {
	        if (StringUtils.contains(queryString.toLowerCase(), " order ")) {
	            queryString = queryString + "," + addString.toString();
	        } else {
	            queryString = queryString + " order by " + addString.toString();
	        }
	    }
      }

    
      Query query = createQuery(queryType, queryString, params, stmts, transformer);

      
      return query ;

   }


    
    protected Query createQuery(String queryType, String queryString) {
    	
    	return createQuery(queryType, queryString, null, null,null);
    }
    protected Query createQuery(String queryType, String queryString, ResultTransformer transformer) {
    	
    	return createQuery(queryType, queryString, null, null,transformer);
    }
    protected Query createQuery(String queryType, String queryString, Map<String, Object> params ) {
      	return createQuery(queryType, queryString, params, null, null);
    }
        
    protected Query createQuery(String queryType, String queryString, Map<String, Object> params, Map<String, Object> stmts, ResultTransformer transformer ) {


        Query query = null;
        
//        System.out.println("resultClass : "+resultClass);
        if (HQL.equals(queryType)) {

             query = entityManager.createQuery(queryString);

  
        } else if (SQL.equals(queryType)) {
        	
            if (stmts != null)
        	{
              StrSubstitutor sub = new StrSubstitutor(stmts, "{", "}");
              queryString = sub.replace(queryString);      	
        	} 
            query = entityManager.createNativeQuery(queryString);
        } else {
            throw new DatabaseException("Unknown query type:  " + queryType);
        }

        if (transformer != null)
        {
            query.unwrap( org.hibernate.Query.class).setResultTransformer(transformer);
//            query.unwrap(SQLQuery.class).setResultTransformer(transformer);
        }
        if (params != null)
        {
        	setupParams(query, params);
        }

        
        return query;
    }

    protected void setupParams(Query query, Map<String, Object> params) {
        String[] namedParameters = ((QueryImpl<?>) query).getHibernateQuery().getNamedParameters();
        
        for (String key : namedParameters) {
        	Object value = params.get(key);
      	
        	query.setParameter(key, value);
        }  
    }



    /**
     * FreeMarker Template Process
     * 
     * @param cmd
     * @return hql/sql queryString
     */
    protected String processTemplate(String queryKey, Map<String, Object> params) {

        // add 'synchronized' to keep the cfg.setTemplateLoader(stringLoader)
        // and cfg.getTemplate(queryKey) using the same 'queryKey' from
        // multi-threading call
        return queryFileHandler.formatQuery(queryKey,
                queryFileHandler.getQueryFile().getString(queryKey), params);
    }


	public static final AliasToEntityLinkedMapResultTransformer ALIAS_TO_ENTITY_LINKED_MAP = new AliasToEntityLinkedMapResultTransformer();
	
    public static class AliasToEntityLinkedMapResultTransformer extends BasicTransformerAdapter implements Serializable{

    	/**
		 *  serialVersionUID
		 */
		private static final long serialVersionUID = -5765052996370923883L;
		
    	/**
    	 * Disallow instantiation of AliasToEntityMapResultTransformer.
    	 */
    	private AliasToEntityLinkedMapResultTransformer() {
    	}

    	/**
    	 * {@inheritDoc}
    	 */
    	public Object transformTuple(Object[] tuple, String[] aliases) {
   		
    		Map<String, Object> result = new LinkedHashMap<String, Object>(tuple.length);
    		for ( int i=0; i<tuple.length; i++ ) {
    			String alias = aliases[i];
    			if ( alias!=null ) {
    				result.put( alias, tuple[i] );
    			}
    		}
    		return result;
    	}

    }
    
	public static final AliasToBeanNamedMapResultTransformer ALIAS_TO_BEAN_NAME_MAP = new AliasToBeanNamedMapResultTransformer();
	
    public static class AliasToBeanNamedMapResultTransformer extends BasicTransformerAdapter implements Serializable{

    	/**
		 *  serialVersionUID
		 */
		private static final long serialVersionUID = -5765052996370923883L;
		
    	/**
    	 * Disallow instantiation of AliasToEntityMapResultTransformer.
    	 */
    	private AliasToBeanNamedMapResultTransformer() {
    	}

    	/**
    	 * {@inheritDoc}
    	 */
    	public Object transformTuple(Object[] tuple, String[] aliases) {
   		
    		Map<String, Object> result = new LinkedHashMap<String, Object>(tuple.length);
    		for ( int i=0; i<tuple.length; i++ ) {
    			String alias = aliases[i];
    			if ( alias!=null ) {
    				result.put( removeUnderscoreAndCapitalize(alias), tuple[i] );
    			}
    		}
    		return result;
    	}

        private static String removeUnderscoreAndCapitalize(String column) {
            StringBuilder result = new StringBuilder();
            String[] arr = column.toLowerCase().split("_");
            for (String str : arr) {
                result.append(StringUtils.capitalize(str));
            }
            return StringUtils.uncapitalize(result.toString());
        }
    }


}