package com.maxim.pos.security.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.security.entity.SystemModule;
import com.maxim.pos.security.persistence.SystemModuleDao;
import com.maxim.pos.security.value.SystemModuleQueryCriteria;

@Service("systemModuleService")
@Transactional
public class SystemModuleServiceImpl implements SystemModuleService {

    @Autowired
    private SystemModuleDao systemModuleDao;

    @Value("${defaultSystemModule.alias}")
    private String systemAlias;

    @Override
    @Transactional(readOnly = true)
    public SystemModule findDefaultSystemModule() {
        return systemModuleDao.findByPermissionAlias(new SystemModuleQueryCriteria(systemAlias));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemModule> findAllSystemModules() {
        return systemModuleDao.getAllList(SystemModule.class);
    }

    @Override
    @Transactional(readOnly = true)
    public SystemModule findSystemModuleById(Long id) {
        return systemModuleDao.getSingle(SystemModule.class, id);
    }

}
