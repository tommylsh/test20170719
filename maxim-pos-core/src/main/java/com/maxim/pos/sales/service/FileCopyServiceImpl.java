package com.maxim.pos.sales.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.config.SecurityConfig;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.LatestJobInd;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.TaskProcessStatus;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.SchedulerJobLogService;
import com.maxim.pos.common.service.TaskJobLogService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.sales.persistence.SchemeInfoDao;
import com.maxim.pos.security.entity.User;
import com.maxim.util.DateUtil;

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
	private SchemeInfoDao schemeInfoDao;
	
	@Autowired
	private TaskJobLogService taskJobLogService;
	
    @Autowired
    private SecurityConfig securityConfig ;
    
    @Autowired
    private SchedulerJobLogService schedulerJobLogService;
    
    @Autowired
    private ApplicationSettingService applicationSettingService;
    
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

	protected Map<String, BranchInfo> sourceFolderMap;
	protected Map<String, BranchScheme> targetTemplateMap;
	protected Map<String, ArrayList<BranchInfo>> targetFolderMap;
	protected Map<String, ArrayList<BranchMaster>> targetMasterMap;

	protected @Value("${filecopy.folder.configrationFile}") String fileCopyFolderConfigrationFile = null;
    protected @Value("${sales.fileArchivePath}")  			String salesFileArchivePath = null;
	private @Value("${system.octopus.mode}")			    String octopusMode;

	@javax.annotation.Resource(name="systemPrincipal")
	private User systemPrincipal;

	protected FileSystemXmlApplicationContext ctx ;
	@PreDestroy
    public void destoy() throws Exception {
	
		ctx.close();
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
    public void init() throws Exception {
		sourceFolderMap = (Map<String, BranchInfo>) appContext.getBean("filecopySourceFolderConfiguration");
		targetTemplateMap = (Map<String, BranchScheme>) appContext.getBean("filecopyTargetTemplateConfiguration");
		targetFolderMap = (Map<String, ArrayList<BranchInfo>>) appContext.getBean("filecopyTargetFolderConfiguration");
		targetMasterMap = (Map<String, ArrayList<BranchMaster>>) appContext.getBean("filecopyTargetBranchConfiguration");
		try{
			
			LogUtils.printLog("fileCopyFolderConfigrationFile : {}", fileCopyFolderConfigrationFile);

		    if (fileCopyFolderConfigrationFile != null)
		    {
	            DefaultResourceLoader loader = new DefaultResourceLoader();

	            Resource localResource = loader.getResource(fileCopyFolderConfigrationFile);
	            if (localResource.exists())
	            {
	    			LogUtils.printLog("fileCopyFolderConfigrationFile : {}", localResource.getURL());

	            	if (ctx != null)
	            	{
	            		ctx.refresh();
	            	}
	            	else
	            	{
				    	ctx = new FileSystemXmlApplicationContext(
				    			new String[] {fileCopyFolderConfigrationFile},appContext);
	//					ctx.getBeanFactory().resolveEmbeddedValue(value)
	            	}
					sourceFolderMap = (Map<String, BranchInfo>) ctx.getBean("filecopySourceFolderConfiguration");
					targetTemplateMap = (Map<String, BranchScheme>) ctx.getBean("filecopyTargetTemplateConfiguration");
					targetFolderMap = (Map<String, ArrayList<BranchInfo>>) ctx.getBean("filecopyTargetFolderConfiguration");
					targetMasterMap = (Map<String, ArrayList<BranchMaster>>) ctx.getBean("filecopyTargetBranchConfiguration");
//			    	ctx.close();
	            }
		    }
		    
		    for (String key : targetTemplateMap.keySet())
		    {
		    	ArrayList<BranchInfo> infoList = new ArrayList<BranchInfo>();
		    	ArrayList<BranchMaster> masterList = new ArrayList<BranchMaster>();
		    	BranchScheme scheme = targetTemplateMap.get(key);
				LogUtils.printLog("scheme {} : {}", key, scheme);
		    	
//		    	for (BranchScheme scheme : schemeList)
		    	{
			    	String passwordPattern	=  scheme.getPollSchemeDesc();
			    	String copyConfig		=  scheme.getPollSchemeName();
			    	
			    	List<Map<String, Object>> configMapList = null ;
			    	if (copyConfig.trim().startsWith("SELECT"))
			    	{
                        configMapList = jdbcTemplate.queryForList(copyConfig, (Map<String, ?>) null);
			    	}
			    	else
			    	{
			    		configMapList = new ArrayList<Map<String, Object>>();
			    		String[] branchCodeList = StringUtils.split(scheme.getPollSchemeName(),",");	
				    	for (String branchCode : branchCodeList)
				    	{
				    		Map<String, Object> map = new HashMap<String, Object>();
				    		BranchScheme realTimeScheme = schemeInfoDao.findbyPollSchemeTypeAndDirectionAndClientType(PollSchemeType.SALES_REALTIME, Direction.POS_TO_STG, null , branchCode);
				    		if (realTimeScheme != null)
				    		{
				    			map.put("CLIENT_DB", realTimeScheme.getBranchInfo().getClientDB());
				    			map.put("CLIENT_HOST", realTimeScheme.getBranchInfo().getClientHost());
				    			map.put("CLIENT_TYPE", realTimeScheme.getBranchInfo().getClientType().name());
				    			map.put("BRANCH_TYPE", realTimeScheme.getBranchMaster().getBranchType());
				    			map.put("BRANCH_CODE", realTimeScheme.getBranchMaster().getBranchCode());
				    		}
				    		else
				    		{
				    			map.put("BRANCH_CODE", branchCode);
				    		}
				    		configMapList.add(map);
				    	}
			    	}
			    	
//			    	for (String branchCode : branchCodeList)
			    	for (Map<String, Object> map : configMapList)
			    	{
			    		String branchCode = (String) map.get("BRANCH_CODE");
			    		String branchType = (String) map.get("BRANCH_TYPE");
			    		BranchInfo info = scheme.getBranchInfo();
			    		BranchMaster master = new BranchMaster();
			    		master.setBranchCode(branchCode);
			    		master.setBranchType(branchType);
	
//			    		BranchInfo realInfo = null ;
//			    		BranchScheme realTimeScheme = schemeInfoDao.findbyPollSchemeTypeAndDirectionAndClientType(PollSchemeType.SALES_REALTIME, Direction.POS_TO_STG, null , branchCode);
//			    		if (realTimeScheme != null)
//			    		{
//			    			realInfo = realTimeScheme.getBranchInfo();
//			    			master = realTimeScheme.getBranchMaster();
//			    		}
			    		
			    		String clientHost = info.getClientHost();
			    		String clientDB = info.getClientDB();
			    		String user = securityConfig.decrypt(info.getUser()) ;
			    		String password = securityConfig.decrypt(info.getPassword()) ;
			    		if (StringUtils.isNotEmpty(passwordPattern))
			    		{
			    			password = StringUtils.replace(passwordPattern,"{PASSWORD}", password);
			    		}
			    		
			    		info = new BranchInfo();
	
			    		info.setUser(getResolvedValue(user, map));
			    		info.setPassword(getResolvedValue(password, map ));
			    		info.setClientHost(getResolvedValue(clientHost, map));
			    		info.setClientDB(getResolvedValue(clientDB, map));
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
			LogUtils.printException("sourceFileCopyServiceImpl", e);
			e.printStackTrace();
		}

		LogUtils.printLog("sourceFolderMap {}", sourceFolderMap);
		LogUtils.printLog("targetFolderMap {}", targetFolderMap);
    }
	
	public SchemeJobLog updatePollBrachSchemeList(SchemeScheduleJob scheduleJob, SchemeJobLog schemeJobLog)
	{
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
		LogUtils.printLog("updatePollBrachSchemeList init {}", targetFolderMap);

        List<String>     branchList = new ArrayList<String>();
        List<TaskJobLog> taskJobLogList = new ArrayList<TaskJobLog>();
		List<BranchScheme> branchSchemeList = new ArrayList<BranchScheme>();
//		List<Map<String, Object>> schemes = new ArrayList<Map<String, Object>>();

		Direction direction = scheduleJob.getPollSchemeDirection();
		PollSchemeType pollSchemeType = scheduleJob.getPollSchemeType();
		
		List<String> directionList = new ArrayList<String>();
		if (direction.equals(Direction.OCT_ALL))
		{
			directionList.addAll(sourceFolderMap.keySet());
		}
		else
		{
			directionList.add(direction.name());
		}
    	Timestamp currentDate = DateUtil.getCurrentTimestamp();

		for (String directionName : directionList)
		{
//			BranchInfo sourceBranchInfo = sourceFolderMap.get(directionName);
			List<BranchInfo> targetBranchInfoList = targetFolderMap.get(directionName);
			List<BranchMaster> targetBranchMasterList = targetMasterMap.get(directionName);
			Iterator<BranchInfo> it = targetBranchInfoList.iterator();
			for (BranchMaster master : targetBranchMasterList)
			{
				BranchInfo info = it.next();
				
          	  	BranchScheme scheme = new BranchScheme();
          	    scheme.setPollSchemeType(pollSchemeType);
          	    scheme.setPollSchemeName("TARGET");
          	    scheme.setDirection(direction);
          	    scheme.setPollSchemeDesc(directionName);
        		scheme.setBranchInfo(info);
        		scheme.setBranchMaster(master) ;
        		info.setClientPort(445);
        		
        		scheme.setSchemeScheduleJob(scheduleJob);
        		scheme.setSchemeJobLog(schemeJobLog);
                
                branchSchemeList.add(scheme);

				
                if (octopusMode.equals("MULTIBATCH"))
                {
					String branchCode = master.getBranchCode();
					
	    	        TaskJobLog taskLog = new TaskJobLog();
	    	        taskLog.setLastestJobInd(LatestJobInd.P);
	    	        taskLog.setStatus(TaskProcessStatus.PENDING);
	    	        taskLog.setCreateUser(systemPrincipal.getUserId());
	    	        taskLog.setCreateTime(currentDate);
	    	        taskLog.setLastUpdateUser(systemPrincipal.getUserId());
	    	        taskLog.setLastUpdateTime(currentDate);
	    	        taskLog.setStartTime(currentDate);
	    	        taskLog.setScheduleJobId(scheduleJob.getId());
	//    	        if (pollBranchSchemeId != null)
	//    	        {
	//    	        	taskLog.setPollSchemeID(pollBranchSchemeId.longValue());
	//    	        }
	    	        taskLog.setDirection(direction);
	    	        taskLog.setPollSchemeType(pollSchemeType);
	    	        taskLog.setPollSchemeJobLogId(schemeJobLog.getId());
	    	        
	    	        taskLog.setBranchCode(branchCode);
	    	        taskLog.setPollBranchId(info.getId());
	//    	        taskLog.setPollSchemeName(pollSchemeName);
	//    	        taskLog.setPollSchemedesc(pollSchemeDesc);	
	    	        
	    	        
	    	        taskLog.setLastTaskJobLogId(Long.MAX_VALUE);
	    	        
	    	        branchList.add(branchCode);
	    	        taskJobLogList.add(taskLog);
	        		scheme.setTaskLog(taskLog);
                }

			}
		}
        if (octopusMode.equals("MULTIBATCH"))
        {
			LogUtils.printLog("{} {} Batch Insert start {} {}",pollSchemeType, direction, currentDate, branchList);
	        taskJobLogService.createTaskJobLogList(taskJobLogList);
			LogUtils.printLog("Batch Insert end");
        }
        schemeJobLog.setBranchSchemeList(branchSchemeList);

    	return schemeJobLog;
	}
	public void fileCopyOneTarget(BranchScheme branchScheme, Logger logger) {
		TaskJobLog taskLog = taskJobLogService.acquireTaskJobLog(branchScheme, branchScheme.getSchemeJobLog());
		
		try {
//			Direction direction = branchScheme.getDirection() ;
//			PollSchemeType pollSchemeType = branchScheme.getPollSchemeType();

			taskJobLogService.startTaskJobLog(branchScheme, taskLog);
			
			String directionName = branchScheme.getPollSchemeDesc();
//			HashMap<String, File> localFileMap = new HashMap<String, File>();

			{
				BranchInfo sourceBranchInfo = sourceFolderMap.get(directionName);
				
				if (sourceBranchInfo == null)
				{
					LogUtils.printLog(logger, "No Source BranchInfo for {} !", directionName);
	                taskJobLogService.createJobExceptionDetail(taskLog, "", "", "No Source BranchInfo " + directionName);
		            return ;
				}
				
		    	File tempDir		= null ;
		    	File tempSrcDir		= null ;
				if (true)
				{
					tempDir = new File(salesFileArchivePath);
					tempSrcDir = new File(tempDir, branchScheme.getPollSchemeType().name()+File.separator+directionName);
					if (!tempSrcDir.exists())
					{
						tempSrcDir.mkdirs();
					}
				}
				
				{
					BranchInfo info = branchScheme.getBranchInfo();
					BranchMaster master = branchScheme.getBranchMaster() ;
					
					String branchCode = master.getBranchCode();

//		            try
//		            {
//		            	applicationSettingService.checkConnection(branchScheme);
//		            }
//		            catch (IOException e)
//		            {
//		            	LogUtils.printException(logger, branchCode + " Connection Error !!", e);
//		                taskJobLogService.createJobExceptionDetail(taskLog, "", "", e);
//			            return ;
//		            }
		
//					SmbFile targetDirectory = smbService.getRootDirectory(master, info, true);
//					if (targetDirectory == null)
//					{
//						LogUtils.printLog(logger, "No Target Directory !");
//		                taskJobLogService.createJobExceptionDetail(taskLog, "", "", "No Target Directory " + branchCode);
//			            return ;
//					}
				
				
//					SmbFile sourceDirectory = smbService.getRootDirectory(master, sourceBranchInfo);
//					if (sourceDirectory == null)
//					{
//						LogUtils.printLog(logger, "No sourceDirectory !");
//		                taskJobLogService.createJobExceptionDetail(taskLog, "", "", "No sourceDirectory" + directionName);
//			            return ;
//					}
					
					try
					{
		            	applicationSettingService.checkConnection(branchScheme);
						SmbFile targetDirectory = smbService.getRootDirectory(master, info, true);
						SmbFile sourceDirectory = smbService.getRootDirectory(master, sourceBranchInfo, true);
					
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
								String path = source.getPath();
						    	String filename = source.getName() ;
						    	long sourceFileLength = source.length();
								File localFile = null ;

//						    	File localFile = localFileMap.get(path) ;
//								if (localFile == null)
								synchronized (systemPrincipal)
								{
									int pos = path.indexOf(sourceBranchInfo.getClientHost());
									if (pos > -1)
									{
										path = path.substring(pos+sourceBranchInfo.getClientHost().length());
										pos = path.lastIndexOf('/');
										path = path.substring(0,pos);
										
										File localPath = new File(tempSrcDir,path);
										if (!localPath.exists()){
											localPath.mkdirs();
										}
										boolean download = true ;
										localFile = new File(localPath, filename);
								    	if (localFile.exists())
								    	{
								    		if (localFile.length() == sourceFileLength)
								    		{
								    			download = false ;
								    		}
								    	}
								    	if (download)
								    	{
								 		   try (OutputStream out = new FileOutputStream(localFile))
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
//										localFileMap.put(path,localFile) ;
									}
								}
										
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
						    		if (target.length() == sourceFileLength)
						    		{
						    			continue ;
						    		}
						    	}
					 		   try (OutputStream out = target.getOutputStream())
					 		   {
//					    		   InputStream in = source.getInputStream();
					    		   InputStream in = localFile != null ? new FileInputStream(localFile) : source.getInputStream();
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
		            	LogUtils.printException("{"+branchCode+"} File Copy Error ",e);
		                taskJobLogService.createJobExceptionDetail(taskLog, "", "", e);
					}
				}
			}
		
		} catch (Exception e1) {
        	LogUtils.printException(" File Copy Error ",e1);
            taskJobLogService.createJobExceptionDetail(taskLog, "", "", e1);
		} 
		finally {
			taskJobLogService.updateTaskJobLogForEnd(taskLog, true);
		}
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
		
		TaskJobLog taskLog = taskJobLogService.acquireTaskJobLog(branchScheme, branchScheme.getSchemeJobLog());
		
		try {
			init();

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
			taskJobLogService.startTaskJobLog(branchScheme, taskLog);
			HashMap<String, File> localFileMap = new HashMap<String, File>();

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
				
		    	File tempDir		= null ;
		    	File tempSrcDir		= null ;
				if (true)
				{
					tempDir = new File(salesFileArchivePath);
					tempSrcDir = new File(tempDir, branchScheme.getPollSchemeType().name()+File.separator+directionName);
					if (!tempSrcDir.exists())
					{
						tempSrcDir.mkdirs();
					}
				}
				
				Iterator<BranchInfo> it = targetBranchInfoList.iterator();
				for (BranchMaster master : targetBranchMasterList)
				{
					BranchInfo info = it.next();
					
					String branchCode = master.getBranchCode();
					
		            try
		            {
		          	  	BranchScheme scheme = new BranchScheme();
		        		scheme.setBranchInfo(info);
		        		scheme.setBranchMaster(master) ;
		        		info.setClientPort(445);

		            	applicationSettingService.checkConnection(scheme);
		            }
		            catch (IOException e)
		            {
		            	LogUtils.printException(logger, branchCode + " Connection Error !!", e);
		                taskJobLogService.createJobExceptionDetail(taskLog, "", "", e);
			            continue ;
		            }
		            

		
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
								String path = source.getPath();
						    	String filename = source.getName() ;
						    	long sourceFileLength = source.length();
								
								File localFile = localFileMap.get(path) ;
								
								if (localFile == null)
								{
									int pos = path.indexOf(sourceBranchInfo.getClientHost());
									if (pos > -1)
									{
										path = path.substring(pos+sourceBranchInfo.getClientHost().length());
										pos = path.lastIndexOf('/');
										path = path.substring(0,pos);
										
										File localPath = new File(tempSrcDir,path);
										if (!localPath.exists()){
											localPath.mkdirs();
										}
										boolean download = true ;
										localFile = new File(localPath, filename);
								    	if (localFile.exists())
								    	{
								    		if (localFile.length() == sourceFileLength)
								    		{
								    			download = false ;
								    		}
								    	}
								    	if (download)
								    	{
								 		   try (OutputStream out = new FileOutputStream(localFile))
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
										localFileMap.put(path,localFile) ;
									}
								}
										
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
						    		if (target.length() == sourceFileLength)
						    		{
						    			continue ;
						    		}
						    	}
					 		   try (OutputStream out = target.getOutputStream())
					 		   {
//					    		   InputStream in = source.getInputStream();
					    		   InputStream in = localFile != null ? new FileInputStream(localFile) : source.getInputStream();
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
		            	LogUtils.printException("{"+branchCode+"} File Copy Error ",e);
		                taskJobLogService.createJobExceptionDetail(taskLog, "", "", e);
					}
					schedulerJobLogService.addOrUpdateSchemeJobLog(branchScheme.getPollSchemeType().name(), branchScheme.getSchemeJobLog());
				}
			}
		
		} catch (Exception e1) {
        	LogUtils.printException(" File Copy Error ",e1);
            taskJobLogService.createJobExceptionDetail(taskLog, "", "", e1);
		} 
		finally {
			taskJobLogService.updateTaskJobLogForEnd(taskLog, true);
		}
	}
	
	private String getResolvedValue(String value, Map<String, Object> map)
	{
        value = StringUtils.replace(value,"{BRANCH_CODE}",	(String) map.get("BRANCH_CODE"));
        value = StringUtils.replace(value,"{BRANCH_TYPE}",	(String) map.get("BRANCH_TYPE"));
        value = StringUtils.replace(value,"{CLIENT_DB}",	(String) map.get("CLIENT_DB"));
        value = StringUtils.replace(value,"{CLIENT_HOST}",	(String) map.get("CLIENT_HOST"));
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
