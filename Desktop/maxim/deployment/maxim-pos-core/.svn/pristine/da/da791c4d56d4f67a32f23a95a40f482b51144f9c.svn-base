package com.maxim.pos.sales.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;

@Repository("realTimeDao")
public class RealTimeDaoImpl implements RealTimeDao {

    @Autowired
    protected NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getRealTimeDataList(SchemeInfo schemeInfo, String branchCode, String mapBranchCode, Date businessDate) {
    	
    	List<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
    	
    	StringBuilder sqlTemplate = new StringBuilder("SELECT %s FROM %s WHERE status<>'C' AND branch_code=:branchCode ");
		if (businessDate != null) {
			sqlTemplate.append(" AND DATEDIFF(day,business_date,:businessDate)=0");
		}
        
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("branchCode", branchCode);
        paramMap.put("businessDate", businessDate);

        String fromTable = schemeInfo.getSource();
        if (StringUtils.isEmpty(fromTable)) {
            return Collections.emptyList();
        }

        StringBuilder columns = new StringBuilder();

        List<SchemeTableColumn> tableColumns = schemeInfo.getSchemeTableColumns();
        if (tableColumns.isEmpty()) {
        	columns.append("*");
        }
        else
        {
	        for (SchemeTableColumn tableColumn : tableColumns) {
	            if (tableColumn == null
	                    || tableColumn.getFromColumn() == null
	                    || tableColumn.getToColumn() == null) {
	                continue;
	            }
	            columns.append(tableColumn.getFromColumn())
	                    .append(" AS ")
	                    .append(removeUnderscore(tableColumn.getFromColumn()))
	                    .append(",");
	        }
	        if (StringUtils.isEmpty(columns.toString())) {
	            return Collections.emptyList();
	        }
	        if (columns.length() > 1) {
	            columns.deleteCharAt(columns.length() - 1);
	        }
        }

        String sql = String.format(sqlTemplate.toString(), columns, fromTable);
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, paramMap);
        for (Map<String, Object> map : list)
        {
        	HashMap<String, Object> newMap = new HashMap<String, Object>();
        	for (String key: map.keySet())
        	{
        		Object value = map.get(key);
        		String newKey = removeUnderscore(key);
                // lotic branch code mapping
//                if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(key,"branchCode") && value != null &&
//                        org.apache.commons.lang.StringUtils.isNotBlank(ContextUtils.MAPPING_BRANCH_CODE.get(value.toString()))){
//                    value = ContextUtils.MAPPING_BRANCH_CODE.get(value.toString());
//                }
                if(org.apache.commons.lang.StringUtils.equalsIgnoreCase(key,"branchCode") && value != null ){
                    value = mapBranchCode ;
                }
        		newMap.put(newKey, value);
        	}
        	rtnList.add(newMap);
        }
        return rtnList;
    }

    @Override
    public int updateStatus(String sourceTable, String branchCode, Date businessDate) {
    	StringBuilder sqlTemplate = new StringBuilder("UPDATE " + sourceTable + " SET status ='C' WHERE status<>'C' AND branch_code=:branchCode ");
		if (businessDate != null) {
			sqlTemplate.append(" AND DATEDIFF(day,business_date,:businessDate)=0");
		}
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("branchCode", branchCode);
        paramMap.put("businessDate", businessDate);
        return jdbcTemplate.update(sqlTemplate.toString(), paramMap);
    }

    private static String removeUnderscore(String column) {
        StringBuilder result = new StringBuilder();
        String[] arr = column.toLowerCase().split("_");
        for (String str : arr) {
            result.append(StringUtils.capitalize(str));
        }
        return StringUtils.uncapitalize(result.toString());
    }

}
