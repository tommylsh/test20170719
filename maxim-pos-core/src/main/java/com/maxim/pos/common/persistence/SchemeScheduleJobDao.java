package com.maxim.pos.common.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Repository("schemeScheduleJobDao")
public class SchemeScheduleJobDao extends HibernateDAO {

    public static final String HQL_findSchemeScheduleJobByCriteria = "findSchemeScheduleJobByCriteria";
    public static final String HQL_findSchemeScheduleJobByBranchScheme = "findSchemeScheduleJobByBranchScheme";
    public SchemeScheduleJob getById(Long id) {
        return getSingle(SchemeScheduleJob.class, id);
    }

    public List<SchemeScheduleJob> findSchemeScheduleJobByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeScheduleJobByCriteria, paramMap);
        return (List<SchemeScheduleJob>) getPaginatedListByCriteriaAndType(cmd, paramMap, SchemeScheduleJob.class);
    }

    public Long getSchemeScheduleJobCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeScheduleJobByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }

	public SchemeScheduleJob getSchemeScheduleJob(BranchScheme branchScheme) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("pollSchemeType", branchScheme.getPollSchemeType());
		paramMap.put("direction", branchScheme.getDirection());
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeScheduleJobByBranchScheme, paramMap);
        return getSingle(cmd, SchemeScheduleJob.class);
	}

}
