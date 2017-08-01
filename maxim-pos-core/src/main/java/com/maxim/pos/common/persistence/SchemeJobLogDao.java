package com.maxim.pos.common.persistence;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.User;
import com.maxim.util.BeanUtil;

@Repository("schemeJobLogDao")
public class SchemeJobLogDao extends HibernateDAO {

	@Resource(name="systemPrincipal")
	private User systemPrincipal;

    private static final String HQL_findSchemeJobLogByCriteria = "findSchemeJobLogByCriteria";
    private static final String HQL_findLatestSchemeJobLog = "findLatestSchemeJobLog";
    private static final String HQL_countSchemeJobLogDifferentUser = "countSchemeJobLogDifferentUser";
    private static final String SQL_updateOtherSchemeJobLogNotLatest = "updateOtherSchemeJobLogNotLatest";
    
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

	public int updateOtherTaskJobLogNotLatest(Long scheduleJobId, String createUser) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        paramMap.put("scheduleJobId", scheduleJobId);
        if (createUser != null)
        {
        	paramMap.put("createUser", createUser);
        }

        String sql = processTemplate(SQL_updateOtherSchemeJobLogNotLatest, paramMap);
		
		return jdbcTemplate.update(sql, paramMap);
    }
    public List<SchemeJobLog> findSchemeJobLogByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeJobLogByCriteria, paramMap);
        return getList(cmd, SchemeJobLog.class);
    }
    
    public long countCreateUserByScheduleJobIdAndCreateUser(Long scheduleJobId, String createUser) {
        Map<String, Object> paramMap =  new HashMap<String, Object>();;
        paramMap.put("scheduleJobId", scheduleJobId);
        paramMap.put("createUser", createUser);
//        paramMap.put("orderbyLastUpdateTimeDesc", "Y");
        paramMap.put("orderbyCreateTimeDesc", "Y");
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeJobLogByCriteria, paramMap);
        List<SchemeJobLog> list = getList(cmd, SchemeJobLog.class,0,2);
        if (list.size() == 0)
        {
        	return 1 ;
        }
        Date datetime = null ;
        for (SchemeJobLog log : list)
        {
        	datetime = log.getLastUpdateTime();
        }
        paramMap =  new HashMap<String, Object>();;
        paramMap.put("scheduleJobId", scheduleJobId);
        paramMap.put("createUser", createUser);
//        paramMap.put("lastUpdateTime", datetime);
        paramMap.put("createTime", datetime);
        Long count = super.getSingle(new PosDaoCmd(HQL_countSchemeJobLogDifferentUser, paramMap), Long.class);
        return count+1;
        
    }

    public SchemeJobLog findLatestSchemeJobLog(Long scheduleJobId) {
    	return findLatestSchemeJobLog(scheduleJobId, null);
    }
    public SchemeJobLog findLatestSchemeJobLog(Long scheduleJobId, String createUser) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("scheduleJobId", scheduleJobId);
        if (createUser != null)
        {
            paramMap.put("createUser", createUser);
        }
        PosDaoCmd cmd = new PosDaoCmd(HQL_findLatestSchemeJobLog, paramMap);
        List<SchemeJobLog> results = getList(cmd, SchemeJobLog.class, 0, 1);

        if (results.size() == 1) {
            return results.get(0);
        }

        return null;
    }

    public SchemeJobLog addOrUpdateSchemeJobLog(SchemeJobLog schemeJobLog) {
        Auditer.audit(schemeJobLog);
        return (SchemeJobLog) save(schemeJobLog);
    }

    public void insertSchemeJobLog(SchemeJobLog schemeJobLog) {
        Auditer.audit(schemeJobLog);
        insert(schemeJobLog);
    }

}