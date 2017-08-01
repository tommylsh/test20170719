package com.maxim.pos.common.persistence;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.entity.TaskJobLogDetail;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("taskJobLogDetailDao")
public class TaskJobLogDetailDao extends HibernateDAO {

    public static final String HQL_findTaskJobLogDetailByCriteria = "findTaskJobLogDetailByCriteria";

    public TaskJobLogDetail getById(Long id) {
        return getSingle(TaskJobLogDetail.class, id);
    }

    public List<TaskJobLogDetail> findTaskJobLogDetailByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobLogDetailByCriteria, paramMap);
        return (List<TaskJobLogDetail>) getPaginatedListByCriteriaAndType(cmd, paramMap, TaskJobLogDetail.class);
    }

    public Long getTaskJobLogDetailCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobLogDetailByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }

    public Long getTaskJobLogDetailCount(Long taskJobLogId) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("queryRecord", false);
		paramMap.put("taskJobLogId", taskJobLogId);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findTaskJobLogDetailByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }
}