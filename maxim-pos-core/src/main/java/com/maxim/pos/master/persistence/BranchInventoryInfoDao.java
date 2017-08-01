package com.maxim.pos.master.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.maxim.dao.JdbcEntityDAO;
import com.maxim.pos.master.entity.BranchInventoryInfo;

@Repository("ranchInventoryInfoDao")
public class BranchInventoryInfoDao extends JdbcEntityDAO<BranchInventoryInfo, BranchInventoryInfo>{

	private static final  String branch_inventory_info_query = "select business_date from branch_inventory_info where branch_code = :branchCode and business_date > :businessDate order by business_date asc";

    @Resource(name="masterJdbcTemplate")
	protected NamedParameterJdbcTemplate nameJdbcTempalte ;

	public BranchInventoryInfoDao()
	{
		super();
	}

	@Override
	protected NamedParameterJdbcTemplate getNamedJdbcTemplate() {

		return this.nameJdbcTempalte;
	}

//	public List<Map<String, Object>>  getPosEODStockDateList(String branchCode,java.sql.Date businessDate){
//		Map<String, Object> paramMap = new HashMap<>();
//		paramMap.put("branchCode",branchCode);
//		paramMap.put("businessDate",businessDate);
//		return  nameJdbcTempalte.queryForList(branch_inventory_info_query,paramMap);
//	}

	public List<Date>  getPosEODStockDateList(String branchCode,java.sql.Date businessDate){
		List<Date> returnDates = new ArrayList<Date>();
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("branchCode",branchCode);
		paramMap.put("businessDate",businessDate);
		List<java.sql.Date> list = nameJdbcTempalte.queryForList(branch_inventory_info_query,paramMap, java.sql.Date.class);
		for (Date date : list)
		{
			returnDates.add(date);
		}
		return returnDates;
	}






}
