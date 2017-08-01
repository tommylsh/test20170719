package com.maxim.pos.sales.persistence;

import java.text.MessageFormat;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;

@Repository("houseKeepingDao")
public class HouseKeepingDao extends HibernateDAO {
	
//    public SchemeJobLog getSchemeJobLog(){
//    	Query query = entityManager.createNativeQuery("select min(CONVERT(datetime, END_TIME, 101)) as endTime from POLL_SCHEME_JOB_LOG");
//    	Date endTime = (Date) query.getSingleResult();
//    	SchemeJobLog schemeJobLog = new SchemeJobLog();
//    	schemeJobLog.setEndTime(endTime);
//    	return schemeJobLog;
//    }
    
    public int removeSchemeJobLog(int days){
//    	Query query = entityManager.createNativeQuery("select POLL_SCHEME_JOB_LOG_ID from (select POLL_SCHEME_JOB_LOG_ID,DATEDIFF(day,LAST_UPDATE_TIME,GETDATE()) AS DiffDate from POLL_SCHEME_JOB_LOG) as t where t.DiffDate > "+days);
//    	List<BigDecimal> list = query.getResultList();
		String del = "DELETE POLL_SCHEME_JOB_LOG\n" +
				"WHERE\n" +
				"\tDATEDIFF(\n" +
				"\t\tDAY,\n" +
				"\t\tLAST_UPDATE_TIME,\n" +
				"\t\tGETDATE()\n" +
				"\t) > {0}";
//    	for (BigDecimal id : list) {
    		return entityManager.createNativeQuery(MessageFormat.format(del,days)).executeUpdate();
//		}
    }
    
//    public TaskJobLog getTaskJobLog(){
//    	TaskJobLog taskJobLog = new TaskJobLog();
//    	Query query = entityManager.createNativeQuery("select min(CONVERT(datetime, END_TIME, 101)) as endTime from TASK_JOB_LOG");
//    	Date endTime = (Date) query.getSingleResult();
//    	taskJobLog.setEndTime(endTime);
//    	return taskJobLog;
//    }
    
    public int removeTaskJobLog(int days){
//    	Query query = entityManager.createNativeQuery("select TASK_JOB_LOG_ID from (select TASK_JOB_LOG_ID,DATEDIFF(day,LAST_UPDATE_TIME,GETDATE()) AS DiffDate from TASK_JOB_LOG) as t where t.DiffDate > "+days);
//    	List<BigDecimal> list = query.getResultList();
//    	for (BigDecimal id : list) {
//    		entityManager.createNativeQuery("delete from TASK_JOB_LOG where TASK_JOB_LOG_ID = "+id).executeUpdate();
//		}
		String del = "DELETE TASK_JOB_LOG " +
				" WHERE " +
				" DATEDIFF(DAY,LAST_UPDATE_TIME,GETDATE())" +
				" >{0}";

		return entityManager.createNativeQuery(MessageFormat.format(del,days)).executeUpdate();
    }
    
	public int removeExceptionList(int days){
//    	Query query = entityManager.createNativeQuery("select TASK_JOB_EXECEPTION_DETAIL_ID from (select TASK_JOB_EXECEPTION_DETAIL_ID,DATEDIFF(day,LAST_UPDATE_TIME,GETDATE()) AS DiffDate from TASK_JOB_EXECEPTION_DETAIL) as t where t.DiffDate > "+days);
//    	List<BigDecimal> list = query.getResultList();
//    	for (BigDecimal id : list) {
//    		entityManager.createNativeQuery("delete from TASK_JOB_EXECEPTION_DETAIL where TASK_JOB_EXECEPTION_DETAIL_ID = "+id).executeUpdate();
//		}

		String del = "DELETE TASK_JOB_EXECEPTION_DETAIL where TASK_JOB_LOG_ID IN (SELECT" +
				" TASK_JOB_LOG_ID " +
				" FROM" +
				" TASK_JOB_LOG " +
				" WHERE " +
				" DATEDIFF(DAY,LAST_UPDATE_TIME,GETDATE())" +
				" >{0})";

		return entityManager.createNativeQuery(MessageFormat.format(del,days)).executeUpdate();
    }
	
	public int removeDetailList(int days){
//    	Query query = entityManager.createNativeQuery("select TASK_JOB_LOG_DETAIL_ID from (select TASK_JOB_LOG_DETAIL_ID,DATEDIFF(day,LAST_UPDATE_TIME,GETDATE()) AS DiffDate from TASK_JOB_LOG_DETAIL) as t where t.DiffDate > "+days);
//    	List<BigDecimal> list = query.getResultList();
//    	for (BigDecimal id : list) {
//    		entityManager.createNativeQuery("delete from TASK_JOB_LOG_DETAIL where TASK_JOB_LOG_DETAIL_ID = "+id).executeUpdate();
//		}
		String del = "DELETE TASK_JOB_LOG_DETAIL where TASK_JOB_LOG_ID IN (SELECT" +
				" TASK_JOB_LOG_ID " +
				" FROM" +
				" TASK_JOB_LOG " +
				" WHERE " +
				" DATEDIFF(DAY,LAST_UPDATE_TIME,GETDATE())" +
				" >{0})";

		 return entityManager.createNativeQuery(MessageFormat.format(del,days)).executeUpdate();
    	
    }

}
