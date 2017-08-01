package com.maxim.pos.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linuxense.javadbf.DBFException;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFWriter;
import com.luhuiguo.chinese.ChineseUtils;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ColumnFormat;
import com.maxim.util.CsvReader;
import com.maxim.util.DateUtil;
import com.microsoft.sqlserver.jdbc.SQLServerException;

public class JavaCSVUtils {

	private static final Logger logger = LoggerFactory
			.getLogger(JavaCSVUtils.class);

	private static final String CSV_DATE_TIME = "yyyy-MM-dd HH:mm:ss.sss";
	private static final String CSV_DATE_TIME2 = "yyyy-MM-dd HH:mm:ss";
	private static final String CSV_DATE = "yyyy-MM-dd";

	private static final String DEFAULT_CHARSET = "UTF-16";

//	private static final String ROWGUID = "ROWGUID";

	
	public static int bulkCopyFromCSVToSQL(String fromDS, Connection destinationConnection,
			SchemeInfo schemeInfo, Integer blockSize, String batchId, boolean skipFirstLine, String conversion) throws IOException
	{
		//open File
		File dbfFile = new File(fromDS);
		if(dbfFile.isDirectory() || !dbfFile.exists()){
			LogUtils.printException(logger, "Failed to open DBF File from path {}", fromDS);
			throw new IOException();
		}
		
		try (InputStream in =  new FileInputStream(dbfFile)) 
		{
			return bulkCopyFromCSVToSQL(in,destinationConnection,schemeInfo,blockSize,batchId,skipFirstLine, conversion);
		} 
	
	}
	
	public static int bulkCopyFromCSVToSQL(InputStream in, Connection destinationConnection,
			SchemeInfo schemeInfo, Integer batchSize, String batchId, boolean skipFirstLine, String conversion)
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

		String destinationTable = schemeInfo.getDestination();
		List<SchemeTableColumn> colEntities = schemeInfo
				.getSchemeTableColumns();
		int[] nullables = null ;
		
		try(CsvReader reader = new CsvReader(in, '\t',Charset.forName(DEFAULT_CHARSET)))
		{
			String[] values = null ;
			
			if (skipFirstLine)
			{
				reader.skipRecord();
			}
			if (reader.readRecord())
			{
				values = reader.getValues();
			}
			if (values == null)
			{
				return 0 ;
			}
			int maxSeq = 0 ;
			try (Statement stmt = destinationConnection.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)) 
			{
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
			//			sourceCols.add(e.getFromColumn());
						destinationCols.add(e.getToColumn());
						if (e.getSeq() > maxSeq)
						{
							maxSeq = e.getSeq() ;
						}
					}
				}
				else
				{
					String metaDataSQL = SQLStmtUtils.getMetaDataStmt(destinationTable);
					ResultSet destRs = stmt.executeQuery(metaDataSQL) ;
					
					int columnCount = destRs.getMetaData().getColumnCount();
					nullables = new int[columnCount];
					maxSeq = columnCount - 1 ;
					colEntities = new ArrayList<SchemeTableColumn>(columnCount);
					
//					boolean guidMapped = false ;
					for(int i = 1; i <= columnCount;i++){
						String colunm = destRs.getMetaData().getColumnLabel(i) ;
//						if (colunm.equalsIgnoreCase(ROWGUID)) {
//							guidMapped = true ;
//						}
//						else
//						{
//							if (i > values.length) {
//								if (guidMapped)
//									break ; 
//								else
//									continue;
//							}
//						}
						ColumnFormat format = ColumnFormat.touch(destRs.getMetaData().getColumnType(i));
						
						nullables[i-1] = destRs.getMetaData().isNullable(i);
						SchemeTableColumn col = new SchemeTableColumn();
						if (format == null)
						{
							format = ColumnFormat.JDBC_VARCHAR;
						}
						
//						if (i > values.length && colunm.equalsIgnoreCase(ROWGUID)) {
//							col.setFromColumn(colunm);
//							col.setSeq(values.length);
//						}
//						else
//						{
							col.setFromColumn(String.valueOf(i-1));
							col.setSeq(i-1);
//						}
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
					logger.info("Prepare SQL : "
							+ countDestinationBeforeCopy);	
					try (PreparedStatement insertStmt = destinationConnection
							.prepareStatement(SQLStmtUtils.getTXTInsertStmtByCols(
									destinationTable, destinationCols, batchId))) {
	
						int batchControl = 0;
						int totalInsertCount = 0;
						List<String> CONV_CHI_TABLE_COLUNM_LIST = JDBCUtils.CONV_CHI_TABLE_MAP.get(destinationTable.toUpperCase());

	//					CsvReader reader = new CsvReader(in, '\t',Charset.forName(DEFAULT_CHARSET));
	//					reader.setCharactersetName(Charset);
	
	//					List<Map<String, Object>> results = readDBF(in, DEFAULT_CHARSET);
	//					for (Map<String, Object> map : results) {
	//					Map<String, Object> map = readDBFRow(reader);
	//					if (skipFirstLine)
	//					{
	//						reader.skipRecord();
	//					}
						int lastindex=0;
						String[] lastValues = null;
//						String lastValue = "" ;
						while (values != null)
						{
	//						String[] values = reader.getValues();
							if (values.length > 0 && (lastindex != 0 || values.length < maxSeq))
							{
								String[] newValues = null ;
								if (lastindex > 0 && lastValues != null)
								{
									newValues = new String[lastindex + values.length-1];
									System.arraycopy(lastValues, 0, newValues, 0, lastValues.length);
									newValues[lastValues.length-1] += System.lineSeparator() + values[0];
									System.arraycopy(values, 1, newValues, lastindex, values.length-1);
								}
								else
								{
									newValues = values ;
								}
								lastindex = newValues.length;
								lastValues = newValues ;
								values = newValues ;
							}
							if (values.length < (maxSeq+1))
							{
								if (reader.readRecord())
								{
									values = reader.getValues();
									continue ;
								}
								String[] newValues = new String[maxSeq];
								System.arraycopy(values, 0, newValues, 0, values.length);
								values = newValues ;
							}

							try
							{
								for (SchemeTableColumn colEntity : colEntities) {


		
									String colDataType = colEntity
											.getToColumnFormat();
									String columnName = colEntity.getToColumn();
									int numCol = colEntity.getSeq().intValue() + 1;
//									String nameCol = colEntity.getFromColumn();
//		
									// Auditable fields staging DB has
//									if (nameCol.equalsIgnoreCase(ROWGUID)) {
//										insertStmt.setObject(numCol,
//												UUID.randomUUID().toString()
//														.getBytes());
//										continue;
//									}
									
									if (colEntity.getSeq() < values.length)
									{
										String value = values[colEntity.getSeq()];
										
										if (value != null && value.length() == 1)
										{
											if (value.charAt(0) == 0)
											{
												if (nullables[numCol-1] == ResultSetMetaData.columnNullable)
													value = null ;
												else
													value = "";
											}
										}
										
										if (value == null) {
											insertStmt.setNull(numCol,
													ColumnFormat.JDBC_NULL.getValue());
										} else if (colDataType.equals(ColumnFormat.JDBC_DOUBLE.toString())
												||colDataType.equals(ColumnFormat.JDBC_DECIMAL.toString())) {
											if (value.trim().equals(""))
												if (nullables[numCol-1] == ResultSetMetaData.columnNullable)
													insertStmt.setNull(numCol,ColumnFormat.JDBC_NULL.getValue());
												else
													insertStmt.setDouble(numCol, 0);
											else
												insertStmt.setDouble(numCol, Double.valueOf(value.trim()));
										} else if (colDataType
												.equals(ColumnFormat.JDBC_DATE.toString()
														.toString())) {
											if (value.trim().equals(""))
												insertStmt.setNull(numCol,
														ColumnFormat.JDBC_NULL.getValue());
											else
												insertStmt.setObject(numCol, DateUtil
													.parse(value.trim(),
															CSV_DATE));
										} else if (colDataType
												.equals(ColumnFormat.JDBC_TIMESTAMP.toString()
														.toString())) {
											if (value.trim().equals(""))
												insertStmt.setNull(numCol,
														ColumnFormat.JDBC_NULL.getValue());
											else
											{
												if (value.trim().indexOf(".") >= 0)
												{
													insertStmt.setObject(numCol, DateUtil
														.parse(value.trim(),
																CSV_DATE_TIME));
												}
												else
												{
													insertStmt.setObject(numCol, DateUtil
															.parse(value.trim(),
																	CSV_DATE_TIME2));
			
												}
											}
										} else if (colDataType.equals(ColumnFormat.JDBC_INTEGER.toString())
												||colDataType.equals(ColumnFormat.JDBC_BIGINT.toString())
												||colDataType.equals(ColumnFormat.JDBC_SMALLINT.toString())
												||colDataType.equals(ColumnFormat.JDBC_TINYINT.toString())) {
											if (value.trim().equals(""))
												if (nullables[numCol-1] == ResultSetMetaData.columnNullable)
													insertStmt.setNull(numCol,ColumnFormat.JDBC_NULL.getValue());
												else
													insertStmt.setDouble(numCol, 0);
											else
												insertStmt.setDouble(numCol,  Integer.valueOf(value.trim()));
										} else if (colDataType
												.equals(ColumnFormat.JDBC_FLOAT.toString()
														.toString())) {
											if (value.trim().equals(""))
												if (nullables[numCol-1] == ResultSetMetaData.columnNullable)
													insertStmt.setNull(numCol,ColumnFormat.JDBC_NULL.getValue());
												else
													insertStmt.setFloat(numCol, 0);
											else
												insertStmt.setFloat(numCol,  Float.valueOf(value.trim()));
										}else {
											
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
								
								lastindex=0;
								lastValues = null;
							}
							catch (Exception e)
							{
								e.printStackTrace();
								LogUtils.printLog(logger, "Skip: {} : {} / {}", (totalInsertCount +batchControl) , batchControl, batchSize);
							}
							if(batchSize != null && batchControl >= batchSize){
								LogUtils.printLog(logger, "commit: {} : {} / {}", (totalInsertCount +batchControl) , batchControl, batchSize);
	
								int[] executeBatchCount = insertStmt.executeBatch();
								for (int i :executeBatchCount)
								{
									totalInsertCount += i ;
								}
								batchControl = 0;
							}
							
							if (reader.readRecord())
							{
								values = reader.getValues();
							}
							else
							{
								break;
							}
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
	
				return rs;
			} catch (Exception e) {
				logger.error("Bulk Copy CSV to SQL Server encounters Exceptions", e);
				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			logger.error("Bulk Copy CSV to SQL Server encounters Exceptions", e);
			throw new RuntimeException(e);
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

	
}
