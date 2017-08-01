package com.maxim.pos.sales.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SystemDashboard;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.common.value.SystemDashboardQueryCriteria;
import com.maxim.pos.common.value.TaskJobLogQueryCriteria;
import com.maxim.util.BeanUtil;

@Repository("branchSchemeDao")
public class BranchSchemeDao extends HibernateDAO {

    public static final String HQL_findBranchSchemeByCriteria = "findBranchSchemeByCriteria";
	public static final String HQL_findEodProcess = "findEodProcess";
	public static final String HQL_findSystemDashboard = "findSystemDashboard";
	
	
    @SuppressWarnings("unchecked")
	public List<BranchScheme> findBranchSchemeByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchSchemeByCriteria, paramMap);
        return (List<BranchScheme>) getPaginatedListByCriteriaAndType(cmd, paramMap, BranchScheme.class);
    }

    public Long getBranchSchemeCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findBranchSchemeByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }

    public BranchScheme getById(Long id) {
        return getSingle(BranchScheme.class, id);
    }

	@SuppressWarnings("unchecked")
	public List<TaskJobLog> findEodProcess(TaskJobLogQueryCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findEodProcess, paramMap);
        return (List<TaskJobLog>) getPaginatedListByCriteriaAndType(cmd, paramMap, TaskJobLog.class);
    }

	public Long getEodProcessCountByCriteria(TaskJobLogQueryCriteria criteria) {
		criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findEodProcess, paramMap);
        return getSingle(cmd, Long.class);
	}

	@SuppressWarnings("unchecked")
	public List<SystemDashboard> findSystemDashboard(SystemDashboardQueryCriteria criteria) {
		Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSystemDashboard, paramMap);
        return (List<SystemDashboard>) getPaginatedListByCriteriaAndType(cmd, paramMap, SystemDashboard.class);
	}

	public Long getSystemDashboardCountByCriteria(SystemDashboardQueryCriteria criteria) {
		criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSystemDashboard, paramMap);
        return getSingle(cmd, Long.class);
	}
}
