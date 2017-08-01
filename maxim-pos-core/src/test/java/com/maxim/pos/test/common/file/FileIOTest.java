package com.maxim.pos.test.common.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileIOTest {

    private static Logger logger = LoggerFactory.getLogger(FtpTest.class);

    @Test
    public void readByReaderTest() throws Exception {
        URL resource = getClass().getResource("/file.txt");

        Assert.assertTrue((resource != null));

        String localFilePath = resource.getFile();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(localFilePath), "UTF-8"));
            List<String> readLines = IOUtils.readLines(reader);

            logger.info("Content: \r\n");

            for (String line : readLines) {
                logger.info(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    @Test
    public void readByStreamTest() throws Exception {
        URL resource = getClass().getResource("/file.txt");

        Assert.assertTrue((resource != null));

        String localFilePath = resource.getFile();

        FileInputStream stream = null;
        try {
            stream = new FileInputStream(localFilePath);
            List<String> readLines = IOUtils.readLines(stream);

            logger.info("Content: \r\n");

            for (String line : readLines) {
                logger.info(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(stream);
        }

    }

    @Test
    public void writeStringTest() throws Exception {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("E:/Mike/Workspace/maxim-pos-core/src/test/java/file2.txt");
            IOUtils.write("美心集团Maxim POS Polling 1\r\n", output);
            IOUtils.write("美心集团Maxim POS Polling 2\r\n", output);
            IOUtils.write("美心集团Maxim POS Polling 3\r\n", output);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }
    
    @Test
    public void writeBinaryTest() throws Exception {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream("E:/Mike/Workspace/maxim-pos-core/src/test/java/file3.txt");
            IOUtils.write("美心集团Maxim POS Polling 1\r\n".getBytes("UTF-8"), output);
            IOUtils.write("美心集团Maxim POS Polling 2\r\n".getBytes("UTF-8"), output);
            IOUtils.write("美心集团Maxim POS Polling 3\r\n".getBytes("UTF-8"), output);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

}
