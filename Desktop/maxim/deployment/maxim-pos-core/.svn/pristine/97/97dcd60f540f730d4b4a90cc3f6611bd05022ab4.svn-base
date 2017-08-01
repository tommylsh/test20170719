package com.maxim.pos.security.service;

import java.util.List;

import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.value.FolderDTO;

public interface FolderService {

    public List<Folder> findFoldersByDefaultSystemAlias();
    
    public List<Folder> findFoldersBySystemAlias(CommonCriteria criteria);
    
    public List<FolderDTO> findFolderDetailsByDefaultSystemAlias(CommonCriteria criteria);
    
    public Folder saveFolder(Folder folder);
    
    public void deleteFolder(Long id);

    public Folder findFolderDetailById(Long id);
    
    
    
}
