package com.maxim.pos.common.persistence;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateEntityDAO;
import com.maxim.dao.JdbcEntityDAO;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.TaskProcessStatus;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Repository("taskJobLog")
public class TaskJobLogDao extends HibernateEntityDAO<TaskJobLog, Long> {

    public static final String HQL_findTaskJobLogBySchemeJobLogIdAndBranchCode = "findTaskJobLogBySchemeJobLogIdAndBranchCode";
    public static final String HQL_findTaskJobLogByCriteria = "findTaskJobLogByCriteria";
    public static final String HQL_findLatestTaskJobLog = "findLatestTaskJobLog";
    public static final String SQL_findLatestTaskJobLog = "findLatestTaskJobLogUpdateTime";
    public static final String SQL_findLatestTaskJobLogWithLock = "findLatestTaskJobLogWithLock";
    public static final String SQL_purgeTaskJobLog = "purgeTaskJobLog";
    public static final String SQL_purgeTaskJobLogWithoutLatestTaskg = "purgeTaskJobLogWithoutLatestTask";
    public static final String SQL_updatePendingTaskJobLog = "updatePendingTaskJobLog";
    public static final String SQL_updateOtherTaskJobLogNotLatest = "updateOtherTaskJobLogNotLatest";

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    protected JdbcEntityDAO<TaskJobLog, Long> jdbcEntityDAO = new JdbcEntityDAO<TaskJobLog, Long>()
    		{
    			protected NamedParameterJdbcTemplate getNamedJdbcTemplate()
    			{
    				return jdbcTemplate ;
    			}

    		};

    public TaskJobLog findTaskJobLog(TaskJobLog taskJobLog) {
    	Map<String, Object> transBeanToMap = BeanUtil.transBeanToMap(taskJobLog);
    	Date startTime = taskJobLog.getStartTime(); 
    	Date lastUpdateTime = taskJobLog.getLastUpdateTime();
    	LogUtils.printLog(transBeanToMap.toString());
    	LogUtils.printLog(""+startTime);
    	LogUtils.printLog(""+lastUpdateTime);
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	
    	if (startTime != null)
    	{
	    	if (startTime instanceof Timestamp)
	    	{
	    		transBeanToMap.put("startTime", startTime.toString());
	    	}
	    	else 
	    	{
	    		transBeanToMap.put("startTime", dataFormat.format(startTime));
	    	}
    	}
    	if (lastUpdateTime != null)
    	{
	    	if (lastUpdateTime instanceof Timestamp)
	    	{
	    		transBeanToMap.put("lastUpdateTime", lastUpdateTime.toString());
	    	}
	    	else 
	    	{
	    		transBeanToMap.put("lastUpdateTime", dataFormat.format(lastUpdateTime));
	    	}
    	}
    	transBeanToMap.put("queryRecord", true);

    	LogUtils.printLog(transBeanToMap.toString());

        List<TaskJobLog> results = super.getEntityListByQueryKey(HQL_findTaskJobLogByCriteria, transBeanToMap, 0, 1);
        
        if (results.size() == 1) {
            return results.get(0);
        }

        return null;        
    }

    public TaskJobLog findTaskJobLogBySchemeJobLogIdAndBranchCode(Long pollSchemeJobLogId, String branchCode) {
    	
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("pollSchemeJobLogId", pollSchemeJobLogId);
        paramMap.put("branchCode", branchCode);

        List<TaskJobLog> results = super.getEntityListByQueryKey(HQL_findTaskJobLogBySchemeJobLogIdAndBranchCode, paramMap, 0, 1);
        if (results.size() == 1) {
            return results.get(0);
        }

        return null;        
    }


    		
    public List<TaskJobLog> findTaskJobLogByCriteria(CommonCriteria criteria) {
        if (criteria == null) {
//            return getList(new PosDaoCmd(HQL_findTaskJobLogByCriteria), TaskJobLog.class);
            return super.getEntityListByQueryKey(HQL_findTaskJobLogByCriteria);
        }
    	Map<String, Object> transBeanToMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobLogByCriteria, BeanUtil.transBeanToMap(criteria));
//        return super.getEntityListByQueryKey(HQL_findTaskJobLogByCriteria, criteria.getStartFrom(),
//                criteria.getMaxResult() == 0 ? Integer.MAX_VALUE : criteria.getMaxResult());
        return super.getEntityListByQueryKey(HQL_findTaskJobLogByCriteria,transBeanToMap);
//        return getList(cmd, TaskJobLog.class, criteria.getStartFrom(),
//                criteria.getMaxResult() == 0 ? Integer.MAX_VALUE : criteria.getMaxResult());
    }

//    public TaskJobLog findLatestTaskJobLog(Direction direction, Long branchSchemeId, PollSchemeType pollSchemeType,
//            Long schemeScheduleJobId) {

    public TaskJobLog findLatestTaskJobLog(Direction direction, Long branchSchemeId, PollSchemeType pollSchemeType,
    		String branchCode) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("direction", direction);
        paramMap.put("branchSchemeId", branchSchemeId);
        paramMap.put("pollSchemeType", pollSchemeType);
//        paramMap.put("schemeScheduleJobId", schemeScheduleJobId);
        paramMap.put("branchCode", branchCode);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findLatestTaskJobLog, paramMap);

        List<TaskJobLog> results = super.getEntityListByQueryKey(HQL_findLatestTaskJobLog, paramMap, 0, 1);
        
        if (results.size() == 1) {
            return results.get(0);
        }

        return null;
    }
    

	public int purgeTaskJobLog(Direction direction, Long branchSchemeId, PollSchemeType pollSchemeType,
	    		String branchCode, TaskProcessStatus status) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("direction", direction.toString());
        paramMap.put("branchSchemeId", branchSchemeId);
        paramMap.put("pollSchemeType", pollSchemeType.toString());
        paramMap.put("branchCode", branchCode);
        paramMap.put("status", status.name());

        String sql = processTemplate(SQL_purgeTaskJobLog, paramMap);
//		LogUtils.printLog("{} branch purgeTaskJobLog {}",
//				branchCode, sql);
		
		return jdbcTemplate.update(sql, paramMap);
    }
	
	public int purgeTaskJobLogWithoutLatestTask(Direction direction, Long branchSchemeId, PollSchemeType pollSchemeType,
    		String branchCode, TaskProcessStatus status, Long newTaskJobLogId) {
	    Map<String, Object> paramMap = new HashMap<String, Object>();
	    paramMap.put("direction", direction.toString());
	    paramMap.put("branchSchemeId", branchSchemeId);
	    paramMap.put("pollSchemeType", pollSchemeType.toString());
	    paramMap.put("branchCode", branchCode);
	    paramMap.put("status", status.name());
	    paramMap.put("newTaskJobLogId", newTaskJobLogId);
	
	    String sql = processTemplate(SQL_purgeTaskJobLogWithoutLatestTaskg, paramMap);
	//	LogUtils.printLog("{} branch purgeTaskJobLog {}",
	//			branchCode, sql);
		
		return jdbcTemplate.update(sql, paramMap);
	}

	
	public int updatePendingTaskJobLog(Direction direction, Long branchSchemeId, PollSchemeType pollSchemeType,
    		String branchCode) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("direction", direction.toString());
        paramMap.put("branchSchemeId", branchSchemeId);
        paramMap.put("pollSchemeType", pollSchemeType.toString());
        paramMap.put("branchCode", branchCode);

        String sql = processTemplate(SQL_updatePendingTaskJobLog, paramMap);
//		LogUtils.printLog("{} branch updatePendingTaskJobLog {}",
//				branchCode, sql);
		
		return jdbcTemplate.update(sql, paramMap);
    }
	
	public int updateOtherTaskJobLogNotLatest(Direction direction, Long branchSchemeId, PollSchemeType pollSchemeType,
    		String branchCode, Long newTaskJobLogId, Long latestTaskJobLogId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        if (newTaskJobLogId != null)
        {
        	paramMap.put("newTaskJobLogId", newTaskJobLogId);
        }
        if (latestTaskJobLogId != null)
        {
        	paramMap.put("latestTaskJobLogId", latestTaskJobLogId);
        }
        paramMap.put("direction", direction.toString());
        paramMap.put("branchSchemeId", branchSchemeId);
        paramMap.put("pollSchemeType", pollSchemeType.toString());
        paramMap.put("branchCode", branchCode);

        String sql = processTemplate(SQL_updateOtherTaskJobLogNotLatest, paramMap);
		
		return jdbcTemplate.update(sql, paramMap);
    }
	
	public TaskJobLog findLatestTaskJobLogForLock(Direction direction, Long branchSchemeId, PollSchemeType pollSchemeType,
    		String branchCode) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("direction", direction.toString());
        paramMap.put("branchSchemeId", branchSchemeId);
        paramMap.put("pollSchemeType", pollSchemeType.toString());
//        paramMap.put("schemeScheduleJobId", schemeScheduleJobId);
        paramMap.put("branchCode", branchCode);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findLatestTaskJobLog, paramMap);

        String sql = processTemplate(SQL_findLatestTaskJobLogWithLock, paramMap);
//		LogUtils.printLog("{} branch code find Latest {}",
//				branchCode, sql);
//		NamedParameterJdbcTemplate jdbc = new NamedParameterJdbcTemplate(dataSource);
        try
        {
			Long id = jdbcTemplate.queryForObject(sql, paramMap, Long.class);
//			LogUtils.printLog("{} branch code find Latest ID {}",
//					branchCode, id);
			if (id != null)
			{
				return super.findByKey(id);
			}
        }catch (EmptyResultDataAccessException e)
        {
            return null;    	
        }
			

        return null;
    }



    public Long getTaskJobLogCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobLogByCriteria, paramMap);
//        return getSingle(cmd, Long.class);
        return (Long) getSingleByQueryKey(HQL_findTaskJobLogByCriteria, paramMap);
    }
    
	public void batchInsert(List<TaskJobLog> objs) {
		this.jdbcEntityDAO.batchInsert(objs);
	}
	
	public void batchInsertWithoutReturnGenKey(List<TaskJobLog> objs) {
		this.jdbcEntityDAO.batchInsertWithoutReturnGenKey(objs);
	}
	
	public void batchUpdate(List<TaskJobLog> objs) {
		this.jdbcEntityDAO.batchUpdate(objs);
	}
	
	public void batchDelete(List<TaskJobLog> objs) {
		this.jdbcEntityDAO.batchDelete(objs);
	}


}
