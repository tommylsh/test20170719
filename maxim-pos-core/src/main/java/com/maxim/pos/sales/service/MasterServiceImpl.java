package com.maxim.pos.sales.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.support.nativejdbc.C3P0NativeJdbcExtractor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.LatestJobInd;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.TaskProcessStatus;
import com.maxim.pos.common.persistence.TaskJobLogDao;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.service.ProcessStgToPosService;
import com.maxim.pos.common.service.SpringBeanUtil;
import com.maxim.pos.common.service.TaskJobLogService;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.JavaDBFUtils;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.PollThreadPoolExecutor;
import com.maxim.pos.common.util.PosClientUtils;
import com.maxim.pos.common.util.SQLStmtUtils;
import com.maxim.pos.common.util.ZipUtils;
import com.maxim.util.CsvWriter;

import javassist.NotFoundException;
import jcifs.smb.SmbFile;

@Service("masterService")
//@Transactional
public class MasterServiceImpl implements MasterService {

    private static PollThreadPoolExecutor pool;
    private static Integer defaultTransactionBatchSize = 1000;
    private static final String BRANCH_EXECUTE_THREADPOOL_SIZE = "BRANCH_EXECUTE_THREADPOOL_SIZE";
    private static final String DEFAULT_TRANSACTION_BATCH_SIZE = "DEFAULT_TRANSACTION_BATCH_SIZE";

    @Autowired
    private PollSchemeInfoService pollSchemeInfoService;
    
	@Autowired
	private ApplicationContext appContext;
	
//    @Autowired
//    private DataSource dataSource;

//    @Autowired
//    private FtpService ftpService;

    @Autowired
    private TaskJobLogService taskJobLogService;

    @Autowired
    private TaskJobLogDao taskJobLogDao;

	@Autowired
	private ApplicationSettingService applicationSettingService;
	
	@Autowired
	private PollBranchSchemeService pollBranchSchemeService;
	
	@Autowired
	private SmbServiceImpl smbService;
	
	
	protected @Value("${master.folder.configrationFile}") String masterFolderConfigrationFile = null;

	protected Map<String, BranchInfo> sourceFolderMap;
	
	protected Map<String, String> sourceFileFilterMap;
//	
//    @Autowired
//    private SchemeScheduleJobService schemeScheduleJobService;
//    
//    @Autowired
//    private SchemeJobLogDao schemeJobLogDao;
    
    @SuppressWarnings("unchecked")
	@PostConstruct
    public void init() throws Exception {
        ApplicationSetting applicationSetting = applicationSettingService
                .findApplicationSettingByCode(BRANCH_EXECUTE_THREADPOOL_SIZE);
        int poolSize = 100;
        if (applicationSetting != null) {
            String pool = applicationSetting.getCodeValue();
            try {
                poolSize = Integer.parseInt(pool);
            } catch (Exception e) {
                LogUtils.printException("SalesServiceImpl init...", e);
            }
        }
        pool =  new PollThreadPoolExecutor(poolSize, poolSize, 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        
        // batch size init
	    ApplicationSetting batchSizeSetting = applicationSettingService
	                .findApplicationSettingByCode(DEFAULT_TRANSACTION_BATCH_SIZE);
	    if(batchSizeSetting != null){
	        defaultTransactionBatchSize = Integer.valueOf(batchSizeSetting.getCodeValue());
        }
	    
		sourceFolderMap = (Map<String, BranchInfo>) appContext.getBean("masterFolderConfiguration");
		sourceFileFilterMap = (Map<String, String>) appContext.getBean("masterFolderFilter");
		try{
			
		    if (masterFolderConfigrationFile != null)
		    {
	            DefaultResourceLoader loader = new DefaultResourceLoader();

	            Resource localResource = loader.getResource(masterFolderConfigrationFile);
	            if (localResource.exists())
	            {
			    	FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
			    			new String[] {masterFolderConfigrationFile}, appContext);
			    	sourceFolderMap  = (Map<String, BranchInfo>) ctx.getBean("masterFolderConfiguration");
			    	sourceFileFilterMap  = (Map<String, String>) ctx.getBean("masterFolderFilter");
			    	ctx.close();
	            }
		    }
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		LogUtils.printLog("MAP {}", sourceFolderMap);

    }
    
    @Override
    public String processStagingToPos(BranchScheme branchScheme, Logger logger) {
        ClientType clientType = branchScheme.getBranchInfo().getClientType();

        String result = null;
//        TaskJobLog taskLog = createTaskJobLog(branchScheme);
        SchemeJobLog schemeJobLog = branchScheme.getSchemeJobLog();
        TaskJobLog taskLog = taskJobLogService.acquireTaskJobLog(branchScheme, schemeJobLog);
		if (taskLog == null) {
			LogUtils.printLog(logger, "BranchScheme {}=={} PROGRESS ING...", branchScheme.getId(),
					branchScheme.getBranchMaster().getBranchCode());
			return "";
		}
		
		taskLog = taskJobLogService.startTaskJobLog(branchScheme,taskLog);

		try {
	        switch (clientType) {
            case SQLPOS:
            case SQLSERVER:
				result = stagingSQLToPos(branchScheme, taskLog, logger);
	            break;
	        case ORACLE:
	            break;
	        case CSV:
	        case TEXT:
				result = writeFileByClienType(branchScheme, taskLog, logger);
	            break;
	        case DBF:
	        	result = writerDBFFile(branchScheme, taskLog, logger);
	        	break;
	        case FOLDER_COPY:
	        	result = writerDBFFile(branchScheme, taskLog, logger);
	        	break;
	        default:
	            LogUtils.printLog("Unsupported Client Type");
	            taskJobLogService.createJobExceptionDetail(taskLog, "", "", new RuntimeException());
	            break;
	        }        
       } catch (Exception e) {
    	    e.printStackTrace();
    	    taskJobLogService.createJobExceptionDetail(taskLog, "", "", e);
            if (e instanceof RuntimeException){
            	throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
       }
       finally
       {
    	   updateTaskJobLog(taskLog, result != null);
       }

        return result;
    }
    
    public boolean assertMonitoring(BranchScheme branchScheme){
        boolean bl = false;
//        BranchInfo branchInfo = branchScheme.getBranchInfo();
        String branchCode = branchScheme.getBranchMaster().getBranchCode();
        try (Connection fromDS = applicationSettingService.getJDBCConection(branchScheme,true)){
            String query = "select * from MASTER_UPDATE_INFO where status = '' and branch_code = '"+ branchCode+"'";
            List<Map<String,Object>> list = PosClientUtils.execCliectQuery(fromDS,query,true);
            if(list.size()>0 && !list.isEmpty()) {
                bl = true;
            }
        } catch(Exception e) {
        	LogUtils.printException("get connection is null",e);
        }

        return bl;
    }

    public boolean updateStatus(BranchScheme branchScheme) {
        boolean bl = false;
//        BranchInfo branchInfo = branchScheme.getBranchInfo();
        String branchCode = branchScheme.getBranchMaster().getBranchCode();
        try (Connection fromDS = applicationSettingService.getJDBCConection(branchScheme,true)){
            String query = "update MASTER_UPDATE_INFO set status = 'C' where status = '' and branch_code = '"+ branchCode+"'";
            bl = PosClientUtils.updateTable(fromDS,query) > 0 ? true : false ;
        } catch(Exception e) {
        	LogUtils.printException("get connection is null or sql execute fail",e);
        }

        return bl;
    }

	@Override
    public boolean processMasterServerToStaging(BranchScheme branchScheme, SchemeJobLog schemeJobLog, Logger logger){

//      TaskJobLog taskLog = createTaskJobLog(branchScheme);
      TaskJobLog taskLog = taskJobLogService.acquireTaskJobLog(branchScheme, schemeJobLog);
		if (taskLog == null) {
			LogUtils.printLog(logger, "BranchScheme {}=={} PROGRESS ING...", branchScheme.getId(),
					branchScheme.getBranchMaster().getBranchCode());
			return false ;
		}     

		doProcessMasterServerToStaging(branchScheme, schemeJobLog, taskLog, logger);
		return true; 
//		if(branchScheme.isReRun() || assertMonitoring(branchScheme)){
//			boolean result = doProcessMasterServerToStaging(branchScheme, schemeJobLog, taskLog, logger);
//			if (result) {
//				updateStatus(branchScheme);	
//				return true ;
//			}
//		} else {
//			logger.error("current BranchScheme:" + branchScheme +" don't have trigger event");
//	    	updateTaskJobLog(taskLog, false);
//		}
//
//		return false; 
	}
	
    public boolean doProcessMasterServerToStaging(BranchScheme branchScheme, SchemeJobLog schemeJobLog, TaskJobLog taskLog, Logger logger){
		
////        TaskJobLog taskLog = createTaskJobLog(branchScheme);
//        TaskJobLog taskLog = taskJobLogService.acquireTaskJobLog(branchScheme, schemeJobLog);
//		if (taskLog == null) {
//			LogUtils.printLog(logger, "BranchScheme {}=={} PROGRESS ING...", branchScheme.getId(),
//					branchScheme.getBranchMaster().getBranchCode());
//			return false ;
//		}     
        
		taskLog = taskJobLogService.startTaskJobLog(branchScheme,taskLog);
        boolean result = false ;

    	try {
			 result = masterSQLToStaging(branchScheme, taskLog, logger);
             LogUtils.printLog(logger, "MST_TO_STG SUCCESS : {}", result);
//             updateTaskJobLog(taskLog);
        } catch (Exception e) {
    	    e.printStackTrace();
//            createJobExceptionDetail(taskLog, "", "", e);
            taskJobLogService.createJobExceptionDetail(taskLog, "", "", e);
            if (e instanceof RuntimeException){
            	throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
       }
       finally
       {
    	   updateTaskJobLog(taskLog, result);
       }

       if(branchScheme.isReRun() && branchScheme.getDirection()==Direction.MST_TO_STG){
		   return result;
	   }


        ProcessStgToPosService processStgToPosService = SpringBeanUtil.context.getBean(ProcessStgToPosService.class);
       // 这里需要获得需要处理的 Branchscheme
        BranchScheme branchSchemeToPos = pollBranchSchemeService.getBranchScheme(branchScheme.getPollSchemeType(),
				Direction.STG_TO_POS, null, branchScheme.getBranchMaster().getBranchCode());
        if(branchSchemeToPos==null){
            LogUtils.printLog(logger,"Master to POS Branch Scheme not found  scheme type:{},Direction={},ClientType={},BranchCode={}"
                    ,branchScheme.getPollSchemeType(),
                    Direction.STG_TO_POS,
                    null,
                    branchScheme.getBranchMaster().getBranchCode());
            return false;
        }

        if(!branchSchemeToPos.isEnabled()){
            LogUtils.printLog(logger,"Branch Scheme not Enable  scheme type:{},Direction={},ClientType={},BranchCode={}"
                    ,branchSchemeToPos.getPollSchemeType(),
                    Direction.STG_TO_POS,
                   	null,
                    branchSchemeToPos.getBranchMaster().getBranchCode());
            return false;
        }

        if ( org.apache.commons.lang.StringUtils.
				startsWith(branchSchemeToPos.getBranchInfo().getClientType().name(),"SQLPOS")) {
        	branchSchemeToPos = pollBranchSchemeService.getBranchScheme(branchScheme.getPollSchemeType(),
    				Direction.STG_TO_POS,branchSchemeToPos.getBranchInfo().getClientType(),
					branchScheme.getBranchMaster().getBranchCode());
        } else if (ClientType.CSV.equals(branchSchemeToPos.getBranchInfo().getClientType())) {
        	branchSchemeToPos = pollBranchSchemeService.getBranchScheme(branchScheme.getPollSchemeType(),
    				Direction.STG_TO_POS,ClientType.CSV, branchScheme.getBranchMaster().getBranchCode());
        } else if (ClientType.DBF.equals(branchSchemeToPos.getBranchInfo().getClientType())) {
        	branchSchemeToPos = pollBranchSchemeService.getBranchScheme(branchScheme.getPollSchemeType(),
    				Direction.STG_TO_POS,ClientType.DBF, branchScheme.getBranchMaster().getBranchCode());
        }
        
        branchSchemeToPos.setSchemeScheduleJob(branchScheme.getSchemeScheduleJob());
        branchSchemeToPos.setSchemeJobLog(schemeJobLog);
        branchSchemeToPos.setDependOnTaskLog(taskLog);

        processStgToPosService.setBranchScheme(branchSchemeToPos);
        processStgToPosService.setLogger(logger);
//        processStgToPosService.run();
        pool.execute(processStgToPosService);

        return result;
    }

    private String stagingSQLToPos(BranchScheme branchScheme, TaskJobLog taskJobLog, Logger logger) throws SQLException {
        String result = null;
        // 源數據庫的信息
//        BranchInfo branchInfo = branchScheme.getBranchInfo();
        String branchCode				= branchScheme.getBranchMaster().getBranchCode();
    	PollSchemeType pollSchemeType	= branchScheme.getPollSchemeType();
//        String pollSchemeName			= branchScheme.getPollSchemeName();
//        List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfoBySchemeTypeAndClientType(pollSchemeName,
//                branchScheme.getBranchInfo().getClientType());
    	
        try
        {
        	applicationSettingService.checkConnection(branchScheme);
        }
        catch (IOException e)
        {
        	LogUtils.printException(logger, branchCode + " Connection Error !!", e);
            taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
            return result;
        }
        
        List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfoByBranchSchemeAndClientType(branchScheme, 
        		branchScheme.getBranchInfo().getClientType());

//        LogUtils.printLog(logger, "schemeInfoList.size: {}", schemeInfoList.size());
        if (schemeInfoList.size() > 0) {
	        try(Connection toDSPool = applicationSettingService.getJDBCConection(branchScheme,true);
	            	Connection fromDSPool = applicationSettingService.getCurrentJDBCConnection()
	        		
	        		) {
	        	
	    		C3P0NativeJdbcExtractor cp30NativeJdbcExtractor = new C3P0NativeJdbcExtractor();
				Connection fromDS = cp30NativeJdbcExtractor.getNativeConnection(fromDSPool);
				Connection toDS = cp30NativeJdbcExtractor.getNativeConnection(toDSPool);
	
//	            LogUtils.printLog(logger, "Ready to copy Master data from: \r\ndatasource: '{}' to \r\n datasource: '{}'", fromDS, toDS);
	            LogUtils.printLog(logger, "{} Ready to copy Master data from Staging to POS, schemeInfoList.size() : {} ", branchCode, schemeInfoList.size());
            
	        	String[] conditions = new String[]{"branch_code = '"+branchCode+"'","status <> 'C'"} ;
	        	
	            boolean isError = false ;
	        	try
	        	{
	            	toDS.setAutoCommit(false);
	            	fromDS.setAutoCommit(false);
		            for (SchemeInfo schemeInfo : schemeInfoList) {
		
		                LogUtils.printLog(logger, "schemeInfo.id: {}", schemeInfo.getId());
		                
		                // 源表與目標表
		                String fromTable = schemeInfo.getSource();
		                String toTable = schemeInfo.getDestination();
	
		                if (isError)
		                {
	                        taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, "Skipped due to error happened");
		                	continue ;
		                }
	
		                    try {
		                    	int[] returnInts = {0,0};
	//	                    	String selectSQL = MessageFormat.format("SELECT count(*) as ss FROM {0} {1}", schemeInfo.getSource(),
	//	                				SQLStmtUtils.getCriteriaString(new String[]{"branch_code = '"+branchCode+"'","status <> 'C'"}));
	//	                        List<Map<String, Object>> execCliectQuery = PosClientUtils.execCliectQuery(fromDS, selectSQL, false);
	////	                        Object obj = execCliectQuery.get(0).get("ss");
	//	                        Object obj = execCliectQuery.size() == 0 ? "0" : execCliectQuery.get(0).get("ss");
	//	                        if(Integer.parseInt(obj.toString())>0){
	        					int stagingCounnt = JDBCUtils.getTableCount(fromDS, schemeInfo.getSource(), conditions);
	        					if (stagingCounnt > 0) {
		        	                LogUtils.printLog(logger, "{} bulk copy result: {} {} start", branchCode, fromTable,toTable);
	
	//		                        if (schemeInfo.isConsistentStructure()) {
	//		                        	toDS.setAutoCommit(false);
			                        	String conversion = JDBCUtils.CONV_NONE ;
			                        	if (JDBCUtils.CONV_CHI_BRANCH_LIST.contains(branchCode))
			                        	{
			                        		conversion = JDBCUtils.CONV_TRADTION_TO_SIMPLIFIED ;
			                        	}
			                        	returnInts = JDBCUtils.jdbcBatchInsertFromResultSet(fromDS, toDS, schemeInfo
			                        			, defaultTransactionBatchSize
			                        			, conditions
			                        			, true
			                        			, true
			                        			, conversion);
			        	                LogUtils.printLog(logger, "{} bulk copy structureConsistentBulkCopy: {} {} {} {}", branchCode, fromTable,toTable, returnInts[0], returnInts[1]);
	//		                        	toDS.commit();
	//		                        	toDS.setAutoCommit(true);
			                        	
	
	//		                        } else {
	////		                        	toDS.setAutoCommit(false);
	//		                        	returnInts = JDBCUtils.bulkCopyFromSQLConn(fromDS, toDS, schemeInfo
	//		                        			, defaultTransactionBatchSize, null
	//		                        			, new String[]{"branch_code = '"+branchCode+"'","status <> 'C'"});
	//		        	                LogUtils.printLog(logger, "{} bulk copy bulkCopyFromSQLConn: {} {} {} {}", branchCode, fromTable,toTable, returnInts[0], returnInts[1]);
	////		                        	toDS.commit();
	////		                        	toDS.setAutoCommit(true);
	//		                        }
		        	                LogUtils.printLog(logger, "{} bulk copy result: {} {} {} {}", branchCode, fromTable,toTable, returnInts[0], returnInts[1]);
		                        } else {
		                        	continue;
		                        }
	                            int count = JDBCUtils.updateCompleteStatusByConditions(fromDS, fromTable, conditions);
	                            LogUtils.printLog(logger, "{} MASTER schemeInfo{} completeRecord {}", branchCode, schemeInfo.getDestination(),count);
	            				if (stagingCounnt != count)
	            				{
	                                throw new RuntimeException("Staging Count not match Before["+stagingCounnt+"] VS After["+count+"]");
	            				}           
	
	//	                        String fromSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(fromTable);
	//        	                LogUtils.printLog(logger, "{} Branch stagingSQLToPos fromSql: {}", branchCode, fromSql);
	//
	//	                        PosClientUtils.updateTable(fromDS, fromSql);
	        					
	//	                        String toSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(toTable);
	//        	                LogUtils.printLog(logger, "{} Branch stagingSQLToPos toSql: {}", branchCode, toSql);
	//	                        PosClientUtils.updateTable(toDS, toSql);
		                        taskJobLogService.createJobLogDetail(taskJobLog, fromTable, toTable, 0, returnInts);
		                        
		                        result = pollSchemeType +":" + branchScheme.getDirection() +" process success!";
		                    } catch (Exception e) {
		                        LogUtils.printException(logger, "Task execte exception:", e);
		                       
		                        result = pollSchemeType +":" + branchScheme.getDirection() +" process failed!";
		                        
	//	                        try{
	//	                            if(toDS!=null){
	//	                            	toDS.rollback();
	//	                            	toDS.setAutoCommit(true);
	//	                            }
	//	                         }catch(SQLException e1){
	//	                        	 LogUtils.printException(logger, "transaction rollback exception:", e1);
	//	                         }
		                        isError = true ;
		                        taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
		                    }
		            }
	        	}
	        	finally
	        	{
	    	        if (isError)
	    	        {
	                    try{
	                    	toDS.rollback();
	                    }catch(SQLException e1){
	                   	 LogUtils.printException(logger, "transaction rollback exception:", e1);
	                     taskJobLogService.createJobExceptionDetail(taskJobLog, "", "Rollback Error", e1);
	                    }
	                    try{
	                    	fromDS.rollback();
	                    }catch(SQLException e1){
	                   	 LogUtils.printException(logger, "transaction rollback exception:", e1);
	                     taskJobLogService.createJobExceptionDetail(taskJobLog, "Rollback Error", "", e1);
	                    }
	                }
	    	        else
	    	        {
	                    try{
	                    	toDS.commit();
	                    }catch(SQLException e1){
	                   	 LogUtils.printException(logger, "transaction commit exception:", e1);
	                     taskJobLogService.createJobExceptionDetail(taskJobLog, "", "Commit Error", e1);
	                    }	        
	                    try{
	                    	fromDS.commit();
	                    }catch(SQLException e1){
	                   	 LogUtils.printException(logger, "transaction commit exception:", e1);
	                     taskJobLogService.createJobExceptionDetail(taskJobLog, "Commit Error", "", e1);
	                    }	        
	    	        }
	            	toDS.setAutoCommit(true);
	            	fromDS.setAutoCommit(true);
	        	}
	        } catch(Exception e) {
	        	LogUtils.printException(logger, "get connection is null");
	        	taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
	        }
        } else {
            LogUtils.printLog(logger, "No Scheme info data");
        }
        return result;
    }
    
    private boolean masterSQLToStaging(BranchScheme branchScheme, TaskJobLog taskJobLog,  Logger logger) throws SQLException {
        boolean result = false;
        boolean isError = false;
        // 源數據庫的信息
//        BranchInfo branchInfo = branchScheme.getBranchInfo();
//    	PollSchemeType pollSchemeType = branchScheme.getPollSchemeType();
//        String pollSchemeName = branchScheme.getPollSchemeName();
        String branchCode = branchScheme.getBranchMaster().getBranchCode();
//      List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfoBySchemeTypeAndClientType(pollSchemeName,
//                ClientType.SQLSERVER);
      List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfoByBranchSchemeAndClientType(branchScheme, 
              ClientType.SQLSERVER);

        LogUtils.printLog(logger, "schemeInfoList.size: {}", schemeInfoList.size());
        
		String[] conditions = {"branch_code = '"+branchCode+"'","status <> 'C'"};

//        try(Connection fromDS = applicationSettingService.getJDBCConection(branchInfo,true);
        try(Connection fromDSPool = applicationSettingService.getMasterJDBCConnection();
        		Connection toDSPool = applicationSettingService.getCurrentJDBCConnection()
        		) {
    		C3P0NativeJdbcExtractor cp30NativeJdbcExtractor = new C3P0NativeJdbcExtractor();
			Connection fromDS = cp30NativeJdbcExtractor.getNativeConnection(fromDSPool);
			Connection toDS = cp30NativeJdbcExtractor.getNativeConnection(toDSPool);

        	
	        if (schemeInfoList.size() > 0) {
	        	try
	        	{
                	fromDS.setAutoCommit(false);
		            for (SchemeInfo schemeInfo : schemeInfoList) {
		
		                LogUtils.printLog(logger, "schemeInfo.id: {}", schemeInfo.getId());
		
		                // 源表與目標表
		                String fromTable = schemeInfo.getSource();
		                String toTable = schemeInfo.getDestination();
		
		                boolean completed = false ;
	                    try {
	                    	int[] returnInts = {0,0};
//	                    	String selectSQL = MessageFormat.format("SELECT count(*) as ss FROM {0} {1}", schemeInfo.getSource(),
//	                				SQLStmtUtils.getCriteriaString(new String[]{"branch_code = '"+branchScheme.getBranchMaster().getBranchCode()+"'","status <> 'C'"}));
//	                        List<Map<String, Object>> execCliectQuery = PosClientUtils.execCliectQuery(fromDS, selectSQL, false);
////	                        Object obj = execCliectQuery.get(0).get("ss");
//	                        Object obj = execCliectQuery.size() == 0 ? "0" : execCliectQuery.get(0).get("ss");
//	                        if(Integer.parseInt(obj.toString())>0){
//        					int masterCounnt = JDBCUtils.updatePendingStatusByBranchCodeAndNotCompleted(fromDS, schemeInfo.getSource(), branchCode);
        					int masterCounnt = JDBCUtils.getTableCount(fromDS, schemeInfo.getSource(), conditions);
        					if (masterCounnt > 0) {
	        	                LogUtils.printLog(logger, "{} Branch masterSQLToStaging fromTable toTable : {} {}", branchCode, fromTable, toTable);

//		                        if (schemeInfo.isConsistentStructure()) {
		                        	returnInts = JDBCUtils.structureConsistentBulkCopy(fromDS, toDS, schemeInfo
		                        			, defaultTransactionBatchSize
		                        			, conditions, JDBCUtils.CONV_NONE);
//		                        } else {
//		                        	returnInts = JDBCUtils.bulkCopyFromSQLConn(fromDS, toDS, schemeInfo 
//		                        			, null, null
//		                        			, conditions);
//		                        }
	        	                LogUtils.printLog(logger, "{} Branch masterSQLToStaging toTable : {} {} {} {}", branchCode, fromTable, toTable, returnInts[0],returnInts[1]);
	                        } else {
	                        	continue;
	                        }
//	                        String fromSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(fromTable, conditions);
//        	                LogUtils.printLog(logger, "{} fromSql  : {} ", branchCode, fromSql);
//	                        PosClientUtils.updateTable(fromDS, fromSql);
	                        taskJobLogService.createJobLogDetail(taskJobLog, fromTable, toTable, 0, returnInts);
	                        
            				if (StringUtils.isEmpty(schemeInfo.getDestCheckSumCols()))
            				{
                                int targetCounnt = returnInts[0] + returnInts[1];
//            					int targetCounnt = JDBCUtils.getTableCount(toDS, schemeInfo.getDestination(),conditions);
                				if (masterCounnt > targetCounnt)
                				{
                                    throw new RuntimeException("Count not match Master["+masterCounnt+"] VS Staging["+targetCounnt+"]");
                				}
            				}

                            int count = JDBCUtils.updateCompleteStatusByConditions(fromDS, fromTable, conditions);
                            LogUtils.printLog(logger, "{} MASTER schemeInfo{} completeRecord {}", branchCode, schemeInfo.getDestination(),count);
            				if (masterCounnt != count)
            				{
                                throw new RuntimeException("Master Count not match Before["+masterCounnt+"] VS After["+count+"]");
            				}           
            				completed = true;
	                    } catch (Exception e) {
	                    	isError = true ;
	                        LogUtils.printException(logger, "Task execte exception:", e);
	                        taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
	                    }
	                    finally
	                    {
	            	        if (completed)
	            	        {
	                            try{
	                            	fromDS.commit();
	    	                    	if (completed)
	    	                    	{
	    	                    		result = true ;
	    	                    	}
	                            }catch(SQLException e1){
	                           	 LogUtils.printException(logger, "transaction commit exception:", e1);
	                             taskJobLogService.createJobExceptionDetail(taskJobLog, "Commit Error", "", e1);
	                            }	        
	                        }
	            	        else
	            	        {
	                            try{
	                            	fromDS.rollback();
	                            }catch(SQLException e1){
	                           	 LogUtils.printException(logger, "transaction rollback exception:", e1);
	                             taskJobLogService.createJobExceptionDetail(taskJobLog, "Rollback Error", "", e1);
	                            }
	            	        }
	                    }
		            }
	            }
                finally
                {
        	        fromDS.setAutoCommit(true);
                }
	        } else {
	            LogUtils.printLog(logger, "data is null");
	        }
        } catch(Exception e) {
        	isError = true ;
        	LogUtils.printException(logger, "get connection is null");
        	taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
        }
        if (isError)
        {
        	return false ;
        }
        return result;
    }
    
	public boolean processFolderCopy(BranchScheme branchScheme, SchemeJobLog schemeJobLog,
			Logger logger)
	{
		boolean start = false ;
		TaskJobLog	taskLog = taskJobLogService.acquireTaskJobLog(branchScheme, schemeJobLog);
		if (taskLog == null) {
			LogUtils.printLog(logger, "BranchScheme {}=={} PROGRESS ING...", branchScheme.getId(),
					branchScheme.getBranchMaster().getBranchCode());
			return false ;
		}
		taskLog = taskJobLogService.startTaskJobLog(branchScheme,taskLog);

		String branchCode = branchScheme.getBranchMaster().getBranchCode() ;
		BranchInfo destincationBranchInfo = branchScheme.getBranchInfo();
		BranchInfo sourceBranchInfo = sourceFolderMap.get(branchScheme.getDirection().name());
		String fileFilter = sourceFileFilterMap.get(branchScheme.getDirection().name());
		
		SmbFile sourceDirectory = smbService.getRootDirectory(branchScheme.getBranchMaster(), sourceBranchInfo);
		if (sourceDirectory == null)
		{
            taskJobLogService.createJobExceptionDetail(taskLog, 
            		sourceBranchInfo.getClientDB(), 
            		destincationBranchInfo.getClientDB(), 
            		new IllegalArgumentException("Incorrect Source Defined"));
            return false ;
		}
		SmbFile targetDirectory = smbService.getRootDirectory(branchScheme.getBranchMaster(), destincationBranchInfo);
		if (targetDirectory == null)
		{
            taskJobLogService.createJobExceptionDetail(taskLog, 
            		sourceBranchInfo.getClientDB(), 
            		destincationBranchInfo.getClientDB(), 
            		new IllegalArgumentException("Incorrect Detincation Defined"));
            return false ;
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
			    	
			    	if (fileFilter.length() > 0)
			    	{
			    		boolean match = false ;
			    		String[] filters = StringUtils.split(fileFilter, ",");
			    		for (String filter : filters)
			    		{
			    			if (filter.length() > 0 && filename.startsWith(filter))
			    			{
			    				match = true ;
			    				break;
			    			}
			    		}
			    		if (!match)
			    		{
			    			continue;
			    		}
			    	}
			    	
			    	
			    	if (target.exists())
			    	{
			    		if (target.getContentLengthLong() == source.getContentLengthLong())
			    		{
			    			continue ;
			    		}
			    	}
			    	
			    	start = true ;
			    	
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
		            	LogUtils.printException("{"+branchCode+"} Master File Copy Error " + filename ,e);
						throw e;
					}
				}
			}
		}
		catch (Exception e)
		{
            taskJobLogService.createJobExceptionDetail(taskLog, 
            		sourceBranchInfo.getClientDB(), 
            		destincationBranchInfo.getClientDB(), 
            		e);
            return false ;
		}
		finally
		{
			updateTaskJobLog(taskLog, start);
		}
		return true;
	}


    private String writeFileByClienType(BranchScheme branchScheme, TaskJobLog taskJobLog, Logger logger) throws SQLException {
    	String result = null;
    	
    	ClientType clientType = branchScheme.getBranchInfo().getClientType();
    	PollSchemeType pollSchemeType = branchScheme.getPollSchemeType();
        String pollSchemeName = branchScheme.getPollSchemeName();
//		String pollSchemeType = branchScheme.getPollSchemeType().name()==branchScheme.getPollSchemeName()
//				? branchScheme.getPollSchemeType().name():branchScheme.getPollSchemeName();
        List<SchemeInfo> schemeInfoList = pollSchemeInfoService
                .findSchemeInfoBySchemeTypeAndClientType(pollSchemeName, clientType);

        LogUtils.printLog(logger, "schemeInfoList.size: {}", schemeInfoList.size());
        if (schemeInfoList.size() > 0) {
            try( Connection connection = applicationSettingService.getCurrentJDBCConnection())
            {
//              String tmpDir = System.getProperty("java.io.tmpdir");
            for (SchemeInfo schemeInfo : schemeInfoList) {

                LogUtils.printLog(logger, "schemeInfo.id: {}", schemeInfo.getId());

                List<SchemeTableColumn> tableColumnList = schemeInfo.getSchemeTableColumns();

                if (tableColumnList.size() > 0) {
                    List<Map<String, Object>> sourceListData = new ArrayList<Map<String, Object>>();

                    List<String> fromColumnList = new ArrayList<String>();
                    StringBuffer sqlBuffer = new StringBuffer();
                    // 源表與目標表
                    String fromTable = schemeInfo.getSource();
                    String toTable = schemeInfo.getDestination();
                    String delimiter = null;
                    if(clientType.equals(ClientType.CSV)) {
                    	delimiter = schemeInfo.getDelimiter();	
                    }

                    sqlBuffer.append("select ");

                    for (int i = 0; i < tableColumnList.size(); i++) {
                        String fromColumn = tableColumnList.get(i).getFromColumn();
                        fromColumnList.add(fromColumn);
                        if (i == tableColumnList.size() - 1) {
                            sqlBuffer.append(fromColumn);
                        } else {
                            sqlBuffer.append(fromColumn + ",");
                        }
                    }
                    sqlBuffer.append(" from " + fromTable + " where status <> 'C' and branch_code = '"+branchScheme.getBranchMaster().getBranchCode()+"'");
                    LogUtils.printLog(logger, "sql: {}", sqlBuffer.toString());
                    // 連接數據庫查詢,得到一個sourceListData
                    try {
                    	
                        sourceListData = PosClientUtils.execCliectQuery(connection, sqlBuffer.toString(), false);
                        
                        if (!sourceListData.isEmpty()) {
                        	String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
                            String filePath = branchScheme.getBranchInfo().getClientDB()+File.separator +branchScheme.getBranchMaster().getBranchCode()+File.separator+date+File.separator+ toTable + clientType.getFileExt();
                            String path = filePath.substring(0,filePath.lastIndexOf("\\"));
                            if (!new File(path).isDirectory()) {
                            	new File(path).mkdir();
                            }
                            // save file to local path
                            int returnInt = saveFile(fromColumnList, sourceListData, filePath, clientType, delimiter);
                            ZipUtils.zip(filePath, filePath.replaceAll("txt", "zip"));
    				        ZipUtils.deleteFile(filePath);
                            // upload files to ftp path
//                            boolean uploadFile = ftpService.uploadFile(branchScheme.getBranchInfo(), branchScheme.getBranchInfo().getClientDB(),
//                                    new File(filePath));

//                            LogUtils.printLog(logger, "uploadFile: {}, result: {} ", filePath, uploadFile);
                            String fromSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(fromTable);
                            PosClientUtils.updateTable(connection, fromSql);
                            taskJobLogService.createJobLogDetail(taskJobLog, fromTable, toTable, 0, returnInt);
                            
                            result = pollSchemeType +":" + branchScheme.getDirection() +" process success!";
                        }

                    } catch (Exception e) {
                        LogUtils.printException(logger, "Task execte exception:", e);
                        
                        result = pollSchemeType +":" + branchScheme.getDirection() +" process failed!";

                        taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
                    } 
                } else {
                    LogUtils.printLog(logger, "columns is empty");
                }
            }

            }
        } else {
            LogUtils.printLog(logger, "data is null");
        }
        
        return result;
    }

    private int saveFile(List<String> fromColumnList, List<Map<String, Object>> sourceListData, String fileName,
            ClientType clientType, String delimiter) throws IOException {
        if (clientType.equals(ClientType.CSV)) {
            return writeCsvFile(fromColumnList, sourceListData, fileName,delimiter);
        } else if (clientType.equals(ClientType.TEXT)) {
        	return writeTextFile(fromColumnList, sourceListData, fileName);
        } else {
            try {
    			throw new NotFoundException("clientType is not found : " + clientType);
    		} catch (NotFoundException e) {
    			throw new RuntimeException(e);
    		}
        }
    }

    private int writeTextFile(List<String> fromColumnList, List<Map<String, Object>> sourceListData, String fileName) 
    	throws IOException
    {
        BufferedWriter bw = null;
        int returnInt = 0;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), JDBCUtils.DEFAULT_CSV_ENCODING)); //UTF-16
//            for (String column : fromColumnList) {
//                bw.write(column + "\t");
//            }
//            bw.newLine();
//            for (Map<String, Object> map : sourceListData) {
//                Collection<Object> list = map.values();
//                for (Object object : list) {
//                    bw.write(object.toString() + "\t");
//                }
//                bw.newLine();
//            }
            for (Map<String, Object> map : sourceListData) {
            	++ returnInt;
                for (String column : fromColumnList) {
            		if(map.containsKey(column)){
            			bw.write(map.get(column).toString()+"\t");
            		}
                }
                bw.newLine();
			}
            
            return returnInt;
         }finally {
            try {
                bw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private int writeCsvFile(List<String> fromColumnList, List<Map<String, Object>> sourceListData, String fileName, String delimiter)
    	throws IOException
    {
    	FileOutputStream output = null;
    	CsvWriter csvWriter = null;
        int returnInt = 0;
        
        char temp = 0;
        if (delimiter.isEmpty()) {
        	temp = ",".toCharArray()[0];
        } else if (delimiter.equals("\t")) {
        	temp = '\t';
        }
        	
        try {
            output = new FileOutputStream(fileName);

//            output.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });
            csvWriter = new CsvWriter(output, temp, Charset.forName(JDBCUtils.DEFAULT_CSV_ENCODING));//JDBCUtils.DEFAULT_CSV_ENCODING
//            for (String column : fromColumnList) {
//                csvWriter.write(column);
//            }
//            csvWriter.endRecord();
        	for (Map<String, Object> map : sourceListData) {
        		++ returnInt;
                for (String column : fromColumnList) {
            		if(map.containsKey(column)){
            			csvWriter.write(map.get(column).toString());
            		}
                }
                csvWriter.endRecord();
			}
        	return returnInt;
        }
        finally{
            try {
            	csvWriter.close();
            	output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    private String writerDBFFile(BranchScheme branchScheme, TaskJobLog taskJobLog, Logger logger) throws SQLException {
    	String result = null;

    	BranchInfo branchInfo = branchScheme.getBranchInfo();
    	ClientType clientType = branchInfo.getClientType();
        String pollSchemeName = branchScheme.getPollSchemeName();
    	PollSchemeType pollSchemeType = branchScheme.getPollSchemeType();
//		String pollSchemeType = branchScheme.getPollSchemeType().name()==branchScheme.getPollSchemeName()
//				? branchScheme.getPollSchemeType().name():branchScheme.getPollSchemeName();
        String branchCode = branchScheme.getBranchMaster().getBranchCode();
        List<SchemeInfo> schemeInfoList = pollSchemeInfoService
                .findSchemeInfoBySchemeTypeAndClientType(pollSchemeName, clientType);

        LogUtils.printLog(logger, "schemeInfoList.size: {}", schemeInfoList.size());

        if (schemeInfoList.size() > 0) {
//            String tmpDir = System.getProperty("java.io.tmpdir");
           try( Connection fromDS = applicationSettingService.getCurrentJDBCConnection())
           {
            for (SchemeInfo schemeInfo : schemeInfoList) {

                LogUtils.printLog(logger, "schemeInfo.id: {}", schemeInfo.getId());

                List<SchemeTableColumn> tableColumnList = schemeInfo.getSchemeTableColumns();

                if (tableColumnList.size() > 0) {

                    // 源表與目標表
                    String fromTable = schemeInfo.getSource();
                    String toTable = schemeInfo.getDestination();

                    // 連接數據庫查詢,得到一個sourceListData
                    try {
//                        String filePath = JavaDBFUtils.getFilePathByScheme(branchScheme.getBranchInfo().getClientDB()+File.separator, branchScheme.getBranchMaster().getBranchCode(), fromTable, new Date());
//                        String subFilePath = filePath.substring(0,filePath.lastIndexOf("\\"));
//                        File file = new File(subFilePath); 
//                        if(!file.exists()){
//                        	file.mkdirs();
//                        }
                    	
                		String branchDirName =  branchInfo.getClientDB() +
            					File.separator +
            					branchCode +
            					File.separator ;
                		
                	    File directory = new File(branchDirName);
                	    if(!directory.exists()){
                	    	directory.mkdirs();
                	    }

                        String filePath = branchDirName + File.separator + toTable  ;
                        if (!filePath.toLowerCase().endsWith(".dbf"))
                        {
                        	filePath += ".dbf";
                        }

                        // save file to local path
						int returnInt = JavaDBFUtils.bulkCopyFromSQLToDBF(fromDS, filePath, fromTable, toTable, tableColumnList, null, null);
						ZipUtils.zip(filePath, filePath.replaceAll("DBF", "zip"), ZipUtils.CHINESE_CHARSET_BIG5);
				        ZipUtils.deleteFile(filePath);
						
						// upload files to ftp path
//                        boolean uploadFile = ftpService.uploadFile(branchScheme.getBranchInfo(), branchScheme.getBranchInfo().getClientDB(),
//                                new File(filePath));
                        String fromSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(fromTable);
                        PosClientUtils.updateTable(fromDS, fromSql);
//                        LogUtils.printLog(logger, "uploadFile: {}, result: {} ", filePath, uploadFile);

                        taskJobLogService.createJobLogDetail(taskJobLog, fromTable, toTable, 0, returnInt);
                        
                        result = pollSchemeType +":" + branchScheme.getDirection() +" process success!";

                    } catch (SQLException e) {
                        LogUtils.printException(logger, "Task execte exception:", e);

                        result = pollSchemeType +":" + branchScheme.getDirection() +" process failed!";
                        
                        taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
                    } catch (IOException e) {
                    	 LogUtils.printException(logger, "bulkCopyFromSQLToDBF execte exception:", e);
                    	 taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
					}
                } else {
                    LogUtils.printLog(logger, "columns is empty");
                }
            }
           }
           
        } else {
            LogUtils.printLog(logger, "data is null");
        }
        return result;
	}
    
//    private void updateTaskJobLog(TaskJobLog taskJobLog) {
//        if (TaskProcessStatus.PROGRESS.equals(taskJobLog.getStatus())) {
//            taskJobLog.setStatus(TaskProcessStatus.COMPLETE);
//        }
//        taskJobLog.setEndTime(new Date());
//        taskJobLogService.addOrUpdateTaskJobLog(taskJobLog);
//    }
    
    private void updateTaskJobLog(TaskJobLog taskJobLog, boolean isComplete) 
    {
        if (TaskProcessStatus.FAILED.equals(taskJobLog.getStatus())) {
        	TaskJobLog lastTaskJobLog = taskJobLog.getLastTaskJobLog();
        	if (lastTaskJobLog != null)
        	{
        		if (!LatestJobInd.N.equals(lastTaskJobLog.getLastestJobInd()))
        		{
	    			lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
	    			taskJobLogDao.save(lastTaskJobLog);
        		}
        	}	
        	taskJobLog.setLastestJobInd(LatestJobInd.Y);
        }
        else
        {
	        if (isComplete)
	        {
	            if (TaskProcessStatus.PROGRESS.equals(taskJobLog.getStatus())) {
	                taskJobLog.setStatus(TaskProcessStatus.COMPLETE);
	            }
	        }
	        else
	        {
	        	TaskJobLog lastTaskJobLog = taskJobLog.getLastTaskJobLog();
	        	if (lastTaskJobLog != null)
	        	{
	        		if (!LatestJobInd.Y.equals(lastTaskJobLog.getLastestJobInd()))
	        		{
		    			lastTaskJobLog.setLastestJobInd(LatestJobInd.Y);
		    			taskJobLogDao.save(lastTaskJobLog);
	        		}
	        	}
	            taskJobLog.setStatus(TaskProcessStatus.NONE);
	        	taskJobLog.setLastestJobInd(LatestJobInd.N);
	        }
        }
        
//        if (TaskProcessStatus.PROGRESS.equals(taskJobLog.getStatus())) {
//            if (isComplete)
//                taskJobLog.setStatus(TaskProcessStatus.COMPLETE);
//            else
//                taskJobLog.setStatus(TaskProcessStatus.NONE);
//        }
        taskJobLog.setEndTime(new Date());
        taskJobLogService.addOrUpdateTaskJobLog(taskJobLog);

    }


//    private void createJobExceptionDetail(TaskJobLog taskJobLog, String fromTable, String toTable, Exception e) {
//    	taskJobLog.setStatus(TaskProcessStatus.FAILED);
//        TaskJobExceptionDetail taskJobExceptionDetail = new TaskJobExceptionDetail();
//        taskJobExceptionDetail.setSource(fromTable);
//        taskJobExceptionDetail.setDestination(toTable);
//        taskJobExceptionDetail.setExceptionContent(LogUtils.getStackTrace(e));
//        taskJobExceptionDetail.setSeverity(2);
//        taskJobExceptionDetail.setStatus(ExceptionDetailStatus.P);        
//        taskJobExceptionDetail.setTaskJobLog(taskJobLog);
//        Auditer.audit(taskJobExceptionDetail);
//
//        taskJobExceptionDetail.setSeverity(2);
//        taskJobExceptionDetail.setStatus(ExceptionDetailStatus.P);
//        taskJobLogService.addOrUpdateTaskJobExceptionDetail(taskJobExceptionDetail);
//        Auditer.audit(taskJobLog);
//        taskJobLogService.addOrUpdateTaskJobLog(taskJobLog);
//
//
////        if (taskJobLog.getTaskJobExceptionDetails() == null) {
////            taskJobLog.setTaskJobExceptionDetails(new TreeSet<TaskJobExceptionDetail>());
////        }
////
////        taskJobLog.getTaskJobExceptionDetails().add(taskJobExceptionDetail);
//    }

//    /**
//     * 
//     * @param taskJobLog
//     * @param fromTable
//     * @param toTable
//     * @param row
//     * @param returnInts (must be an array length > 1 and <= 2)
//     */
//    private void createJobLogDetail(TaskJobLog taskJobLog, String fromTable, String toTable, int row, int...returnInts) {
//    	TaskJobLogDetail taskJobLogDetail = new TaskJobLogDetail();
//		taskJobLogDetail.setTaskJobLog(taskJobLog);
//		taskJobLogDetail.setSource(fromTable);
//		taskJobLogDetail.setDestination(toTable);
//		taskJobLogDetail.setNumOfRecDelete(row);
//        taskJobLogDetail.setNumOfRecProcessed(IntStream.of(returnInts).sum());
//        if(returnInts.length > 1){
//	        taskJobLogDetail.setNumOfRecInsert(returnInts[0]);
//	        taskJobLogDetail.setNumOfRecUpdate(returnInts[1]);
//        }
//        else{
//	        taskJobLogDetail.setNumOfRecInsert(returnInts[0]);
//	        taskJobLogDetail.setNumOfRecUpdate(0);
//        }
//		Auditer.audit(taskJobLogDetail);
//
////		if (taskJobLog.getTaskJobLogDetails() == null) {
////			taskJobLog.setTaskJobLogDetails(new TreeSet<TaskJobLogDetail>());
////		}
//		
//        if (taskJobLog.getTaskJobLogDetails() == null) {
//            taskJobLog.setTaskJobLogDetails(new TreeSet<TaskJobLogDetail>());
//        }
//
//		taskJobLogService.addOrUpdateTaskJobLogDetail(taskJobLogDetail);
//		// taskJobLog.getTaskJobLogDetails().add(taskJobLogDetail);
//    }
//
//    private TaskJobLog createTaskJobLog(BranchScheme branchScheme) {
//    	TaskJobLog taskJobLog = taskJobLogService.findLatestTaskJobLog(branchScheme);
//		if (taskJobLog != null) {
//			if (taskJobLog.getStatus() == TaskProcessStatus.PROGRESS) {
//				if(System.currentTimeMillis() - taskJobLog.getLastUpdateTime().getTime() > 3600000){
//					LogUtils.printLog("{} branch code process continue 1 hour ,auto update status  to failed ",
//							branchScheme.getBranchMaster().getBranchCode());
//					taskJobLog.setStatus(TaskProcessStatus.FAILED);
//				} else {
//					return null;
//				}
//			}
//			taskJobLog.setLastestJobInd(LatestJobInd.N);
//			Auditer.audit(taskJobLog);
//			taskJobLogService.addOrUpdateTaskJobLog(taskJobLog);
//		}
//
//		TaskJobLog taskLog = new TaskJobLog();
//		taskLog.setLastestJobInd(LatestJobInd.Y);
//		taskLog.setStatus(TaskProcessStatus.PROGRESS);
//		taskLog.setStartTime(new Date());
//		Auditer.audit(taskLog);
//		taskLog.setSchemeScheduleJob(branchScheme.getSchemeScheduleJob());
//		taskLog.setPollSchemeID(branchScheme.getId());
//		taskLog.setDirection(branchScheme.getDirection());
//		taskLog.setPollSchemeType(branchScheme.getPollSchemeType());
//
//        taskLog.setPollSchemeName(branchScheme.getPollSchemeName());
//
//        taskLog.setBranchCode(branchScheme.getBranchMaster().getBranchCode());
//        taskLog.setPollBranchId(branchScheme.getBranchInfo().getId());
//
////        Long scheduleJobId = schemeScheduleJob.getId();
////        SchemeJobLog SchemeJobLog = schemeJobLogDao.findLatestSchemeJobLog(scheduleJobId);
////        taskLog.setSchemeJobLog(SchemeJobLog);
//		taskLog = taskJobLogService.addOrUpdateTaskJobLog(taskLog);
//
//		return taskLog;
//    }

}
