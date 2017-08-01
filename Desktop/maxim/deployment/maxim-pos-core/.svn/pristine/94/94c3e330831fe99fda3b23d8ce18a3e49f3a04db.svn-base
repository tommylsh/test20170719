package com.maxim.pos.sales.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.LogUtils;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

/**
 * Class SalesServiceDbfImpl
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

public abstract class SalesServiceFileImpl extends  SalesServiceBaseImpl {
	
	
	protected @Value("${sales.zipFileReadMode}") 		String zipFileReadMode = null;
	protected @Value("${sales.textFileReadMode}") 		String textFileReadMode = null;
	protected @Value("${sales.textFileTempDirectory}")	String textFileTempDirectory = null;
	protected @Value("${sales.checkRealTimeLocalFile}")	boolean checkRealTimeLocalFile ;
	protected @Value("${sales.keepLocalFile}")			boolean keepLocalFile ;

	
	public static String FILE_READ_MODE_REMOTE	= "REMOTE";
	public static String FILE_READ_MODE_LOCAL	= "LOCAL";

	public static String FILE_METHOD_PROPERTIES	= "PROPPERTIES";
	public static String FILE_METHOD_DATABASE	= "DATABASE";
    abstract protected String getFileMethod() ;
    abstract protected String getFilePattern() ;
	abstract protected int processToStagingTable(BranchScheme branchScheme, SchemeInfo schemeInfo, Date bizDate, InputStream in , Connection conn, int defaultTransactionBatchSize ) throws SQLException, IOException;

    protected String getZipPattern()
    {
    	return null ;
    }
    protected String getSTZipPattern()
    {
    	return null ;
    }

	@Autowired
	private SmbServiceImpl smbService;

	@Override
    protected int getDefaultScanDayIfNoControl()
	{
		return textFileDefaultScanDayIfNoControl ;
	}
	@Override
    protected int getMaxScanDay()
    {
		return textFileMaxScanDay ;
    }

	@Override
	protected List<Date> doGetPosProcessDate(BranchScheme branchScheme, List<SchemeInfo> schemeList, 
			java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate, Logger logger) {
        List<Date> processDates = new ArrayList<Date>();
        
    	BranchInfo branchInfo			= branchScheme.getBranchInfo();
        String branchType				= branchScheme.getBranchMaster().getBranchType();
        String branchCode				= branchScheme.getBranchMaster().getBranchCode();
        ClientType clientType			= branchScheme.getBranchInfo().getClientType();
		PollSchemeType pollSchemeType	= branchScheme.getPollSchemeType();
        LogUtils.printLog(logger, "{} {} {} doGetPosProcessDate  {}", branchCode, clientType, pollSchemeType, schemeList.size());


        SimpleDateFormat dateFormat		= new SimpleDateFormat("yyMMdd");
        SimpleDateFormat dateFormat2	= new SimpleDateFormat("yyyyMMdd");
	    String fileMethod 				= getFileMethod() ;
	    String filePattern 				= getFilePattern() ;
	    String zipPattern				= getZipPattern() ;
        
        LogUtils.printLog(logger, "{} {} {} fileMethod / filePattern / zipPattern {} {} {} ", branchCode, clientType, pollSchemeType, fileMethod, filePattern, zipPattern);
	    
	    // Get the Window Share Directory
		SmbFile rootDirectory = smbService.getRootDirectory(branchScheme.getBranchMaster(), branchInfo) ;
		if (rootDirectory == null)
		{
			return null ;
		}
        LogUtils.printLog(logger, "{} {} {} rootDirectory {}", branchCode, clientType, pollSchemeType, fileMethod,  rootDirectory.getCanonicalPath());
        
        
		File tempDir = new File(salesFileArchivePath);
		File tempBranchDir = new File(tempDir, branchCode);
		boolean checkIdentical = checkRealTimeLocalFile ;
		if (branchScheme.isReRun())
		{
			checkIdentical = false ;
		}
        if (checkIdentical)
        {
			if (tempBranchDir.exists())
			{
				File errorFile = new File(tempBranchDir, "ERROR.TXT") ;
				if (errorFile.exists())
				{
					checkIdentical = false ;
				}
			}
			else
			{
				checkIdentical = false ;
			}
        }
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(controlDate);
		if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
		{
			cal.add(Calendar.DATE, 1);
		}
		java.sql.Date date = new java.sql.Date(cal.getTime().getTime());
		date = java.sql.Date.valueOf(date.toString());
        LogUtils.printLog(logger, "{} {} {} chkdate {} {} {}", branchCode, clientType, pollSchemeType, date,  currentDate, date.after(currentDate));
		while (!date.after(currentDate))
		{
//			if (date.equals(controlDate) && PollSchemeType.SALES_REALTIME.equals(pollSchemeType) ||
//					date.after(controlDate) )
//					
			{
				boolean exists		= true ;
				boolean identical	= checkIdentical ;
				if (!date.equals(controlDate))
				{
					identical = false ;
				}
				
				try
				{
	                if (!StringUtils.isEmpty(zipPattern))
	                {
	  	    			String zipFilename = StringUtils.replace(zipPattern, "{BRANCH_CODE}", branchCode);
	  	    			zipFilename = StringUtils.replace(zipFilename,       "{BRANCH_TYPE}", branchType);
		    			if (date.equals(controlDate))
		    			{
		    				zipFilename = StringUtils.replace(zipFilename,"{YYMMDD}"	,"000000");
		    				zipFilename = StringUtils.replace(zipFilename,"{YYYYMMDD}"	,"00000000");
		    			}
		    			else
//		    			if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
		    			{
		    				zipFilename = StringUtils.replace(zipFilename,"{YYMMDD}"	,dateFormat.format(date));
		    				zipFilename = StringUtils.replace(zipFilename,"{YYYYMMDD}"	,dateFormat2.format(date));
		    			}    
		    			SmbFile file = new SmbFile(rootDirectory, zipFilename);
		            	if (!file.exists())
		            	{
		            		exists = false ;
		            	}
		            	else
		            	{
			            	if (identical)
			            	{
	        					Long filesize = file.length();
	        					File localFile = new File(tempBranchDir, file.getName());
	        					if (!localFile.exists())
	        					{
	        						identical = false ;
	        					}
	        					else if (filesize != localFile.length())
	        					{
	        						identical = false ;
	        					}
	    		                LogUtils.printLog(logger, "{} {} {} schemeInfo{}  check filesize {} == {}", branchCode, clientType, pollSchemeType, file.getName(), filesize, localFile.length());
			            	}
		            	}
	                }
	                else {   
				    	for (SchemeInfo schemeInfo : schemeList) {
				    		if (FILE_METHOD_DATABASE.equals(fileMethod))
				    		{
				    			filePattern = schemeInfo.getSource();
				    		}
			    			String filename = StringUtils.replace(filePattern, "{BRANCH_CODE}", branchCode);
			    			filename = StringUtils.replace(filename,           "{BRANCH_TYPE}", branchType);
							filename = StringUtils.replace(filename,           "{TABLE}",       schemeInfo.getSource().toUpperCase());
//			    			if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
			    			if (date.equals(controlDate))
			    			{
			    				filename = StringUtils.replace(filename,"{YYMMDD}"		,"000000");
			    				filename = StringUtils.replace(filename,"{YYYYMMDD}"	,"00000000");
			    			}
			    			else
//			    			if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
			    			{
			    				filename = StringUtils.replace(filename,"{YYMMDD}"		,dateFormat.format(date));
			    				filename = StringUtils.replace(filename,"{YYYYMMDD}"	,dateFormat2.format(date));
			    			}    			
		
			        		try
			        		{
		                        LogUtils.printLog(logger, "{} {} {} schemeInfo{}  check filename {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), filename);
			            		SmbFile file = new SmbFile(rootDirectory, filename);
			            		if (!file.exists())
			            		{
			            			exists = false ;
			            			break ;
			            		}
			            		else
			            		{
					            	if (identical)
					            	{
			        					Long filesize = file.length();
			        					File localFile = new File(tempBranchDir, file.getName());
			        					if (!localFile.exists())
			        					{
			        						identical = false ;
			        					}
			        					else if (filesize != localFile.length())
			        					{
			        						identical = false ;
			        					}
			    		                LogUtils.printLog(logger, "{} {} {} schemeInfo{}  check filesize {} == {}", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), filesize, localFile.length());
					            	}
			            		}
			    			} catch (MalformedURLException | UnknownHostException | SmbException e) {
		                        LogUtils.printLog(logger, "{} {} {} schemeInfo{}  check filename error {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), filename);
			            		LogUtils.printException(logger, "Chk File Exception ",e);
			        			exists = false ;
			        			break ;
			    			}
				    	}
	                }
	                LogUtils.printLog(logger, "{} {} {} chkdate {} exists {} identical {}", branchCode, clientType, pollSchemeType, date, exists, identical);
					if (exists)
			    	{
		    			if (date.equals(controlDate))
		    			{
			            	if (!identical)
			            	{
			            		processDates.add(REALTIME_DATE);
			            	}
			            	else
			            	{
				    			if (date.equals(controlDate))
				    			{
				    				branchScheme.getTaskLog().setErrorMsg("File is Identical in Configuration Path");
				    			}
			            	}
		    			}
		    			else
		    			{
		    				processDates.add(date);
		    			}
		    		}
					else
					{
		    			if (date.equals(controlDate))
		    			{
		    				branchScheme.getTaskLog().setErrorMsg("File Not Exists in Configuration Path");
		    			}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
	        		LogUtils.printException(logger, "Chk File Exception ",e);
				}
//				if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
//				{
//					if (date.after(controlDate) && exists)
//						break;
//				}
			}
			cal.add(Calendar.DATE, 1);
			date = new java.sql.Date(cal.getTime().getTime());
			date = java.sql.Date.valueOf(date.toString());
	        LogUtils.printLog(logger, "{} {} {} chkdate {} {} {}", branchCode, clientType, pollSchemeType, date,  currentDate, date.after(currentDate));
    	}

        LogUtils.printLog(logger, "{} {} {}  dates {} size{}", branchCode, clientType, pollSchemeType, processDates, processDates.size());
//		if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
//		{
//			if (processDates.size() == 0)
//			{
//				return null;
//			}
//			else
//			{
////				processDates.clear();
//				return processDates;
//			}
//		}

		return processDates;
	}

	@Override
	protected List<Date> doFilterStockTakeReady (List<Date> dates, BranchScheme branchScheme, List<SchemeInfo> schemeList, java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate,Logger logger)	  
	{
	    String stZipPattern				= getSTZipPattern() ;
	    
	    if (stZipPattern == null)
	    {
	    	return dates ;
	    }
	    	
        String branchType				= branchScheme.getBranchMaster().getBranchType();
        String branchCode				= branchScheme.getBranchMaster().getBranchCode();
        SimpleDateFormat dateFormat		= new SimpleDateFormat("yyMMdd");
        SimpleDateFormat dateFormat2	= new SimpleDateFormat("yyyyMMdd");
        
        SmbFile rootDirectory = smbService.getRootDirectory(branchScheme.getBranchMaster(), branchScheme.getBranchInfo());
        
		String zipPattern = StringUtils.replace(stZipPattern, "{BRANCH_CODE}", branchCode);
		zipPattern = StringUtils.replace(zipPattern,          "{BRANCH_TYPE}", branchType);

		List<Date> returnDates = new ArrayList<Date>();
        for (Date date: dates)
        {
        	String zipFilename	= StringUtils.replace(zipPattern, "{YYMMDD}"	,dateFormat.format(date));
        	zipFilename 		= StringUtils.replace(zipFilename,"{YYYYMMDD}"	,dateFormat2.format(date));

	        try {
	    		SmbFile file = new SmbFile(rootDirectory, zipFilename);
	        	if (file.exists())
	        	{
	        		returnDates.add(date);
	            } else {
	                LogUtils.printLog(logger,"{} stock take not ready in {} ",zipFilename,rootDirectory.getPath());
	            }
	        } catch (Exception e) {
	            LogUtils.printException(logger, "SmbFile exception {}", zipFilename);
	        }
	    }
        return returnDates ;
	}


	@Override
    protected List<Date> doProcessPosDataToStg(BranchScheme branchScheme, List<SchemeInfo> schemeList, 
    		List<Date> procDates, List<Date> stDates, List<Date> nonStDoneDates, List<Date> stDoneDates, List<Date> stReady,
    		java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate, TaskJobLog taskJobLog, Logger logger){

    	BranchInfo branchInfo			= branchScheme.getBranchInfo();
        String branchType				= branchScheme.getBranchMaster().getBranchType();
        String branchCode				= branchScheme.getBranchMaster().getBranchCode();
        ClientType clientType			= branchScheme.getBranchInfo().getClientType();
		PollSchemeType pollSchemeType	= branchScheme.getPollSchemeType();
        LogUtils.printLog(logger, "{} {} {} doProcessPosDataToStg  {}", branchCode, clientType, pollSchemeType, schemeList.size());

    	SimpleDateFormat dateFormat		= new SimpleDateFormat("yyMMdd");
        SimpleDateFormat dateFormat2	= new SimpleDateFormat("yyyyMMdd");
    	SimpleDateFormat dateFormat3	= new SimpleDateFormat("yyyy-MM-dd");
    	
    	Map<Date,File> tempBranchDateDirMap	= new HashMap<Date,File>() ;
    	File tempDir			= null ;
    	File tempBranchDir		= null ;
		if (textFileReadMode.equals(FILE_READ_MODE_LOCAL) || zipFileReadMode.equals(FILE_READ_MODE_LOCAL))
		{
			tempDir = new File(salesFileArchivePath);
			tempBranchDir = new File(tempDir, branchCode);
			if (!tempBranchDir.exists())
			{
				tempBranchDir.mkdirs();
			}
		}
        
	    // Get the Window Share Directory
		SmbFile rootDirectory = smbService.getRootDirectory(branchScheme.getBranchMaster(), branchInfo) ;
		if (rootDirectory == null)
		{
			return null ;
		}
		if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
		{
			procDates.add(null);
		}

        int totalCount = 0 ;
        boolean isError = false ;
        String fileMethod = this.getFileMethod() ; 
        String filePattern = this.getFilePattern() ;
//        List<SmbFile> fileList = new ArrayList<SmbFile>();
        
        Map<String, SmbFile> fileListMap		= new HashMap<String, SmbFile>(); 
        Map<String, File>    localFileListMap	= new HashMap<String, File>();
        Map<String, File>    zipFileListMap		= new HashMap<String, File>();
        
        SimpleDateFormat sdf	= new SimpleDateFormat("yyyyMMddHHmmss");
//        String dateSuffix		= sdf.format(DateUtil.getCurrentUtilDate());
        
        try (Connection conn = applicationSettingService.getCurrentJDBCConnection())
        {
            LogUtils.printLog(logger, "{} {} {} Ready to copy data from: \r\nsource: '{}' to \r\n datasource: '{}'", branchCode, clientType, pollSchemeType, rootDirectory.getCanonicalPath(), conn);
        	for (SchemeInfo schemeInfo : schemeList) 
        	{
                String toTable = schemeInfo.getDestination();
                LogUtils.printLog(logger, "{} {} {} schemeInfo{}  : {} -> {} ", branchCode, clientType, pollSchemeType, schemeInfo.getId(), schemeInfo.getSource(), schemeInfo.getDestination());

                int deleteCount = 0 ;
                int rs = 0;
            	 try 
            	 {
             		if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
             		{
                      	int idx = 0;
                      	String minDate	= null ;
                      	String maxDate	= null ;
                      	for (Date date : procDates)
                      	{
                      		if (idx == 0)
                      		{
                      			minDate=dateFormat3.format(date);
                      		}
                      		idx++;
                      		if (idx == procDates.size())
                      		{
                      			maxDate=dateFormat3.format(date);
                      		}
                      	}
                      	String deleteDateConditionStr = "CONVERT(varchar(16),business_date,23) >= '"+minDate+"' and CONVERT(varchar(16),business_date,23) <= '"+maxDate+"'"; 
                      	deleteCount = JDBCUtils.deleteByBranchAndBizDate(conn, schemeInfo.getDestination(),
                  			branchScheme.getBranchMaster().getBranchCode(), deleteDateConditionStr);
                         LogUtils.printLog(logger, "{} {} {} schemeInfo{}  delete table :{} {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), deleteCount, deleteDateConditionStr);
             		}
             		else
             		{
             			deleteCount = JDBCUtils.deleteByBranchAndBizDate(conn, schemeInfo.getDestination(),
 						        branchScheme.getBranchMaster().getBranchCode(), (String) null);
                        LogUtils.printLog(logger, "{} {} {} schemeInfo{}  delete table :{} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), deleteCount);
             		}

     	       	    for (Date bizDate : procDates)
     	     	    {
     	       	    	if (PollSchemeType.SALES_EOD.equals(pollSchemeType)) {
	                		if (stDates.contains(bizDate))
	                		{
	                			if (stTableList.contains(toTable.toLowerCase()))
	                			{
		                			if (!stReady.contains(bizDate))
		                			{
	                					continue ;
	                				}
	                				if (stDoneDates.contains(bizDate))
	                				{
	                					continue ;
	                				}
		                		}
	                			else
	                			{
	                				if (nonStDoneDates.contains(bizDate))
	                				{
	                					continue;
	                				}
	                			}
	                		}
     	       	    	}

     	   	    		if (FILE_METHOD_DATABASE.equals(fileMethod))
     	   	    		{
     	   	    			filePattern = schemeInfo.getDestination();
     	   	    		}
     	       		
    	    			String filename = StringUtils.replace(filePattern, "{BRANCH_CODE}", branchCode);
    	    			filename = StringUtils.replace(filename,           "{BRANCH_TYPE}", branchType);
    					filename = StringUtils.replace(filename,           "{TABLE}",       schemeInfo.getSource().toUpperCase());
    	    			if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
    	    			{
    	    				filename = StringUtils.replace(filename,"{YYMMDD}"		,"000000");
    	    				filename = StringUtils.replace(filename,"{YYYYMMDD}"	,"00000000");
    	    			}
    	    			if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
    	    			{
    	    				filename = StringUtils.replace(filename,"{YYMMDD}"		,dateFormat.format(bizDate));
    	    				filename = StringUtils.replace(filename,"{YYYYMMDD}"	,dateFormat2.format(bizDate));
    	    			}    			
                         LogUtils.printLog(logger, "{} {} {} schemeInfo{}  source filename {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), filename);
                         
                         SmbFile file = null ;
                         InputStream in = null ;
                         
                         if (StringUtils.isEmpty(getZipPattern()))
                         {
                    		 file = new SmbFile(rootDirectory, filename);	
    	                     if (file.exists()) 
    	                     {
 		                		in = file.getInputStream() ;
	                			if (textFileReadMode.equals(FILE_READ_MODE_LOCAL))
	                			{
	            					File targetFolder = tempBranchDir ;
	        	             		if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
	            					{
		            					String dateFolder = dateFormat2.format(bizDate);
		            					targetFolder = new File(tempBranchDir, dateFolder);
		        	             		if (!targetFolder.exists())
		        	             		{
		        	             			targetFolder.mkdirs();
		        	             		}
	            					}
	            					File targetFile = new File(targetFolder, file.getName());
	                    	    	if (enableArchive && targetFile.exists())
	                    	    	{
		            					File dateDir = null ;
	                    	    		Date lastModifed = new Date(targetFile.lastModified()) ;
		        	             		if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
		        	             		{
			            					String dateFolder = dateFormat2.format(bizDate);
			            					dateDir = new File(tempBranchDir, dateFolder);
		        	             		}
		        	             		else 
		        	             		{
			            					String dateFolder = dateFormat2.format(lastModifed);
			            					dateDir = new File(tempBranchDir, dateFolder);
		        	             		}
		            					if (!dateDir.exists())
		            					{
		            						dateDir.mkdirs();
		            					}
	                    	    		targetFile.renameTo(new File(dateDir, file.getName()+"."+sdf.format(lastModifed)));
		            					targetFile = new File(targetFolder, file.getName());
	                    	    	}
	                    	    	localFileListMap.put(file.getCanonicalPath(), targetFile);
	                    	    	
	            					try (FileOutputStream out = new FileOutputStream(targetFile))
	            					{
	         			 				byte[] bs= new byte[1024];
	        				    		int i = in.read(bs);
	        				    		while (i > 0)
	        				    		{
	        				    			out.write(bs, 0, i);
	        				    			i = in.read(bs);
	        				    		}
	        				    		in.close();
	        			  			} catch (IOException e) {
	        			            	LogUtils.printException("{"+branchCode+"} File Copy Locally Error " + toTable ,e);
	        							throw e;
	        						}
	         			 		   
	         			 		    in = new FileInputStream(targetFile);
	                			}
//	                			if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType) && checkRealTimeLocalFile)
//	                			{
//	            					File tempDir = new File(textFileTempDirectory);
//	            					if (!tempDir.exists())
//	            					{
//	            						tempDir.mkdirs();
//	            					}
//	            					File tempBranchDir = new File(tempDir, branchCode);
//	            					if (!tempBranchDir.exists())
//	            					{
//	            						tempBranchDir.mkdirs();
//	            					}
//	            					
//	            					File targetFile = new File(tempBranchDir, file.getName());
//	            					try (FileOutputStream out = new FileOutputStream(targetFile))
//	            					{
//	         			 				byte[] bs= new byte[1024];
//	        				    		int i = in.read(bs);
//	        				    		while (i > 0)
//	        				    		{
//	        				    			out.write(bs, 0, i);
//	        				    			i = in.read(bs);
//	        				    		}
//	        				    		in.close();
//	        			  			} catch (IOException e) {
//	        			            	LogUtils.printException("{"+branchCode+"} File Copy Locally Error " + toTable ,e);
//	        							throw e;
//	        						}
//	         			 		   
//	         			 		    in = new FileInputStream(targetFile);
//	                			}
    	                     }
                         }
                         else
                         {
                        	String zipPattern = getZipPattern();
	    	    			 String zipFilename = StringUtils.replace(zipPattern, "{BRANCH_CODE}", branchCode);
	       	    			 zipFilename = StringUtils.replace(zipFilename,       "{BRANCH_TYPE}", branchType);
	       	    			 zipFilename = StringUtils.replace(zipFilename,       "{TABLE}",       schemeInfo.getSource().toUpperCase());
	     	    			 if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
	     	    			 {
	     	    				zipFilename = StringUtils.replace(zipFilename,"{YYMMDD}"	,"000000");
	     	    				zipFilename = StringUtils.replace(zipFilename,"{YYYYMMDD}"	,"00000000");
	     	    			 }
	     	    			 if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
	     	    			 {
	     	    				zipFilename = StringUtils.replace(zipFilename,"{YYMMDD}"	,dateFormat.format(bizDate));
	     	    				zipFilename = StringUtils.replace(zipFilename,"{YYYYMMDD}"	,dateFormat2.format(bizDate));
	     	    			 }
	                         LogUtils.printLog(logger, "{} {} {} schemeInfo{}  source zipFilename {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), zipFilename);
         	    			 
         	    			 boolean stFileExists = false ;
                      		if (stDates.contains(bizDate))
                     		{
                     			if (stTableList.contains(toTable.toLowerCase()))
                     			{
	                               	 String stZipPattern = getSTZipPattern();
	                               	 
	                               	 LogUtils.printLog(logger, "{} {} {} schemeInfo{}  source stZipPattern {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), stZipPattern);
	                               	 
	               	    			 String stZipFilename = StringUtils.replace(stZipPattern, "{BRANCH_CODE}", branchCode);
	               	    			 stZipFilename = StringUtils.replace(stZipFilename,       "{BRANCH_TYPE}", branchType);
	               	    			 stZipFilename = StringUtils.replace(stZipFilename,       "{TABLE}",       schemeInfo.getSource().toUpperCase());
	             	    			 if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
	             	    			 {
	             	    				stZipFilename = StringUtils.replace(stZipFilename,"{YYMMDD}"	,"000000");
	             	    				stZipFilename = StringUtils.replace(stZipFilename,"{YYYYMMDD}"	,"00000000");
	             	    			 }
	             	    			 if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
	             	    			 {
	             	    				stZipFilename = StringUtils.replace(stZipFilename,"{YYMMDD}"		,dateFormat.format(bizDate));
	             	    				stZipFilename = StringUtils.replace(stZipFilename,"{YYYYMMDD}"	,dateFormat2.format(bizDate));
	             	    			 }
	    	                         LogUtils.printLog(logger, "{} {} {} schemeInfo{}  source stZipFilename {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), stZipFilename);

	                           		 file = new SmbFile(rootDirectory, stZipFilename);
	           	                     if (file.exists()) 
	           	                     {
	           	                    	stFileExists = true ;
	           	                    	
	           	                    	InputStream stin = file.getInputStream() ;
	    	                			if (textFileReadMode.equals(FILE_READ_MODE_LOCAL))
			                			{
			                				File zipFile = localFileListMap.get(file.getCanonicalPath()); 
			                				if (zipFile == null)
			                				{
				            					zipFile = new File(tempBranchDir, file.getName()) ;
				                    	    	if (enableArchive && zipFile.exists())
				                    	    	{
					            					File dateDir = null ;
				                    	    		Date lastModifed = new Date(zipFile.lastModified()) ;
					        	             		if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
					        	             		{
						            					String dateFolder = dateFormat2.format(bizDate);
						            					dateDir = new File(tempBranchDir, dateFolder);
					        	             		}
					        	             		else 
					        	             		{
						            					String dateFolder = dateFormat2.format(lastModifed);
						            					dateDir = new File(tempBranchDir, dateFolder);
					        	             		}
					            					if (!dateDir.exists())
					            					{
					            						dateDir.mkdirs();
					            					}
					            					zipFile.renameTo(new File(dateDir, file.getName()+"."+sdf.format(lastModifed)));
					            					zipFile = new File(tempBranchDir, file.getName());
				                    	    	}
				                    	    	localFileListMap.put(file.getCanonicalPath(), zipFile);

				            					try (FileOutputStream out = new FileOutputStream(zipFile))
				            					{
				         			 				byte[] bs= new byte[1024];
				        				    		int i = stin.read(bs);
				        				    		while (i > 0)
				        				    		{
				        				    			out.write(bs, 0, i);
				        				    			i = stin.read(bs);
				        				    		}
				        				    		stin.close();
				        			  			} catch (IOException e) {
				        			            	LogUtils.printException("{"+branchCode+"} File Copy Locally Error " + toTable ,e);
				        							throw e;
				        						}
			                				}
			            					stin = new FileInputStream(zipFile);
			                			}
	           	                    	ZipInputStream zin = new ZipInputStream(stin);
	           	                    	ZipEntry entry = zin.getNextEntry();
	           	                    	while (entry != null )
	           	                    	{
	           	                    		if (entry.getName().equals(filename))
	           	                    		{
		           	                    		in = zin ;
	           	                    			break;
	           	                    		}
	           	                    		entry = zin.getNextEntry();
	           	                    	}
	           	                     }
	           	                     else
	           	                     {
	           	                    	stFileExists = false ;
	           	                     }
                     			}
                     		}
                      		if (file == null || !stFileExists)
                      		{
	                    		 file = new SmbFile(rootDirectory, zipFilename);
	    	                     if (file.exists()) 
	    	                     {
	           	                    InputStream zfin = file.getInputStream() ;
		                			if (textFileReadMode.equals(FILE_READ_MODE_LOCAL))
		                			{
		                				File zipFile = localFileListMap.get(file.getCanonicalPath()); 
		                				if (zipFile == null)
		                				{
			            					zipFile = new File(tempBranchDir, file.getName()) ;
			                    	    	if (enableArchive && zipFile.exists())
			                    	    	{
				            					File dateDir = null ;
			                    	    		Date lastModifed = new Date(zipFile.lastModified()) ;
				        	             		if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
				        	             		{
					            					String dateFolder = dateFormat2.format(bizDate);
					            					dateDir = new File(tempBranchDir, dateFolder);
				        	             		}
				        	             		else 
				        	             		{
					            					String dateFolder = dateFormat2.format(lastModifed);
					            					dateDir = new File(tempBranchDir, dateFolder);
				        	             		}
				            					if (!dateDir.exists())
				            					{
				            						dateDir.mkdirs();
				            					}
				            					zipFile.renameTo(new File(dateDir, file.getName()+"."+sdf.format(lastModifed)));
				            					zipFile = new File(tempBranchDir, file.getName());
			                    	    	}
			                    	    	localFileListMap.put(file.getCanonicalPath(), zipFile);

			            					try (FileOutputStream out = new FileOutputStream(zipFile))
			            					{
			         			 				byte[] bs= new byte[1024];
			        				    		int i = zfin.read(bs);
			        				    		while (i > 0)
			        				    		{
			        				    			out.write(bs, 0, i);
			        				    			i = zfin.read(bs);
			        				    		}
			        				    		zfin.close();
			        			  			} catch (IOException e) {
			        			            	LogUtils.printException("{"+branchCode+"} File Copy Locally Error " + toTable ,e);
			        							throw e;
			        						}
		                				}
	                					zfin = new FileInputStream(zipFile);
		                			}
	    	                    	 
	    	                    	ZipInputStream zin = new ZipInputStream(zfin);
	    	                    	ZipEntry entry = zin.getNextEntry();
	    	                    	while (entry != null )
	    	                    	{
	    	                    		if (entry.getName().equals(filename))
	    	                    		{
		    	                    		in = zin ;
	    	                    			break;
	    	                    		}
	    	                    		entry = zin.getNextEntry();
	    	                    	}
	    	                     }
                      		}
                			if (zipFileReadMode.equals(FILE_READ_MODE_LOCAL))
                			{
            					File tempBranchDateDir = tempBranchDateDirMap.get(bizDate) ;
            					if (tempBranchDateDir == null)
            					{
            						tempBranchDateDir = new File(tempBranchDir, dateFormat2.format(bizDate));
            						tempBranchDateDirMap.put(bizDate, tempBranchDateDir);
            					}
            					if (!tempBranchDateDir.exists())
            					{
            						tempBranchDateDir.mkdirs();
            					}
//            					File targetFile = File.createTempFile(toTable, ".tmp", tempBranchDir);
            					File targetFile = new File(tempBranchDateDir, filename) ;
            					zipFileListMap.put(filename, targetFile);

            					try (FileOutputStream out = new FileOutputStream(targetFile))
            					{
         			 				byte[] bs= new byte[1024];
        				    		int i = in.read(bs);
        				    		while (i > 0)
        				    		{
        				    			out.write(bs, 0, i);
        				    			i = in.read(bs);
        				    		}
        				    		in.close();
        			  			} catch (IOException e) {
        			            	LogUtils.printException("{"+branchCode+"} File Copy Locally Error " + toTable ,e);
        							throw e;
        						}
         			 		   
         			 		    in = new FileInputStream(targetFile);
                			}

                         }
                         
//                		 SmbFile file = new SmbFile(rootDirectory, filename);	
	                     if (in != null) {
		                		try
		                		{
			                		if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
			                		{
//			                         	int idx = 0;
//			                         	String minDate	= null ;
//			                         	String maxDate	= null ;
//			                         	for (Date date : procDates)
//			                         	{
//			                         		if (idx == 0)
//			                         		{
//			                         			minDate=dateFormat3.format(date);
//			                         		}
//			                         		idx++;
//			                         		if (idx == procDates.size())
//			                         		{
//			                         			maxDate=dateFormat3.format(date);
//			                         		}
//			                         	}
//			                         	String deleteDateConditionStr = "CONVERT(varchar(16),business_date,23) >= '"+minDate+"' and CONVERT(varchar(16),business_date,23) <= '"+maxDate+"'"; 
//			                         	deleteCount = JDBCUtils.deleteByBranchAndBizDate(conn, schemeInfo.getDestination(),
//			                     			branchScheme.getBranchMaster().getBranchCode(), deleteDateConditionStr);
//			                            LogUtils.printLog(logger, "{} {} {} schemeInfo{}  delete table :{} {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), deleteCount, deleteDateConditionStr);

			                            
				                        rs += processToStagingTable(branchScheme, schemeInfo, bizDate, in, conn,  defaultTransactionBatchSize);
				                        LogUtils.printLog(logger, "{} {} {} schemeInfo{} insert records: {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), rs);
			                		}
			                		else
			                		{
//			                 			deleteCount = JDBCUtils.deleteByBranchAndBizDate(conn, schemeInfo.getDestination(),
//			     						        branchScheme.getBranchMaster().getBranchCode(), (String) null);
//				                        LogUtils.printLog(logger, "{} {} {} schemeInfo{}  delete table :{} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), deleteCount);

				                        rs += processToStagingTable(branchScheme, schemeInfo, bizDate, in, conn,  defaultTransactionBatchSize);
				                        LogUtils.printLog(logger, "{} {} {} schemeInfo{} insert records: {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), rs);
			                        	
			                        	// Update Status to 'P'
//			                        	int updateRow = JDBCUtils.updatePendingStatusByBranchCode(conn, schemeInfo.getDestination(), branchCode);
//				                        LogUtils.printLog(logger, "{} {} {} schemeInfo{} update status: {} ", branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), updateRow);
			                		}
//		                    	    fileList.add(file);
		                    	    if (!fileListMap.containsKey(file.getCanonicalPath()))
		                    	    {
		                    	    	fileListMap.put(file.getCanonicalPath(), file);
		                    	    }
		                		}
		                		catch (SQLException | IOException e)
		                		{
			                         LogUtils.printException(logger, "schemeInfo Branch : File IO / SQL Exception Error ",e);

			                         LogUtils.printLog(logger, "{} {} {} schemeInfo{} Branch : File IO / SQL Exception Error{} ",
			                        		 branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), filename);
			                         taskJobLogService.createJobExceptionDetail(taskJobLog, schemeInfo.getSource(), schemeInfo.getDestination(), e);
			                         break;
		                		}
		                		finally
		                		{
		                			in.close();
		                		}
					                		
                         } else {
	                         LogUtils.printLog(logger, "{} {} {} schemeInfo{} File {} not exists ",
	                        		 branchCode, clientType, pollSchemeType, schemeInfo.getDestination(), filename);
	                         taskJobLogService.createJobExceptionDetail(taskJobLog, schemeInfo.getSource(), schemeInfo.getDestination(), new FileNotFoundException(filename + " not exists"));
	                         isError = true ;
//	                         break;
                         }
	                     
	             		if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
	            		{
	             			break;
	            		}
	                     
                     }
                } catch (Exception e) {
                    LogUtils.printException(logger, MessageFormat.format("{0}->{1} process excepiton",
                            schemeInfo.getSource(), schemeInfo.getDestination()), e);
                    taskJobLogService.createJobExceptionDetail(taskJobLog, schemeInfo.getSource(), schemeInfo.getDestination(), e);
                    isError = true ;
//                    break;
                }
            	taskJobLogService.createJobLogDetail(taskJobLog, schemeInfo.getSource(), schemeInfo.getDestination(), deleteCount, rs, 0);
            	totalCount +=rs ;

    	    }
        } catch(Exception e) {
        	LogUtils.printException(logger, "get connection is null");
        	taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
        }
        finally
        {
    		if (textFileReadMode.equals(FILE_READ_MODE_LOCAL))
    		{
    			File errorFile = new File(tempBranchDir, "ERROR.TXT") ;
    	        if (isError)
    	        {
    	        	if (!errorFile.exists())
    	        	{
    	        		try {
    						errorFile.createNewFile();
    					} catch (IOException e) {
    		    			LogUtils.printException("{"+branchCode+"} Branch Create Error File Error " + errorFile.getAbsolutePath(), e);
    					}
    	        	}
    	        }
    	        else
    	        {
    	        	if (errorFile.exists())
    	        	{
    					errorFile.delete();
    	        	}
    	        }
            }
        }
	       
        // Remove the file to archive Directory
        LogUtils.printLog(logger, "{} {} {} schemeInfo{} enableArchive {} textFileReadMode {} isError {} ",
       		 branchCode, clientType, pollSchemeType, enableArchive, textFileReadMode, isError);
        
        
		for (File file : zipFileListMap.values())
		{
			file.delete();
		}
		for (File tempBranchDateDir : tempBranchDateDirMap.values())
		{
			if (tempBranchDateDir.isDirectory() && tempBranchDateDir.list().length ==0)
			{
				tempBranchDateDir.delete();
			}
		}
		
        if (enableArchive)
        {
			if (!textFileReadMode.equals(FILE_READ_MODE_LOCAL))
			{
	        	try
	        	{
	        		smbService.archiveDataFile(branchCode, fileListMap.values(), salesFileArchivePath) ;
	   			} catch (IOException e) {
					taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
	   			}
			}
			else
			{
			    for (SmbFile file : fileListMap.values())
			    {
	    		   try 
	    		   {
	   				file.delete();
	   			   } 
	    		   catch (SmbException e) 
	    		   {
	    			   LogUtils.printException("{"+branchCode+"} Branch Remove File Error " + file.getName(), e);
	    			   taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
	  			   }
			    }
			}
        }
        else
        {
			if (textFileReadMode.equals(FILE_READ_MODE_LOCAL))
			{
				if (!keepLocalFile)
				{
					for (File file : localFileListMap.values())
					{
						file.delete();
					}
				}
			}
        }

        LogUtils.printLog(logger, "{} {} {} {} {}", branchCode, clientType, pollSchemeType, branchScheme.getDirection(), branchScheme.getPollSchemeName());

        if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType)) {
            
        	if (totalCount <= 0)
        	{
        		branchScheme.getTaskLog().setErrorMsg("No data in Configuration Path");
        		return null;
        	}
        	else
        	{
        		return new ArrayList<Date>();
        	}
        }
		
		
        return procDates;

	
	}
	
//	
//	private SmbFile getRootDirectory(String branchCode, BranchInfo branchInfo)
//	{
//		SmbFile directory = null ;
//
//		String user = branchInfo.getUser() ;
//		String password = branchInfo.getPassword() ;
////		LogUtils.printLog(" {} Branch : bdebug2 {}-{}-{}-{}", branchCode,user,password );
////
//		try {
//			password = securityConfig.decrypt(password);
//		} catch (Exception e) {
//			LogUtils.printException("aesDecrypt error in SalesServiceFileImpl's getRootDirectory", e);
//		}
//		try {
//			user = securityConfig.decrypt(user);
//		} catch (Exception e) {
//			LogUtils.printException("aesDecrypt error in SalesServiceFileImpl's getRootDirectory", e);
//		}
//		
//		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, user, password);
//	
//		String brachDirName =  "smb://"+branchInfo.getClientHost() +
//				'/' +
//				branchInfo.getClientDB() +
//				'/';
////		
////		
////		LogUtils.printLog(" {} Branch : bdebug {}-{}-{}-{}", branchCode, brachDirName,user,password );
//
//		try {
//			
//			directory = new SmbFile(brachDirName, auth);
//			
//    	    if(!directory.exists()){
//        		LogUtils.printLog(" {} Branch : brachDirname not exists {} ", branchCode, brachDirName);
//    	    	return null ;
//    	    }
//		} catch (MalformedURLException | SmbException e1) {
//    		LogUtils.printLog(" {} Branch : brachDirname connet error {} ", branchCode, brachDirName);
//        	LogUtils.printException("{"+branchCode+"} Branch Archive File Error " + brachDirName ,e1);
//	    	return null ;
//	    }
//		
//		return directory ;
//	}
//
//	private void archiveDataFile(String branchCode, List<SmbFile> fileList, TaskJobLog taskJobLog, Logger logger)
//	{
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String dateSuffix = sdf.format(DateUtil.getCurrentUtilDate());
//		byte[] bs= new byte[1024];
//		File archiveDirectory =  new File(salesFileArchivePath, branchCode);  
//		if (!archiveDirectory.exists())
//		{
//			archiveDirectory.mkdirs();
//		}
//
//	    for (SmbFile file : fileList)
//	    {
//	    	String filename =file.getName()+"."+dateSuffix ;
//    		   try (FileOutputStream f = new FileOutputStream( new File (archiveDirectory, filename)))
//    		   {
//	    		   InputStream in = file.getInputStream();
//	    		   int i = in.read(bs);
//	    		   while (i > 0)
//	    		   {
//	    			   f.write(bs, 0, i);
//	    			   i = in.read(bs);
//	    		   }
//	    		   in.close();
//	   			} catch (IOException e) {
//	            	LogUtils.printException(logger, "{} Branch Archive File Error " + filename ,e);
//	            	taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
//					break;
//				}
//    		   try 
//    		   {
//   				file.delete();
//   			   } catch (SmbException e) {
//   				LogUtils.printException(logger, "{} Branch Remove File Error", file.getName(), e);
//   				taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
//   			}
//
//        }
//
//	}

}
