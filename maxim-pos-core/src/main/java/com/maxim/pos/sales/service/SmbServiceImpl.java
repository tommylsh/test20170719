package com.maxim.pos.sales.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.maxim.pos.common.config.SecurityConfig;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.util.DateUtil;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Class SmbServiceImpl
 * 
 * Created by Tommy Leung
 * Created on 12 Apr 2017
 *  
 * Amendment History
 * 
 * Name                  Modified on  Comment
 * --------------------  -----------  ----------------------------------------
 * 
 * 
 */

@Service
public class SmbServiceImpl  {
	
    @Autowired
    protected SecurityConfig securityConfig ;
    
	public SmbFile getSmbFile(String path, String user, String password)
	{
		SmbFile file = null ;

		try {
			password = securityConfig.decrypt(password);
		} catch (Exception e) {
			LogUtils.printException("aesDecrypt error in SalesServiceFileImpl's getRootDirectory", e);
		}
		try {
			user = securityConfig.decrypt(user);
		} catch (Exception e) {
			LogUtils.printException("aesDecrypt error in SalesServiceFileImpl's getRootDirectory", e);
		}
		
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, password);
	
		String brachDirName =  "smb://" + path ;

		try {
			
			file = new SmbFile(brachDirName, auth);
			
    	    if(!file.exists()){
        		LogUtils.printLog("Branch : path {} ", brachDirName);
    	    	return null ;
    	    }
		} catch (MalformedURLException | SmbException e1) {
    		LogUtils.printLog("brachDirname connet error {} ",brachDirName);
	    	return null ;
	    }
		
		return file ;
	}

	public SmbFile getRootDirectory(BranchMaster branchMaster, BranchInfo branchInfo)
	{
		return getRootDirectory(branchMaster, branchInfo, false); 
	}
	public SmbFile getRootDirectory(BranchMaster branchMaster, BranchInfo branchInfo, boolean throwException)
	{
		SmbFile directory = null ;

		String user = branchInfo.getUser() ;
		String password = branchInfo.getPassword() ;
		try {
			password = securityConfig.decrypt(password);
		} catch (Exception e) {
			LogUtils.printException("aesDecrypt error in SalesServiceFileImpl's getRootDirectory", e);
		}
		try {
			user = securityConfig.decrypt(user);
		} catch (Exception e) {
			LogUtils.printException("aesDecrypt error in SalesServiceFileImpl's getRootDirectory", e);
		}
		
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, password);
	
		String brachDirName =  "smb://"+branchInfo.getClientHost() +
				'/' +
				branchInfo.getClientDB() +
				'/';
		
        String branchType				= branchMaster.getBranchType();
        String branchCode				= branchMaster.getBranchCode();
		brachDirName = StringUtils.replace(brachDirName,"{BRANCH_CODE}", branchCode);
		brachDirName = StringUtils.replace(brachDirName,"{BRANCH_TYPE}", branchType);

		LogUtils.printLog("{} {} {} {}",brachDirName , user, password);
		try {
			
			directory = new SmbFile(brachDirName, auth);
			
    	    if(!directory.exists()){
        		LogUtils.printLog(" {} Branch : brachDirname not exists {} ", branchCode, brachDirName);
        		if (throwException)
        		{
        			throw new RuntimeException(" {"+branchCode+"} Branch : brachDirname not exists " + brachDirName);
        		}
    	    	return null ;
    	    }
		} catch (MalformedURLException | SmbException e1) {
    		LogUtils.printLog(" {} Branch : brachDirname connet error {} ", branchCode, brachDirName);
    		LogUtils.printException(" {"+branchCode+"} Branch : brachDirname connet error ", e1);
    		if (throwException)
    		{
    			throw new RuntimeException(" {"+branchCode+"} Branch : brachDirname connet error ", e1);
    		}
	    	return null ;
	    }
		
		return directory ;
	}

	public void archiveDataFile(String branchCode, Collection<SmbFile> fileList, String fileArchivePath) throws IOException
	{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateSuffix = sdf.format(DateUtil.getCurrentUtilDate());
		byte[] bs= new byte[1024];
		File archiveDirectory =  new File(fileArchivePath, branchCode);  
		if (!archiveDirectory.exists())
		{
			archiveDirectory.mkdirs();
		}

	    for (SmbFile file : fileList)
	    {
	    	String filename =file.getName()+"."+dateSuffix ;
    		   try (FileOutputStream f = new FileOutputStream( new File (archiveDirectory, filename)))
    		   {
	    		   InputStream in = file.getInputStream();
	    		   int i = in.read(bs);
	    		   while (i > 0)
	    		   {
	    			   f.write(bs, 0, i);
	    			   i = in.read(bs);
	    		   }
	    		   in.close();
	   			} catch (IOException e) {
	            	LogUtils.printException("{"+branchCode+"} Branch Archive File Error " + filename ,e);
					throw e;
				}
    		   try 
    		   {
   				file.delete();
   			   } 
    		   catch (SmbException e) 
    		   {
    			   LogUtils.printException("{"+branchCode+"} Branch Remove File Error " + file.getName(), e);
    			   throw new IOException(e);
  			   }
        }
	}

}
