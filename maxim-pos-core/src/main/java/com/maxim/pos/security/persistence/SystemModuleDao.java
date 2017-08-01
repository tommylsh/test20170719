package com.maxim.pos.security.persistence;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Permission;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.util.BeanUtil;

@Repository("systemModuleDao")
public class SystemModuleDao extends HibernateDAO {
    
    public static final String HQL_findSystemModuleByAlias = "findSystemModuleByAlias";

    public SystemModule findByPermissionAlias(CommonCriteria criteria) {
        Map<String, Object> paramMap = BeanUtil.transBeanToMap(criteria);
        PosDaoCmd cmd = new PosDaoCmd(HQL_findSystemModuleByAlias, paramMap);

        return getSingle(cmd, Permission.class);
    }
    
}
