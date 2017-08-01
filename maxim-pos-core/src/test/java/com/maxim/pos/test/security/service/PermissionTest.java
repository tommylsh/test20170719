package com.maxim.pos.test.security.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.security.entity.Permission;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.pos.security.value.PermissionQueryCriteria;
import com.maxim.pos.test.common.BaseTest;

public class PermissionTest extends BaseTest {

    @Transactional
    @Test
    public void test1() {
        SystemModule defaultSystemModule = systemModuleService.findDefaultSystemModule();
        logger.info("defaultSystemModule: {}", defaultSystemModule.getAlias());

        String alias = "DUMMY_PERMISSION";
        Permission permission = permissionService.findByPermissionAlias(alias);
        Assert.assertTrue((permission == null));
        
        permission = new Permission(defaultSystemModule);
        permission.setEnabled(true);
        permission.setAlias(alias);
        permission.setName(alias);
        permission.setDescription(alias);
        Auditer.audit(permission);
        permission = permissionService.savePermission(permission);
        
        Assert.assertTrue((permission != null));

        PermissionQueryCriteria criteria = new PermissionQueryCriteria();
        criteria.setSystemAlias(systemAlias);
        criteria.setAliasKeyword("DUMMY_");
        criteria.setMaxResult(10);
        List<Permission> permissions = permissionService.findPermissionByCriteria(criteria);
        logger.info("permissions size: {}", permissions.size());
        
        Assert.assertTrue((permissions.size() == 1));
    }

}
