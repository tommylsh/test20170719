package com.maxim.pos.security.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Permission;
import com.maxim.util.BeanUtil;

@Repository("permissionDao")
public class PermissionDao extends HibernateDAO {

    public static final String HQL_findPermissionByAlias = "findPermissionByAlias";
    public static final String HQL_findPermissionsByCriteria = "findPermissionsByCriteria";
    public static final String HQL_findPermissionsByUserId = "findPermissionsByUserId";

    public Permission findByPermissionAlias(String alias) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("alias", alias);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findPermissionByAlias, paramMap);

        return getSingle(cmd, Permission.class);
    }

    @SuppressWarnings("unchecked")
    public List<Permission> findPermissionByCriteria(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findPermissionsByCriteria, paramMap);

        return (List<Permission>) getPaginatedListByCriteriaAndType(cmd, paramMap, Permission.class);
    }

    public Long getPermissionCountByCriteria(CommonCriteria criteria) {
        criteria.setQueryRecord(false);
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findPermissionsByCriteria, paramMap);

        return getSingle(cmd, Long.class);
    }

    public List<Permission> findAllPermissionsByUserId(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findPermissionsByUserId, paramMap);
        
        return getList(cmd, Permission.class);
    }

}
