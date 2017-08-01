package com.maxim.pos.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import org.slf4j.Logger;

public class JcifsUtils {
	
	/**

	 * 把本地磁盘中的文件上传到局域网共享文件下

	 * @param remoteUrl 共享电脑路径 如：smb://lotic:passw0rd@192.168.1.17/share

	 * @param localFilePath 本地路径 如：D:/

	 */
	public static void smbPut(String remoteUrl, String localFilePath, Logger logger) throws Exception {

		File file = new File(localFilePath);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (files.length == 0) {
            	LogUtils.printLog(logger, "{}  directory don't  have file", file);
                return;
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                    	smbPut(remoteUrl+file2.getName(),localFilePath+File.separator+file2.getName(), logger);
                    } else {
						String fileName = file2.getName();
						
						SmbFile file3 = new SmbFile(remoteUrl);
						if(!file3.exists()){
							file3.mkdirs();
						}
						SmbFile remoteFile = new SmbFile(remoteUrl+"/"+fileName);
						
						BufferedInputStream in = new BufferedInputStream(new FileInputStream(file2));
					
						BufferedOutputStream out = new BufferedOutputStream(new SmbFileOutputStream(remoteFile));
					
						byte []buffer = new byte[1024];
					
						while((in.read(buffer)) != -1){
					
							out.write(buffer);
						
							buffer = new byte[1024];
					
						}
						
						out.close();
					
						in.close();
                    }
                }
            }
        }
    }
	
	
	public static void smbGet(String remoteUrl, String localDir, Logger logger) throws Exception {

		SmbFile file = new SmbFile(remoteUrl);
        if (file.exists()) {
            SmbFile[] files = file.listFiles();
            if (files.length == 0) {
            	LogUtils.printLog(logger, "{}  directory don't  have file", file);
                return;
            } else {
                for (SmbFile file2 : files) {
                    if (file2.isDirectory()) {
                    	smbGet(file2.getPath(),localDir+File.separator+file2.getName(), logger);
                    } else {
						String fileName = file2.getName();
						File file3 = new File(localDir);
						if(!file3.exists()){
							file3.mkdirs();
						}
						File localFile = new File(localDir+File.separator+fileName);
						
            			BufferedInputStream in = new BufferedInputStream(new SmbFileInputStream(file2));
            		
            			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(localFile));
            		
            			byte []buffer = new byte[1024];
            		
            			while((in.read(buffer)) != -1){
            				out.write(buffer);
            				buffer = new byte[1024];
            			}
        				out.close();
        				in.close();
                    }
                }
            }
        }
    }
	
	public static void main(String[] args) throws Exception {

//	smbPut("smb://lotic:passw0rd@192.168.1.17/share/", "D:/Share/o_o", LogUtils.LOGGER);
	smbPut("smb://ParkoLam:passw0rd@192.168.1.52/share/", "D:/Share/o_o", LogUtils.LOGGER);

//	smbGet("smb://lotic:passw0rd@192.168.1.17/share/", "D:/Share/o_o", LogUtils.LOGGER);

	System.out.println(".....");

	}
}
