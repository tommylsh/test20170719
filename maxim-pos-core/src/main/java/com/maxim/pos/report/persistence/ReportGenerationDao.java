package com.maxim.pos.report.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateBaseDAO;

@Repository("reportGenerationDao")
public class ReportGenerationDao extends HibernateBaseDAO {
	
	public final static String QUERY_KEY_findEDOCompletedBranchByCreateTimeRange = "findEDOCompletedBranchByCreateTimeRange";
	
	public List<Map<String, Object>> getListBySQL(String sql)
	{
		return getListBySQL(sql, null);
	}
	public List<Map<String, Object>> getListBySQL(String sql, Map<String, Object> params)
	{
		return super.getMapList(SQL, sql, params);
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> getObectArrayListBySQL(String sql, Map<String, Object> params)
	{
        Query query = createQuery(SQL, sql, params, null, null);

        Object[] header = query.unwrap(SQLQuery.class).getReturnAliases();

        List<Object[]> resultList = new ArrayList<Object[]>();
        
        resultList.add(header);
        
        resultList.addAll((List<Object[]>) super.getList(query, null, null));

		return resultList;
	}
	public List<?> getListByHQL(String sql)
	{
		return getListByHQL(sql, null);
	}
	public List<?> getListByHQL(String sql, Map<String, Object> params)
	{
		return super.getList(HQL, sql, params);
	}
	
	public List<Map<String, Object>> getEDOCompletedBranchByBusinessDate(java.sql.Date businessDate)
	{
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessDate",businessDate);
		
		System.out.println("businessDate : "+businessDate);
		
		return super.getMapListByQueryKey(QUERY_KEY_findEDOCompletedBranchByCreateTimeRange, params);
	}
}
