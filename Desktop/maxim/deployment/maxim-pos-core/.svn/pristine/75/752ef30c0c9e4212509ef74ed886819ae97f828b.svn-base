package com.maxim.pos.test.common.service;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.service.NetworkShareService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:pos-core-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class NetworkShareServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkShareServiceTest.class);

    @Autowired
    private NetworkShareService networkShareService;

    @Before
    public void setUp() throws Exception {
        Assert.notNull(networkShareService);
    }

    @Test
    public void testDownloadFile() throws Exception {
        Assert.notEmpty(networkShareService.downloadFile(createBranchInfo()));
    }

    @Test
    public void testUploadFile() throws Exception {
        // Assert.isTrue(networkShareService.uploadFile(createBranchInfo(), "/share", new File("D:\\Ftp\\test.CSV")));
    }

    private BranchInfo createBranchInfo() {
        BranchInfo branchInfo = new BranchInfo();
        branchInfo.setClientHost("192.168.1.250");
        branchInfo.setClientDB("/share");
        branchInfo.setClientType(ClientType.CSV);
        branchInfo.setUser("admin");
        branchInfo.setPassword("Maxim2017");
        branchInfo.setEnable(true);
        return branchInfo;
    }

}
