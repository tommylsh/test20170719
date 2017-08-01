package com.maxim.pos.common.service;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.dao.QueryFileHandler;
import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.ColumnFormat;
import com.maxim.pos.common.persistence.PollSchemeInfoDao;
import com.maxim.pos.common.persistence.PosDaoCmd;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.TableColumnConstants;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.sales.persistence.SchemeInfoDao;

@Transactional
@Service(PollSchemeInfoService.BEAN_NAME)
public class PollSchemeInfoServiceImpl implements PollSchemeInfoService{

    @Autowired
    private PollSchemeInfoDao pollSchemeInfoDao;
    
    @Autowired
    private QueryFileHandler queryFileHandler;
    
    @Autowired
    private ApplicationSettingService applicationSettingService;
    
    public Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void delete(Long schemeInfoId) {
		if (schemeInfoId == null) {
			throw new RuntimeException("[Validation failed] - this argument [schemeInfoId] is required; it must not be null");
		}
		SchemeInfo schemeInfo = pollSchemeInfoDao.getById(schemeInfoId);
		if (schemeInfo == null) {
			throw new RuntimeException("The record which schemeInfoId=" + schemeInfoId + " is not exist.");
		}
		pollSchemeInfoDao.delete(schemeInfo);
	}

	@Override
	public Long getSchemeInfoCountByCriteria(CommonCriteria criteria) {
		return pollSchemeInfoDao.getSchemeInfoCountByCriteria(criteria);
	}

    @Override
    public List<SchemeInfo> findSchemeInfoByCriteria(CommonCriteria criteria) {
        return pollSchemeInfoDao.findSchemeInfoByCriteria(criteria);
    }

    @Override
    public List<SchemeInfo> findSchemeInfo(Map<String, Object> paramMap) {
        return pollSchemeInfoDao.findSchemeInfo(paramMap);
    }

    @Override
    public List<SchemeInfo> findSchemeInfoBySchemeTypeAndClientType(String pollSchemeType, ClientType clientType) {
        List<SchemeInfo>  list =  pollSchemeInfoDao.findSchemeInfoBySchemeTypeAndClientType(pollSchemeType,clientType);
        Collections.shuffle(list);
        return list;
    }

    @Override
    public List<SchemeInfo> findSchemeInfoByBranchSchemeAndClientType(BranchScheme branchScheme, ClientType clientType) {
    	List<SchemeInfo>  list = null ;
    	if (clientType == ClientType.SQLSVRPOS)
    	{
    		clientType = ClientType.SQLPOS;
    	}
    	if (branchScheme.getSchemeJobLog() != null)
    	{
	    	synchronized(branchScheme.getSchemeJobLog())
	    	{
	    		if (branchScheme.getSchemeJobLog().getSchemeInfoListMap() == null)
	    		{
	    			branchScheme.getSchemeJobLog().setSchemeInfoListMap(new HashMap<String, List<SchemeInfo>>());
	    		}
	    		String key = branchScheme.getPollSchemeName() + "_" + clientType.name(); 
	    		list = branchScheme.getSchemeJobLog().getSchemeInfoListMap().get(key);
	    		if (list == null)
	    		{
	    			list =  pollSchemeInfoDao.findSchemeInfoBySchemeTypeAndClientType(branchScheme.getPollSchemeName(),clientType);
	    	        Collections.shuffle(list);
	    			branchScheme.getSchemeJobLog().getSchemeInfoListMap().put(key, list);
	    		}
	    	}
    	}
    	else
    	{
			list =  pollSchemeInfoDao.findSchemeInfoBySchemeTypeAndClientType(branchScheme.getPollSchemeName(), clientType);
	        Collections.shuffle(list);
    	}
        return list;
    }

    @Override
    public SchemeInfo addOrUpdateSchemeInfo(SchemeInfo schemeInfo) {
        return pollSchemeInfoDao.addOrUpdateSchemeInfo(schemeInfo);
    }
    
    
    @Override
    @Transactional
    public List<SchemeTableColumn> generateSchemeTableColumnData(SchemeInfo schemeInfo){
    	
    	String tableName = "";
    	// when poll scheme type is ORACLE, Staging is the source table
    	// for other client type, staging is the destination table
    	if(schemeInfo.getClientType().equals(ClientType.ORACLE))
    		tableName = schemeInfo.getSource();
    	else
    		tableName = schemeInfo.getDestination();
    	
    	List<Map<String, Object>> columnMapList = getTableColumnInfo(tableName);;

    	
    	LogUtils.printLog(logger,"Get Table[{}] Column Map List Size = {}"
    			,schemeInfo.getDestination()
    			,columnMapList.size());
    	
    	List<SchemeTableColumn> schemeTableColumnList = new ArrayList<SchemeTableColumn>();
    	
    	Integer seq = 0;
    	
    	for(Map<String, Object> columnMap: columnMapList){
    		
    		SchemeTableColumn schemeTableColumn = new SchemeTableColumn();
    		// format field values by client types
        	if(schemeInfo.getClientType().equals(ClientType.CSV)){
        		schemeTableColumn.setFromColumn(
        				(String)columnMap.get(TableColumnConstants.KEY_COLUMN_NAME));
        		schemeTableColumn.setFromColumnFormat(
        				(String)columnMap.get(TableColumnConstants.KEY_COLUMN_FORMAT));
        		
        	}
        	else if(schemeInfo.getClientType().equals(ClientType.DBF)){
        		String columnName = (String)columnMap.get(TableColumnConstants.KEY_COLUMN_NAME);
        		
        		// Skip the fields DBF does not contain
        		if(columnName.equalsIgnoreCase(TableColumnConstants.DBF_SKIP_STATUS)
        				||columnName.equalsIgnoreCase(TableColumnConstants.DBF_SKIP_LAST_UPDATE_TIME))
        			continue;

        		//
        		if(columnName.equalsIgnoreCase(TableColumnConstants.DBF_SKIP_ROWGUID)){
        			schemeTableColumn.setFromColumn(columnName);
        		}
        		else{
            		String fromColumn = TableColumnConstants.DBF_COLUMN_PREFIX + 
            				String.format("%03d", seq);
            		schemeTableColumn.setFromColumn(fromColumn);
        		}

        		String columnFormat = (String)columnMap.get(TableColumnConstants.KEY_COLUMN_FORMAT);
        		
        		if(columnFormat.equals(ColumnFormat.JDBC_BIGINT.toString())
        				||columnFormat.equals(ColumnFormat.JDBC_DECIMAL.toString())
        				||columnFormat.equals(ColumnFormat.JDBC_DOUBLE.toString())
        				||columnFormat.equals(ColumnFormat.JDBC_FLOAT.toString())
        				||columnFormat.equals(ColumnFormat.JDBC_SMALLINT.toString())
        				||columnFormat.equals(ColumnFormat.JDBC_TINYINT.toString())
        				||columnFormat.equals(ColumnFormat.JDBC_INTEGER.toString())){
        			schemeTableColumn.setFromColumnFormat(ColumnFormat.DBF_FIELD_TYPE_N.toString());
        		}else
        			schemeTableColumn.setFromColumnFormat(ColumnFormat.DBF_FIELD_TYPE_C.toString());
        		
        	}
        	// special field handling for SQLPOS, ORACLE and SQLSERVER

        	else if(StringUtils.startsWith(schemeInfo.getClientType().name(),"SQLPOS")
        			||schemeInfo.getClientType().equals(ClientType.ORACLE)
        			||schemeInfo.getClientType().equals(ClientType.SQLSERVER)){
        		schemeTableColumn.setFromColumn(
        				(String)columnMap.get(TableColumnConstants.KEY_COLUMN_NAME));
        		schemeTableColumn.setFromColumnFormat(
        				(String)columnMap.get(TableColumnConstants.KEY_COLUMN_FORMAT));
        	}

        	// set other common fields
        	schemeTableColumn.setSeq(seq);
    		schemeTableColumn.setFromColumnLength(
    				(Integer)columnMap.get(TableColumnConstants.KEY_COLUMN_LENGTH));
    		schemeTableColumn.setFromColumnPrecision(
    				(Integer)columnMap.get(TableColumnConstants.KEY_COLUMN_PRECISION));
    		schemeTableColumn.setToColumn(
    				(String)columnMap.get(TableColumnConstants.KEY_COLUMN_NAME));
    		schemeTableColumn.setToColumnFormat(
    				(String)columnMap.get(TableColumnConstants.KEY_COLUMN_FORMAT));
    		schemeTableColumn.setToColumnLength(
    				(Integer)columnMap.get(TableColumnConstants.KEY_COLUMN_LENGTH));
    		schemeTableColumn.setToColumnPrecision(
    				(Integer)columnMap.get(TableColumnConstants.KEY_COLUMN_PRECISION));
    		
    		schemeTableColumn.onCreate(Auditer.SYSTEM_RESERVED_USER_ID);
    		schemeTableColumn.onUpdate(Auditer.SYSTEM_RESERVED_USER_ID);
    		schemeTableColumn.setSchemeInfo(schemeInfo);
    		
    		schemeTableColumnList.add(schemeTableColumn);
    		
    		LogUtils.printLog(logger, "Record Seq={} From Column Info: {}", seq, schemeTableColumn.getFromColumnInfo());
    		LogUtils.printLog(logger, "Record Seq={} To Column Info: {}", seq, schemeTableColumn.getToColumnInfo());
    		++seq;
    	}

    	return schemeTableColumnList;
    }
    
    @Override
    public List<Map<String, Object>> getTableColumnInfo(String tableName){
    	
    	Connection conn = applicationSettingService.getCurrentJDBCConnection();
    	
    	String databaseName = "";
    	try{
    		databaseName = conn.getCatalog();
    	}
    	catch(SQLException e){
    		throw new RuntimeException(e);
    	}
    	PosDaoCmd cmd = new PosDaoCmd(SchemeInfoDao.SQL_getTableColumnInfoByDBNameAndTable);
    	String sql = queryFileHandler.formatQuery(cmd.getQueryKey(),
                queryFileHandler.getQueryFile().getString(cmd.getQueryKey()), cmd.getParams());
    	
    	LogUtils.printLog(logger, "Start to Execute SQL Statement: {}", sql);
    	
    	List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
    	try(PreparedStatement stmt = conn.prepareStatement(sql,
    			ResultSet.TYPE_SCROLL_SENSITIVE,
				ResultSet.CONCUR_READ_ONLY)){
    		stmt.setString(1, databaseName);
    		stmt.setString(2, tableName);
    		
    		try(ResultSet rs = stmt.executeQuery()){
    			
    			while(rs.next()){
    				Map<String, Object> map = new HashMap<String,Object>();
    				map.put("columnName", rs.getObject(TableColumnConstants.KEY_COLUMN_NAME));
    				map.put("columnFormat", rs.getObject(TableColumnConstants.KEY_COLUMN_FORMAT));
    				map.put("columnLength", rs.getObject(TableColumnConstants.KEY_COLUMN_LENGTH));
    				map.put("columnPrecision", rs.getObject(TableColumnConstants.KEY_COLUMN_PRECISION));
    				
    				listMap.add(map);
    			}
    			return listMap;
    		}
        	catch(Exception e){
        		throw new RuntimeException(e);
        	}
    		
    	}
    	catch(Exception e){
    		throw new RuntimeException(e);
    	}
    }
}