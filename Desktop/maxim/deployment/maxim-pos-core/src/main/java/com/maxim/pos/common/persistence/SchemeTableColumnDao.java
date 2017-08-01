package com.maxim.pos.common.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.maxim.dao.HibernateEntityDAO;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;

@Component("schemeTableColumnDao")
public class SchemeTableColumnDao extends HibernateEntityDAO<SchemeTableColumn, Long> {

	private static final String HQL_findSchemeTableColumnByCriteria = "findSchemeTableColumnByCriteria";
	
    public List<SchemeTableColumn> findSchemeTableColumnByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
//        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeTableColumnByCriteria, paramMap);

//        return (List<SchemeTableColumn>) getPaginatedListByCriteriaAndType(cmd, paramMap, SchemeTableColumn.class);
        return (List<SchemeTableColumn>) super.getEntityListByQueryKey(HQL_findSchemeTableColumnByCriteria, paramMap);
    }
    public List<SchemeTableColumn> findSchemeTableColumnByCriteria(Map<String, Object> paramMap) {
        return (List<SchemeTableColumn>) super.getEntityListByQueryKey(HQL_findSchemeTableColumnByCriteria, paramMap);
    }
    
   }
