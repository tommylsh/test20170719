//package com.maxim.pos.sales.persistence;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.stereotype.Repository;
//
//import com.maxim.dao.HibernateDAO;
//import com.maxim.pos.common.entity.SystemDashboardEntity;
//import com.maxim.pos.common.persistence.PosDaoCmd;
//import com.maxim.pos.common.value.SystemDashboardQueryCriteria;
//import com.maxim.util.BeanUtil;
//
//@Repository("systemDashboardDao")
//public class SystemDashboardDao extends HibernateDAO {	
//	public static final String HQL_findEodProcess = "findEodProcess";
//	
//	@SuppressWarnings("unchecked")
//	public List<SystemDashboardEntity> findEodProcess(SystemDashboardQueryCriteria criteria) {
//        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findEodProcess, paramMap);
//
//        return (List<SystemDashboardEntity>) getPaginatedListByCriteriaAndType(cmd, paramMap, SystemDashboardEntity.class);
//    }
//
//	public Long getEodProcessCountByCriteria(SystemDashboardQueryCriteria criteria) {
//		criteria.setQueryRecord(false);
//        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findEodProcess, paramMap);
//        return getSingle(cmd, Long.class);
//	}
//}
