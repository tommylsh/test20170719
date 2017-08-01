package com.maxim.pos.sales.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.config.SecurityConfig;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.util.LogUtils;

import jcifs.smb.SmbFile;

@Service("fileCopyService")
public class FileCopyServiceImpl implements FileCopyService{

//	@Autowired
//	private ApplicationSettingService applicationSettingService;
//	
//	@Autowired
//	private static NetworkShareService networkShareService;
//	
//	public static final String encryptKey = "90206f7a4fc149b592a14b7629caad5e";
	
	
	@Autowired
	private ApplicationContext appContext;
	
	@Autowired
	private SmbServiceImpl smbService;
	
    @Autowired
    private SecurityConfig securityConfig ;
	
	protected Map<String, BranchInfo> sourceFolderMap;
	protected Map<String, BranchScheme> targetTemplateMap;
	protected Map<String, ArrayList<BranchInfo>> targetFolderMap;
	protected Map<String, ArrayList<BranchMaster>> targetMasterMap;

	protected @Value("${filecopy.folder.configrationFile}") String fileCopyFolderConfigrationFile = null;

	@SuppressWarnings("unchecked")
	@PostConstruct
    public void init() throws Exception {
		sourceFolderMap = (Map<String, BranchInfo>) appContext.getBean("filecopySourceFolderConfiguration");
		targetTemplateMap = (Map<String, BranchScheme>) appContext.getBean("filecopyTargetTemplateConfiguration");
		targetFolderMap = (Map<String, ArrayList<BranchInfo>>) appContext.getBean("filecopyTargetFolderConfiguration");
		targetMasterMap = (Map<String, ArrayList<BranchMaster>>) appContext.getBean("filecopyTargetBranchConfiguration");
		try{
		    if (fileCopyFolderConfigrationFile != null)
		    {
	            DefaultResourceLoader loader = new DefaultResourceLoader();

	            Resource localResource = loader.getResource(fileCopyFolderConfigrationFile);
	            if (localResource.exists())
	            {

			    	FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
			    			new String[] {fileCopyFolderConfigrationFile}, appContext);
					sourceFolderMap = (Map<String, BranchInfo>) ctx.getBean("filecopySourceFolderConfiguration");
					targetTemplateMap = (Map<String, BranchScheme>) ctx.getBean("filecopyTargetTemplateConfiguration");
					targetFolderMap = (Map<String, ArrayList<BranchInfo>>) ctx.getBean("filecopyTargetFolderConfiguration");
					targetMasterMap = (Map<String, ArrayList<BranchMaster>>) ctx.getBean("filecopyTargetBranchConfiguration");
//					ctx.getBeanFactory().resolveEmbeddedValue(value)
			    	ctx.close();
	            }
		    }
		    
		    for (String key : targetTemplateMap.keySet())
		    {
		    	ArrayList<BranchInfo> infoList = new ArrayList<BranchInfo>();
		    	ArrayList<BranchMaster> masterList = new ArrayList<BranchMaster>();
		    	BranchScheme scheme = targetTemplateMap.get(key);
//		    	for (BranchScheme scheme : schemeList)
		    	{
			    	String passwordPattern =  scheme.getPollSchemeDesc();
			    	String[] branchCodeList = StringUtils.split(scheme.getPollSchemeName(),",");
			    	for (String branchCode : branchCodeList)
			    	{
			    		BranchInfo info = scheme.getBranchInfo();
			    		BranchMaster master = new BranchMaster();
			    		master.setBranchCode(branchCode);
	
			    		String clientHost = info.getClientHost();
			    		String clientDB = info.getClientDB();
			    		String user = securityConfig.decrypt(info.getUser()) ;
			    		String password = securityConfig.decrypt(info.getPassword()) ;
			    		if (StringUtils.isNotEmpty(passwordPattern))
			    		{
			    			password = StringUtils.replace(passwordPattern,"{PASSWORD}", password);
			    		}
			    		
			    		info = new BranchInfo();
	
			    		info.setUser(getResolvedValue(user, master));
			    		info.setPassword(getResolvedValue(password, master));
			    		info.setClientHost(getResolvedValue(clientHost, master));
			    		info.setClientDB(getResolvedValue(clientDB, master));
			    		info.setEnable(true);
			    		
			    		infoList.add(info);
			    		masterList.add(master);
			    		
			    	}
		    	}
		    	targetFolderMap.put(key, infoList);
		    	targetMasterMap.put(key, masterList);
		    }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		LogUtils.printLog("sourceFolderMap {}", sourceFolderMap);
		LogUtils.printLog("targetFolderMap {}", targetFolderMap);

    }
	@Override
	@Transactional
	public void fileCopy(BranchScheme branchScheme, Logger logger) {
//		ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("FILE COPY");
//		String codeValue = applicationSetting.getCodeValue();
//		String resource = codeValue.split(";")[0].split("=")[1];
//		String destination = codeValue.split(";")[1].split("=")[1];
//		if (new File(resource).exists() && new File(resource).isDirectory() && new File(destination).exists() && new File(destination).isDirectory()) {
//			traverseFolder2(resource, destination, logger);
//		} else {
//			LogUtils.printLog(logger, "{} or {} not exists", resource, destination);
//		}
//
//		Properties properties = new Properties();
//		//download
//		downloadFile(properties, logger);
//		//upload
//		uploadFile(properties, branchScheme, logger);

		Direction direction = branchScheme.getDirection() ;
//        PollSchemeType pollSchemeType = schemeScheduleJob.getPollSchemeType();
		
		List<String> directionList = new ArrayList<String>();
		if (direction.equals(Direction.OCT_ALL))
		{
			directionList.addAll(sourceFolderMap.keySet());
		}
		else
		{
			directionList.add(direction.name());
		}
			

		for (String directionName : directionList)
		{
			BranchInfo sourceBranchInfo = sourceFolderMap.get(directionName);
			List<BranchInfo> targetBranchInfoList = targetFolderMap.get(directionName);
			List<BranchMaster> targetBranchMasterList = targetMasterMap.get(directionName);
			
			if (sourceBranchInfo == null)
			{
				LogUtils.printLog(logger, "No Source BranchInfo for {} !", directionName);
	            continue ;
			}
			if (targetBranchInfoList == null)
			{
				LogUtils.printLog(logger, "No Target BranchInfoList for {} !", directionName);
	            continue ;
			}
			if (targetBranchMasterList == null)
			{
				LogUtils.printLog(logger, "No Target BranchMasterList for {} !", directionName);
	            continue ;
			}
			
			Iterator<BranchInfo> it = targetBranchInfoList.iterator();
			for (BranchMaster master : targetBranchMasterList)
			{
				BranchInfo info = it.next();
				
				String branchCode = master.getBranchCode();
	
				SmbFile targetDirectory = smbService.getRootDirectory(master, info);
				if (targetDirectory == null)
				{
					LogUtils.printLog(logger, "No Target Directory !");
		            continue ;
				}
			
			
				SmbFile sourceDirectory = smbService.getRootDirectory(master, sourceBranchInfo);
				if (sourceDirectory == null)
				{
					LogUtils.printLog(logger, "No sourceDirectory !");
					continue ;
				}
				
				try
				{
				
					byte[] bs= new byte[1024];
					SmbFile[] sourceFiles = null ;
					if (sourceDirectory.isDirectory())
					{
						sourceFiles = sourceDirectory.listFiles();
					}
					else
					{
						sourceFiles = new SmbFile[] {sourceDirectory} ;
					}
					
					for (SmbFile source : sourceFiles)
					{
						if (source.isFile())
						{
					    	String filename = source.getName() ;
					    	SmbFile target = new SmbFile(targetDirectory, filename);
					    	
	//				    	if (fileFilter.length() > 0)
	//				    	{
	//				    		boolean match = false ;
	//				    		String[] filters = StringUtils.split(fileFilter, ",");
	//				    		for (String filter : filters)
	//				    		{
	//				    			if (filter.length() > 0 && filename.startsWith(filter))
	//				    			{
	//				    				match = true ;
	//				    				break;
	//				    			}
	//				    		}
	//				    		if (!match)
	//				    		{
	//				    			continue;
	//				    		}
	//				    	}
					    	if (target.exists())
					    	{
					    		if (target.length() == source.length())
					    		{
					    			continue ;
					    		}
					    	}
					    	
				 		   try (OutputStream out = target.getOutputStream())
				 		   {
					    		   InputStream in = source.getInputStream();
					    		   int i = in.read(bs);
					    		   while (i > 0)
					    		   {
					    			   out.write(bs, 0, i);
					    			   i = in.read(bs);
					    		   }
					    		   in.close();
				  			} catch (IOException e) {
				            	LogUtils.printException("{"+branchCode+"} File Copy Error " + filename ,e);
								throw e;
							}
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
	            	LogUtils.printException("{"+branchCode+"} File Copy Error ",e);
				}
			}
		}
	}
	
	private String getResolvedValue(String value, BranchMaster master)
	{
		if (master != null)
		{
	        String branchType				= master.getBranchType();
	        String branchCode				= master.getBranchCode();
	        value = StringUtils.replace(value,"{BRANCH_CODE}", branchCode);
	        value = StringUtils.replace(value,"{BRANCH_TYPE}", branchType);
		}
		return value ;
	}

	
//	public void downloadFile(Properties properties, Logger logger) {
//		try {
//			properties.load(FileCopyServiceImpl.class.getClassLoader().getResourceAsStream("pos-config.properties"));
//
//	        String username = properties.getProperty("file.octopusSourcePath.username");
//	        String password = properties.getProperty("file.octopusSourcePath.password");
//	        String aesDecrypt = EncryptionUtil.aesDecrypt(password, encryptKey);
//	
//	        String clientHost = properties.getProperty("file.octopusSourcePath.clientHost");
//	        String directory = properties.getProperty("file.octopusSourcePath.directory");
//	        String localDir = properties.getProperty("file.octopusSourcePath.localDir");
//	        
//	//		#smb://lotic:passw0rd@192.168.1.17/share/
//	        String remoteUrl = "smb://"+username+":"+aesDecrypt+"@"+clientHost+"/"+directory+"/";
//	        JcifsUtils.smbGet(remoteUrl, localDir, logger);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void uploadFile(Properties properties, BranchScheme branchScheme, Logger logger) {
//		try {
//			BranchInfo branchInfo = branchScheme.getBranchInfo();
//			String clientDB = branchInfo.getClientDB();
//			String username = branchInfo.getUser();
//			String password = EncryptionUtil.aesDecrypt(branchInfo.getPassword(), encryptKey);
//			String clientHost = branchInfo.getClientHost();
//			
//	//		#smb://lotic:passw0rd@192.168.1.17/share/
//			String remoteUrl = "smb://"+username+":"+password+"@"+clientHost+"/"+clientDB+"/";
//			properties.load(FileCopyServiceImpl.class.getClassLoader().getResourceAsStream("pos-config.properties"));
//			String localDir = properties.getProperty("file.octopusSourcePath.localDir");
//
//			JcifsUtils.smbPut(remoteUrl, localDir, logger);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void traverseFolder2(String resource, String destination, Logger logger) {
//
//        File file = new File(resource);
//        if (file.exists()) {
//            File[] files = file.listFiles();
//            if (files.length == 0) {
//            	LogUtils.printLog(logger, "{}  directory don't  have file", file);
//                return;
//            } else {
//                for (File file2 : files) {
//                    if (file2.isDirectory()) {
//                        traverseFolder2(file2.getAbsolutePath(), destination+File.separator+file2.getName(), logger);
//                    } else {
//						 File file3 = new File(destination);
//						 if (file3.exists()) {
//							 file3.delete();
//						 } else {
//							 file3.mkdirs();
//						 }
//						 file2.renameTo(new File(destination+File.separator+file2.getName()));
//                    }
//                }
//            }
//        }
//    }
	
//	public static void main(String[] args) {
//		traverseFolder2("D:\\Maxim_test\\csv","D:\\Maxim_test\\Infrasys_Dbf",LogUtils.getCurrentThreadLogger());
//	}	
	
}
