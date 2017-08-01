package com.maxim.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class FtpUtil {

    public static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

    private static FTPClient ftp;

    public static boolean connect(String path, String addr, int port, String username, String password) throws Exception {
        ftp = new FTPClient();
        int reply;
        ftp.connect(addr, port);

        // ftp.setControlEncoding("utf-8");
        // FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
        // conf.setServerLanguageCode("zh");

        ftp.login(username, password);
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return false;
        }
        return ftp.changeWorkingDirectory(path);
    }

    public static boolean connect(String[] paths, String addr, int port, String username, String password)
            throws Exception {
        boolean result = false;
        ftp = new FTPClient();
        int reply;
        ftp.connect(addr, port);
        ftp.login(username, password);
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return result;
        }
        if (paths != null && paths.length > 0) {
            for (int i = 0; i < paths.length; i++) {
                ftp.changeWorkingDirectory(paths[i]);
            }
        }
        result = true;
        return result;
    }

    public static void closeFtp() {
        if (ftp != null && ftp.isConnected()) {
            try {
                ftp.logout();
                ftp.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean removeFile(String filePath) {
        boolean flag = false;
        if (ftp != null) {
            try {
                flag = ftp.deleteFile(filePath);
            } catch (IOException e) {
                flag = false;
            } finally {
                FtpUtil.closeFtp();
            }
        }
        return flag;
    }

    public static void upload(File file) throws IOException {
        try {
            if (file.isDirectory()) {
                ftp.makeDirectory(file.getName());
                ftp.changeWorkingDirectory(file.getName());
                String[] files = file.list();
                for (int i = 0; i < files.length; i++) {
                    File file1 = new File(file.getPath() + "\\" + files[i]);
                    if (file1.isDirectory()) {
                        upload(file1);
                        ftp.changeToParentDirectory();
                    } else {
                        File file2 = new File(file.getPath() + "\\" + files[i]);
                        FileInputStream input = new FileInputStream(file2);
                        ftp.storeFile(file2.getName(), input);
                        input.close();
                    }
                }
            } else {
                File file2 = new File(file.getPath());
                FileInputStream input = new FileInputStream(file2);
                ftp.storeFile(file2.getName(), input);
                input.close();
            }
        } finally {
        	FtpUtil.closeFtp();
        }
    }

    /**
     * @param suffix
     * @param localpath
     * @return
     */
    public static boolean downloadFile(String suffix, String localpath) {
        boolean flag = false;
        try {
            FTPFile[] ftpFiles = ftp.listFiles();
            for (FTPFile file : ftpFiles) {

                if (wildcardMatch(suffix, file.getName())) {
                    File f = new File(localpath);
                    if (!f.exists() && !f.isDirectory()) {
                        f.mkdir();
                    }
                    File localFile = new File(localpath + "/" + file.getName());

                    OutputStream os = new FileOutputStream(localFile);
                    ftp.retrieveFile(file.getName(), os);
                    os.close();
                }
                // removeFile(file.getName());
            }
            flag = true;
        } catch (Exception e) {
            flag = false;
        } finally {
            FtpUtil.closeFtp();
        }
        return flag;
    }

    public static boolean downloadFile1(String suffix, String localpath) {
        boolean flag = false;
        try {
            FTPFile[] ftpFiles = ftp.listFiles();
            for (FTPFile file : ftpFiles) {

                if (wildcardMatch(suffix, file.getName())) {
                    File f = new File(localpath);
                    if (!f.exists() && !f.isDirectory()) {
                        f.mkdir();
                    }

                    File localFile = new File(localpath + "/" + file.getName());

                    OutputStream os = new FileOutputStream(localFile);
                    ftp.retrieveFile(file.getName(), os);
                    os.close();
                }
                // removeFile(file.getName());
            }
            flag = true;
        } catch (IOException e) {
            flag = false;
        } finally {
            FtpUtil.closeFtp();
        }
        return flag;
    }

    /**
     * @param pattern   e.g. (.*).csv,(.*).CSV OR (.*).(?i)%s
     * @param localpath
     * @return
     */
    public static List<File> downloadFile2(String pattern, String localpath) {
        try {
            List<Pattern> patterns = PatternUtil.buildPatterns(pattern);
            List<File> files = new ArrayList<File>();
            FTPFile[] ftpFiles = ftp.listFiles();
            for (FTPFile file : ftpFiles) {
                if (PatternUtil.match(patterns, file.getName())) {
                    File f = new File(localpath);
                    if (!f.exists() && !f.isDirectory()) {
                        f.mkdir();
                    }
                    File localFile = new File(localpath + "/" + file.getName());
                    files.add(localFile);
                    OutputStream os = new FileOutputStream(localFile);
                    ftp.retrieveFile(file.getName(), os);
                    os.close();
                }
                // removeFile(file.getName());
            }
            return files;
        } catch (IOException e) {
            return Collections.emptyList();
        } finally {
            FtpUtil.closeFtp();
        }
    }

    public static void downloadFile(FTPFile ftpFile, String relativeLocalPath, String relativeRemotePath) {
        if (ftpFile.isFile()) {
            if (ftpFile.getName().indexOf("?") == -1) {
                OutputStream outputStream = null;
                try {
                    File locaFile = new File(relativeLocalPath + ftpFile.getName());
                    if (locaFile.exists()) {
                        return;
                    } else {
                        outputStream = new FileOutputStream(relativeLocalPath + ftpFile.getName());
                        ftp.retrieveFile(ftpFile.getName(), outputStream);
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (Exception e) {

                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        } else {
            String newlocalRelatePath = relativeLocalPath + ftpFile.getName();
            String newRemote = new String(relativeRemotePath + ftpFile.getName().toString());
            File fl = new File(newlocalRelatePath);
            if (!fl.exists()) {
                fl.mkdirs();
            }
            try {
                newlocalRelatePath = newlocalRelatePath + '/';
                newRemote = newRemote + "/";
                String currentWorkDir = ftpFile.getName().toString();
                boolean changedir = ftp.changeWorkingDirectory(currentWorkDir);
                if (changedir) {
                    FTPFile[] files = null;
                    files = ftp.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        downloadFile(files[i], newlocalRelatePath, newRemote);
                    }
                }
                if (changedir) {
                    ftp.changeToParentDirectory();
                }
            } catch (Exception e) {

            }
        }
    }

    public static boolean wildcardMatch(String pattern, String str) {
        int patternLength = pattern.length();
        int strLength = str.length();
        int strIndex = 0;
        char ch;
        for (int patternIndex = 0; patternIndex < patternLength; patternIndex++) {
            ch = pattern.charAt(patternIndex);
            if (ch == '*') {
                while (strIndex < strLength) {
                    if (wildcardMatch(pattern.substring(patternIndex + 1), str.substring(strIndex))) {
                        return true;
                    }
                    strIndex++;
                }
            } else if (ch == '?') {
                strIndex++;
                if (strIndex > strLength) {
                    return false;
                }
            } else {
                if ((strIndex >= strLength) || (ch != str.charAt(strIndex))) {
                    return false;
                }
                strIndex++;
            }
        }
        return (strIndex == strLength);
    }

    public static boolean copyDirectory(String from, String to, String fileName) {
        boolean copyFalg = false;
        FTPFile[] filelist;
        try {
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            if (!System.getProperty("os.name").contains("Windows")) {
                ftp.enterLocalPassiveMode();
            }

            filelist = ftp.listFiles(from);
            int length = filelist.length;
            FTPFile ftpFile = null;
            String category = null;
            InputStream inputStream = null;
            for (int i = 0; i < length; i++) {
                ftpFile = filelist[i];
                if (!fileName.equals(ftpFile.getName())) {
                    continue;
                }
                category = ftpFile.getName();
                if (ftpFile.isFile()) {
                    inputStream = ftp.retrieveFileStream(from + category);
                    if (!ftp.completePendingCommand()) {
                        copyFalg = false;
                        return copyFalg;
                    }
                    if (inputStream != null) {
                        copyFalg = ftp.storeFile(to + category, inputStream);
                        inputStream.close();
                        if (!copyFalg) {
                            return copyFalg;
                        }
                    }

                } else if (ftpFile.isDirectory()) {
                    copyFalg = ftp.makeDirectory(to + category);
                    copyDirectory(from + category, to + category, fileName);
                }
            }
        } catch (IOException e) {
            logger.error("FtpClientUtil.copyDirectory failed. caused by " + e.getMessage(), e);
            copyFalg = false;
        }
        return copyFalg;

    }

    public static void createDir(String ftpPath) throws Exception {
        if (ftpPath != null) {
            ftp.makeDirectory(ftpPath);
        }

    }

    public static boolean moveFile(String fromPath, String toPath, String fromFileName) {
        boolean flag = false;
        try {
            flag = FtpUtil.copyDirectory(fromPath, toPath, fromFileName);
            if (flag) {
                removeFile(fromPath + fromFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FtpUtil.closeFtp();
        }
        return flag;
    }

    public static void main(String[] args) {
        try {
            String[] a = {"/test/error/", "/test/"};
            FtpUtil.connect(a, "192.168.1.253", 21, "ftp", "pass1234");

            FtpUtil.createDir("/test/error/");
            // FtpUtil.upload(new File("/test/abc.txt"));
            // FtpUtil.removeFile("record_20160804182340.bpg");
            // FtpUtil.downloadFile1("*.txt", "D://test//");
            // FtpUtil.copyDirectory("/source/", "/backup/");

            // moveFtpFile("/source/","/backup/","test2.bpg");
            FtpUtil.closeFtp();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            FtpUtil.closeFtp();
        }

    }

}
