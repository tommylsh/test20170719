package com.maxim.pos.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Permission;
import com.maxim.pos.security.persistence.PermissionDao;

@Service("permissionService")
@Transactional
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionDao permissionDao;

    @Override
    @Transactional(readOnly = true)
    public Permission findByPermissionAlias(String alias) {
        return permissionDao.findByPermissionAlias(alias);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findPermissionByCriteria(CommonCriteria criteria) {
        return permissionDao.findPermissionByCriteria(criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getPermissionCountByCriteria(CommonCriteria criteria) {
        return permissionDao.getPermissionCountByCriteria(criteria);
    }

    @Override
    public Permission savePermission(Permission permission) {
        return (Permission) permissionDao.save(permission);
    }

    @Override
    public void deletePermissionById(Long permissionId) {
        permissionDao.delete(permissionDao.getSingle(Permission.class, permissionId));
    }

    @Override
    public List<Permission> findAllPermissionsByUserId(CommonCriteria criteria) {
        return permissionDao.findAllPermissionsByUserId(criteria);
    }

}
