package com.maxim.pos.common.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.entity.TaskJobExceptionDetail;
import com.maxim.pos.common.enumeration.ExceptionDetailStatus;
import com.maxim.pos.common.enumeration.Severity;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Repository("taskJobExceptionDetailDao")
public class TaskJobExceptionDetailDao extends HibernateDAO {

    public static final String HQL_findTaskJobExceptionDetailByCriteria = "findTaskJobExceptionDetailByCriteria";

    public static final String HQL_findTaskJobExceptionDetailByStatusAndSeverity = "findTaskJobExceptionDetailByStatusAndSeverity";
    
    public TaskJobExceptionDetail getById(Long id) {
        return getSingle(TaskJobExceptionDetail.class, id);
    }

    public List<TaskJobExceptionDetail> findTaskJobExceptionDetailByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobExceptionDetailByCriteria, paramMap);
        return (List<TaskJobExceptionDetail>) getPaginatedListByCriteriaAndType(cmd, paramMap, TaskJobExceptionDetail.class);
    }

    public Long getTaskJobExceptionDetailCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobExceptionDetailByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }

	public List<TaskJobExceptionDetail> findTaskJobExeptionDetailByStatusAndSeverity(
			ExceptionDetailStatus status, Severity severity) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("status", status);
		paramMap.put("severity", severity.getValue(severity));
        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobExceptionDetailByStatusAndSeverity, paramMap);
		return getList(cmd, TaskJobExceptionDetail.class);
	}

}