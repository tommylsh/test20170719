package com.maxim.pos.security.persistence;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Role;
import com.maxim.util.BeanUtil;

@Repository("roleDao")
public class RoleDao extends HibernateDAO {

    public static final String HQL_findRolesBySystemAlias = "findRolesBySystemAlias";
    public static final String HQL_findRolesByCriteria = "findRolesByCriteria";

    public List<Role> findRolesBySystemAlias(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findRolesBySystemAlias, paramMap);

        return getList(cmd, Role.class);
    }

    @SuppressWarnings("unchecked")
    public List<Role> findRolesByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findRolesByCriteria, paramMap);

        return (List<Role>) getPaginatedListByCriteriaAndType(cmd, paramMap, Role.class);
    }

    public Long getRoleCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findRolesByCriteria, paramMap);

        return getSingle(cmd, Long.class);
    }

}
