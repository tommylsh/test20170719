package com.maxim.pos.test.common.file;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxim.util.FtpUtil;

public class FtpTest {

    private static Logger logger = LoggerFactory.getLogger(FtpTest.class);

    @Test
    public void uploadTest() throws Exception {
        String[] paths = { "/test/" };
        String address = "127.0.0.1";
        int port = 21;
        String username = "maximftp";
        String password = "P@ssw0rd";
        FtpUtil.connect(paths, address, port, username, password);
        URL resource = getClass().getResource("/test.txt");

        Assert.assertTrue((resource != null));

        String localFilePath = resource.getFile();

        logger.info("localFilePath: {}", localFilePath);

        FtpUtil.upload(new File(localFilePath));
    }

    @Test
    public void downloadTest() throws Exception {

        uploadTest();

        boolean result = FtpUtil.downloadFile("*.txt", "C://");

        Assert.assertTrue(result);
    }

    @Test
    public void moveTest() throws Exception {
        uploadTest();
        
        boolean result = FtpUtil.moveFile("/test/", "/test1/", "test.txt");

        Assert.assertTrue(result);
    }

    @Test
    public void removeTest() throws Exception {
        uploadTest();
        
        boolean result = FtpUtil.removeFile("/test/test.txt");
        
        Assert.assertTrue(result);
    }
    
}
