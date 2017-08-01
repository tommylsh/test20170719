package com.maxim.pos.sales.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.springframework.jdbc.support.nativejdbc.C3P0NativeJdbcExtractor;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.PosClientUtils;


/**
 * Class SalesServiceSqlImpl
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


@Service("sqlSalesService")
public class SalesServiceSqlImpl extends  SalesServiceBaseImpl {
	
	@Override
    protected int getDefaultScanDayIfNoControl()
	{
		return sqlDefaultScanDayIfNoControl ;
	}
	@Override
    protected int getMaxScanDay()
    {
		return sqlMaxScanDay ;
    }

	@Override
	protected List<Date> doGetPosProcessDate(BranchScheme branchScheme, List<SchemeInfo> schemeList,
			java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate, Logger logger) {
		
        List<java.util.Date> procDates = new ArrayList<java.util.Date>();
        String branchCode = branchScheme.getBranchMaster().getBranchCode();

        try (Connection connection = applicationSettingService.getJDBCConection(branchScheme, true)) {
            String query = "select business_date from hist_possystem where branch_code = '" + branchCode + "' and business_date > '" + controlDate + "' order by business_date asc";
            List<Map<String, Object>> posSystemList = PosClientUtils.execCliectQuery(connection, query, false);
            if (posSystemList.size() > 0 && !posSystemList.isEmpty()) {
                for (Map<String, Object> map : posSystemList) {
                    java.util.Date date = (java.util.Date) map.get("business_date");
                    
            		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            		sqlDate = java.sql.Date.valueOf(sqlDate.toString());

                    procDates.add(sqlDate);
                }
//            } else {
//                LogUtils.printLog(logger, "{} Branch No hist_possystem > {}", branchCode, controlDate);
            }
        } catch (SQLException e) {
        	LogUtils.printException(logger, "Connection is Exception Or Execute fail", e);
            throw new RuntimeException(e);
        } catch (RuntimeException e) {
        	if( e.getCause() instanceof SQLException)
        	{
        		LogUtils.printException(logger, "Connection is Exception Or execute fail:", e);
        		branchScheme.getTaskLog().setErrorMsg(e.getMessage() + "-" + e.getCause().getMessage() );
        	}
        	throw e;
        }

		
        if (PollSchemeType.SALES_REALTIME.equals(branchScheme.getPollSchemeType())) {
        	procDates.add(REALTIME_DATE);
        }
        
        return procDates ;
	}

	@Override
	protected List<Date> doFilterStockTakeReady (List<Date> dates, BranchScheme branchScheme, List<SchemeInfo> schemeList, java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate,Logger logger)	  
	{
		List<Date> returnDates = new ArrayList<Date>();
		if (dates.isEmpty())
		{
			return returnDates ;
		}
        try (Connection connection = applicationSettingService.getJDBCConection(branchScheme, true)) {
        	
            String branchCode = branchScheme.getBranchMaster().getBranchCode();
            
        	int idx = 0;
        	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        	StringBuffer dateStrBuf = new StringBuffer("(");
        	for (Date date : dates)
        	{
        		dateStrBuf.append("'").append(df.format(date));
        		if (++idx == dates.size())
        		{
        			dateStrBuf.append("')");
        		}
        		else
        		{
        			dateStrBuf.append("',");
        		}
        	}
        	StringBuffer dateConditionStrBuf = new StringBuffer("CONVERT(varchar(16),business_date,23) in ").append(dateStrBuf);
        	String dateConditionStr = dateConditionStrBuf.toString();

        	
            String query = "select distinct business_date from hist_itemstock where branch_code = '" + branchCode + "' and " + dateConditionStr + " order by business_date asc";
            List<Map<String, Object>> list = PosClientUtils.execCliectQuery(connection, query, false);
            if (list.size() > 0 && !list.isEmpty()) {
                for (Map<String, Object> map : list) {
                	Date date = (Date) map.get("business_date");;
                	
            		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            		sqlDate = java.sql.Date.valueOf(sqlDate.toString());

                	returnDates.add(sqlDate);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return returnDates ;
	}

	@Override
    protected List<Date> doProcessPosDataToStg(BranchScheme branchScheme, List<SchemeInfo> schemeList, 
    		List<Date> procDates, List<Date> stDates, List<Date> nonStDoneDates, List<Date> stDoneDates, List<Date> stReady,
    		java.sql.Date currentDate, java.sql.Date yesterdayDate, java.sql.Date controlDate, TaskJobLog taskJobLog, Logger logger){
		
        PollSchemeType pollSchemeType	= branchScheme.getPollSchemeType();
//        BranchInfo branchInfo			= branchScheme.getBranchInfo();
        String branchCode				= branchScheme.getBranchMaster().getBranchCode();

        LogUtils.printLog(logger,"{} {} doProcessPosDataToStg procDates {} stDates {} nonStDoneDates {} stDoneDates {} stReady {}", branchCode, pollSchemeType, procDates, stDates, nonStDoneDates,  stDoneDates,  stReady);                	


		int totalCount = 0;
		boolean isError = false ;
		Connection fromDSPool = null ;
        try (Connection toDSPool = applicationSettingService.getCurrentJDBCConnection()){
        	
        	fromDSPool = applicationSettingService.getJDBCConection(branchScheme, true) ;
        	
    		C3P0NativeJdbcExtractor cp30NativeJdbcExtractor = new C3P0NativeJdbcExtractor();
			Connection fromConn = cp30NativeJdbcExtractor.getNativeConnection(fromDSPool);
			Connection toConn = cp30NativeJdbcExtractor.getNativeConnection(toDSPool);

        	
//            LogUtils.printLog(logger, "{} {} Ready to copy data from: \r\ndatasource: '{}' to \r\n datasource: '{}'", branchCode, pollSchemeType, fromConn, toConn);
            LogUtils.printLog(logger, "{} {} Ready to copy data from POS to Staging {}", branchCode, pollSchemeType, schemeList.size());

            // Real Time (SQL Server)
            // Lookup the table name configured in database.
            // Get the records with status = '' and specific branch code
            if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType)) {
            	
//                LogUtils.printLog(logger, "{} REAL_TIME processSqlToStg {} ", branchCode, schemeList.size());
//
                String[] conditions = {" LTRIM(RTRIM(status)) = \'P'", " branch_code  = \'" + branchCode + "\'"};
                for (SchemeInfo schemeInfo : schemeList) 
                {
                    String fromTable	= schemeInfo.getSource();
                    String toTable		= schemeInfo.getDestination();
                    try 
                    {
                    	// Update the record to Pending State '' -> 'P'
                    	int row = JDBCUtils.updatePendingStatusByBranchCodeAndStatus(fromConn, fromTable, branchCode,"");
                        LogUtils.printLog(logger, "{} REAL_TIME schemeInfo{} row:{} : {} -> {} ", branchCode, schemeInfo.getId(), row, schemeInfo.getSource(), schemeInfo.getDestination());
                        if (row > 0)
                        {
                            int[] returnInts = {0,0};
                        	String conversion = JDBCUtils.CONV_NONE ;
                        	if (JDBCUtils.CONV_CHI_BRANCH_LIST.contains(branchCode))
                        	{
                        		conversion = JDBCUtils.CONV_SIMPLIFIED_TO_TRADTION ;
                        	}

//	                        if (schemeInfo.isConsistentStructure()) {
                            try
                            {
	                        	returnInts = JDBCUtils.structureConsistentBulkCopy(fromConn
	                            		,toConn
	                            		,schemeInfo
	                            		,defaultTransactionBatchSize
	                            		,conditions,conversion);
                        	}
                        	catch (SQLException e)
                        	{
                                LogUtils.printLog(logger, "{} REAL_TIME structureConsistentBulkCopy error :{} {} {} {}", branchCode, e.getSQLState(), e.getErrorCode(), e.getMessage(), e.getCause());
                                try
                                {
                                	fromConn.createStatement().executeQuery("SELECT 1");
                                	throw e ;
                                }
                            	catch (SQLException e2)
                            	{
                                    LogUtils.printLog(logger, "{} REAL_TIME reonnection ", branchCode);
                                    fromDSPool = applicationSettingService.getJDBCConection(branchScheme, true);
        							fromConn = cp30NativeJdbcExtractor.getNativeConnection(fromDSPool);
    	                        	returnInts = JDBCUtils.structureConsistentBulkCopy(fromConn
    	                            		,toConn
    	                            		,schemeInfo
    	                            		,defaultTransactionBatchSize
    	                            		,conditions,conversion);
        						}
                        	}
	                        	
	                        	
//	                        	returnInts = new int[]{returnInts[0], returnInts[1], row, returnInts[0]+returnInts[1]};
//	                        } else {
//	                        	returnInts = JDBCUtils.bulkCopyFromSQLConn(fromConn, toConn, schemeInfo, null, null, conditions);
//	                        	returnInts = new int[]{returnInts[0], returnInts[1], row, returnInts[0]+returnInts[1]};
//	                        }
                            LogUtils.printLog(logger, "{} REAL_TIME schemeInfo {} insert records: {}; update records:{} ", branchCode, schemeInfo.getDestination(), returnInts);
                            // Update Status to 'C'
                            int targetCounnt = returnInts[0] + returnInts[1];
                            if (targetCounnt > 0) {
                            
	                            taskJobLogService.createJobLogDetail(taskJobLog, fromTable, toTable, 0, returnInts);
	
	                            // format checkSum criteria for POS data process (status
	                            // = 'P', and branchCode = pos branch)
	            				if (StringUtils.isBlank(schemeInfo.getDestCheckSumCols()))
	            				{
	                				if (row > targetCounnt)
	                				{
	                                    throw new RuntimeException("Count not match Souce["+row+"] VS Target["+targetCounnt+"]");
	                				}
	            				}
	            				else
	            				{
		                            boolean passCheckSum = JDBCUtils.checkSumBySchemeInfo(fromConn, toConn, branchCode, schemeInfo,
		                                    conditions);
		                            LogUtils.printLog(logger, "{} REAL_TIME schemeInfo{} passCheckSum {}", branchCode, schemeInfo.getDestination(), passCheckSum);
		                            if (!passCheckSum) {
		                                throw new RuntimeException("checkSumColumn false");
		                            }
	            				}

                                int count = JDBCUtils.updateCompleteStatusByBranchCodeAndStatus(fromConn, fromTable, branchCode,"P");
                                LogUtils.printLog(logger, "{} REAL_TIME schemeInfo{} completeRecord {}", branchCode, schemeInfo.getDestination(),count);
                                
                            	totalCount +=row ;
    						}
                        }
                    } catch (Exception e) {
                    	isError = true ;
                        LogUtils.printException(logger, "Task execte exception: ", e);
                        taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
                    }
                }
            } else if (PollSchemeType.SALES_EOD.equals(pollSchemeType)) {
            	
//                LogUtils.printLog(logger, "{} SALES_EOD processSqlToStg {} ", branchCode, schemeList.size());
//
            	int idx = 0;
            	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            	StringBuffer dateStrBuf = new StringBuffer("(");
            	StringBuffer nonStockTakeDateStrBuf = new StringBuffer("(");
            	StringBuffer stockTakeDateStrBuf = new StringBuffer("(");
            	String minDate	= controlDate.toString() ;
            	String maxDate	= null ;
            	String stDate	= null ;
            	for (Date date : procDates)
            	{
//            		if (idx == 0)
//            		{
//            			minDate=df.format(date);
//            		}
            		dateStrBuf.append("'").append(df.format(date));
            		
            		if (procDates.size() != stReady.size())
            		{
	            		if (stReady.contains(date))
	            		{
	            			nonStockTakeDateStrBuf.append("'").append(df.format(date)).append("',");
	            		}
	            		else
	            		{
	            			stockTakeDateStrBuf.append("'").append(df.format(date)).append("',");
	                		stDate=df.format(date);
	            		}
            		}
            		
            		if (++idx == procDates.size())
            		{
            			dateStrBuf.append("')");
            		}
            		else
            		{
            			dateStrBuf.append("',");
            		}
            		if (idx == procDates.size())
            		{
            			maxDate=df.format(date);
            		}
            	}
            	
            	StringBuffer dateConditionStrBuf = new StringBuffer("CONVERT(varchar(16),business_date,23) in ").append(dateStrBuf);
            	String dateConditionStr = dateConditionStrBuf.toString();
            	
            	StringBuffer nonStockTakeDateConditionStrBuf = new StringBuffer("CONVERT(varchar(16),business_date,23) in ").append(nonStockTakeDateStrBuf);
            	String nonStockTakeDateStr = nonStockTakeDateConditionStrBuf.toString();
            	nonStockTakeDateStr = nonStockTakeDateStr.substring(0, nonStockTakeDateStr.length() - 1) + ")";

//            	StringBuffer stockTakeDateConditionStrBuf = new StringBuffer("CONVERT(varchar(16),business_date,23) in ").append(stockTakeDateStrBuf);
//            	String stockTakeDateDateStr = stockTakeDateConditionStrBuf.toString();
//            	stockTakeDateDateStr = stockTakeDateDateStr.substring(0, stockTakeDateDateStr.length() - 1) + ")";

            	
            	String deleteDateConditionStr = "CONVERT(varchar(16),business_date,23) > '"+minDate+"' and CONVERT(varchar(16),business_date,23) <= '"+maxDate+"'"; 
            	String nonStockTakeDeleteDateConditionStr = "CONVERT(varchar(16),business_date,23) > '"+minDate+"' and CONVERT(varchar(16),business_date,23) <= '"+stDate+"'"; 
//            	String stockTakeDeleteDateConditionStr = "CONVERT(varchar(16),business_date,23) > '"+stDate+"' and CONVERT(varchar(16),business_date,23) <= '"+maxDate+"'"; 
            	
            	String[] conditions = null ;
            	String   deleteCondition = null ;
//                String[] conditions = new String[]{" branch_code  = \'" + branchCode + "\'",
//                		dateConditionStr};
                
                for (SchemeInfo schemeInfo : schemeList) {
                    
                    LogUtils.printLog(logger, "{} SALES_EOD schemeInfo{}  : {} -> {} ", branchCode, schemeInfo.getId(), schemeInfo.getSource(), schemeInfo.getDestination());

                    String fromTable = schemeInfo.getSource();
                    String toTable = schemeInfo.getDestination();
//    		List<Date> procDates, List<Date> stDates, List<Date> nonStDoneDates, List<Date> stDoneDates, List<Date> stReady,
                    
            		if (stDates.size() > 0)
            		{
            			if (stTableList.contains(toTable.toLowerCase()))
            			{
                    		if (procDates.size() != stReady.size())
            				{
                				if (procDates.size() == 1)
                				{
                					continue ;
                				}
                                conditions = new String[]{" branch_code  = \'" + branchCode + "\'",
                                		nonStockTakeDeleteDateConditionStr};
                                deleteCondition = nonStockTakeDeleteDateConditionStr ;
            				}
            				else
            				{
                        		if (procDates.size() == stDoneDates.size())
                        		{
                        			continue ;
                        		}
            					conditions = new String[]{" branch_code  = \'" + branchCode + "\'",
            	                		dateConditionStr};
                                deleteCondition = deleteDateConditionStr ;
            				}
            			}
            			else
            			{
                    		if (procDates.size() == nonStDoneDates.size())
                    		{
                    			continue ;
                    		}
                    		if (procDates.size() == stDoneDates.size())
                    		{
                    			continue ;
                    		}
        					conditions = new String[]{" branch_code  = \'" + branchCode + "\'",
        	                		dateConditionStr};
                            deleteCondition = deleteDateConditionStr ;
            			}
            		}
            		else
            		{
    					conditions = new String[]{" branch_code  = \'" + branchCode + "\'",
    	                		dateConditionStr};
                        deleteCondition = deleteDateConditionStr ;
            		}
                    
                    try {
                        int[] returnInts = {0,0};
                        int deleteCount = JDBCUtils.deleteByBranchAndBizDate(toConn, schemeInfo.getDestination(),
                    			branchScheme.getBranchMaster().getBranchCode(), deleteCondition);
                        LogUtils.printLog(logger, "{} SALES_EOD schemeInfo{} delete table :{} {} ", branchCode, schemeInfo.getDestination(), deleteCount, deleteCondition);
                    	String conversion = JDBCUtils.CONV_NONE ;
                    	if (JDBCUtils.CONV_CHI_BRANCH_LIST.contains(branchCode))
                    	{
                    		conversion = JDBCUtils.CONV_SIMPLIFIED_TO_TRADTION ;
                    	}

//                        if (schemeInfo.isConsistentStructure()) {
                    	try
                    	{
                    		
                        	returnInts = JDBCUtils.structureConsistentBulkCopy(fromConn
                            		,toConn
                            		,schemeInfo
                            		,defaultTransactionBatchSize
                            		, conditions
                            		, false,conversion);
                    	}
                    	catch (SQLException e)
                    	{
                            LogUtils.printLog(logger, "{} SALES_EOD structureConsistentBulkCopy error :{} {} {} {}", branchCode, e.getSQLState(), e.getErrorCode(), e.getMessage(), e.getCause());
                            try
                            {
                            	fromConn.createStatement().executeQuery("SELECT 1");
                            	throw e ;
                            }
                        	catch (SQLException e2)
                        	{
                                LogUtils.printLog(logger, "{} SALES_EOD reonnection ", branchCode);
                                fromDSPool = applicationSettingService.getJDBCConection(branchScheme, true);
    							fromConn = cp30NativeJdbcExtractor.getNativeConnection(fromDSPool);
    	                        deleteCount = JDBCUtils.deleteByBranchAndBizDate(toConn, schemeInfo.getDestination(),
    	                    			branchScheme.getBranchMaster().getBranchCode(), deleteCondition);
    	                        LogUtils.printLog(logger, "{} SALES_EOD schemeInfo{} delete table :{} {} ", branchCode, schemeInfo.getDestination(), deleteCount, deleteCondition);
                            	returnInts = JDBCUtils.structureConsistentBulkCopy(fromConn
                                		,toConn
                                		,schemeInfo
                                		,defaultTransactionBatchSize
                                		, conditions
                                		, false,conversion);
    						}
                    	}
                    	
//                        } 
//                        else 
//                        {
//                        	returnInts = JDBCUtils.bulkCopyFromSQLConn(fromConn, toConn, schemeInfo, defaultTransactionBatchSize, null, conditions);
//                        }
                        LogUtils.printLog(logger, "{} SALES_EOD schemeInfo{} insert records: {}; update records:{} ", branchCode, schemeInfo.getDestination(), returnInts);

                        // Update the Pos Status to Complete(C) 
                        int updateCount = JDBCUtils.updateCompleteStatusByConditions(fromConn, fromTable, conditions);
        				LogUtils.printLog(logger, " {} Branch update Source Status Count : {}", branchCode, updateCount) ;

                        // Update the Staging Status to Blank 
                        int toUpdateCount = JDBCUtils.updateBlankStatusByConditions(toConn, toTable, conditions);
        				LogUtils.printLog(logger, " {} Branch update Target Status Count : {}", branchCode, toUpdateCount) ;
        				
        				if (returnInts == null)
        				{
           					returnInts = new int[] {toUpdateCount, 0};
        				}
        				
                        // Create Job Log Detail for each Table
        				taskJobLogService.createJobLogDetail(taskJobLog, fromTable, toTable, deleteCount, returnInts);
        				
        				if (updateCount != toUpdateCount)
        				{
                            throw new RuntimeException("Count not match Souce["+updateCount+"] VS Target["+toUpdateCount+"]");
        				}

                        // format checkSum criteria for POS data process branchCode = pos branch)
                        boolean passCheckSum = JDBCUtils.checkSumBySchemeInfo(fromConn, toConn, branchCode, schemeInfo,
                                conditions);
                        LogUtils.printLog(logger, "{} SALES_EOD schemeInfo{} passCheckSum {}", branchCode, schemeInfo.getDestination(), passCheckSum);
                        if (!passCheckSum) {
                            throw new RuntimeException("checkSumColumn false");
                        }

                    } catch (Exception e) {
                    	isError = true ;
                        LogUtils.printException(logger, "Task execte exception: ", e);
                        taskJobLogService.createJobExceptionDetail(taskJobLog, fromTable, toTable, e);
                    }
                }
            }
        } catch(Exception e) {
        	e.printStackTrace();
        	LogUtils.printException(logger, "get connection is null",e);
        	taskJobLogService.createJobExceptionDetail(taskJobLog, "", "", e);
        	procDates = null;
        }
        
        if (fromDSPool != null)
        {
        	try {
        		fromDSPool.close();
			} catch (SQLException e) {
                LogUtils.printException(logger, "Souce Connection exception: ", e);
			}
        }
        LogUtils.printLog(logger, "{} {} {} {} {} {}", branchCode, pollSchemeType, branchScheme.getDirection(), branchScheme.getPollSchemeName(), totalCount, isError);
        
        if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType)) {
        
        	if (!branchScheme.isReRun() && totalCount <= 0)
        	{
        		branchScheme.getTaskLog().setErrorMsg("No update data in SQL Client");
        		return null;
        	}
        	else
        	{
        		return new ArrayList<Date>();
        	}
        }

        return procDates ;
	}



}
