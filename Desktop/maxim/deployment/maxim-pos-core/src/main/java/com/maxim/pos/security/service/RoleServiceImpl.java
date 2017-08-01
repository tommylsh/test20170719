package com.maxim.pos.security.service;

import java.util.List;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.persistence.RoleDao;
import com.maxim.pos.security.value.RoleQueryCriteria;
import com.maxim.pos.security.value.UpdateRoleDTO;

@Service("roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Value("${defaultSystemModule.alias}")
    private String systemAlias;

    @Override
    @Transactional(readOnly = true)
    public List<Role> findRolesByDefaultSystemAlias() {
        return roleDao.findRolesBySystemAlias(new RoleQueryCriteria(systemAlias));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Role> findRoleByCriteria(CommonCriteria criteria) {
        return roleDao.findRolesByCriteria(criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getRoleCountByCriteria(CommonCriteria criteria) {
        return roleDao.getRoleCountByCriteria(criteria);
    }

    @Override
    public Role findRoleDetailById(Long id) {
        Role role = roleDao.getSingle(Role.class, id);
        Hibernate.initialize(role.getFolders());
        Hibernate.initialize(role.getLinks());
        Hibernate.initialize(role.getPermissions());
        return role;
    }

    @Override
    public Role saveRole(Role role) {
        return (Role) roleDao.save(role);
    }

    @Override
    public void deleteRoleById(Long id) {
        roleDao.delete(roleDao.getSingle(Role.class, id));
    }

    @Override
    public Role saveRoleWithDetails(Role role, UpdateRoleDTO updateRoleDTO) {
        if (updateRoleDTO != null) {
            if (updateRoleDTO.getFolders() != null) {
                role.getFolders().clear();
                role.getFolders().addAll(updateRoleDTO.getFolders());
            }

            if (updateRoleDTO.getLinks() != null) {
                role.getLinks().clear();
                role.getLinks().addAll(updateRoleDTO.getLinks());
            }

            if (updateRoleDTO.getPermissions() != null) {
                role.getPermissions().clear();
                role.getPermissions().addAll(updateRoleDTO.getPermissions());
            }
        }

        return saveRole(role);
    }

    

}
