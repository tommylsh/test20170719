package com.maxim.pos.security.service;

import java.util.List;

import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Permission;

public interface PermissionService {

    public Permission findByPermissionAlias(String alias);

    public List<Permission> findPermissionByCriteria(CommonCriteria criteria);
    
    public List<Permission> findAllPermissionsByUserId(CommonCriteria criteria);

    public Long getPermissionCountByCriteria(CommonCriteria criteria);
    
    public Permission savePermission(Permission permission);
    
    public void deletePermissionById(Long permissionId);

}
