package com.maxim.pos.test.common;

import java.util.Date;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.maxim.i18n.MessageSource;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.security.persistence.UserDao;
import com.maxim.pos.security.service.FolderService;
import com.maxim.pos.security.service.LinkService;
import com.maxim.pos.security.service.PermissionService;
import com.maxim.pos.security.service.RoleService;
import com.maxim.pos.security.service.SystemModuleService;
import com.maxim.pos.security.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:pos-core-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class BaseTest {

    public Logger logger;

    @Autowired
    protected ApplicationSettingService applicationSettingService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected UserDao userDao;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected FolderService folderService;

    @Autowired
    protected LinkService linkService;

    @Autowired
    protected SystemModuleService systemModuleService;

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected MessageSource messageSource;

    @Value("${defaultSystemModule.alias}")
    protected String systemAlias;

    @Before
    public void setup() {
        logger = LoggerFactory.getLogger(getClass());
        logger.info("setup at : {}", new Date());
    }

}
