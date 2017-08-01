package com.maxim.pos.security.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.persistence.FolderDao;
import com.maxim.pos.security.persistence.LinkDao;
import com.maxim.pos.security.value.FolderDTO;
import com.maxim.pos.security.value.FolderQueryCriteria;
import com.maxim.pos.security.value.LinkDTO;
import com.maxim.util.BeanMapper;

@Service("folderService")
@Transactional
public class FolderServiceImpl implements FolderService {

    @Autowired
    private FolderDao folderDao;

    @Autowired
    private LinkDao linkDao;

    @Autowired
    private BeanMapper beanMapper;

    @Value("${defaultSystemModule.alias}")
    private String systemAlias;

    @Override
    @Transactional(readOnly = true)
    public List<Folder> findFoldersByDefaultSystemAlias() {
        List<Folder> folders = folderDao.findFoldersBySystemAlias(new FolderQueryCriteria(systemAlias));
        return folders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FolderDTO> findFolderDetailsByDefaultSystemAlias(CommonCriteria criteria) {
        ((FolderQueryCriteria) criteria).setSystemAlias(systemAlias);
        List<Link> links = linkDao.findLinksBySystemAlias(criteria);

        List<FolderDTO> folders = new ArrayList<FolderDTO>();
        for (Link link : links) {
            LinkDTO linkDTO = beanMapper.map(link, LinkDTO.class);

            FolderDTO folderDTO = beanMapper.map(link.getFolder(), FolderDTO.class);
            boolean found = false;
            for (FolderDTO _folderDTO : folders) {
                if (_folderDTO.equals(folderDTO)) {
                    _folderDTO.getLinkDtos().add(linkDTO);
                    found = true;
                    break;
                }
            }

            if (!found) {
                folderDTO.getLinkDtos().add(linkDTO);
                folders.add(folderDTO);
            }

        }

        return folders;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Folder> findFoldersBySystemAlias(CommonCriteria criteria) {
        return folderDao.findFoldersBySystemAlias(criteria);
    }

    @Override
    public Folder saveFolder(Folder folder) {
        return (Folder) folderDao.save(folder);
    }

    @Override
    public void deleteFolder(Long id) {
        folderDao.delete(folderDao.getSingle(Folder.class, id));        
    }

    @Override
    public Folder findFolderDetailById(Long id) {
        return folderDao.findFolderDetailById(id);
    }

}
