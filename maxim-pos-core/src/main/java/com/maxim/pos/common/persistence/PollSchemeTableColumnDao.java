package com.maxim.pos.common.persistence;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.BeanUtil;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Repository("pollSchemeTableColumnDao")
public class PollSchemeTableColumnDao extends HibernateDAO {

    public static final String HQL_findSchemeTableColumnByCriteria = "findSchemeTableColumnByCriteria";

    public SchemeTableColumn getById(Long id) {
        return getSingle(SchemeTableColumn.class, id);
    }

    public List<SchemeTableColumn> findSchemeTableColumnByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeTableColumnByCriteria, paramMap);
        return (List<SchemeTableColumn>) getPaginatedListByCriteriaAndType(cmd, paramMap, SchemeTableColumn.class);
    }

    public Long getSchemeTableColumnCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSchemeTableColumnByCriteria, paramMap);
        return getSingle(cmd, Long.class);
    }
    public int delete(Long id){

        Query query = entityManager.createQuery("delete SchemeTableColumn  where id=?1");
        query.setParameter(1, id);
         return query.executeUpdate(); //影响的记录数
    }

}