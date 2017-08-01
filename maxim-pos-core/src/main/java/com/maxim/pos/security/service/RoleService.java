package com.maxim.pos.security.service;

import java.util.List;

import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.value.UpdateRoleDTO;

public interface RoleService {

    public List<Role> findRolesByDefaultSystemAlias();
    
    public List<Role> findRoleByCriteria(CommonCriteria criteria);

    public Long getRoleCountByCriteria(CommonCriteria criteria);
    
    public Role findRoleDetailById(Long id);
    
    public Role saveRole(Role role);
    
    public Role saveRoleWithDetails(Role role, UpdateRoleDTO updateRoleDTO);
    
    public void deleteRoleById(Long id);
    
}
