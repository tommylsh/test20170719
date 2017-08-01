package com.maxim.pos.security.service;

import java.util.List;

import com.maxim.pos.security.entity.SystemModule;

public interface SystemModuleService {
    
    public SystemModule findDefaultSystemModule();
    
    public List<SystemModule> findAllSystemModules();
    
    public SystemModule findSystemModuleById(Long id);
    
}
