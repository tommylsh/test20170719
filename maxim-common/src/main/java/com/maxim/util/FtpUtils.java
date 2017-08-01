package com.maxim.util;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FtpUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtils.class);

    private FTPClient ftpClient = null;

    private FtpUtils() {
        this.ftpClient = new FTPClient();
    }

    public static FtpUtils newInstance() {
        return new FtpUtils();
    }

    public boolean connect(String hostname, int port, String username, String password, String remoteDir) {
        try {
            // Set connection timeout
            ftpClient.setConnectTimeout(2000);
            ftpClient.connect(hostname, port);
            // Set open socket connection timeout
            ftpClient.setSoTimeout(2000);
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return false;
            }
            if (!ftpClient.changeWorkingDirectory(remoteDir)) {
                ftpClient.disconnect();
                return false;
            }
            return true;
        } catch (SocketException e) {
            LOGGER.warn("SocketTimeoutException", e);
            return false;
        } catch (IOException e) {
            LOGGER.warn("IOException", e);
            return false;
        }
    }

    public String[] listFiles() {
        return listFiles("*");
    }

    /**
     * @param pattern e.g. *.csv
     */
    public String[] listFiles(String pattern) {
        try {
            String[] files = ftpClient.listNames(pattern);
            if (files == null) {
                return new String[0];
            }
            return files;
        } catch (IOException e) {
            LOGGER.warn("IOException", e);
            return new String[0];
        }
    }

    public boolean uploadFile(File file) {
        return uploadFile(file, file.getName());
    }

    public boolean uploadFile(File file, String remoteFileName) {
        InputStream in = null;
        try {
            if (!file.isFile()) {
                return false;
            }
            ftpClient.setBufferSize(1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            in = new FileInputStream(file);
            ftpClient.storeFile(remoteFileName, in);
            return true;
        } catch (IOException e) {
            LOGGER.warn("IOException", e);
            return false;
        } finally {
            IOUtil.closeQuietly(in);
            this.close();
        }

    }

    /**
     * @param localPath localPath
     * @param pattern   e.g. *.csv
     * @return File List
     */
    public List<File> downloadFile(String localPath, String pattern) {
        try {
            List<File> files = new ArrayList<File>();
            for (String fileName : listFiles(pattern)) {
                File localFile = new File(localPath);
                if (!localFile.exists() && !localFile.isDirectory()) {
                    localFile.mkdir();
                }
                localFile = new File(localPath + File.separator + fileName);
                OutputStream out = new FileOutputStream(localFile);
                files.add(localFile);
                ftpClient.retrieveFile(fileName, out);
                out.close();
            }
            return files;
        } catch (IOException e) {
            LOGGER.warn("IOException", e);
            return Collections.emptyList();
        } finally {
            this.close();
        }
    }

    public void close() {
        try {
            if (ftpClient != null && ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            LOGGER.warn("IOException", e);
        }
    }

    // public static void main(String[] args) {
    //     FtpUtils ftpUtils = FtpUtils.newInstance();
    //     if (ftpUtils.connect("192.168.1.250", 21, "maxim-ftp", "P@ssw0rd", "/test")) {
    //         // ftpUtils.downloadFile("D:\\Ftp\\", "*.txt");
    //         ftpUtils.uploadFile(new File("D:\\Ftp\\aa.TXT"), "aaaa.TXT");
    //     }
    // }

}
