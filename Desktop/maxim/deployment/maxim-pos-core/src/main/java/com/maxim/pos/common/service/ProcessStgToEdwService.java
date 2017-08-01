package com.maxim.pos.common.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.PosClientUtils;
import com.maxim.pos.common.util.SQLStmtUtils;

@Service("processStgToEdwService")
@Scope("prototype")
public class ProcessStgToEdwService 
//implements Runnable {
{
	public static final String TO_EDW_CHANNEL_JDBC = "JDBC";
	public static final String TO_EDW_CHANNEL_WEBSERVICE = "WEBSERVICE";
//	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessStgToEdwService.class);
	@Autowired
	private PollSchemeInfoService pollSchemeInfoService;
	@Autowired
	private ApplicationSettingService applicationSettingService;

	@Autowired
	private TaskJobLogService taskJobLogService;

//	@Autowired
//	private PollEodControlService pollEodControlService;
	
	private BranchScheme branchScheme;
	private TaskJobLog taskJobLog;
	private Logger logger;
	private int defaultTransactionBatchSize ;
	private boolean enableBranchCodeMapping = false ;


//    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	public String processStgToEdwJDBC(java.sql.Date controlDate, List<Date> prcDates) {
		
		if (branchScheme == null || taskJobLog == null) {
			LogUtils.printLog(logger,"branchScheme or taskJobLog is must ... return");
			return null;
		}
		String result = null;
		
		List<String> prcDatesStr = new ArrayList<String>(prcDates.size());

		// Poll Branch Scheme should belong to ORACLE client type
		String branchCode				= branchScheme.getBranchMaster().getBranchCode();
		String mappingBranchCode		= branchScheme.getBranchMaster().getMappingBranchCode();
		PollSchemeType pollSchemeType	= branchScheme.getPollSchemeType();
//        String pollSchemeName 			= branchScheme.getPollSchemeName();
//		String pollSchemeType = branchScheme.getPollSchemeType().name()==branchScheme.getPollSchemeName()
//				? branchScheme.getPollSchemeType().name():branchScheme.getPollSchemeName();
		ClientType clientType			= branchScheme.getBranchInfo().getClientType() ;
//		List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfoBySchemeTypeAndClientType(pollSchemeName,
//				ClientType.ORACLE);
        List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfoByBranchSchemeAndClientType(branchScheme, ClientType.ORACLE);
		
		LogUtils.printLog(logger, "{} {} processStgToEdwJDBC schemeInfoList.size: {}", branchCode, pollSchemeType, schemeInfoList.size());
		
		try {
			try (Connection fromConn = applicationSettingService.getCurrentJDBCConnection();
					Connection toConn =  applicationSettingService.getEdwJDBCConnection()) {
				
//	            LogUtils.printLog(logger, "{} {} Ready to copy data from: \r\ndatasource: '{}' to \r\n datasource: '{}'", 
//	            		branchCode, pollSchemeType, fromConn, toConn);
	            Map<String, String> branchCodeMap = null ;
	            if (enableBranchCodeMapping) {
	            	branchCodeMap = new HashMap<String, String>();
		            branchCodeMap.put(branchCode, mappingBranchCode);
	            }
	            else
	            {
	            	mappingBranchCode = branchCode ;
	            }

				boolean flag = false;
				boolean isError = false;
				if (schemeInfoList.size() > 0) {
	            	StringBuffer dateStrBuf = new StringBuffer("(");
	            	StringBuffer oracleDateStrBuf = new StringBuffer("(");
	        		String[] conditions= null;
	        		String[] oracleConditions= null;
					if(PollSchemeType.SALES_EOD.equals(branchScheme.getPollSchemeType())){
						
		            	int idx = 0;
		            	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		            	for (Date date : prcDates)
		            	{
		            		String dateStr = df.format(date) ;
		            		dateStrBuf.append("'").append(dateStr);
		            		oracleDateStrBuf.append("to_date('").append(dateStr);
		            		if (++idx == prcDates.size())
		            		{
		            			dateStrBuf.append("')");
		            			oracleDateStrBuf.append("','YYYY-MM-DD'))");
		            		}
		            		else
		            		{
		            			dateStrBuf.append("',");
		            			oracleDateStrBuf.append("','YYYY-MM-DD'),");
		            		}
		            		
		            		prcDatesStr.add(dateStr);
		            	}
		            	
		            	
		            	StringBuffer dateConditionStrBuf = new StringBuffer("CONVERT(date,business_date) in ").append(dateStrBuf);
		            	String dateConditionStr = dateConditionStrBuf.toString();
		            	StringBuffer oracleDateConditionStrBuf = new StringBuffer("business_date in ").append(oracleDateStrBuf);
		            	String oracleDateConditionStr = oracleDateConditionStrBuf.toString();
//		            	String dateStr = dateStrBuf.toString();

		        		conditions= new String[] { dateConditionStr, " branch_code  = \'" + branchScheme.getBranchMaster().getBranchCode() + "\'"} ;
		        		oracleConditions= new String[] { oracleDateConditionStr, " branch_code  = \'" + branchScheme.getBranchMaster().getBranchCode() + "\'"} ;
					}
					else
					{
    	            	if (clientType.equals(ClientType.DBF))
    	            	{
							conditions= new String[] { " branch_code  = \'" + branchCode + "\'"} ;
							oracleConditions= new String[] { " branch_code  = \'" + mappingBranchCode + "\'"} ;
    	            	}
    	            	else
    	            	{
							conditions= new String[] { "status = \'P\'", " branch_code  = \'" + branchCode + "\'"} ;
							oracleConditions= new String[] { "status = \'P\'", " branch_code  = \'" + mappingBranchCode + "\'"} ;
    	            	}
					}

					for (SchemeInfo schemeInfo : schemeInfoList) {
						String fromTable = schemeInfo.getSource();
						String toTable = schemeInfo.getDestination();
						try {
								int deleteCount = 0;
								int[] rs = null;
	
								if(PollSchemeType.SALES_EOD.equals(branchScheme.getPollSchemeType())){
									
			                        LogUtils.printLog(logger, "{} {} {} schemeInfo id:{} : {} -> {} [{}]", branchCode, pollSchemeType, clientType, 
			                        		schemeInfo.getId(), fromTable, toTable, conditions[0]);
			                        
		                        	deleteCount = JDBCUtils.deleteByConditions(toConn, schemeInfo.getDestination(), oracleConditions);
		                            LogUtils.printLog(logger, "{} {} SALES_EOD schemeInfo delete table :{} {} ", 
		                            		branchCode, pollSchemeType, schemeInfo.getDestination(), deleteCount );
			                            
			                        flag = true;
			                        rs = JDBCUtils.bulkCopyFromSQLToOracle(fromConn, toConn, schemeInfo, defaultTransactionBatchSize, null,
			                        		conditions, branchCodeMap, false);
			                        
			                        int count = rs[0];
		            				if (StringUtils.isBlank(schemeInfo.getDestCheckSumCols()))
		            				{
		            					int sourceCount = JDBCUtils.getTableCount(fromConn, schemeInfo.getSource(),conditions);
		                				if (sourceCount != count)
		                				{
		                                    throw new RuntimeException("Count not match Staging["+sourceCount+"] VS EDW["+count+"]");
		                				}
		            				}
		            				else
		            				{
										//**********************checkSumBySchemeInfo********************
				                        // format checkSum criteria for POS data process branchCode = pos branch)
				                        boolean passCheckSum = JDBCUtils.checkSumBySchemeInfoToEDW(fromConn, toConn, branchCode, schemeInfo,
				                        		oracleConditions,conditions);
				                        if (!passCheckSum) {
					                        LogUtils.printLog(logger, "{} SALES_EOD schemeInfo{} count {} passCheckSum {} fail", branchCode, schemeInfo.getDestination(), count, passCheckSum);
				                            throw new RuntimeException("checkSumColumn false");
				                        }
		            				}
								} else {
									
//									String selectSQL = MessageFormat.format("SELECT count(1) as ss FROM {0} {1}", schemeInfo.getDestination(),
//			                				SQLStmtUtils.getCriteriaString(conditions));
//			                        List<Map<String, Object>> execCliectQuery = PosClientUtils.execCliectQuery(fromConn, selectSQL, false);
//			                        Object obj = execCliectQuery.size() == 0?"0":execCliectQuery.get(0).get("ss");
//			                        if(Integer.parseInt(obj.toString())>0){
	            					int souceCounnt = JDBCUtils.getTableCount(fromConn, schemeInfo.getSource(),conditions);
	            					
			                        LogUtils.printLog(logger, "{} {} {} schemeInfo id {} : {} -> {} [{}]", branchCode, pollSchemeType, clientType,
			                        		schemeInfo.getId(), fromTable, toTable, souceCounnt);
	            					
	            					if (souceCounnt > 0) {
			            	            if (StringUtils.isNotBlank(toTable)) {
			            	            	if (clientType.equals(ClientType.DBF))
			            	            	{
					                        	deleteCount = JDBCUtils.deleteByConditions(toConn, schemeInfo.getDestination(), oracleConditions);
					                            LogUtils.printLog(logger, "{} SALES_REALTIME schemeInfo delete table :{} {} ", 
					                            		branchCode, schemeInfo.getDestination(), deleteCount );
				            	            	rs = JDBCUtils.bulkCopyFromSQLToOracle(fromConn, toConn, schemeInfo, defaultTransactionBatchSize, null,
														conditions, branchCodeMap, false );
			            	            	}
			            	            	else
			            	            	{
				            	            	rs = JDBCUtils.bulkCopyFromSQLToOracle(fromConn, toConn, schemeInfo, defaultTransactionBatchSize, null,
														conditions, branchCodeMap, true );
				            	            	deleteCount = rs[3];
			            	            	}
			            	            
			            	            	flag = true;
					                        Boolean statusNullable = JDBCUtils.CURRENT_THREAD_STATUS_NULLABLE.get();
					                        String toOrcl = SQLStmtUtils.getUpdateNullSqlByStatus(toTable, oracleConditions);
					                        if (statusNullable != null && !statusNullable.booleanValue())
					                        {
					                        	toOrcl = SQLStmtUtils.getUpdateSpaceSqlByStatus(toTable, oracleConditions);
					                        }
					                        JDBCUtils.CURRENT_THREAD_STATUS_NULLABLE.remove();
											int targetUpdateCount = PosClientUtils.updateTable(toConn, toOrcl);
											String fromSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(fromTable, conditions);
											int sourceUpdateCount = PosClientUtils.updateTable(fromConn, fromSql);
					        				LogUtils.printLog(logger, " {} SALES_REALTIME Branch update Status Count : {} {} ", branchCode, sourceUpdateCount, targetUpdateCount) ;
				            				if (StringUtils.isBlank(schemeInfo.getDestCheckSumCols()))
				            				{
				                				if (souceCounnt > targetUpdateCount)
				                				{
				                                    throw new RuntimeException("Count not match Staging["+souceCounnt+"] VS EDW["+targetUpdateCount+"]");
				                				}
				            				}
				            				else
				            				{
												//**********************checkSumBySchemeInfo********************
						                        // format checkSum criteria for POS data process branchCode = pos branch)
						                        boolean passCheckSum = JDBCUtils.checkSumBySchemeInfoToEDW(fromConn, toConn, branchCode, schemeInfo,
						                        		oracleConditions,conditions);
						                        if (!passCheckSum) {
							                        LogUtils.printLog(logger, "{} SALES_REALTIME schemeInfo{} count {} passCheckSum {} fail", branchCode, schemeInfo.getDestination(), (rs[0]+rs[1]), passCheckSum);
						                            throw new RuntimeException("checkSumColumn false");
						                        }
						                        LogUtils.printLog(logger, "{} SALES_REALTIME schemeInfo{} count {} passCheckSum {}", branchCode, schemeInfo.getDestination(), (rs[0]+rs[1]), passCheckSum);
				            				}
			            	            } else {
			            	            	continue;
			            	            }
			                        } else {
			                            LogUtils.printLog(logger, "{} SALES_REALTIME schemeInfo{} No source data for table :{}  ", 
			                            		branchCode, schemeInfo.getDestination(), schemeInfo.getSource() );
			                        	continue;
			                        }
								}
								LogUtils.printLog("{} branchCode, {} : update sucess {} {} {}" , branchCode, toTable, deleteCount, rs[0], rs[1]);
								taskJobLogService.createJobLogDetail(taskJobLog, fromTable, toTable,deleteCount, rs);
	
								result = pollSchemeType + ":" + branchScheme.getDirection() + " process success!";
							} catch (Exception e) {
								isError = true ;
								LogUtils.printException(logger, "Task execute exception:", e);
	
								taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
	
								result = pollSchemeType + ":" + branchScheme.getDirection() + " process failed!";

							}
					}
				} else {
					LogUtils.printLog(logger, "{} {} processStgToEdwJDBC SchemeInfo is null", branchCode, pollSchemeType);
					isError = true;
				}
				if (isError)
				{
					LogUtils.printLog(logger, "{} {} processStgToEdwJDBC, have error", branchCode, pollSchemeType);
					return result;
				}
				if(PollSchemeType.SALES_EOD.equals(branchScheme.getPollSchemeType())){
					LogUtils.printLog("{} {} processStgToEdwJDBC update convert log {} {} ",branchCode, pollSchemeType, flag, isError);
					for (String date : prcDatesStr)
					{
						if(flag && !isError){
							boolean mark = findConvertLogByBusinessDate(toConn, branchScheme, date);
							if (!mark)
							{
								String sql = "INSERT INTO CONVERT_LOG(TTDATE,BRNO,RUNNO,POLL_METHOD,CONV_DATE,CONV_FLAG,LAST_UPDATE_USER,CHK_UPLOAD_DATA_DATETIME) "
										+ "VALUES(to_date('"+date+"','YYYY-MM-DD'),'"+branchScheme.getBranchMaster().getBranchCode()+"',0,'1',null,null,'ESB_SYSTEM',SYSTIMESTAMP)";
								int count = PosClientUtils.updateTable(toConn,sql);
								LogUtils.printLog(logger,"{} {} processStgToEdwJDBC insert convert_log success {}",
										branchCode, pollSchemeType,count);
							}
							else
							{
								String sql = "UPDATE CONVERT_LOG SET LAST_UPDATE_USER = 'ESB_SYSTEM' ,CHK_UPLOAD_DATA_DATETIME= sysdate "
										+ "where TO_CHAR(TTDATE,'yyyy-MM-dd') = '"+date+"' and BRNO = '"+branchCode+"'";
								int count = PosClientUtils.updateTable(toConn,sql);
								LogUtils.printLog(logger,"{} {} processStgToEdwJDBC update convert_log success {} ",
										branchCode, pollSchemeType,count);
							}
						}
					}
				}
			} 
		} catch (Exception e) {
			e.printStackTrace();
			LogUtils.printException(logger, "JDBC Process to EDW Oracle encounters exception", e);
			taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
			if (e instanceof RuntimeException)
			{
				throw (RuntimeException) e ;
			}
			throw new RuntimeException(e);
		}
		return result;
	}

	public boolean findConvertLogByBusinessDate(Connection conn, BranchScheme branchScheme, String dateStr) throws SQLException {
//	    BranchInfo branchInfo = branchScheme.getBranchInfo();
	    String branchCode = branchScheme.getBranchMaster().getBranchCode();
//		LogUtils.printLog("	 {}",branchCode );
//	    try(Connection connection = applicationSettingService.getJDBCConection(branchInfo, true)){
//	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String datetime =sdf.format(new Date());
//	    	String datetime = DateUtil.getCurrentDateString();
	    	String query ="select count(1) as SS from CONVERT_LOG where TO_CHAR(TTDATE,'yyyy-MM-dd') = '"+dateStr+"' and BRNO = '"+branchCode+"'";
	    	List<Map<String, Object>> list = PosClientUtils.execCliectQuery(conn, query, false);
	    	Object obj = list.size() == 0 ? "0" : list.get(0).get("SS");
	    	if(Integer.parseInt(obj.toString()) > 0){
	    		return true;
	    	}
//	    }catch (Exception e) {
//			LogUtils.printException("select convert_log fail", e);
//		}
        return false;
	}

	public BranchScheme getBranchScheme() {
		return branchScheme;
	}

	public void setBranchScheme(BranchScheme branchScheme) {
		this.branchScheme = branchScheme;
	}

	public TaskJobLog getTaskJobLog() {
		return taskJobLog;
	}

	public void setTaskJobLog(TaskJobLog taskJobLog) {
		this.taskJobLog = taskJobLog;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	
	public void setDefaultTransactionBatchSize(int defaultTransactionBatchSize) {
		this.defaultTransactionBatchSize = defaultTransactionBatchSize;
	}
	public void setEnableBranchCodeMapping(boolean enableBranchCodeMapping) {
		this.enableBranchCodeMapping = enableBranchCodeMapping;
	}



}
