package com.maxim.pos.test.security.service;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.pos.security.enumeration.ResourceType;
import com.maxim.pos.security.value.FolderDTO;
import com.maxim.pos.security.value.FolderQueryCriteria;
import com.maxim.pos.test.common.BaseTest;

public class FolderServiceTest extends BaseTest {

    @Test
    public void test1() {
        FolderQueryCriteria criteria = new FolderQueryCriteria();
        criteria.setSystemAlias(systemAlias);
        criteria.setNotAdminUser(false);
        criteria.setUserId("admin");
        List<FolderDTO> folders = folderService.findFolderDetailsByDefaultSystemAlias(criteria);
        logger.info("folders.size: {}", folders.size());

        for (FolderDTO folderDTO : folders) {
            logger.info("folder: {}, links: {}", folderDTO.getName(), folderDTO.getLinkDtos().size());
        }
    }

    @Test
    public void test2() {
        FolderQueryCriteria criteria = new FolderQueryCriteria();
        criteria.setSystemAlias(systemAlias);
        criteria.setNotAdminUser(true);
        criteria.setUserId("test1");
        List<FolderDTO> folders = folderService.findFolderDetailsByDefaultSystemAlias(criteria);
        logger.info("folders.size: {}", folders.size());

        for (FolderDTO folderDTO : folders) {
            logger.info("folder: {}, links: {}", folderDTO.getName(), folderDTO.getLinkDtos().size());
        }
    }

    @Transactional
    @Test
    public void test3() {
        String name = "TestFolder";

        SystemModule defaultSystemModule = systemModuleService.findDefaultSystemModule();
        Folder folder = new Folder(defaultSystemModule);
        folder.setName(name);
        folder.setEnabled(true);
        folder.setType(ResourceType.FOLDER);

        Auditer.audit(folder);

        folder = folderService.saveFolder(folder);

        Assert.assertNotNull(folder);
        Assert.assertTrue(name.equals(folder.getName()));
        Assert.assertTrue(folder.isEnabled());
    }

    @Transactional
    @Test
    public void test4() {
        SystemModule defaultSystemModule = systemModuleService.findDefaultSystemModule();

        Folder folder = new Folder(defaultSystemModule);
        folder.setName("TestFolder");
        folder.setEnabled(true);
        folder.setType(ResourceType.FOLDER);

        Auditer.audit(folder);

        folder = folderService.saveFolder(folder);

        String linkName = "TestLink";

        Link link = new Link(folder);
        link.setName(linkName);
        link.setEnabled(true);
        link.setType(ResourceType.FOLDER);

        Auditer.audit(link);

        link = linkService.saveLink(link);

        Assert.assertNotNull(link);
        Assert.assertTrue(linkName.equals(link.getName()));
        Assert.assertTrue(link.isEnabled());
    }

}
