package com.maxim.pos.common.util;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.luhuiguo.chinese.ChineseUtils;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ColumnFormat;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopy;
import com.microsoft.sqlserver.jdbc.SQLServerBulkCopyOptions;
import com.microsoft.sqlserver.jdbc.SQLServerException;

public class JDBCUtils {

	private static final Logger logger = LoggerFactory.getLogger(JDBCUtils.class);
	
	public static final String DEFAULT_CSV_ENCODING = "UTF-16";
	
    public static final char TAB = '\t';
    
    public static final String COMMA = ",";

    public static final String[] DEFAULT_ID = {"ROWGUID"};
    
    public static Map<String, List<String>> CONV_CHI_TABLE_MAP = new HashMap<String, List<String>>();
    public static List<String> CONV_CHI_BRANCH_LIST = new ArrayList<String>();

    public static final String CONV_NONE = "CONV_NONE";
    public static final String CONV_TRADTION_TO_SIMPLIFIED = "TRADTION_TO_SIMPLIFIED";
    public static final String CONV_SIMPLIFIED_TO_TRADTION = "SIMPLIFIED_TO_TRADTION";

	/**
	 * 
	 * @param fromConn
	 *            source
	 * @param toConn
	 *            destination
	 * @param schemeInfo
	 * @param batchSize if pass null
	 * 		then commitment control and transaction size must be handled by external caller
	 * @param criteria
	 *            select condition eg:"status='C'"
	 * @return [0] insert count, [1] update count
	 * @throws Exception
	 */
	public static int[] structureConsistentBulkCopy(Connection fromConn, Connection toConn, SchemeInfo schemeInfo,
			Integer batchSize, String[] criteria,String conversion) throws Exception {
		return structureConsistentBulkCopy(fromConn, toConn, schemeInfo, batchSize, criteria, true, conversion);
	}
	public static int[] structureConsistentBulkCopy(Connection fromConn, Connection toConn, SchemeInfo schemeInfo,
			Integer batchSize, String[] criteria, boolean removeDuplication,String conversion) throws Exception {
//		int[] returnInts = null;

		if(conversion != CONV_NONE || StringUtils.startsWith(schemeInfo.getClientType().name(),"SQLPOS")) {
//			System.out.println("jdbcBatchInsertFromResultSet");

			int[] returnInts =  jdbcBatchInsertFromResultSet(fromConn, toConn, schemeInfo, batchSize, criteria, removeDuplication,conversion);
			return  returnInts;
		}
		
		int[] returnInts = {0,0,0,0};
		int delCount = 0 ;
		if (removeDuplication)
		{
			try{
//				returnInts = handleDuplicatedRecords(fromConn, toConn, schemeInfo, batchSize, criteria);
				
				String selectSQL = MessageFormat.format("SELECT * FROM {0} {1}", schemeInfo.getSource(),
						SQLStmtUtils.getCriteriaString(criteria));
				Statement stmt = fromConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
				ResultSet rsSourceData = stmt.executeQuery(selectSQL) ;

				delCount = delRepeatingData(toConn, schemeInfo.getDestination(),
						schemeInfo.getSrcKeyColumns().split(COMMA),
						schemeInfo.getDestKeyColumns().split(COMMA), 
						batchSize,
						rsSourceData);
				returnInts[3] = delCount;
			}
			catch(SQLException e){
				throw new RuntimeException(e);
			}
		}
		
		try (Statement stmt = fromConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);) {

			String selectSQL = SQLStmtUtils.getSelectAllStmt(schemeInfo.getSource(),criteria);

			try (ResultSet rsSourceData = stmt.executeQuery(selectSQL)){
				
				try(SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy(toConn)){
					bulkCopy.setDestinationTableName(schemeInfo.getDestination());
	
					SQLServerBulkCopyOptions copyOptions = new SQLServerBulkCopyOptions();
					copyOptions.setKeepIdentity(true);
					
					if(batchSize != null){
						copyOptions.setBatchSize(batchSize);
					}
						
//					copyOptions.setUseInternalTransaction(true);
					bulkCopy.setBulkCopyOptions(copyOptions);
					copyOptions.setBulkCopyTimeout(0);
	
					LogUtils.printLog("SQLServerBulkCopy start writeToServer");
					bulkCopy.writeToServer(rsSourceData);
					LogUtils.printLog("SQLServerBulkCopy end writeToServer");
				}
				catch (Exception e) {
					LogUtils.printException("SQLServerBulkCopy excepiton",e);
					if(rsSourceData !=null) {
						return jdbcBatchInsertFromResultSet(fromConn, toConn, schemeInfo, batchSize, criteria,conversion);
					}
				}
				int afterCount = getTableCount(toConn, schemeInfo.getDestination(),criteria);
				returnInts[0] = afterCount - delCount;
				returnInts[1] = delCount ;
				returnInts[2] = afterCount ;
			}
		}				
		return returnInts;
	}
	
	/**
	 * 
	 * @param fromConn
	 * @param toConn
	 * @param schemeInfo
	 * @param batchSize if pass null
	 * 		then commitment control and transaction size must be handled by external caller
	 * @param criteria
	 * @return [0] insert count; [1] update count
	 * @throws Exception
	 */
	public static int[] jdbcBatchInsertFromResultSet(Connection fromConn, Connection toConn, SchemeInfo schemeInfo,
			Integer batchSize, String[] criteria,String conversion) throws Exception {
		return jdbcBatchInsertFromResultSet(fromConn, toConn, schemeInfo, batchSize, criteria, true,conversion);
	}
	public static final Map<String,String> CURRENT_THREAD_BRANCH_CODE_MAP = new HashMap<String,String>();

	public static int[] jdbcBatchInsertFromResultSet(Connection fromConn, Connection toConn, SchemeInfo schemeInfo,
			Integer batchSize, String[] criteria, boolean removeDuplication,String conversion) throws Exception {
		
		int[] returnInts = {0,0,0,0};
		
		String insert = "insert into {0} ({1}) values ({2})" ;
		
		String selectSQL = MessageFormat.format("SELECT * FROM {0} {1}", schemeInfo.getSource(),
				SQLStmtUtils.getCriteriaString(criteria));
		Statement stmt = fromConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ResultSet rsSourceData = stmt.executeQuery(selectSQL) ;
		if (removeDuplication)
		{
			returnInts[1] = delRepeatingData(toConn, schemeInfo.getDestination(),
						schemeInfo.getSrcKeyColumns().split(COMMA),
						schemeInfo.getDestKeyColumns().split(COMMA),
						batchSize,
						rsSourceData);
		}
		
//		StringBuffer copyStringBuffer  =new StringBuffer();
		StringBuffer columnStringBuffer  =new StringBuffer();
		StringBuffer valueStringBuffer  =new StringBuffer();
//		Map<String, List> CONV_GB_TABLE_MAP = JsonConfigServiceImpl.CONV_GB_TABLE_MAP;
		String destinationTable= schemeInfo.getDestination();
		List<String> CONV_CHI_TABLE_COLUNM_LIST = CONV_CHI_TABLE_MAP.get(destinationTable.toUpperCase());

		int columnCount = -1;
		PreparedStatement prest = null;
		String[] columnNames = null ;

		try{
			
			int batchControl = 0;
			int totalInsertCount = 0;
			while (rsSourceData.next()) {
	
				columnCount = rsSourceData.getMetaData().getColumnCount();
				
				if(columnStringBuffer.length() <= 0){
					
					columnNames = new String[columnCount];
					
					for(int i = 1; i <= columnCount;i++){
						
						columnNames[i-1] = rsSourceData.getMetaData().getColumnLabel(i) ;
						columnStringBuffer.append(columnNames[i-1]);
						rsSourceData.getMetaData().getColumnType(i);
						valueStringBuffer.append("?");
						if(i != columnCount){
							columnStringBuffer.append(COMMA);
							valueStringBuffer.append(COMMA);
						}		
					}


					
					String insertStmt= MessageFormat.format(insert, destinationTable,
							columnStringBuffer.toString(),
							valueStringBuffer.toString());
				
					 prest = toConn.prepareStatement(insertStmt, 
							ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					 
					 for(int i=1; i <= columnCount; i++){
						 
						 String columnName = columnNames[i-1] ;
						 Object object = rsSourceData.getObject(i) ;

						 if (object instanceof String)
						 {
							String str= (String) object;
							if (CONV_CHI_TABLE_MAP.containsKey(destinationTable.toUpperCase())
									&& CONV_CHI_TABLE_COLUNM_LIST.contains(columnName.toLowerCase()) )
							{
								 if(conversion == CONV_SIMPLIFIED_TO_TRADTION){
									 str =  ChineseUtils.toTraditional(str);
								 } else if(conversion == CONV_TRADTION_TO_SIMPLIFIED){
									 str =  ChineseUtils.toSimplified(str);
								 }
							}
							 prest.setString(i, str);
						 } else {
							 prest.setObject(i, rsSourceData.getObject(i));
						 }
					}
					prest.addBatch();
				} 
				else {
					
					for(int i=1; i<= columnCount; i++){
						String columnName = columnNames[i-1] ;
						Object object = rsSourceData.getObject(i) ;
						if (object instanceof String)
						{
							String str= (String) object;
							if (CONV_CHI_TABLE_MAP.containsKey(destinationTable.toUpperCase())
									&& CONV_CHI_TABLE_COLUNM_LIST.contains(columnName.toLowerCase()) )
							{
								 if(conversion == CONV_SIMPLIFIED_TO_TRADTION){
									 str =  ChineseUtils.toTraditional(str);
								 } else if(conversion == CONV_TRADTION_TO_SIMPLIFIED){
									 str =  ChineseUtils.toSimplified(str);
								 }
							}

							prest.setString(i, str);
						} else {
							prest.setObject(i, rsSourceData.getObject(i));
						}
					}
					prest.addBatch();

					
				}
				batchControl ++;
				if(batchSize != null && batchControl >= batchSize){
					LogUtils.printLog(logger, schemeInfo.getDestination() +" commit: {} : {} / {}", (totalInsertCount +batchControl) , batchControl, batchSize);

					int[] executeBatchCount = prest.executeBatch();
					totalInsertCount += executeBatchCount.length;
					batchControl = 0;
				}
			}
			if(prest!=null){
				if (batchControl > 0)
				{
					LogUtils.printLog(logger, schemeInfo.getDestination() +" commit: {} : {} / {}", (totalInsertCount +batchControl) , batchControl, batchSize);
	
					int[] executeBatchCount = prest.executeBatch();
					totalInsertCount += executeBatchCount.length;
				}
				//insert count = total insert count - repeat data count
				returnInts[0] = totalInsertCount - returnInts[1];
				returnInts[2] = totalInsertCount ;
				returnInts[3] = returnInts[1];
			} 

		}finally {
			if(stmt!=null){
				stmt.close();
			}
			if(rsSourceData!=null){
				rsSourceData.close();
			}
			if(prest!=null){
				prest.close();
			}
		}
		
		return returnInts;
		
	}

	/**
	 * 
	 * @param conn
	 * @param destinationTable
	 * @param srcKeys
	 * @param destKeys
	 * @param batchSize if pass null
	 * 		then commitment control and transaction size must be handled by external caller
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private static int delRepeatingData(Connection conn, String destinationTable,
				String[] srcKeys, String [] destKeys, Integer batchSize, ResultSet rs) throws SQLException {
		int delCount = 0;
		String deleteSQL = SQLStmtUtils.getDeleteByKeysStmt(destinationTable, destKeys);

		PreparedStatement prest = null;
		try  {
			int batchControl = 0;
			int stmtCount = 0;
			while (rs.next()) {
				if(prest==null) {
					prest = conn.prepareStatement(deleteSQL, ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
//					LogUtils.printLog(logger, "delRepeatingData: {}", deleteSQL);
				}
				try {
					for(int i = 0; i < srcKeys.length; i++){
//						LogUtils.printLog("Add record deletion: " + srcKeys[i] + "=" + rs.getString(srcKeys[i]));
						prest.setString(i + 1, rs.getString(srcKeys[i]));
					}
					prest.addBatch();
					batchControl++;
					if(batchSize != null && batchControl >= batchSize){
						int[] is = prest.executeBatch();
						stmtCount += is.length ;
						int oldDelCount = delCount ;
						for (int i : is) {
							if (i > 0) {
								delCount++;
							}
						}
						if (delCount > oldDelCount)
						{
							LogUtils.printLog("delete {} {} {}", delCount, stmtCount, batchControl);
						}
						batchControl=0;
					}
				} catch (Exception e) {
					LogUtils.printException("ResultSet no rowguid field",e);
					return delCount;
				}
			}
			if(prest!=null && batchControl > 0){
				int[] is = prest.executeBatch();
				stmtCount += is.length ;
				int oldDelCount = delCount ;
				for (int i : is) {
						if (i > 0) {
							delCount++;
						}
				}
				if (delCount > oldDelCount)
				{
					LogUtils.printLog("delete {} {} {}", delCount, stmtCount, batchControl);
				}
			}


		}finally {
			rs.beforeFirst();
			if(prest!=null){
				prest.close();
			}
		}


		return delCount;
	}
	
//	/**
//	 *            select condition eg:"status='C'"
//	 * @return int[0]= add record count,int[1] = update record count
//	 * @throws Exception
//	 */
//	public static int[] bulkCopyFromSQL(String fromDS, String toDS, SchemeInfo schemeInfo, Integer batchSize,
//			String batchId, String[] criteria) throws SQLServerException, SQLException {
//		
//		int[] returnInts = new int[2];
//		
//		if (null == schemeInfo) {
//			LogUtils.printException(logger, "SchemeInfo in Bulk Copy from SQL cannot be NULL");
//			throw new RuntimeException("Invalid SchemeInfo");
//		}
//
//		if (schemeInfo.getSchemeTableColumns().isEmpty()) {
//			LogUtils.printException(logger, "Poll Scheme {} is not properly setup", schemeInfo.getId());
//			throw new RuntimeException("Table column list is null");
//		}
//
//		try (Connection fromConn = DriverManager.getConnection(fromDS);
//			 Connection toConn = DriverManager.getConnection(toDS))
//		{
//			returnInts = handleDuplicatedRecords(fromConn, toConn, schemeInfo, batchSize, criteria);
//			bulkCopyFromSQLConn(fromConn, toConn, schemeInfo, batchSize, batchId, criteria);
//		}
//		catch (Exception e) {
//			LogUtils.printException(logger, "Exception occurs in Bulk Copy from SQL process", e);
//			throw new RuntimeException(e);
//		}
//		
//		return returnInts;
//
//	}
//	/**
//	 * 
//	 * @param fromConn
//	 * @param toConn
//	 * @param schemeInfo
//	 * @param batchSize if pass null
//	 * 		then commitment control and transaction size must be handled by external caller
//	 * @param batchId
//	 * @param criteria
//	 * @return [0] insert count; [1] update count
//	 * @throws SQLServerException
//	 * @throws SQLException
//	 * @throws IllegalArgumentException
//	 */
//	public static int[] bulkCopyFromSQLConn(Connection fromConn, Connection toConn, SchemeInfo schemeInfo, Integer batchSize,
//			String batchId, String[] criteria) 
//					throws SQLServerException, SQLException, IllegalArgumentException {
//
//		int[] returnInts = {0,0};
//		
//		if (null == schemeInfo
//				|| fromConn == null
//				|| toConn == null) {
//			LogUtils.printException(logger, "Invalid parameters input");
//			throw new IllegalArgumentException("Invalid Parameters");
//		}
//
//		if (schemeInfo.getSchemeTableColumns().isEmpty()) {
//			LogUtils.printException(logger, "Poll Scheme {} is not properly setup", schemeInfo.getId());
//			throw new RuntimeException("Table column list is null");
//		}
//		
////		try{
////			fromConn.isValid(0);
////			toConn.isValid(0);
////		}catch(SQLServerException e){
////			LogUtils.printException(logger, "SQL Bulk Copy Connection is not valid", e);
////			throw new RuntimeException(e);
////		}
//		
//		// initialize source and destination info
//		List<String> sourceCols = new ArrayList<String>();
//		List<String> destinationCols = new ArrayList<String>();
//		String sourceTable = schemeInfo.getSource();
//		String destinationTable = schemeInfo.getDestination();
//		List<SchemeTableColumn> colEntities = schemeInfo.getSchemeTableColumns();
//
//		for (SchemeTableColumn e : colEntities) {
//			sourceCols.add(e.getFromColumn());
//			destinationCols.add(e.getToColumn());
//		}
//
//		
////		returnInts = handleDuplicatedRecords(fromConn, toConn, schemeInfo, criteria);
//		// bulk copy process
//		boolean copyWithBatch = batchId != null && !batchId.isEmpty();
//
//		// bulk copy process
//
//		try (Statement sourceStmt = fromConn.createStatement(
//				ResultSet.TYPE_SCROLL_INSENSITIVE,
//				ResultSet.CONCUR_READ_ONLY)) {
//
//			// perform an initial count on the source table
//			int countSource = 0;
//			try (ResultSet sourceRowCount = sourceStmt.executeQuery(SQLStmtUtils.getCountTableStmt(sourceTable))) {
//				sourceRowCount.next();
//				countSource = sourceRowCount.getInt(1);
//				LogUtils.printLog(logger, "Row count = {}", countSource);
//
//			}
//
//			// get data from the source table as a ResultSet
//			try (ResultSet rsSourceData = sourceStmt.executeQuery(
//				copyWithBatch ? SQLStmtUtils.getSelectStmtByCols(sourceTable, sourceCols, batchId, criteria)
//						: SQLStmtUtils.getSelectStmtByCols(sourceTable, sourceCols, criteria))) {
//
//				Statement destinationStmt = toConn.createStatement();
//				long countDestinationBeforeCopy = 0;
//				try (ResultSet rsRowCount = destinationStmt
//						.executeQuery(SQLStmtUtils.getCountTableStmt(destinationTable))) {
//					rsRowCount.next();
//					countDestinationBeforeCopy = rsRowCount.getInt(1);
//					// logger.info("Starting row count = " +
//					// countDestinationBeforeCopy);
//					LogUtils.printLog(logger, "Starting row count =  {}", countDestinationBeforeCopy);
//				}
//
//				try (SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy(toConn)) {
//
//					// destination connection
//					SQLServerBulkCopyOptions copyOptions = new SQLServerBulkCopyOptions();
//
//					// if block size is defined, set into bulk copy
//					// options
//					if (null != batchSize) {
//						copyOptions.setBatchSize(batchSize);
//					}
//					// keep IDs from original result set
//					copyOptions.setKeepIdentity(true);
//					copyOptions.setCheckConstraints(true);
////					copyOptions.setUseInternalTransaction(true);
//					copyOptions.setBulkCopyTimeout(0);
//
//					// To destination table
//					bulkCopy.setDestinationTableName(destinationTable);
//
//					// Format batch ID in destination table
//					// batch ID handling
//					if (copyWithBatch) {
//						bulkCopy.addColumnMapping(SQLStmtUtils.BATCH_ID_COLUMN_NAME,
//								SQLStmtUtils.BATCH_ID_COLUMN_NAME);
//					}
//
//					for (int i = 0; i < sourceCols.size(); i++) {
//						bulkCopy.addColumnMapping(sourceCols.get(i), destinationCols.get(i));
//					}
//
//					bulkCopy.writeToServer(rsSourceData);
//
//					try (ResultSet destinationRowCount = destinationStmt
//							.executeQuery(SQLStmtUtils.getCountTableStmt(destinationTable))) {
//						destinationRowCount.next();
//						long countDestinationAfterCopy = destinationRowCount.getInt(1);
//
//						LogUtils.printLog(logger, "Ending row count = {}", countDestinationAfterCopy);
//
//						LogUtils.printLog(logger, "{} rows were added.",
//								countDestinationAfterCopy - countDestinationBeforeCopy);
//						//count insert
//						returnInts[0] = (int) (countDestinationAfterCopy - countDestinationBeforeCopy);
//						//count update
//						returnInts[1] = countSource - returnInts[0];
//					}
//
//				}
//
//			}
//			
//		} catch (Exception e) {
//			LogUtils.printException(logger, "Exception occurs in Bulk Copy from SQL process", e);
//			throw new RuntimeException(e);
//		}
//		
//		return returnInts;
//	}
//
//	/**
//	 * 
//	 * @param fromDS
//	 * @param toDS
//	 * @param fromTable
//	 * @param toTable
//	 * @param sourceCols
//	 * @param destinationCols
//	 * @param batchSize if pass null
//	 * 		then commitment control and transaction size must be handled by external caller
//	 * @param batchId
//	 * @param criteria
//	 * @throws Exception
//	 */
//	public static void bulkCopyFromSQL(String fromDS, String toDS, String fromTable, String toTable,
//			List<String> sourceCols, List<String> destinationCols, Integer batchSize, String batchId,
//			String... criteria) throws Exception {
//
//		if (fromDS.isEmpty() || toDS.isEmpty() || fromTable.isEmpty() || toTable.isEmpty()) {
//			LogUtils.printException(logger, "Bulk Copy process info is missing {}-{}-{}-{}", fromDS, toDS, fromTable,
//					toTable);
//			throw new RuntimeException("Bulk Copy process info is missing.");
//		}
//
//		boolean copyWithBatch = batchId != null && !batchId.isEmpty();
//
//		// initialize source and destination info
//		String sourceTable = fromTable;
//		String destinationTable = toTable;
//
//		// bulk copy process
//
//		// Class.forName(sqlDriverClass);
//		try (Connection sourceConnection = DriverManager.getConnection(fromDS)) {
//			try (Statement sourceStmt = sourceConnection.createStatement()) {
//
//				// perform an initial count on the source table
//				long countSource = 0;
//				try (ResultSet sourceRowCount = sourceStmt.executeQuery(SQLStmtUtils.getCountTableStmt(sourceTable))) {
//					sourceRowCount.next();
//					countSource = sourceRowCount.getInt(1);
//					LogUtils.printLog(logger, "Row count = {}", countSource);
//
//				}
//
//				// get data from the source table as a ResultSet
//				try (ResultSet rsSourceData = sourceStmt.executeQuery(
//						copyWithBatch ? SQLStmtUtils.getSelectStmtByCols(sourceTable, sourceCols, batchId, criteria)
//								: SQLStmtUtils.getSelectStmtByCols(sourceTable, sourceCols, criteria))) {
//
//					try (Connection destConn = DriverManager.getConnection(toDS)) {
//
//						try(Statement destinationStmt = destConn.createStatement()){
//							
//
//							long countDestinationBeforeCopy = 0;
//							try (ResultSet rsRowCount = destinationStmt
//									.executeQuery(SQLStmtUtils.getCountTableStmt(destinationTable))) {
//								rsRowCount.next();
//								countDestinationBeforeCopy = rsRowCount.getInt(1);
//								// logger.info("Starting row count = " +
//								// countDestinationBeforeCopy);
//								LogUtils.printLog(logger, "Starting row count =  {}", countDestinationBeforeCopy);
//							}
//	
//							try (SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy(destConn)) {
//	
//								// destination connection
//								SQLServerBulkCopyOptions copyOptions = new SQLServerBulkCopyOptions();
//	
//								// if block size is defined, set into bulk copy
//								// options
//								if (null != batchSize) {
//									copyOptions.setBatchSize(batchSize);
//								}
//								// keep IDs from original result set
//								copyOptions.setKeepIdentity(true);
//								copyOptions.setCheckConstraints(true);
////								copyOptions.setUseInternalTransaction(true);
//								copyOptions.setBulkCopyTimeout(0);
//	
//								// To destination table
//								bulkCopy.setDestinationTableName(destinationTable);
//	
//								// Format batch ID in destination table
//								// batch ID handling
//								if (copyWithBatch) {
//									bulkCopy.addColumnMapping(SQLStmtUtils.BATCH_ID_COLUMN_NAME,
//											SQLStmtUtils.BATCH_ID_COLUMN_NAME);
//								}
//	
//								for (int i = 0; i < sourceCols.size(); i++) {
//									bulkCopy.addColumnMapping(sourceCols.get(i), destinationCols.get(i));
//								}
//	
//								bulkCopy.writeToServer(rsSourceData);
//	
//								try (ResultSet destinationRowCount = destinationStmt
//										.executeQuery(SQLStmtUtils.getCountTableStmt(destinationTable))) {
//									destinationRowCount.next();
//									long countDestinationAfterCopy = destinationRowCount.getInt(1);
//	
//									LogUtils.printLog(logger, "Ending row count = {}", countDestinationAfterCopy);
//	
//									LogUtils.printLog(logger, "{} rows were added.",
//											countDestinationAfterCopy - countDestinationBeforeCopy);
//								}
//	
//							}
//						}
//
//					}
//					
//				}
//				
//			}
//
//		}
//
//	}
	
//	/*
//	 * Param1 file path Param2 target data source connection Param3 encoding
//	 * (can be null) Param4 delimiter string Param5 conditions array (length can
//	 * be 0) Param6 block size (can be null)
//	 */
//	public static void bulkCopyFromCSV(String filePath, String toDS, String encoding, SchemeInfo schemeInfo,
//			Integer batchSize, String batchId, boolean skipFirstLine)
//			throws SQLServerException, SQLException, IOException {
//		
//		if(filePath == null
//				|| toDS == null
//				|| schemeInfo == null){
//			LogUtils.printException(logger, "Input Parameters contain null values");
//			throw new IllegalArgumentException();
//		}
//		
//		File csvFile = new File(filePath);
//		if(csvFile.isDirectory() || !csvFile.exists()){
//			LogUtils.printException(logger, "Failed to open DBF File from path {}", toDS);
//			throw new IOException();
//		}
//		
//		SQLServerBulkCSVFileRecord csvFileRecord = null;
//		List<SchemeTableColumn> cols = schemeInfo.getSchemeTableColumns();
//
//		try {
//			csvFileRecord = new SQLServerBulkCSVFileRecord(filePath
//					,encoding==null ? DEFAULT_CSV_ENCODING : encoding
//					,schemeInfo.getDelimiter()
//					,skipFirstLine);
//
//			for (SchemeTableColumn col : cols) {
//				/*
//				 * addColumnMetadata param1 position param2 column name param3
//				 * jdbcType param3 precision param4 scale
//				 */
//				LogUtils.printLog("CSV Column Metadata: {}-{}({})-{}-{}",col.getSeq()+1,
//						col.getToColumnFormat(),
//						ColumnFormat.valueOf(col.getToColumnFormat()).getValue(),
//						col.getToColumnLength().intValue(),
//						col.getToColumnPrecision().intValue());
//				
//				csvFileRecord.addColumnMetadata(col.getSeq() + 1, null
//						,ColumnFormat.valueOf(col.getToColumnFormat()).getValue()
//						,col.getToColumnLength().intValue()
//						,col.getToColumnPrecision()==null? 0
//								:col.getToColumnPrecision().intValue()<0 ? 0
//										:col.getToColumnPrecision().intValue()
//						);
//
//			}
//
//			// Class.forName(sqlDriverClass);
//			try (Connection destConn = DriverManager.getConnection(toDS)) {
//
//				try (Statement destinationStmt = destConn.createStatement()) {
//					// performance initial count
//					long countDestinationBeforeCopy = 0;
//
//					try (ResultSet rsRowCount = destinationStmt
//							.executeQuery(SQLStmtUtils.getCountTableStmt(schemeInfo.getDestination()))) {
//						rsRowCount.next();
//						countDestinationBeforeCopy = rsRowCount.getInt(1);
//						LogUtils.printLog(logger, "Starting row count = {}", countDestinationBeforeCopy);
//					}
//
//					try (SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy(destConn)) {
//						bulkCopy.setDestinationTableName(schemeInfo.getDestination());
//						{
//
//							try {
//								// Write from the source to the destination.
//								bulkCopy.writeToServer(csvFileRecord);
//							} catch (Exception e) {
//								// Handle any errors that may have occurred.
//								LogUtils.printException(logger, String.format(
//										"Bulk Copy from CSV[%s] to DB[%s] has encountered error.", filePath, toDS), e);
//								throw new RuntimeException(e);
//							}
//						}
//
//						// Perform a final count on the destination
//						// table to see how many rows were added.
//						long countDestinationAfterCopy = 0;
//						try (ResultSet rsRowCount = destinationStmt
//								.executeQuery(SQLStmtUtils.getCountTableStmt(schemeInfo.getDestination()))) {
//							rsRowCount.next();
//							countDestinationAfterCopy = rsRowCount.getInt(1);
//							LogUtils.printLog(logger, "Ending row count = {}.", 
//									countDestinationAfterCopy);
//							LogUtils.printLog(logger, "{} rows were added.",
//									(countDestinationAfterCopy - countDestinationBeforeCopy));
//						}
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		} finally {
//			if (csvFileRecord != null) {
//				try {
//					csvFileRecord.close();
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//				;
//			}
//		}
//	}
	
//	/**
//	 * 
//	 * @param filePath
//	 * @param destConn
//	 * @param encoding
//	 * @param schemeInfo
//	 * @param batchSize
//	 * @param batchId
//	 * @param skipFirstLine
//	 * @throws SQLServerException
//	 * @throws SQLException
//	 * @throws IOException
//	 */
//	public static int bulkCopyFromCSVConn(String filePath, Connection destConn, String encoding, SchemeInfo schemeInfo,
//			Integer batchSize, String batchId, boolean skipFirstLine) 
//					throws SQLServerException, SQLException, IOException{
//		int rs = 0;
//		
//		File csvFile = new File(filePath);
//		if(csvFile.isDirectory() || !csvFile.exists()){
//			LogUtils.printException(logger, "Failed to open DBF File from path {}", filePath);
//			throw new IOException();
//		}
//		
//		SQLServerBulkCSVFileRecord csvFileRecord = null;
//		List<SchemeTableColumn> cols = schemeInfo.getSchemeTableColumns();
//
//		try {
//			csvFileRecord = new SQLServerBulkCSVFileRecord(filePath
//					,encoding==null ? DEFAULT_CSV_ENCODING : encoding
//					,schemeInfo.getDelimiter()
//					,skipFirstLine);
//
//			for (SchemeTableColumn col : cols) {
//				/*
//				 * addColumnMetadata param1 position param2 column name param3
//				 * jdbcType param3 precision param4 scale
//				 */
//				LogUtils.printLog("CSV Column Metadata: {}-{}({})-{}-{}",col.getSeq()+1,
//						col.getToColumnFormat(),
//						ColumnFormat.valueOf(col.getToColumnFormat()).getValue(),
//						col.getToColumnLength().intValue(),
//						col.getToColumnPrecision().intValue());
//				
//				csvFileRecord.addColumnMetadata(col.getSeq() + 1, null
//						,ColumnFormat.valueOf(col.getFromColumnFormat()).getValue()
//						,col.getFromColumnLength().intValue()
//						,col.getToColumnPrecision().intValue()
//						);
//
//			}
//
//			try (Statement destinationStmt = destConn.createStatement()) {
//				
//
//				// performance initial count
//				long countDestinationBeforeCopy = 0;
//				try (ResultSet rsRowCount = destinationStmt
//						.executeQuery(SQLStmtUtils.getCountTableStmt(schemeInfo.getDestination()))) {
//					rsRowCount.next();
//					countDestinationBeforeCopy = rsRowCount.getInt(1);
//					LogUtils.printLog(logger, "Starting row count = {}", countDestinationBeforeCopy);
//				}
//
//				try (SQLServerBulkCopy bulkCopy = new SQLServerBulkCopy(destConn)) {
//					bulkCopy.setDestinationTableName(schemeInfo.getDestination());
//					{
//
//						try {
//							// Write from the source to the destination.
//							bulkCopy.writeToServer(csvFileRecord);
//						} catch (Exception e) {
//							// Handle any errors that may have occurred.
//							LogUtils.printException(logger, "Bulk Copy from CSV [{}] to DB[{}] has encountered error.", 
//									filePath, destConn.getClientInfo(), e);
//							throw new RuntimeException(e);
//						}
//					}
//
//					// Perform a final count on the destination
//					// table to see how many rows were added.
//					long countDestinationAfterCopy = 0;
//					try (ResultSet rsRowCount = destinationStmt
//							.executeQuery(SQLStmtUtils.getCountTableStmt(schemeInfo.getDestination()))) {
//						rsRowCount.next();
//						countDestinationAfterCopy = rsRowCount.getInt(1);
//						LogUtils.printLog(logger, "Ending row count = {}.", 
//								countDestinationAfterCopy);
//						LogUtils.printLog(logger, "{} rows were added.",
//								(countDestinationAfterCopy - countDestinationBeforeCopy));
//						rs = (int) (countDestinationAfterCopy - countDestinationBeforeCopy);
//					}
//				}
//			}
//			return rs;
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		} finally {
//			if (csvFileRecord != null) {
//				try {
//					csvFileRecord.close();
//				} catch (Exception e) {
//					throw new RuntimeException(e);
//				}
//				;
//			}
//		}
//	}
//	
	/**
	 * 
	 * @param fromConn - a SQL Server JDBC Connection
	 * @param toConn - a Oracle JDBC Connnection
	 * @param schemeInfo
	 * @param batchSize
	 * @param batchId
	 * @param criteria
	 * @return int[3]
	 * 			0 - source record count; 1 - add record count; 2 delete record count
	 * @throws SQLServerException
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 */
	public static int[] bulkCopyFromSQLToOracle(Connection fromConn, Connection toConn
			,SchemeInfo schemeInfo, Integer batchSize
			,String batchId, String[] criteria) 
					throws SQLServerException, SQLException, BatchUpdateException, IllegalArgumentException{
		return bulkCopyFromSQLToOracle(fromConn, toConn, schemeInfo, batchSize, batchId, criteria, true);
	}

	
	public static int[] bulkCopyFromSQLToOracle(Connection fromConn, Connection toConn
			,SchemeInfo schemeInfo, Integer batchSize
			,String batchId, String[] criteria, boolean removeDuplication) 
					throws SQLServerException, SQLException, BatchUpdateException, IllegalArgumentException{
		return bulkCopyFromSQLToOracle(fromConn, toConn, schemeInfo, batchSize, batchId, criteria, null, true);
	}
		
	public static final ThreadLocal<Boolean> CURRENT_THREAD_STATUS_NULLABLE = new ThreadLocal<Boolean>();

	public static int[] bulkCopyFromSQLToOracle(Connection fromConn, Connection toConn
			,SchemeInfo schemeInfo, Integer batchSize
			,String batchId, String[] criteria, Map<String,String> branchCodeMap, boolean removeDuplication) 
					throws SQLServerException, SQLException, BatchUpdateException, IllegalArgumentException{
		// Null parameter validation 
		if(fromConn == null
				|| toConn == null
				|| schemeInfo == null
				|| criteria == null
				|| criteria.length < 0){
			LogUtils.printException(logger, "Input Parameters contain null values");
			throw new IllegalArgumentException();
		}
		
//		// Connection validity validation
//		try{
//			fromConn.isValid(0);
//			toConn.isValid(0);
//		}
//		catch(SQLServerException e){
//			LogUtils.printException(logger, "SQL Bulk Copy Connection is not valid", e);
//			throw new RuntimeException(e);
//		}
		
		int[] returnInts = {0,0,0,0};
		
		if (removeDuplication)
		{
			try{
				int[] duplReturnInts = handleDuplicatedRecords(fromConn, toConn, schemeInfo, batchSize, criteria);
				returnInts[2] = duplReturnInts[0] + duplReturnInts[1] ;
				returnInts[3] = duplReturnInts[1] ;
			}catch(SQLException e){
				LogUtils.printException(e.getMessage(),e);
				throw e;
	//			 throw new RuntimeException(e);
			}
		}
		
//		LogUtils.printLog(
//				"Start to copy Table for {} {}",
//				schemeInfo.getSource(), schemeInfo.getDestination());
		
		try (Statement sourceStmt = fromConn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
				Statement destinationStmt = toConn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
		) {

//			long countSource = 0;
			String sourceTable = schemeInfo.getSource();
			String destinationTable = schemeInfo.getDestination();
			List<String> sourceCols = new ArrayList<String>();
			List<String> destinationCols = new ArrayList<String>();
			List<SchemeTableColumn> tableCols = schemeInfo.getSchemeTableColumns();
			
			int[] nullables = null ;
			if (tableCols.isEmpty()) {
//				LogUtils.printException(null,
//						"Scheme Table Columns List is empty");
//				throw new RuntimeException("Scheme Table Columns List is empty");
				String metaDataSQL = SQLStmtUtils.getMetaDataStmt(destinationTable);
				ResultSet destRs = destinationStmt.executeQuery(metaDataSQL) ;

				sourceCols.add("*"); 

				int columnCount = destRs.getMetaData().getColumnCount();
				nullables = new int[columnCount];
				for(int i = 1; i <= columnCount;i++){
					SchemeTableColumn col = new SchemeTableColumn();
					String colunm = destRs.getMetaData().getColumnLabel(i) ;
					ColumnFormat format = ColumnFormat.touch(destRs.getMetaData().getColumnType(i));
					if (format == null)
					{
						format = ColumnFormat.JDBC_VARCHAR;
					}
					
					
					col.setFromColumn("");
					col.setToColumn(colunm);
					col.setToColumnFormat(format.name());
					col.setSeq(i-1);
					destinationCols.add(colunm);
					
					nullables[i-1] = destRs.getMetaData().isNullable(i);
					if (colunm.equalsIgnoreCase("status"))
					{
						if (nullables[i-1] == ResultSetMetaData.columnNullable)
						{
							CURRENT_THREAD_STATUS_NULLABLE.set(Boolean.TRUE);
						}
						else
						{
							CURRENT_THREAD_STATUS_NULLABLE.set(Boolean.FALSE);
						}
					}
//					
				}
				
			}
			else
			{
				// format column name list
				for (SchemeTableColumn col : tableCols) {
					sourceCols.add(col.getFromColumn()); 
					destinationCols.add(col.getToColumn());
				}
				nullables = new int[tableCols.size()];
			}
			
			
			
//			try (ResultSet rsRowCount = sourceStmt.executeQuery(SQLStmtUtils
//					.getCountTableStmt(sourceTable, criteria))) {
//				rsRowCount.next();
//				countSource = rsRowCount.getInt(1);
//				LogUtils.printLog(
//						"Totally {} Records from table {} need to copy",
//						countSource, sourceTable);
//			}

			// process result set
//			Object[][] data;

			try (ResultSet rowData = sourceStmt.executeQuery(SQLStmtUtils
					.getSelectStmt(sourceTable, sourceCols, false, criteria))) {
//				LogUtils.printLog(logger, "Start to process SQL Select Result Set.");

				// initialize two-dimensional array for result set
//				rowData.last();
//				int numRows = rowData.getRow();
//				rowData.beforeFirst();
//				data = new Object[numRows][numCols];

				try (PreparedStatement insertStmt = toConn
						.prepareStatement(SQLStmtUtils.getOracleInsertStmtByCols(destinationTable, destinationCols, batchId));
					) {
					
//					// process result set into target connection
//					long countDestinationBeforeCopy = 0;
//					try (ResultSet rsRowCount = destinationStmt.executeQuery(SQLStmtUtils.getCountTableStmtForOracle(destinationTable))) {
//						rsRowCount.next();
//						countDestinationBeforeCopy = rsRowCount.getInt(1);
//						LogUtils.printLog(logger,"Starting row count = {}", countDestinationBeforeCopy);
//					}
					
					int i = 0;
					int batchControl = 0 ;
					// process from Conn Result Set
					String columnName;
					int numCols = rowData.getMetaData().getColumnCount(); 

					int totalInsertCount = 0;
					while (rowData.next()) {
//						Object[] row = new Object[numCols];
						for (int j = 1; j <= numCols; j++) {
							columnName= rowData.getMetaData().getColumnName(j);
							Object object = rowData.getObject(j);
							
//							row[j - 1] = (object == null) ? null : object;
							if (object == null) {
								insertStmt.setNull(j, ColumnFormat.JDBC_NULL.getValue());
							} else {
								
							    if(object instanceof String) {
							    	String str =  (String) object;
							    	
									if(branchCodeMap != null &&
											StringUtils.equalsIgnoreCase(columnName,"branch_code") && object != null &&
											StringUtils.isNotBlank(branchCodeMap.get(str))){
										str	 = branchCodeMap.get(str);
									}	
									
							    	if(StringUtils.isBlank(str)){
										if (nullables[j-1] == ResultSetMetaData.columnNullable)
											insertStmt.setNull(j,ColumnFormat.JDBC_NULL.getValue());
										else
											insertStmt.setString(j, " ");
							    	} else{
							    		insertStmt.setString(j,str);
							    	}
							 	} else {
							 		insertStmt.setObject(j, object);
							 	}
							}
						}
						
//						data[i] = row;
						++i;
						
						insertStmt.addBatch();
						
						batchControl++;
						if(batchSize != null && batchControl >= batchSize){
							
							LogUtils.printLog(logger, schemeInfo.getDestination() +" commit: {} : {} / {}", i , batchControl, batchSize);
							
							int[] executeBatchCount = insertStmt.executeBatch();
							totalInsertCount += executeBatchCount.length;

							batchControl=0;
						}
					}
					
					if(insertStmt!=null && batchControl > 0){
						LogUtils.printLog(logger, schemeInfo.getDestination() +" commit: {} : {} / {}", i , batchControl, batchSize);
						int[] executeBatchCount = insertStmt.executeBatch();
						totalInsertCount += executeBatchCount.length;
//						logger.info("executeBatch: {}", IntStream.of(executeBatch).sum());
					}

//					if (!removeDuplication)
//					{
//						returnInts[0] = totalInsertCount ;
//					}
					returnInts[0] = totalInsertCount ;
					if (returnInts[2] == 0)
					{
						returnInts[2] = totalInsertCount ;
					}

//					try (ResultSet destinationRowCount = destinationStmt
//							.executeQuery(SQLStmtUtils.getCountTableStmtForOracle(destinationTable))) {
//						destinationRowCount.next();
//						long countDestinationAfterCopy = destinationRowCount
//								.getInt(1);
//
//                        LogUtils.printLog(logger, "Ending row count = "
//                                + countDestinationAfterCopy);
//                        LogUtils.printLog(logger, (countDestinationAfterCopy - countDestinationBeforeCopy)
//                                + " rows were added.");
//
//					}
	
//					LogUtils.printLog(logger, "End of process Result Set output.");
				}
			}
		}
		catch(Exception e){
			LogUtils.printException(logger, "JDBCUtils.bulkCopyFromSQLToOracle() encounter exception.", e);
			throw new RuntimeException(e);
		}
		
		return returnInts;
	}
	
	/**
	 *            select condition eg:"status='C'"
	 * @return int = number of record deleted
	 * @throws IllegalArgumentException 
	 * @throws SQLServerException 
	 * @throws SQLException 
	 * @throws Exception
	 */	
	public static int deleteByBranchAndBizDate(Connection conn, String table, 
			String branchCode, Date businessDate) throws SQLServerException, IllegalArgumentException, SQLException 
	{
		if (businessDate != null)
		{
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    	StringBuffer dateConditionStrBuf = new StringBuffer("convert(date,business_date) = '").append(df.format(businessDate)).append("')");
	    	String dateConditionStr = dateConditionStrBuf.toString().substring(0,dateConditionStrBuf.length()-1);
			
			return deleteByBranchAndBizDate(conn, table, branchCode, dateConditionStr);
		}
		else
		{
			return deleteByBranchAndBizDate(conn, table, branchCode, (String) null);
		}
	}

	public static int deleteByBranchAndBizDate(Connection conn, String table, 
			String branchCode, String dateConditionStr) 
						throws SQLException, SQLServerException, IllegalArgumentException{
		if(conn == null
				|| table == null
				|| branchCode == null
//				|| businessDate == null
		){
			LogUtils.printException(logger, "Input Parameters contain null values");
			throw new IllegalArgumentException();
		}
		
//		try{
//			conn.isValid(0);
//		}
//		catch(SQLException e){
//			throw e;
//		}
		
		String branCode = String.format("BRANCH_CODE = \'%s\'",branchCode);
//		String bizDate = String.format("CONVERT(VARCHAR(8),BUSINESS_DATE,112) = \'%s\'", DateUtil.format(businessDate, "yyyyMMdd"));
		String deleteStmt = dateConditionStr == null? SQLStmtUtils.getDeleteSQL(table, branCode) : SQLStmtUtils.getDeleteSQL(table, branCode, dateConditionStr);

		int deleteCount = 0;
		
		try(Statement stmt = conn.createStatement()){
			deleteCount = stmt.executeUpdate(deleteStmt);
		}
		
		return deleteCount;
	}
	
	
//	/**
//	 *            select condition eg:"status='C'"
//	 * @return int = number of record deleted
//	 * @throws SQLException 
//	 * @throws Exception
//	 */	
//	public static int deleteByBranchAndBizDateOracle(Connection oracleConn, String table, 
//				String branchCode, Date businessDate) 
//						throws SQLException, SQLServerException, IllegalArgumentException{
//		if(oracleConn == null
//				|| table == null
//				|| branchCode == null
//				|| businessDate == null){
//			LogUtils.printException(logger, "Input Parameters contain null values");
//			throw new IllegalArgumentException();
//		}
//		
////		try{
////			oracleConn.isValid(0);
////		}
////		catch(SQLException e){
////			throw e;
////		}
//		
//		String branCode = String.format("BRANCH_CODE = \'%s\'",branchCode);
//		String bizDate = String.format("TO_CHAR(BUSINESS_DATE,\'yyyyMMdd\') = \'%s\'", DateUtil.format(businessDate, "yyyyMMdd"));
//		String deleteStmt = SQLStmtUtils.getDeleteSQL(table, branCode, bizDate);
//		int deleteCount = 0;
//		
//		LogUtils.printLog(logger, "Start to execute delete Stmt [{}]", deleteStmt);
//		try(Statement stmt = oracleConn.createStatement()){
//			deleteCount = stmt.executeUpdate(deleteStmt);
//		}
//		
//		LogUtils.printLog(logger, "{} records deleted from table [{}]", deleteCount, table);
//		
//		return deleteCount;
//	}
	
	
	public static int deleteByConditions(Connection conn, String table, String[] conditions)
			throws SQLException, SQLServerException, IllegalArgumentException
	{
		
		//parameters checking
		if(conn == null
				|| table == null
				|| conditions == null){
			LogUtils.printException(logger, "Input Parameters contain null values");
			throw new IllegalArgumentException();
		}
		
		//connection checking
//		try{
//			conn.isValid(0);
//		}
//		catch(SQLException e){
//			throw e;
//		}
		
		String deleteStmt = SQLStmtUtils.getDeleteSQL(table, conditions);
		int deleteCount = 0;
		
//		LogUtils.printLog(logger, "Start to execute delete Stmt [{}]", deleteStmt);
//		
		try(Statement stmt = conn.createStatement()){
			deleteCount = stmt.executeUpdate(deleteStmt);
		}
		
//		LogUtils.printLog(logger, "{} records deleted from table [{}]", deleteCount, table);
//		
		return deleteCount;
	}
	
	/**
	 * 
	 * @param fromConn
	 * @param toConn
	 * @param info
	 * @param criteria
	 * @return [0] insert count; [1] update count
	 * @throws SQLException
	 */
	public static int[] handleDuplicatedRecords(Connection fromConn, Connection toConn, SchemeInfo info, 
			Integer batchSize, String...criteria) 
			throws SQLException{
		int[] returnInts = new int[2];
		
		try(Statement stmt = fromConn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)){
			
			String selectSQL = SQLStmtUtils.getSelectStmt(info.getSource(), 
											Arrays.asList(info.getSrcKeyColumns() == null ? 
													DEFAULT_ID:info.getSrcKeyColumns().split(COMMA)), 
											false, criteria);
//			LogUtils.printLog(logger, "HandleDuplicatedRecords: {}", selectSQL);
			
			String countSQL = SQLStmtUtils.getCountTableStmt(info.getSource(),criteria);
//			LogUtils.printLog(logger, "HandleDuplicatedRecords: {}", countSQL);
			
			int rowCount = 0;
			try (ResultSet rsRowCount = stmt.executeQuery(countSQL)) {
				rsRowCount.next();
				rowCount = rsRowCount.getInt(1);
//				LogUtils.printLog("BulkCopyFromSQL Extraction data SQL: {},row count ={} ", countSQL, rowCount);
			}
			
			try (ResultSet rsSourceData = stmt.executeQuery(selectSQL)){
				int updateCount = delRepeatingData(toConn, info.getDestination(),
						info.getSrcKeyColumns().split(COMMA),
						info.getDestKeyColumns().split(COMMA), 
						batchSize,
						rsSourceData);
				
				returnInts[0] = rowCount - updateCount;
				returnInts[1] = updateCount;
			}
			catch(SQLException e){
				LogUtils.printException(logger, "Destination table {} does not have ROWGUID", info.getDestination());
			}
		}
		return returnInts;
	}
	
   public static boolean checkSumBySchemeInfo(Connection fromConn, Connection toConn, 
		   String branchCode, SchemeInfo schemeInfo, String...criteria)
   			throws SQLServerException, SQLException, IllegalArgumentException {

		//parameters checking
		if(fromConn == null
			|| toConn == null
			|| schemeInfo == null
			|| branchCode == null){
		LogUtils.printException(logger, "Input Parameters contain null values");
			throw new IllegalArgumentException();
		}
		
		//connection checking
//		try{
////			fromConn.isValid(0);
////			toConn.isValid(0);
//		}
//		catch(SQLException e){
//			LogUtils.printException(logger, "SQL Bulk Copy Connection is not valid", e);
//			throw new RuntimeException(e);
//		}
		String srcCheckSumCols = schemeInfo.getSrcCheckSumCols();
        String destCheckSumCols = schemeInfo.getDestCheckSumCols();
        
        if (null == srcCheckSumCols || null == destCheckSumCols) {
            return true;
        }
        
        String toSql = SQLStmtUtils.getCheckSumStmt(destCheckSumCols, schemeInfo.getDestination(), criteria);
        String fromSql = SQLStmtUtils.getCheckSumStmt(srcCheckSumCols, schemeInfo.getSource(), criteria);
        LogUtils.printLog(logger, "toSql : {}    fromSql : {}", toSql, fromSql);

        try (PreparedStatement toPs = toConn.prepareStatement(toSql);
             PreparedStatement fromPs = fromConn.prepareStatement(fromSql);
             ResultSet toRs = toPs.executeQuery();
             ResultSet fromRs = fromPs.executeQuery();
        ) {
            int col = toRs.getMetaData().getColumnCount();
            StringBuffer toCol = new StringBuffer();
            StringBuffer fromCol = new StringBuffer();
            while (toRs.next()) {
                for (int i = 0; i < col; i++) {
                    toCol.append(toRs.getString(i + 1));
                }
            }
            while (fromRs.next()) {
                for (int j = 0; j < col; j++) {
                    fromCol.append(fromRs.getString(j + 1));
                }
            }
            String toSumCol = toCol.toString();
            String fromSumCol = fromCol.toString();
            if (toSumCol.equals(fromSumCol)) {
                LogUtils.printLog(logger, "   End : {}=={}  checkSumColumn Success!", toSumCol, fromSumCol);
                return true;
            } else {
                LogUtils.printLog(logger, "   End : {}=={}  checkSumColumn false!", toSumCol, fromSumCol);
            }
        } catch (SQLException e) {
            throw new RuntimeException("checkSumColumn ERROR!");
        }
        return false;
    }
   
   public static boolean checkSumBySchemeInfoToEDW(Connection fromConn, Connection toConn, 
		   String branchCode, SchemeInfo schemeInfo, String[] oracleConditions, String[] conditions)
   			throws SQLServerException, SQLException, IllegalArgumentException {

		//parameters checking
		if(fromConn == null
			|| toConn == null
			|| schemeInfo == null
			|| branchCode == null){
		LogUtils.printException(logger, "Input Parameters contain null values");
			throw new IllegalArgumentException();
		}
		
		//connection checking
//		try{
////			fromConn.isValid(0);
////			toConn.isValid(0);
//		}
//		catch(SQLException e){
//			LogUtils.printException(logger, "SQL Bulk Copy Connection is not valid", e);
//			throw new RuntimeException(e);
//		}
		String srcCheckSumCols = schemeInfo.getSrcCheckSumCols();
        String destCheckSumCols = schemeInfo.getDestCheckSumCols();
        
        if (null == srcCheckSumCols || null == destCheckSumCols) {
            return true;
        }
        
        String toSql = SQLStmtUtils.getCheckSumStmtToEDW(destCheckSumCols, schemeInfo.getDestination(), oracleConditions);
        String fromSql = SQLStmtUtils.getCheckSumStmt(srcCheckSumCols, schemeInfo.getSource(), conditions);
        LogUtils.printLog(logger, "toSql : {}    fromSql : {}", toSql, fromSql);

        try (PreparedStatement toPs = toConn.prepareStatement(toSql);
             PreparedStatement fromPs = fromConn.prepareStatement(fromSql);
             ResultSet toRs = toPs.executeQuery();
             ResultSet fromRs = fromPs.executeQuery();
        ) {
            int col = toRs.getMetaData().getColumnCount();
            StringBuffer toCol = new StringBuffer();
            StringBuffer fromCol = new StringBuffer();
            while (toRs.next()) {
                for (int i = 0; i < col; i++) {
                    toCol.append(toRs.getString(i + 1));
                }
            }
            while (fromRs.next()) {
                for (int j = 0; j < col; j++) {
                    fromCol.append(fromRs.getString(j + 1));
                }
            }
            String toSumCol = toCol.toString();
            String fromSumCol = fromCol.toString();
            if (toSumCol.equals(fromSumCol)) {
                LogUtils.printLog(logger, "   End : {}=={}  checkSumColumn Success!", toSumCol, fromSumCol);
                return true;
            } else {
                LogUtils.printLog(logger, "   End : {}=={}  checkSumColumn false!", toSumCol, fromSumCol);
            }
        } catch (SQLException e) {
            throw new RuntimeException("checkSumColumn ERROR!");
        }
        return false;
    }
	   
//	public static void backupDataByBatchId(String ds, String originalTable, String histTable, String batchId)
//			throws SQLServerException, SQLException {
//
//	}
//
//	public static void backupDataByConditions(String ds, String originalTable, String histTable, String... conditions)
//			throws SQLServerException, SQLException {
//
//	}
//	
//	public static String getCSVFilePath(String fileRoot, String branchCode, String table, Date fileDate){
//		return MessageFormat.format("{0}{1}{2}{3}{4}{5}.txt", 
//						fileRoot, 
//						File.separator,
//						branchCode,
//						File.separator,
//						DateUtil.format(fileDate, "yyyyMMdd")
//						,table);
//	}
//	
//	public static String getCSVFilePath(String fileRoot,String branchCode, Date fileDate){
//		return MessageFormat.format("{0}{1}{2}{3}{4}", 
//						fileRoot,
//						File.separator,
//						branchCode,
//						File.separator,
//						DateUtil.format(fileDate, "yyyyMMdd")
//						);
//	}
//	
//	public static String getBusinessDate(Date date){
//		Date startDate = DateUtil.trimTimePart(date);
//		
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(date);
//		cal.set(Calendar.HOUR_OF_DAY, 4);
//		cal.set(Calendar.MINUTE, 0);
//		cal.set(Calendar.SECOND, 0);
//		cal.set(Calendar.MILLISECOND, 0);
//		
//		Date cutOffDate = cal.getTime();
//		
//		if(DateUtil.compareDate(startDate, cutOffDate, date)){
//			cal.setTime(date);
//			cal.add(Calendar.DATE, -1);
//			return DateUtil.format(cal.getTime(),"yyyyMMdd");
//		}
//		else{
//			return DateUtil.format(date,"yyyyMMdd");
//		}
//		
//	}
//
//
//	public static LocalDate splitDate(Date date,String cutOffTime){
//		if(StringUtils.isBlank(cutOffTime)){
//			cutOffTime = "16:00:00";
//		}
//		LocalDate businessDate;
//		LocalDateTime trancDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
//		LocalTime trancTimeLocalTime = trancDateTime.toLocalTime();
//		LocalTime cutOffTimeLocalTime = null;
//		try {
//			 cutOffTimeLocalTime = LocalTime.parse(cutOffTime);
//		}catch (Exception e){
//			LogUtils.printException("parse cutOfftime Excepiton ，user 16:00:00 defalut",e);
//			cutOffTimeLocalTime = LocalTime.parse("16:00:00");
//		}
//
//		if(trancTimeLocalTime.isAfter(cutOffTimeLocalTime)) {
//			businessDate = trancDateTime.toLocalDate();
//		} else {
//			businessDate =  trancDateTime.toLocalDate().plusDays(-1);
//		}
//		LocalDate now =  LocalDate.now();
//		if(businessDate.isAfter(now)){
//			businessDate = now;
//		}
//		return businessDate;
//	}
	
	public static int updateBlankStatusByBranchCode(Connection conn, String table, String branchCode) throws SQLException
	{
		return updateBlankStatusByBranchCodeAndStatus(conn, table, branchCode, null);
	}
	public static int updateBlankStatusByBranchCodeAndStatus(Connection conn, String table, String branchCode, String status) throws SQLException
	{
        String[] initConditions = status == null ? new String[]{" branch_code  = \'" + branchCode + "\'"} 
        									: new String[] {" LTRIM(RTRIM(status)) = '"+status+"'", " branch_code  = \'" + branchCode + "\'"};

        String updateBlankStatusSql = SQLStmtUtils.getUpdateBlankSqlByStatus(table,initConditions);
        return updateTable(conn, updateBlankStatusSql);
	}		
	public static int updateBlankStatusByConditions(Connection conn, String table, String[] conditions) throws SQLException
	{
        String updateBlankStatusSql = SQLStmtUtils.getUpdateBlankSqlByStatus(table,conditions);
        return updateTable(conn, updateBlankStatusSql);
	}
	public static int updatePendingStatusByBranchCode(Connection conn, String table, String branchCode) throws SQLException
	{
		return updatePendingStatusByBranchCodeAndStatus(conn, table, branchCode, null);
	}
	public static int updatePendingStatusByBranchCodeAndStatus(Connection conn, String table, String branchCode, String status) throws SQLException
	{
        String[] initConditions = status == null ? new String[]{" branch_code  = \'" + branchCode + "\'"} 
        									: new String[] {" LTRIM(RTRIM(status)) = '"+status+"'", " branch_code  = \'" + branchCode + "\'"};

        String updatePendingStatusSql = SQLStmtUtils.getUpdatePendingStatusSql(table,initConditions);
        return updateTable(conn, updatePendingStatusSql);
	}	
	public static int updatePendingStatusByConditions(Connection conn, String table, String[] conditions) throws SQLException
	{
        String updatePendingStatusSql = SQLStmtUtils.getUpdatePendingStatusSql(table,conditions);
        return updateTable(conn, updatePendingStatusSql);
	}
	public static int updateCompleteStatusByBranchCode(Connection conn, String table, String branchCode) throws SQLException
	{
		return updateCompleteStatusByBranchCodeAndStatus
				(conn, table, branchCode, null);
	}
	public static int updateCompleteStatusByBranchCodeAndStatus(Connection conn, String table, String branchCode, String status) throws SQLException
	{
        String[] initConditions = status == null ? new String[]{" branch_code  = \'" + branchCode + "\'"} 
        									: new String[] {" LTRIM(RTRIM(status)) = '"+status+"'", " branch_code  = \'" + branchCode + "\'"};

        String updatePendingStatusSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(table,initConditions);
        return updateTable(conn, updatePendingStatusSql);
	}
	public static int updateCompleteStatusByConditions(Connection conn, String table, String[] conditions) throws SQLException
	{
        String updatePendingStatusSql = SQLStmtUtils.getUpdateCompleteSqlByStatus(table,conditions);
        return updateTable(conn, updatePendingStatusSql);
	}
	
	public static int updateTable(Connection conn, String query) throws SQLException {
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(query);
			return stmt.executeUpdate();
//		}catch(Exception e){
//			LogUtils.printException("---updateTable error---:",e);
//			return -1 ;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	

	public static int getTableCount(Connection conn, String table, 
			String[] conditions) 
						throws SQLException, SQLServerException, IllegalArgumentException{
		if(conn == null
				|| table == null
		){
			LogUtils.printException(logger, "Input Parameters contain null values");
			throw new IllegalArgumentException();
		}


		try (Statement stmt = conn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {

	        String selectCountSql = SQLStmtUtils.getCountTableStmt(table,conditions);

			int countSource = 0;

			try (ResultSet rsRowCount = stmt.executeQuery(selectCountSql)) {
				rsRowCount.next();
				countSource = rsRowCount.getInt(1);
				return countSource;
			}
		}

	}
	

}

