package com.maxim.pos.common.util;

import java.util.List;

public class SQLStmtUtils {

//	private static final Logger logger = LoggerFactory.getLogger(SQLStmtUtils.class);
	
	protected static final String SELECT_METADATA = "SELECT * FROM %s WHERE 1=2";
	protected static final String SELECT_COUNT= "SELECT COUNT(1) FROM %s WITH (NOLOCK) %s";
	protected static final String SELECT_COUNT_ORACLE= "SELECT COUNT(1) FROM %s";
	protected static final String SELECT_ALL_COLS = "SELECT * FROM %s WITH (NOLOCK) %s";
	protected static final String WHITE_SPACE = " ";
	protected static final String COMMA = ",";
	protected static final String SEMI_COLUMN = ";";
	protected static final String BATCH_ID_COLUMN_NAME = "batch_id";
	protected static final String POS_DB_ENCODING = "UTF-8";
	protected static final String DELETE_FROM = "DELETE FROM %s %s";
	//DBF File does not have 3 fields: status, last_update_time, rowguid
	protected static final String INSERT_INTO_SQL_FROM_DBF = "INSERT INTO %s (last_update_time,status,%s) VALUES (getdate(),'',%s)";
	protected static final String INSERT_INTO_SQL_FROM_TXT = "INSERT INTO %s (%s) VALUES (%s)";
	protected static final String INSERT_INTO_ORACLE_UPDATE_AUDITABLES = "INSERT INTO %s (LAST_UPDATE_TIME,STATUS,%s) VALUES (sysdate,'P',%s)";
	protected static final String INSERT_INTO_ORACLE = "INSERT INTO %s (%s) VALUES (%s)";
	protected static final String INSERT_FROM_SELECT = "INSERT INTO %";
	protected static final String SELECT_STMT = "SELECT %s FROM %s WITH (NOLOCK) %s";
	protected static final String SELECT_STMT_EDW = "SELECT %s FROM %s %s";
	
    protected static final String UPDATE_NULL_STATUS = "UPDATE %s SET status = null  %s ";
    protected static final String UPDATE_BLANK_STATUS = "UPDATE %s SET status = \'\'  %s ";
    protected static final String UPDATE_SPACE_STATUS = "UPDATE %s SET status = \' \'  %s ";
    protected static final String UPDATE_COMPLETE_STATUS = "UPDATE %s SET status = \'C\'  %s ";
    protected static final String UPDATE_PENDING_STATUS = "UPDATE %s SET status = \'P\'  %s ";
//    protected static final String UPDATE_BY_STATUS = "UPDATE %s SET status = \'C\'  WHERE  LTRIM(RTRIM(status)) <> \'C\'  AND  branch_code  =  \'%s\' AND  business_date  = \'%s\'";

	public static final String FILE_TPYE_DBF = "DBF";
	public static final String FILE_TPYE_TXT = "TXT";

	public static String getCountTableStmt(String table, String...criteria){	
		String stmt = String.format(SELECT_COUNT, table,getCriteriaString(criteria));
//		LogUtils.printLog(logger, "SQLStmtUtils: {}", stmt);
		return stmt;
		
	}
	
	public static String getMetaDataStmt(String table){	
		String stmt = String.format(SELECT_METADATA, table);
//		LogUtils.printLog(logger, "SQLStmtUtils: {}", stmt);
		return stmt;
		
	}
	
	public static String getCountTableStmtForOracle(String table){	
		String stmt = String.format(SELECT_COUNT_ORACLE, table);
//		LogUtils.printLog(logger, "SQLStmtUtils: {}", stmt);
		return stmt;
		
	}
	
	public static String getSelectAllStmt(String table, String...criteria){
		String  stmt = String.format(SELECT_ALL_COLS, table, getCriteriaString(criteria));
//		LogUtils.printLog(logger, "SQLStmtUtils: {}", stmt);
		return stmt;
		
	}

	
	
	public static String getSelectStmtByCols(String table, List<String> cols, String batchId, String...criteria){
		String stmt = String.format(SELECT_STMT, getColsWithBatchIdValue(cols, batchId), table, getCriteriaString(criteria));
//		logger.info(stmt);
		
		return stmt;
		
	}
	
	public static String getSelectStmtByCols(String table, List<String> cols, String...criteria){
		String stmt = String.format(SELECT_STMT, getColsWithBatchIdValue(cols,null), table, getCriteriaString(criteria));
//		logger.info(stmt);
		
		return stmt;
		
	}

	public static String getDBFInsertStmtByCols(String table, List<String>cols, String batchId){
		return getInsertStmtByCols(table, cols, batchId, FILE_TPYE_DBF);
	}
	
	public static String getTXTInsertStmtByCols(String table, List<String>cols, String batchId){
		return getInsertStmtByCols(table, cols, batchId, FILE_TPYE_TXT);
	}
	
	public static String getInsertStmtByCols(String table, List<String>cols, String batchId, String fileType){
		StringBuffer colSb = new StringBuffer();
		StringBuffer valueSb = new StringBuffer();
		
		if(null != batchId){
			//Add batch id into the query
			colSb.append(BATCH_ID_COLUMN_NAME);
			colSb.append(COMMA);
			valueSb.append("\'" + batchId + "\'");
			valueSb.append(COMMA);
		}
		for(int i = 0; i < cols.size(); i++){
			colSb.append("[");
			colSb.append(cols.get(i));
			colSb.append("]");
			valueSb.append("?");
			if( i < cols.size() - 1){
				colSb.append(COMMA);
				valueSb.append(COMMA);
			}
		}
		String stmt = fileType.equals(FILE_TPYE_DBF) ? String.format(INSERT_INTO_SQL_FROM_DBF, table, colSb.toString(), valueSb.toString())
						:  String.format(INSERT_INTO_SQL_FROM_TXT, table, colSb.toString(), valueSb.toString());
//		LogUtils.printLog("SQLStmtUtils {}", stmt);
		return stmt;
	}
	
	public static String getOracleInsertStmtByCols(String table, List<String>cols, String batchId){
		StringBuffer colSb = new StringBuffer();
		StringBuffer valueSb = new StringBuffer();
		
		if(null != batchId){
			//Add batch id into the query
			colSb.append(BATCH_ID_COLUMN_NAME);
			colSb.append(COMMA);
			valueSb.append("\'" + batchId + "\'");
			valueSb.append(COMMA);
		}
		for(int i = 0; i < cols.size(); i++){
			colSb.append("\"");
			colSb.append(cols.get(i));
			colSb.append("\"");
			valueSb.append("?");
			if( i < cols.size() - 1){
				colSb.append(COMMA);
				valueSb.append(COMMA);
			}
		}
		String stmt = String.format(INSERT_INTO_ORACLE, table, colSb.toString(), valueSb.toString());
//		LogUtils.printLog("Start to execute statement {}", stmt);
		return stmt;
	}
	
	public static String getDeleteByKeysStmt(String table, String[] keys){
		return String.format(DELETE_FROM, table, getParametizedCriteriaString(keys));
	}
	
	public static String getCheckSumStmt(String checkSumColumns, String table, String...criteria){
		
        StringBuffer cols = new StringBuffer();
        
        String[] columns = checkSumColumns.split(COMMA);
        if (columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
            	cols.append("SUM(" + columns[i] + ")");
                if (i < columns.length - 1) {
                	cols.append(COMMA);
                }
            }
        }
        
		String stmt = String.format(SELECT_STMT, cols.toString(), table, getCriteriaString(criteria));
		return stmt;
	}
	
	public static String getDeleteSQL(String table, String...criteria){
		
		return String.format(DELETE_FROM, table, getCriteriaString(criteria));
	}
	
	public static String getCriteriaString(String...criteria){
		if (criteria == null)
			return "";
		
		StringBuffer criteriaSb = new StringBuffer();
		if(criteria.length > 0){
			criteriaSb.append(" WHERE ");
			for(int i = 0 ; i < criteria.length; i++){
				criteriaSb.append(criteria[i]);
				if(i < criteria.length - 1){
					criteriaSb.append(" AND ");
				}
					
			}
		}
		
		return String.format(criteriaSb.toString());
	}
	
	public static String getParametizedCriteriaString(String...keys){
		StringBuffer keyBuffer = new StringBuffer();
		if(keys.length > 0){
			keyBuffer.append(" WHERE ");
			for(int i = 0 ; i < keys.length; i++){
				keyBuffer.append(keys[i]);
				keyBuffer.append("=?");
				if(i < keys.length - 1){
					keyBuffer.append(" AND ");
				}
			}
		}
		return keyBuffer.toString();
	}
	
	protected static String getColsWithBatchIdValue(List<String> cols, String batchId){
		StringBuffer colSb = new StringBuffer();
		
		if(null != batchId){
			//Add batch id into the query
			colSb.append("\'" + batchId + "\' AS " + BATCH_ID_COLUMN_NAME );
			colSb.append(COMMA);
		}
		
		for(int i = 0; i < cols.size(); i++){
			colSb.append(cols.get(i));
			if( i < cols.size() - 1){
				colSb.append(COMMA);
			}
		}
		return colSb.toString();
	}
	
	public static String getSelectStmt(String table, List<String> cols, boolean withBatchId, String...criteria){
		
		StringBuffer sb = new StringBuffer();
		
		if(withBatchId){
			//Add batch id into the query
			sb.append(BATCH_ID_COLUMN_NAME );
			sb.append(COMMA);
		}
		
		for(int i = 0; i < cols.size(); i++){
			sb.append(cols.get(i));
			if( i < cols.size() - 1){
				sb.append(COMMA);
			}
		}
		
		String stmt = String.format(SELECT_STMT, sb.toString(), table, getCriteriaString(criteria));
//		LogUtils.printLog(stmt);
		
		return stmt;
		
	}
	
	public static String getInsertSelectStmt(String originalTable, String histTable, 
					List<String> cols, boolean withBatchId, String...criteria){
		
		String stmt = String.format(INSERT_FROM_SELECT, histTable, 
							getSelectStmt(originalTable, cols, withBatchId));
		
		return stmt;
	}
	
    public static String getUpdatePendingStatusSql(String table, String... criteria) {
        return String.format(UPDATE_PENDING_STATUS, table, getCriteriaString(criteria));
    }

    public static String getUpdateCompleteSqlByStatus(String table, String... criteria) {
        return String.format(UPDATE_COMPLETE_STATUS, table, getCriteriaString(criteria));
    }

    public static String getUpdateNullSqlByStatus(String table, String... criteria) {
        return String.format(UPDATE_NULL_STATUS, table, getCriteriaString(criteria));
    }

    public static String getUpdateSpaceSqlByStatus(String table, String... criteria) {
        return String.format(UPDATE_SPACE_STATUS, table, getCriteriaString(criteria));
    }
    
    public static String getUpdateBlankSqlByStatus(String table, String... criteria) {
        return String.format(UPDATE_BLANK_STATUS, table, getCriteriaString(criteria));
    }

//    public static String getUpdateSqlByStatus(String table, String branchCode, String businessDate) {
//        return String.format(UPDATE_BY_STATUS, table, branchCode, businessDate);
//    }

	public static String getCheckSumStmtToEDW(String checkSumColumns, String table, String...criteria) {
		StringBuffer cols = new StringBuffer();
        
        String[] columns = checkSumColumns.split(COMMA);
        if (columns.length > 0) {
            for (int i = 0; i < columns.length; i++) {
            	cols.append("SUM(" + columns[i] + ")");
                if (i < columns.length - 1) {
                	cols.append(COMMA);
                }
            }
        }
        
		String stmt = String.format(SELECT_STMT_EDW, cols.toString(), table, getCriteriaString(criteria));
		return stmt;
	}
}
