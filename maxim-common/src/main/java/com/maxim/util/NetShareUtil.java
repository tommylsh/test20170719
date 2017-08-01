package com.maxim.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

public abstract class NetShareUtil {

    public static final String SLASHES_SEPARATOR = "/";

    private static void uploadFile(SmbFile smbFile, File localFile) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(new FileInputStream(localFile));
            out = new BufferedOutputStream(new SmbFileOutputStream(smbFile));
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
        } finally {
            IOUtil.closeQuietly(in, out);
        }
    }

    private static File downloadFile(SmbFile smbFile, String localPath) throws IOException {
        OutputStream out = null;
        InputStream in = null;
        try {
            File localFile = new File(localPath);
            if (!localFile.exists() && !localFile.isDirectory()) {
                localFile.mkdirs();
            }
            localFile = new File(localPath + File.separator + smbFile.getName());
            in = new BufferedInputStream(new SmbFileInputStream(smbFile));
            out = new BufferedOutputStream(new FileOutputStream(localFile));
            byte[] buf = new byte[1024];
            int len = -1;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            return localFile;
        } finally {
            IOUtil.closeQuietly(out, in);
        }
    }

    /**
     * @param remoteUrl e.g. 'smb://ip/shareFolder/'
     * @param localFile e.g. 'D:\\Ftp\\'
     * @return true|false
     */
    public static boolean uploadFile(String remoteUrl, File localFile) {
        return uploadFile(remoteUrl, localFile, null);
    }

    public static boolean uploadFile(String remoteUrl, File localFile, String fileName) {
        Assert.hasText(remoteUrl);
        Assert.notNull(localFile);
        if (!remoteUrl.endsWith(SLASHES_SEPARATOR)) {
            remoteUrl = remoteUrl + SLASHES_SEPARATOR;
        }
        try {
            if (fileName == null) {
                fileName = localFile.getName();
            }
            uploadFile(new SmbFile(remoteUrl + File.separator + fileName), localFile);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean uploadFile(String hostname, String remotePath, File localFile) {
        return uploadFile(hostname, remotePath, localFile, null);
    }

    public static boolean uploadFile(String hostname, String remotePath, File localFile, String fileName) {
        return uploadFile("smb://" + hostname + SLASHES_SEPARATOR + remotePath, localFile, fileName);
    }

    public static boolean uploadFile(String username, String password, String hostname, String remotePath, File localFile) {
        return uploadFile(username, password, hostname, remotePath, localFile, null);
    }

    public static boolean uploadFile(String username, String password, String hostname, String remotePath, File localFile, String fileName) {
        return uploadFile("smb://" + username + ":" + password + "@" + hostname + SLASHES_SEPARATOR + remotePath, localFile, fileName);
    }

    /**
     * @param remoteUrl e.g. 'smb://ip/shareFolder/', 'smb://username:password@ip/shareFolder/test.csv'
     * @param localPath e.g. 'D:\\Ftp\\'
     * @param pattern   e.g. '*.csv'
     * @return File List
     */
    public static List<File> downloadFile(String remoteUrl, String localPath, String pattern) {
        Assert.hasText(remoteUrl);
        Assert.hasText(localPath);
        Assert.hasText(pattern);
        if (!remoteUrl.endsWith(SLASHES_SEPARATOR)) {
            remoteUrl = remoteUrl + SLASHES_SEPARATOR;
        }
        try {
            List<File> files = new ArrayList<File>();
            SmbFile smbFile = new SmbFile(remoteUrl);
            // if (smbFile.exists()) {
            //     throw new RuntimeException("Can not access target resources, please check whether the parameters are correct.");
            // }
            if (smbFile.isDirectory()) {
                for (SmbFile remoteFile : smbFile.listFiles(pattern)) {
                    files.add(downloadFile(remoteFile, localPath));
                }
            } else {
                files.add(downloadFile(smbFile, localPath));
            }
            return files;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public static List<File> downloadFile(String hostname, String remotePath, String localPath, String pattern) {
        return downloadFile("smb://" + hostname + SLASHES_SEPARATOR + remotePath, localPath, pattern);
    }

    public static List<File> downloadFile(String username, String password, String hostname, String remotePath, String localPath, String pattern) {
        return downloadFile("smb://" + username + ":" + password + "@" + hostname + SLASHES_SEPARATOR + remotePath, localPath, pattern);
    }

    // public static void main(String[] args) {
    //     Assert.notEmpty(downloadFile("smb://admin:Maxim2017@192.168.1.250/share/", "D:\\Ftp\\", "*.csv"));
    //     Assert.isTrue(uploadFile("smb://admin:Maxim2017@192.168.1.250/share/", new File("D:\\Ftp\\test.CSV"), "cc.csv"));
    // }
    
    
    protected static Map<String, ShareFolderAccessThread> shareFolderAccessMap = new HashMap<String, ShareFolderAccessThread>();

    protected static String[] getFileNameList(String dirName, String user, String password)
    {
		ShareFolderAccessThread extraction = null ;
    	synchronized (shareFolderAccessMap)
    	{
    		extraction = shareFolderAccessMap.get(dirName);
    		if (extraction == null)
    		{
    			extraction = new ShareFolderAccessThread(dirName, user, password);
    			shareFolderAccessMap.put(dirName, extraction);
    			extraction.start();
    		}
    		else
    		{
        		if (extraction.state.equals("E"))
        		{
        			Timestamp timestamp = DateUtil.getCurrentTimestamp();
        			if (timestamp.getTime() - extraction.timestamp.getTime() > 5000)
        			{
	         			extraction = new ShareFolderAccessThread(dirName, user, password);
	        			shareFolderAccessMap.put(dirName, extraction);
	        			extraction.start();
        			}
        			else
        			{
            			return extraction.filenames;
        			}
        		}
    		}
    	}
    	    	
    	synchronized (extraction)
    	{
    		if (extraction.state.equals("S"))
    		{
    			try {
					extraction.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
		return extraction.filenames;
    }
    
    private static class ShareFolderAccessThread extends Thread
    {
    	String state ;
    	String user;
    	String password;
    	String dirName;
    	String[] filenames ;
    	Timestamp timestamp ;
    	
    	ShareFolderAccessThread(String dirName, String user, String password)
    	{
    		this.state = "S";
    		this.dirName = dirName ;
    		this.user = user ;
    		this.password = password ;
    	}
    	@Override
    	public void run() {

    		
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, password);
			
   			SmbFile directory = null ;
			try {
				
				directory = new SmbFile(dirName, auth);
				
	    	    if(!directory.exists()){
	    	    	return  ;
	    	    }
	    	    
				filenames = directory.list();
			} catch (MalformedURLException | SmbException e1) {
		    	return  ;
		    }
			
			this.timestamp = DateUtil.getCurrentTimestamp();
    		this.state = "E";
    		synchronized (this)
    		{
    			this.notifyAll();
    		}
    	}
    }




}
