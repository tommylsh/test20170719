package com.maxim.pos.common.web.faces.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.bean.SessionScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.pos.security.service.FolderService;
import com.maxim.pos.security.service.SystemModuleService;
import com.maxim.pos.security.value.FolderDTO;
import com.maxim.pos.security.value.FolderQueryCriteria;
import com.maxim.user.Principal;
import com.maxim.web.faces.utils.FacesUtils;

@Controller("commonController")
@SessionScoped
public class CommonController implements Serializable {

    private static final long serialVersionUID = -7147990764739642014L;
    
    public static final int MAX_RESULT = 100;

    @Value("${defaultSystemModule.alias}")
    private String systemAlias;

    @Autowired
    private FolderService folderService;

    @Autowired
    private SystemModuleService systemModuleService;

    private SystemModule defaultSystemModule;

    @SuppressWarnings("unchecked")
    public List<FolderDTO> getUserFolders() {
        String key = systemAlias + "_folders";
        List<FolderDTO> folders = (List<FolderDTO>) FacesUtils.getSessionScope(key);

        if (folders == null) {
            FolderQueryCriteria criteria = new FolderQueryCriteria();
            criteria.setNotAdminUser(!UserDetailsService.getUser().isAdmin());
            criteria.setUserId(UserDetailsService.getUserId());
            folders = folderService.findFolderDetailsByDefaultSystemAlias(criteria);

            FacesUtils.putSessionScope(key, folders);
        }

        return folders;
    }

    public SystemModule getDefaultSystemModule() {
        if (defaultSystemModule == null) {
            defaultSystemModule = systemModuleService.findDefaultSystemModule();
        }

        return defaultSystemModule;
    }

    public String getSystemAlias() {
        return systemAlias;
    }

    public void setSystemAlias(String systemAlias) {
        this.systemAlias = systemAlias;
    }
    
    public Principal getLoginUser() {
        return UserDetailsService.getUser();
    }
    
    public static boolean isAnyUpdated(Collection<? extends AbstractEntity> originals, Collection<? extends AbstractEntity> targets) {
        if (originals.size() != targets.size()) {
            return true;
        }
        
        ArrayList<? extends AbstractEntity> copyAll = new ArrayList<AbstractEntity>(originals);
        copyAll.removeAll(targets);
        
        return (copyAll.size() > 0);
    }
    
}
