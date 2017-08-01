package com.maxim.pos.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.luhuiguo.chinese.ChineseUtils;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ColumnFormat;
import com.maxim.util.DateUtil;
import com.microsoft.sqlserver.jdbc.SQLServerException;

public class JavaDBFUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(JavaDBFUtils.class);

	private static final String DBF_DATE_TIME = "yyyyMMdd HHmmss";
	private static final String DBF_DATE = "yyyyMMdd";
	private static final String REGEX_FILENAME = "M(\\d+)_(\\d+)_(\\w+)";

	private static final String DEFAULT_CHARSET = "Big5-HKSCS";
	
	private static final Timestamp EMPTY_TIMESTAMP =  Timestamp.valueOf("1900-01-01 00:00:00");

	private static final String ROWGUID = "ROWGUID";

//	public static void bulkCopyFromDBFToSQL(String fromDS, String toDS,
//			SchemeInfo schemeInfo, Integer blockSize, String batchId)
//			throws SQLException, SQLServerException, IOException {
//		if (null == schemeInfo) {
//			throw new RuntimeException("Invalid SchemeInfo");
//		}
//
//		if (schemeInfo.getSchemeTableColumns().isEmpty()) {
//			LogUtils.printException(logger,
//					"Poll Scheme {} is not properly setup", schemeInfo.getId());
//			throw new RuntimeException("Table column list is null");
//		}
//		
//		//open File
//		File dbfFile = new File(fromDS);
//		if(dbfFile.isDirectory() || !dbfFile.exists()){
//			LogUtils.printException(logger, "Failed to open DBF File from path {}", toDS);
//			throw new IOException();
//		}
//
//		// initialize source and destination info
//		List<String> sourceCols = new ArrayList<String>();
//		List<String> destinationCols = new ArrayList<String>();
//
//		String destinationTable = schemeInfo.getDestination();
//		List<SchemeTableColumn> colEntities = schemeInfo
//				.getSchemeTableColumns();
//
//		// Sort the scheme table column list
//		Collections.sort(colEntities, new Comparator<SchemeTableColumn>() {
//			@Override
//			public int compare(SchemeTableColumn o1, SchemeTableColumn o2) {
//				return o1 == null ? -1 : o2 == null ? 1 : o1.getSeq()
//						.compareTo(o2.getSeq());
//			}
//		});
//
//		LogUtils.printLog("Poll Scheme Columns:{}", colEntities);
//
//		for (SchemeTableColumn e : colEntities) {
//			sourceCols.add(e.getFromColumn());
//			destinationCols.add(e.getToColumn());
//		}
//
//		try {
//
//			// Class.forName(className);
//			try (Connection destinationConnection = DriverManager
//					.getConnection(toDS)) {
//
//				try (Statement stmt = destinationConnection.createStatement(
//						ResultSet.TYPE_SCROLL_INSENSITIVE,
//						ResultSet.CONCUR_READ_ONLY)) {
//
////					destinationConnection.setAutoCommit(false);
//
//					long countDestinationBeforeCopy = 0;
//					try (ResultSet rsRowCount = stmt.executeQuery(SQLStmtUtils
//							.getCountTableStmt(destinationTable))) {
//						rsRowCount.next();
//						countDestinationBeforeCopy = rsRowCount.getInt(1);
//						logger.info("Starting row count = "
//								+ countDestinationBeforeCopy);
//					}
//
//					try (PreparedStatement insertStmt = destinationConnection
//							.prepareStatement(SQLStmtUtils.getInsertStmtByCols(
//									destinationTable, destinationCols, batchId))) {
//
//						List<Map<String, Object>> results = readDBF(dbfFile, DEFAULT_CHARSET);
//						for (Map<String, Object> map : results) {
//							for (SchemeTableColumn colEntity : colEntities) {
//
//								String colDataType = colEntity
//										.getToColumnFormat();
//								int numCol = colEntity.getSeq().intValue() + 1;
//								String nameCol = colEntity.getFromColumn();
//
//								// Auditable fields staging DB has
//								if (nameCol.equalsIgnoreCase(ROWGUID)) {
//									insertStmt.setObject(numCol,
//											UUID.randomUUID().toString()
//													.getBytes());
//									continue;
//								}
//
//								Object obj = map.get(nameCol);
//
//								if (obj == null) {
//									insertStmt.setNull(numCol,
//											ColumnFormat.JDBC_NULL.getValue());
//								} else if (colDataType
//										.equals(ColumnFormat.JDBC_DOUBLE
//												.toString())) {
//									insertStmt.setDouble(numCol, (double) obj);
//								} else if (colDataType
//										.equals(ColumnFormat.JDBC_DATE
//												.toString())) {
//									insertStmt.setObject(numCol, DateUtil
//											.parse(((String) obj).trim(),
//													DBF_DATE));
//								} else if (colDataType
//										.equals(ColumnFormat.JDBC_TIMESTAMP
//												.toString())) {
//									insertStmt.setObject(numCol, DateUtil
//											.parse(((String) obj).trim(),
//													DBF_DATE_TIME));
//								} else if (colDataType.equals(ColumnFormat.JDBC_INTEGER)
//										||colDataType.equals(ColumnFormat.JDBC_BIGINT)
//										||colDataType.equals(ColumnFormat.JDBC_SMALLINT)
//										||colDataType.equals(ColumnFormat.JDBC_TINYINT)) {
//									insertStmt.setDouble(numCol, (int) obj);
//								} else if (colDataType
//										.equals(ColumnFormat.JDBC_FLOAT
//												.toString())) {
//									insertStmt.setFloat(numCol, (float) obj);
//								} else {
//									insertStmt.setString(numCol, (String) obj);
//								}
//
//							}
//							insertStmt.addBatch();
//						}
//						int[] executeBatch = insertStmt.executeBatch();
//						logger.info("executeBatch: {}", executeBatch.length);
//						destinationConnection.commit();
//					}
//
//					try (ResultSet destinationRowCount = stmt
//							.executeQuery(SQLStmtUtils
//									.getCountTableStmt(destinationTable))) {
//						destinationRowCount.next();
//						long countDestinationAfterCopy = destinationRowCount
//								.getInt(1);
//						logger.info("Ending row count = "
//								+ countDestinationAfterCopy);
//						logger.info((countDestinationAfterCopy - countDestinationBeforeCopy)
//								+ " rows were added.");
//					}
//				}
//
//			}
//
//		} catch (Exception e) {
//			logger.error("Bulk Copy DBF to SQL Server encounters Exceptions", e);
//			throw new RuntimeException(e);
//		}
//	}
	
	public static int bulkCopyFromDBFToSQL(String fromDS, Connection destinationConnection,
			SchemeInfo schemeInfo, Integer blockSize, String batchId, String conversion) throws IOException
	{
		//open File
		File dbfFile = new File(fromDS);
		if(dbfFile.isDirectory() || !dbfFile.exists()){
			LogUtils.printException(logger, "Failed to open DBF File from path {}", fromDS);
			throw new IOException();
		}
		
		try (InputStream in =  new FileInputStream(dbfFile)) 
		{
			return bulkCopyFromDBFToSQL(in,destinationConnection,schemeInfo,blockSize,batchId, conversion);
		} 
	
	}
	
	public static int bulkCopyFromDBFToSQL(InputStream in, Connection destinationConnection,
			SchemeInfo schemeInfo, Integer batchSize, String batchId, String conversion)
	{
		int rs = 0;
		
		if (null == schemeInfo) {
			throw new RuntimeException("Invalid SchemeInfo");
		}

//		if (schemeInfo.getSchemeTableColumns().isEmpty()) {
//			LogUtils.printException(logger,
//					"Poll Scheme {} is not properly setup", schemeInfo.getId());
//			throw new RuntimeException("Table column list is null");
//		}


		// initialize source and destination info
//		List<String> sourceCols = new ArrayList<String>();
		List<String> destinationCols = new ArrayList<String>();
		int[] nullables = null ;

		String destinationTable = schemeInfo.getDestination();
		List<SchemeTableColumn> colEntities = schemeInfo
				.getSchemeTableColumns();

		
		try {
			boolean byColunmIdx = ! (colEntities != null && colEntities.size() > 0) ;
			DBFReader reader = new DBFReader(in);
			reader.setCharactersetName(DEFAULT_CHARSET);
			Map<String, Object> map = readDBFRow(reader, byColunmIdx);
			if (map == null)
			{
				return 0;
			}


			try (Statement stmt = destinationConnection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)) {
				
				if (colEntities != null && colEntities.size() > 0)
				{
					// Sort the scheme table column list
					Collections.sort(colEntities, new Comparator<SchemeTableColumn>() {
						@Override
						public int compare(SchemeTableColumn o1, SchemeTableColumn o2) {
							return o1 == null ? -1 : o2 == null ? 1 : o1.getSeq()
									.compareTo(o2.getSeq());
						}
					});
					nullables = new int[colEntities.size()];
					for (SchemeTableColumn e : colEntities) {
//						sourceCols.add(e.getFromColumn());
						destinationCols.add(e.getToColumn());
					}
				}
				else
				{
					String metaDataSQL = SQLStmtUtils.getMetaDataStmt(destinationTable);
					ResultSet destRs = stmt.executeQuery(metaDataSQL) ;
					
					int columnCount = destRs.getMetaData().getColumnCount();
					nullables = new int[columnCount];
					
					colEntities = new ArrayList<SchemeTableColumn>(columnCount);
					boolean guidMapped = false ;
					for(int i = 1; i <= columnCount;i++){
						String colunm = destRs.getMetaData().getColumnLabel(i) ;
						if (colunm.equalsIgnoreCase(ROWGUID)) {
							guidMapped = true ;
						}
						else
						{
							if (i > map.size()) {
								if (guidMapped)
									break ; 
								else
									continue;
							}
						}
						nullables[i-1] = destRs.getMetaData().isNullable(i);

						SchemeTableColumn col = new SchemeTableColumn();
						ColumnFormat format = ColumnFormat.touch(destRs.getMetaData().getColumnType(i));
						if (format == null)
						{
							format = ColumnFormat.JDBC_VARCHAR;
						}
						if (i > map.size() && colunm.equalsIgnoreCase(ROWGUID)) {
							col.setFromColumn(colunm);
							col.setSeq(map.size());
							
						}
						else
						{
							col.setFromColumn(String.valueOf(i-1));
							col.setSeq(i-1);
						}
						col.setToColumn(colunm);
						col.setToColumnFormat(format.name());
						
						colEntities.add(col);
						destinationCols.add(colunm);
					}
				}
						
		
//				LogUtils.printLog("Poll Scheme Columns:{}", colEntities);
		


//				destinationConnection.setAutoCommit(false);

				long countDestinationBeforeCopy = 0;
				try (ResultSet rsRowCount = stmt.executeQuery(SQLStmtUtils
						.getCountTableStmt(destinationTable))) {
					rsRowCount.next();
					countDestinationBeforeCopy = rsRowCount.getInt(1);
					logger.info("Starting row count = "
							+ countDestinationBeforeCopy);
				}

				try (PreparedStatement insertStmt = destinationConnection
						.prepareStatement(SQLStmtUtils.getDBFInsertStmtByCols(
								destinationTable, destinationCols, batchId))) {

					int batchControl = 0;
					int totalInsertCount = 0;
					List<String> CONV_CHI_TABLE_COLUNM_LIST = JDBCUtils.CONV_CHI_TABLE_MAP.get(destinationTable.toUpperCase());

//					DBFReader reader = new DBFReader(in);
//					reader.setCharactersetName(DEFAULT_CHARSET);
					
//					List<Map<String, Object>> results = readDBF(in, DEFAULT_CHARSET);
//					for (Map<String, Object> map : results) {
//					Map<String, Object> map = readDBFRow(reader);
					while (map != null)
					{
//						LogUtils.printLog("Poll Scheme map:{}", map);
						for (SchemeTableColumn colEntity : colEntities) {

							String colDataType = colEntity
									.getToColumnFormat();
							int numCol = colEntity.getSeq().intValue() + 1;
							String souceColName = colEntity.getFromColumn();
							String columnName = colEntity.getToColumn();

							// Auditable fields staging DB has
							if (souceColName.equalsIgnoreCase(ROWGUID)) {
								insertStmt.setObject(numCol,
										UUID.randomUUID().toString()
												.getBytes());
								continue;
							}

							Object obj = map.get(souceColName);
//							LogUtils.printLog("Poll Scheme col : {} {} {} {} [{}]", numCol, columnName, colDataType, colEntity.getToColumn(), obj);
//
							if (obj == null && nullables[numCol-1] == ResultSetMetaData.columnNullable) {
								insertStmt.setNull(numCol,
										ColumnFormat.JDBC_NULL.getValue());
							} else if (colDataType.equals(ColumnFormat.JDBC_DOUBLE.toString())
									||colDataType.equals(ColumnFormat.JDBC_DECIMAL.toString())) {
								if(StringUtils.isBlank(obj.toString()))
								{
									insertStmt.setDouble(numCol, 0);
								}
								else if (obj instanceof String)
								{
									String str = (String) obj;
									str = str.trim();
									if (str.isEmpty())
										if (nullables[numCol-1] == ResultSetMetaData.columnNullable)
											insertStmt.setNull(numCol,ColumnFormat.JDBC_NULL.getValue());
										else
											insertStmt.setDouble(numCol, 0);
									else
										insertStmt.setDouble(numCol, Double.valueOf((String)obj));
								}
								else
								{
									if (obj instanceof Double)
									{
										insertStmt.setDouble(numCol, (double) obj);
									}
									else if (obj instanceof Float)
									{
										insertStmt.setFloat(numCol, (float) obj);
									}
									else if (obj instanceof Integer)
									{
										insertStmt.setInt(numCol, (int) obj);
									}
									else if (obj instanceof BigDecimal)
									{
										insertStmt.setBigDecimal(numCol, (BigDecimal) obj);
									}									
								}
							} else if (colDataType
									.equals(ColumnFormat.JDBC_DATE.toString()
											.toString())) {
								if(StringUtils.isBlank(obj.toString()))
								{
									insertStmt.setObject(numCol, EMPTY_TIMESTAMP);
								}
								else
								{
									insertStmt.setObject(numCol, DateUtil
											.parse(((String) obj).trim(),
													DBF_DATE));
								}
							} else if (colDataType
									.equals(ColumnFormat.JDBC_TIMESTAMP.toString()
											.toString())) {
								if(StringUtils.isBlank(obj.toString()))
								{
									insertStmt.setObject(numCol, EMPTY_TIMESTAMP);
								}
								else
								{
									insertStmt.setObject(numCol, DateUtil
											.parse(((String) obj).trim(),
													String.valueOf(obj).trim().length() == 8 ? DBF_DATE : DBF_DATE_TIME));
								}
							} else if (colDataType.equals(ColumnFormat.JDBC_INTEGER.toString())
									||colDataType.equals(ColumnFormat.JDBC_BIGINT.toString())
									||colDataType.equals(ColumnFormat.JDBC_SMALLINT.toString())
									||colDataType.equals(ColumnFormat.JDBC_TINYINT.toString())) {
								if(StringUtils.isBlank(obj.toString()))
								{
									insertStmt.setDouble(numCol, 0);
								}
								else if (obj instanceof String)
								{
									String str = (String) obj;
									str = str.trim();
									if (str.isEmpty())
										if (nullables[numCol-1] == ResultSetMetaData.columnNullable)
											insertStmt.setNull(numCol,ColumnFormat.JDBC_NULL.getValue());
										else
											insertStmt.setDouble(numCol, 0);
									else
										insertStmt.setDouble(numCol, Long.valueOf((String)obj));
								}
								else
								{
									if (obj instanceof Double)
									{
										insertStmt.setDouble(numCol, (double) obj);
									}
									else if (obj instanceof Float)
									{
										insertStmt.setFloat(numCol, (float) obj);
									}
									else if (obj instanceof Integer)
									{
										insertStmt.setInt(numCol, (int) obj);
									}
									else if (obj instanceof BigDecimal)
									{
										insertStmt.setBigDecimal(numCol, (BigDecimal) obj);
									}									
								}
							} else if (colDataType
									.equals(ColumnFormat.JDBC_FLOAT.toString()
											.toString())) {
								if(StringUtils.isBlank(obj.toString()))
								{
									insertStmt.setFloat(numCol, 0);
								}
								else if (obj instanceof String)
								{
									String str = (String) obj;
									str = str.trim();
									if (str.isEmpty())
										if (nullables[numCol-1] == ResultSetMetaData.columnNullable)
											insertStmt.setNull(numCol,ColumnFormat.JDBC_NULL.getValue());
										else
											insertStmt.setFloat(numCol, 0);
									else
										insertStmt.setFloat(numCol, Float.valueOf((String)obj));
								}
								else
								{
									if (obj instanceof Double)
									{
										insertStmt.setDouble(numCol, (double) obj);
									}
									else if (obj instanceof Float)
									{
										insertStmt.setFloat(numCol, (float) obj);
									}
									else if (obj instanceof Integer)
									{
										insertStmt.setInt(numCol, (int) obj);
									}
									else if (obj instanceof BigDecimal)
									{
										insertStmt.setBigDecimal(numCol, (BigDecimal) obj);
									}									
								}
							}else {
								if (obj == null)
								{
									insertStmt.setString(numCol, "");
								}
								else
								{
									String value = StringUtils.stripEnd(obj.toString(), null);
									
									if (JDBCUtils.CONV_CHI_TABLE_MAP.containsKey(destinationTable.toUpperCase())
											&& CONV_CHI_TABLE_COLUNM_LIST.contains(columnName.toLowerCase()) )
									{
										 if(conversion == JDBCUtils.CONV_SIMPLIFIED_TO_TRADTION){
											 value =  ChineseUtils.toTraditional(value);
										 } else if(conversion == JDBCUtils.CONV_TRADTION_TO_SIMPLIFIED){
											 value =  ChineseUtils.toSimplified(value);
										 }
									}
	
									insertStmt.setString(numCol, value);
								}
							}

						}
						insertStmt.addBatch();
						
						batchControl ++;
						if(batchSize != null && batchControl >= batchSize){
							LogUtils.printLog(logger, "commit: {} : {} / {}", (totalInsertCount +batchControl) , batchControl, batchSize);

							int[] executeBatchCount = insertStmt.executeBatch();
							for (int i :executeBatchCount)
							{
								totalInsertCount += i ;
							}
							batchControl = 0;
						}
						map = readDBFRow(reader, byColunmIdx);
					}
					
					if (batchControl > 0)
					{
						LogUtils.printLog(logger, "commit: {} : {} / {}", (totalInsertCount +batchControl) , batchControl, batchSize);
		
						int[] executeBatchCount = insertStmt.executeBatch();
						for (int i :executeBatchCount)
						{
							totalInsertCount += i ;
						}
					}
					logger.info("executeBatch: {}", totalInsertCount);

//					destinationConnection.commit();
					rs = totalInsertCount ;
				}

//				try (ResultSet destinationRowCount = stmt
//						.executeQuery(SQLStmtUtils
//								.getCountTableStmt(destinationTable))) {
//					destinationRowCount.next();
//					long countDestinationAfterCopy = destinationRowCount
//							.getInt(1);
//					logger.info("Ending row count = "
//							+ countDestinationAfterCopy);
//					logger.info((countDestinationAfterCopy - countDestinationBeforeCopy)
//							+ " rows were added.");
//					rs = (int) (countDestinationAfterCopy - countDestinationBeforeCopy);
//				}
			}

			return rs;
			
		} catch (Exception e) {
			logger.error("Bulk Copy DBF to SQL Server encounters Exceptions", e);
			throw new RuntimeException(e);
		}
	}

	// Method to extract data from SQL source and output it as DBF file
	// according to the field definition
	/**
	 * 
	 * @param fromDS
	 * @param toDS
	 * @param sourceTable
	 * @param destinationTable
	 * @param cols
	 * @param blockSize
	 * @param batchId
	 * @param conditions
	 * @throws SQLServerException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void bulkCopyFromSQLToDBF(String fromDS, String toDS,
			String sourceTable, String destinationTable,
			List<SchemeTableColumn> cols, Integer blockSize, String batchId,
			String... conditions) throws SQLServerException, SQLException,
			IOException {

		File outFile = new File(toDS);
		if (outFile.isDirectory()) {
			LogUtils.printException(logger, "Invalid file path {}", toDS);
			throw new IOException();
		}
		if (!outFile.exists()) {
			try {
				outFile.createNewFile();
			} catch (IOException e) {
				LogUtils.printException(logger, "Failed to create file {}",
						toDS, e);
				throw new RuntimeException(e);
			}
		}

		if (cols.isEmpty()) {
			LogUtils.printException(logger,
					"Scheme Table Columns List is empty");
			throw new RuntimeException("Scheme Table Columns List is empty");
		}

		List<String> sourceCols = new ArrayList<String>();

		// Class.forName(className);
		try (Connection sourceConnection = DriverManager.getConnection(fromDS)) {

			try (Statement stmt = sourceConnection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)) {

				long countSource = 0;

				try (ResultSet rsRowCount = stmt.executeQuery(SQLStmtUtils
						.getCountTableStmt(sourceTable, conditions))) {
					rsRowCount.next();
					countSource = rsRowCount.getInt(1);
					LogUtils.printLog(logger,
							"Totally {} Records from table {} need to copy",
							countSource, sourceTable);
				}

				// process result set
				Object[][] data;

				// format column name list
				for (SchemeTableColumn col : cols) {
					sourceCols.add(col.getToColumn()); // SQL -> DBF is reverted
														// from DBF to SQL, so
														// use ToColumn
				}

				try (ResultSet rowData = stmt.executeQuery(SQLStmtUtils
						.getSelectStmt(sourceTable, sourceCols, false,
								conditions))) {
					LogUtils.printLog(logger, "Start to process DBF output.");

					// initialize two-dimensional array for result set
					rowData.last();
					int numCols = rowData.getMetaData().getColumnCount() - 1; // eliminate
																				// the
																				// ROWGUID
					int numRows = rowData.getRow();
					rowData.beforeFirst();
					data = new Object[numRows][numCols];

					// process the result set data into two-dimensional array
					int i = 0;
					while (rowData.next()) {
						Object[] row = new Object[numCols];
						for (int j = 1; j <= numCols; j++) {
							// column format justification
							String dbfColDataType = cols.get(j - 1).getFromColumnFormat();
							String jdbcColDataType = cols.get(j - 1).getToColumnFormat();
							
							Object object = null;
							if (dbfColDataType.equals(ColumnFormat.DBF_FIELD_TYPE_N.toString())) {
								object = new Double(rowData.getDouble(j));
							} else if (dbfColDataType.equals(ColumnFormat.DBF_FIELD_TYPE_D.toString())) {
								object = rowData.getDate(j);
							} else if (dbfColDataType.equals(ColumnFormat.DBF_FIELD_TYPE_M.toString())) {
								object = rowData.getByte(j);
							} else if (dbfColDataType.equals(ColumnFormat.DBF_FIELD_TYPE_F.toString())) {
								object = new Float(rowData.getFloat(j));
							} else if (dbfColDataType.equals(ColumnFormat.DBF_FIELD_TYPE_L.toString())) {
								object = new Boolean(rowData.getBoolean(j));
							} else {
								String temp = "";
								if(jdbcColDataType.equals(ColumnFormat.JDBC_DATE.toString())){
									temp = DateUtil.format(rowData.getDate(j), DBF_DATE);
								}
								else if(jdbcColDataType.equals(ColumnFormat.JDBC_TIMESTAMP.toString())){
									temp = DateUtil.format(rowData.getDate(j), DBF_DATE_TIME);
								}else{
									temp = rowData.getString(j);
								}
								object = temp;
							}
							row[j - 1] = (object == null) ? null : object;
						}
						data[i] = row;
						++i;
					}

					writeDBF(data, cols, toDS);

					LogUtils.printLog(logger, "End of process DBF output.");
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static List<Map<String, Object>> readDBF(File file, String characterset) 
	{
		try (InputStream in =  new FileInputStream(file)) 
		{
			return readDBF( in ,characterset ) ;
		} 
		catch (IOException e) 
		{
			LogUtils.printException(logger,
				"File Input Stream Error While reading DBF", e);
			throw new RuntimeException(e);
		}		
	}
	
	public static List<Map<String, Object>> readDBF(InputStream fis, String characterset)
			throws IOException {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

//		try (FileInputStream fis = new FileInputStream(file)) {

			DBFReader reader = new DBFReader(fis);
			reader.setCharactersetName(characterset);
			
			Map<String, Object> map = readDBFRow(reader, false);
			while (map != null)
			{
				data.add(map);
				map = readDBFRow(reader, false);
			}
//
//			Object[] rowValues;
//			Object value;
//			DBFField dbfField;
//			while ((rowValues = reader.nextRecord()) != null) {
//				Map<String, Object> map = new HashMap<>();
//				for (int i = 0; i < rowValues.length; i++) {
//					dbfField = reader.getField(i);
//					value = rowValues[i];
//					if(dbfField.getDataType() ==ColumnFormat.DBF_FIELD_TYPE_N.getValue()){
//						if(dbfField.getDecimalCount()==0 && value!=null){
//							if(StringUtils.isNotBlank(value.toString())){
//								
//								if(value instanceof Double){
//									double d = (double)value;
//									Integer s1 = (int)d;
//									value = s1;
//								}
//							}
//						}
//					}
//					map.put(reader.getField(i).getName(), value);
//				}
//
//				data.add(map);
//			}
//		} catch (Exception e) {
//			LogUtils.printException(logger,
//					"File Input Stream Error While reading DBF", e);
//			throw new RuntimeException(e);
//		}

		return data;
	}
	

	public static Map<String, Object> readDBFRow(DBFReader reader, boolean byColunmIdx)
			throws IOException {
		
			Object[] rowValues= reader.nextRecord() ;

			if (rowValues  != null) {
				Map<String, Object> map = new HashMap<>();
				for (int i = 0; i < rowValues.length; i++) {
					DBFField dbfField = reader.getField(i);
					Object value = rowValues[i];
					if(dbfField.getDataType() ==ColumnFormat.DBF_FIELD_TYPE_N.getValue()){
						if(dbfField.getDecimalCount()==0 && value!=null){
							if(StringUtils.isNotBlank(value.toString())){
								
								if(value instanceof Double){
									double d = (double)value;
									Integer s1 = (int)d;
									value = s1;
								}
							}
						}
					}
					if (byColunmIdx)
					{
						map.put(String.valueOf(i), value);
					}
					else
					{
						map.put(reader.getField(i).getName(), value);
					}
				}

				return map;
			}
			else
			{
				return null ;
			}
	}
	



	/**
	 * @param fromConn
	 * @param toDS
	 * @param sourceTable
	 * @param destinationTable
	 * @param cols
	 * @param blockSize
	 * @param batchId
	 * @param conditions
	 * @return 
	 * @throws SQLServerException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static int bulkCopyFromSQLToDBF(Connection fromConn, String toDS,
			String sourceTable, String destinationTable,
			List<SchemeTableColumn> cols, Integer blockSize, String batchId,
			String... conditions) throws SQLServerException, SQLException,
			IOException {

		File outFile = new File(toDS);
		if (outFile.isDirectory()) {
			LogUtils.printException(logger, "Invalid file path {}", toDS);
			throw new IOException();
		}
		if (!outFile.exists()) {
			try {
				outFile.createNewFile();
			} catch (IOException e) {
				LogUtils.printException(logger, "Failed to create file {}",
						toDS, e);
				throw new RuntimeException(e);
			}
		}

		if (cols.isEmpty()) {
			LogUtils.printException(logger,
					"Scheme Table Columns List is empty");
			throw new RuntimeException("Scheme Table Columns List is empty");
		}

		List<String> sourceCols = new ArrayList<String>();

		// Class.forName(className);

		try (Statement stmt = fromConn.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY)) {

			long countSource = 0;

			try (ResultSet rsRowCount = stmt.executeQuery(SQLStmtUtils
					.getCountTableStmt(sourceTable, conditions))) {
				rsRowCount.next();
				countSource = rsRowCount.getInt(1);
				LogUtils.printLog(logger,
						"Totally {} Records from table {} need to copy",
						countSource, sourceTable);
			}

			// process result set
			Object[][] data;

			// format column name list
			for (SchemeTableColumn col : cols) {
				sourceCols.add(col.getToColumn()); // SQL -> DBF is reverted
													// from DBF to SQL, so
													// use ToColumn
			}

			try (ResultSet rowData = stmt.executeQuery(SQLStmtUtils
					.getSelectStmt(sourceTable, sourceCols, false,
							conditions))) {
				LogUtils.printLog(logger, "Start to process DBF output.");

				// initialize two-dimensional array for result set
				rowData.last();
				int numCols = rowData.getMetaData().getColumnCount() - 1; // elminate
																			// the
																			// ROWGUID
				int numRows = rowData.getRow();
				rowData.beforeFirst();
				data = new Object[numRows][numCols];

				// process the result set data into two-dimensional array
				int count = 0;
				while (rowData.next()) {
					Object[] row = new Object[numCols];
					for (int j = 1; j <= numCols; j++) {
						String colDataType = cols.get(j - 1)
								.getFromColumnFormat();
						Object object = null;
						if (colDataType
								.equals(ColumnFormat.DBF_FIELD_TYPE_N
										.toString())) {
							object = new Double(rowData.getDouble(j));
						} else if (colDataType
								.equals(ColumnFormat.DBF_FIELD_TYPE_D
										.toString())) {
							object = rowData.getDate(j);
						} else if (colDataType
								.equals(ColumnFormat.DBF_FIELD_TYPE_M
										.toString())) {
							object = rowData.getByte(j);
						} else if (colDataType
								.equals(ColumnFormat.DBF_FIELD_TYPE_F
										.toString())) {
							object = new Float(rowData.getFloat(j));
						} else if (colDataType
								.equals(ColumnFormat.DBF_FIELD_TYPE_L
										.toString())) {
							object = new Boolean(rowData.getBoolean(j));
						} else {
							Pattern patter = Pattern.compile("\\d{4}(-)\\d{2}(-)\\d{2}\\s\\d{2}(:)\\d{2}(:)\\d{2}(.)\\d{3}");
							Matcher matcher = patter.matcher(rowData.getString(j));
							boolean rs = matcher.find();
							if (rs) {
								object =rowData.getString(j).split("\\.")[0].replace("-", "").replace(":", "");
							} else {
								object = rowData.getString(j);	
							}
						}
						row[j - 1] = (object == null) ? null : object;
					}
					data[count] = row;
					++count;
				}

				writeDBF(data, cols, toDS);

				LogUtils.printLog(logger, "End of process DBF output.");
				
				return count;
			}
		}
	}
	
	/**
	 * 
	 * @param data
	 * @param cols
	 * @param fileFullPath
	 * @throws DBFException
	 * @throws IOException
	 */
	public static void writeDBF(Object[][] data, List<SchemeTableColumn> cols,
			String fileFullPath) throws DBFException, IOException {

		// in the DBF field setting,
		// the ROWGUID should be removed from the cols
		// -1 for the size
		// in Scheme Table Column Setting, ROWGUID must be in the last seq
		int numberOfCol = cols.size() - 1;
		try {

			// set field definition
			DBFField[] fields = new DBFField[numberOfCol];
			for (int i = 0; i < numberOfCol; i++) {

				LogUtils.printLog(logger, "DBF Field Definition {}-{}", i, cols
						.get(i).getFromColumnInfo());

				fields[i] = new DBFField();
				fields[i].setName(cols.get(i).getFromColumn());

				// Parse the enum into byte value
				int colFormat = ColumnFormat.valueOf(
						cols.get(i).getFromColumnFormat()).getValue();

				fields[i].setDataType(Byte.parseByte(Integer
						.toString(colFormat)));
				
				if(colFormat != ColumnFormat.DBF_FIELD_TYPE_D.getValue()){
					fields[i].setFieldLength(cols.get(i).getFromColumnLength()
							.intValue());	
				}

				if (cols.get(i).getFromColumnPrecision().intValue() >= 0) {
					fields[i].setDecimalCount(cols.get(i)
							.getFromColumnPrecision().intValue());
				}

			}

			// populate DBFWriter
			DBFWriter writer = new DBFWriter();
			writer.setCharactersetName("BIG5");
			writer.setFields(fields);

			int numberOfRecords = 0;
			for (int i = 0; i < data.length; i++) {
				writer.addRecord(data[i]);
				++numberOfRecords;
			}

			try (FileOutputStream fos = new FileOutputStream(fileFullPath)) {
				writer.write(fos);
				LogUtils.printLog(logger,
						"Successfully write {} records into DBF file {}",
						numberOfRecords, fileFullPath);
			} catch (IOException e) {
				LogUtils.printException(logger,
						"Exception occurs when outputing file to {}.",
						fileFullPath, e);
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			LogUtils.printException(logger,
					"Exception occur when writing DBF file from table .", e);
			throw new RuntimeException(e);
		}
	}

	public static boolean checkSumDBF(String source, String destination) {

		return false;
	}

	/*
	 * return a String array length 3 0 - branch code; 1 - business date yyMMdd;
	 * 2 - table name
	 */
	public static String[] getTableFromFileName(String fileName) {
		String str[] = new String[3];
		Pattern p = Pattern.compile(REGEX_FILENAME);
		Matcher m = p.matcher(fileName);
		if (m.find()) {
			for (int i = 0; i < str.length; i++) {
				str[i] = m.group(i + 1);
			}
		}
		return str;
	}

	/*
	 * the root path must end with \, e.g. \repos\osb\output
	 */
	public static String getFilePathByScheme(String ftpRoot, String branchCode,
			String table, Date fileDate) {

		return MessageFormat.format("{0}{1}{2}{3}{4}{5}{6}", 
				ftpRoot,
				File.separator,
				branchCode,
				File.separator,
				DateUtil.format(fileDate, "yyyyMMdd"),
				File.separator,
				getDbfFileName(branchCode, table, fileDate));
	}
	
	public static String getFilePathByScheme(String ftpRoot, String branchCode, Date fileDate) {

		return MessageFormat.format("{0}{1}{2}{3}{4}", 
				ftpRoot,
				File.separator,
				branchCode,
				File.separator,
				DateUtil.format(fileDate, "yyyyMMdd"));
	}
	
	
	/*
	 * the root path must end with \, e.g. \repos\osb\output
	 */
	public static String getDbfFileName(String branchCode,
			String table, Date fileDate) {

		return MessageFormat.format("M{0}_{1}_{2}.DBF", 
				branchCode, DateUtil.format(fileDate, "yyMMdd"), table).toUpperCase();
	}
	
	public static ColumnFormat getDBFFormatByJDBCFormat(String format){
		if(format.equals("")
			||format.equals(ColumnFormat.JDBC_INTEGER)
			||format.equals(ColumnFormat.JDBC_SMALLINT)
			||format.equals(ColumnFormat.JDBC_DOUBLE)
			||format.equals(ColumnFormat.JDBC_FLOAT)
			||format.equals(ColumnFormat.JDBC_TINYINT)
			||format.equals(ColumnFormat.JDBC_DECIMAL)){
			return ColumnFormat.DBF_FIELD_TYPE_N;
		}
		else
			return ColumnFormat.DBF_FIELD_TYPE_C;
	}
	

	
}