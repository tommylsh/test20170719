package com.maxim.pos.test.common.service;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.service.FtpService;
import org.junit.Assert;
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

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:pos-core-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class FtpServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpServiceTest.class);

    @Autowired
    private FtpService ftpService;

    @Before
    public void setUp() throws Exception {
        Assert.assertNotNull(ftpService);
    }

    @Test
    public void testDownloadFile() throws Exception {
        ftpService.downloadFile(createBranchInfo());
    }

    @Test
    public void testUploadFile() throws Exception {
        Assert.assertTrue(ftpService.uploadFile(createBranchInfo(), "/test", new File("D:/Ftp/b.csv")));
    }

    private BranchInfo createBranchInfo() {
        BranchInfo branchInfo = new BranchInfo();
        branchInfo.setClientHost("192.168.1.250");
        branchInfo.setClientPort(21);
        branchInfo.setClientDB("/test");
        branchInfo.setClientType(ClientType.CSV);
        branchInfo.setUser("maxim-ftp");
        branchInfo.setPassword("P@ssw0rd");
        branchInfo.setEnable(true);
        return branchInfo;
    }

}
