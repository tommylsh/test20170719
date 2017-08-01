package com.maxim.pos.test.security.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.pos.security.value.RoleQueryCriteria;
import com.maxim.pos.test.common.BaseTest;

public class RoleTest extends BaseTest {

    @Transactional
    @Test
    public void test1() {
        
        SystemModule defaultSystemModule = systemModuleService.findDefaultSystemModule();
        logger.info("defaultSystemModule: {}", defaultSystemModule.getAlias());
        
        RoleQueryCriteria criteria = new RoleQueryCriteria(defaultSystemModule.getAlias());
        criteria.setMaxResult(10);
        criteria.setAliasKeyword("DUMMY");
        
        Long count = roleService.getRoleCountByCriteria(criteria);
        logger.info("count: {}", count);
        
        Assert.assertTrue((count.intValue() == 0));
        
        String alias = "DUMMY_ROLE";
        Role role = new Role(defaultSystemModule);
        role.setAlias(alias);
        role.setName(alias);
        role.setDescription(alias);
        role.setEnabled(true);
        Auditer.audit(role);
        role = roleService.saveRole(role);
        
        Assert.assertTrue((role != null));
        
        List<Role> roles = roleService.findRoleByCriteria(criteria);
        logger.info("roles size: {}", roles.size());

        Assert.assertTrue((roles.size() == 1));
        
    }

}
