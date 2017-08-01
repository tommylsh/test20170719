package com.maxim.dao;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.ejb.QueryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.exception.DatabaseException;

@Transactional
@Repository("hibernateDAO")
public class HibernateDAO implements DAO {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    private QueryFileHandler queryFileHandler;

    protected static final String HQL = "hql";
    protected static final String SQL = "sql";

    public static final String START_FROM_KEY = "startFrom";
    public static final String MAX_RESULT_KEY = "maxResult";

    @SuppressWarnings("unchecked")
    public <T> T getSingle(DaoCmd cmd, Class<?> T) {
        Query query = createQuery(cmd);
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public <T> T getSingle(Class<T> entityClass, Object entityPK) {
        return entityManager.find(entityClass, entityPK);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getAllList(Class<?> T) {
        StringBuffer queryBuffer = new StringBuffer();
        queryBuffer.append("select entity from ").append(T.getSimpleName()).append(" entity");

        Query query = entityManager.createQuery(queryBuffer.toString());
        return query.getResultList();
    }

    @Override
    public <T> List<T> getList(DaoCmd cmd, Class<?> T) {
        return getList(cmd, T, 0, Integer.MAX_VALUE);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(DaoCmd cmd, Class<?> T, Integer startFrom, Integer maxResult) {
        if (startFrom == null || startFrom.intValue() < 0) {
            throw new IllegalArgumentException("startFrom should be equal to 0 or larger than 0");
        }
        
        if (maxResult == null || maxResult.intValue() <= 0) {
            throw new IllegalArgumentException("maxResult should larger than 0");
        }
        
        Query query = createQuery(cmd);
        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }
        if (startFrom != null) {
            query.setFirstResult(startFrom);
        }
        return query.getResultList();
    }

    private Query createQuery(DaoCmd cmd) {

        String queryType = queryFileHandler.getQueryFile().getString(cmd.getQueryKey() + "[@type]");
        if (queryType == null) {
            queryType = HQL;
        }

        Query query = null;
        if (HQL.equals(queryType)) {

            StringBuilder addString = new StringBuilder();
            if (cmd.getParamString() != null) {
                addString.append(cmd.getParamString());
            }
            if (cmd.getOrderString() != null) {
                addString.append(cmd.getOrderString());
            }

            // String queryString =
            // queryFileLoader.getQueryFile().getString(cmd.getQueryKey());
            String queryString = processTemplate(cmd);

            if (StringUtils.isNotBlank(addString.toString())) {
                if (StringUtils.contains(queryString.toLowerCase(), " order ")) {
                    queryString = queryString + "," + addString.toString();
                } else {
                    queryString = queryString + " order by " + addString.toString();
                }
            }
            query = entityManager.createQuery(queryString);

        } else if (SQL.equals(queryType)) {
            StringBuilder addString = new StringBuilder();
            if (StringUtils.isNotBlank(addString.toString())) {
                if (cmd.getParamString() != null) {
                    addString.append(cmd.getParamString());
                }
                if (cmd.getOrderString() != null) {
                    addString.append(cmd.getOrderString());
                }
            }

            String queryString = processTemplate(cmd);

            if (cmd.getStatements() == null || cmd.getStatements().size() <= 0) {
                query = entityManager.createNativeQuery(queryString);
            } else {
                StrSubstitutor sub = new StrSubstitutor(cmd.getStatements(), "{", "}");
                queryString = sub.replace(queryString);
                if (StringUtils.contains(queryString.toLowerCase(), " order ")) {
                    queryString = queryString + "," + addString.toString();
                } else {
                    queryString = queryString + " order by " + addString.toString();
                }
                query = entityManager.createNativeQuery(queryString);
            }
        } else {
            throw new DatabaseException("Unknown query type: " + queryType);
        }
        
        if (cmd.getTransformer() != null)
        {
            query.unwrap( org.hibernate.Query.class).setResultTransformer(cmd.getTransformer());
        }
        
        setupParams(cmd, query);

        return query;
    }

    public void update(Object obj) {
    	merge(obj);
    }

    public Object save(Object obj) {
        return merge(obj);
    }

    public Object merge(Object obj) {
        return entityManager.merge(obj);
    }

    public void delete(Object obj) {
        entityManager.remove(obj);
    }
    public void insert(Object obj) {
        entityManager.persist(obj);
    }

    public void execute(DaoCmd cmd) {
        String queryType = queryFileHandler.getQueryFile().getString(cmd.getQueryKey() + "[@type]");
        if (queryType == null) {
            queryType = HQL;
        }

        String queryString = processTemplate(cmd);

        Query query = null;
        if (HQL.equals(queryType)) {
            query = entityManager.createQuery(queryString);
        } else if (SQL.equals(queryType)) {
            query = entityManager.createNativeQuery(queryString);
        } else {
            throw new DatabaseException("Unknown query type: " + queryType);
        }

        setupParams(cmd, query);
        query.executeUpdate();
    }

    protected void setupParams(DaoCmd cmd, Query query) {
        String[] namedParameters = ((QueryImpl<?>) query).getHibernateQuery().getNamedParameters();
        
        for (Entry<String, Object> entry : cmd.getParams().entrySet()) {
            
            for (String param : namedParameters) {
                if (param.equals(entry.getKey())) {
                    query.setParameter(entry.getKey(), entry.getValue());
                    break;
                }
            }
        }
    }

    /**
     * FreeMarker Template Process
     * 
     * @param cmd
     * @return hql/sql queryString
     */
    protected String processTemplate(DaoCmd cmd) {

        // add 'synchronized' to keep the cfg.setTemplateLoader(stringLoader)
        // and cfg.getTemplate(queryKey) using the same 'queryKey' from
        // multi-threading call
        return queryFileHandler.formatQuery(cmd.getQueryKey(),
                queryFileHandler.getQueryFile().getString(cmd.getQueryKey()), cmd.getParams());
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

    protected List<? extends Object> getPaginatedListByCriteriaAndType(DaoCmd cmd, Map<String, Object> paramMap, Class<?> clazz) {
        return getList(cmd, clazz, (Integer) paramMap.get(START_FROM_KEY), (Integer) paramMap.get(MAX_RESULT_KEY));
    }

}
