package com.maxim.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class SftpUtil {

    public static final Logger log = LoggerFactory.getLogger(SftpUtil.class);
    
    private static ChannelSftp sftp;  
    
    private static Session session;
    
    
    public static void connectSftp(String host, int port,String username,String password){
        try {  
            JSch jsch = new JSch();  
             
            log.info("sftp connect by host:%s username:%s",host,username);  
  
            session = jsch.getSession(username, host, port);  
            log.info("Session is build");  
            if (password != null) {  
                session.setPassword(password);  
            }  
            Properties config = new Properties();  
            config.put("StrictHostKeyChecking", "no");  
              
            session.setConfig(config);  
            session.connect();  
            log.info("Session is connected");  
              
            Channel channel = session.openChannel("sftp");  
            channel.connect();  
            log.info("channel is connected");  
  
            sftp = (ChannelSftp) channel;  
            log.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", host, port));  
        } catch (JSchException e) {  
            log.error("Cannot connect to specified sftp server : {}:{} \n Exception message is: {}", new Object[]{host, port, e.getMessage()});  
            throw new RuntimeException(e.getMessage(),e);  
        }  
    } 
    
    public static void close(){  
        if (sftp != null) {  
            if (sftp.isConnected()) {  
                sftp.disconnect();  
                log.info("sftp is closed already");  
            }  
        }  
        if (session != null) {  
            if (session.isConnected()) {  
                session.disconnect();  
                log.info("sshSession is closed already");  
            }  
        }  
    }  
    
    public static void upload(String ftpPath, String uploadFile) throws FileNotFoundException, SftpException{  
        mkdirs(uploadFile);
        File file = new File(uploadFile); 
        try {  
            sftp.cd(ftpPath);  
            sftp.put(new FileInputStream(file), file.getName());  
            log.info("file:{} is upload successful" , file.getName());  
        } catch (SftpException e) {  
            log.warn("directory is not exist");  
            sftp.mkdir(ftpPath);  
            sftp.cd(ftpPath);  
        }  
    } 
    
    public static boolean download(String ftpPath, String downloadFile, String saveFile) throws SftpException, FileNotFoundException{ 
        boolean flag = false;
        try {
            sftp.cd(ftpPath);  
            mkdirs(saveFile);
            FileOutputStream ops = new FileOutputStream(new File(saveFile));
            sftp.get(downloadFile, ops);  
            log.info("file:{} is download successful" , downloadFile);
            flag = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            flag = false;
        } catch (SftpException e) {
            e.printStackTrace();
            flag = false;
        }
        return flag;
    }  
    

    @SuppressWarnings("unchecked")
    public static boolean downloadFiles(String ftpPath,String suffix, String localpath){
        boolean flag = false;
        try {
          List<LsEntry> ftpFiles = (List<LsEntry>) listFiles(ftpPath);
          for(LsEntry file : ftpFiles){
              
              if(wildcardMatch(suffix,file.getFilename())){
                  File f = new File(localpath);
                  if (!f.exists()  && !f.isDirectory()) {                   
                      f.mkdir();    
                  }
                  download(ftpPath, file.getFilename(), localpath + "/" + file.getFilename());
              }
              //removeFile(file.getName());
          }
          flag = true;
        } catch (Exception e) {
          e.printStackTrace();
          FtpUtil.closeFtp();  
        }
        return flag;
      }
    
   /* public boolean batchDownLoadFile(String remotPath, String localPath, String fileFormat, boolean del) {
        try {
            Vector v = (Vector) listFiles(remotPath);
            if (v.size() > 0) {

                Iterator it = v.iterator();
                while (it.hasNext()) {
                    LsEntry entry = (LsEntry) it.next();
                    String filename = entry.getFilename();
                    SftpATTRS attrs = entry.getAttrs();
                    if (!attrs.isDir()) {
                        if (fileFormat != null && !"".equals(fileFormat.trim())) {
                            if (filename.startsWith(fileFormat)) {
                                if (this.download(remotPath, filename, localPath, filename) && del) {
                                    delete(remotPath, filename);
                                }
                            }
                        } else {
                            if (this.download(remotPath, filename, localPath, filename) && del) {
                                delete(remotPath, filename);
                            }
                        }
                    }
                }
            }
        } catch (SftpException e) {
            e.printStackTrace();
        } finally {
            close();
        }
        return false;
    }*/
    
    
    public static void delete(String ftpPath, String deleteFile) throws SftpException{  
        sftp.cd(ftpPath);  
        sftp.rm(deleteFile); 
        log.info("file is delete successful" +deleteFile);  
    } 
    
    public static List<?> listFiles(String ftpPath) throws SftpException {  
        return sftp.ls(ftpPath);  
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
    
   /* @SuppressWarnings("static-access")
    public static void createDir(String ftpPath) throws Exception {
        try{
            sftp.cd(ftpPath);
        }catch(SftpException sException){
            if(sftp.SSH_FX_NO_SUCH_FILE == sException.id){
                sftp.mkdir(ftpPath);
                sftp.cd(ftpPath);
            }
    }*/
       
     
  
    public static boolean copyDirectory(String from, String to, String fileName,String saveFile) {
        try {
            download(from, fileName, saveFile + File.separator + fileName);
            delete(from, fileName);
            upload(to, saveFile + File.separator + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    
    
    public static boolean createDir(String ftpPath) {
        try {
            if (isDirExist(ftpPath)) {
                sftp.cd(ftpPath);
                return true;
            }
            String pathArry[] = ftpPath.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString())) {
                    sftp.cd(filePath.toString());
                } else {
                    sftp.mkdir(filePath.toString());
                    sftp.cd(filePath.toString());
                }

            }
            sftp.cd(ftpPath);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean isDirExist(String directory) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such file")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }
    
    public static void mkdirs(String path) {
        File f = new File(path);

        String fs = f.getParent();

        f = new File(fs);

        if (!f.exists()) {
            f.mkdirs();
        }
    }
    
}
