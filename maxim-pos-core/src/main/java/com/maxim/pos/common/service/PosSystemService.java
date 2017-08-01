package com.maxim.pos.common.service;

import java.util.List;

import org.slf4j.Logger;

import com.maxim.pos.common.entity.BranchScheme;

public interface PosSystemService {	
	/**
	 * check EOD is Complete
	 *  select HIST_POS_SYSTEM by branchCode and businessDate= date
	 * @param branchCode
	 * @param date
	 * @return if exist record return true  else return false
	 */
//	public boolean checkEodComplete(String branchCode,Date date);

//	public boolean checkEODStart(BranchScheme branchScheme,Logger logger);
	public List<java.util.Date> getPosEODBusinessDateList(BranchScheme branchScheme, java.sql.Date businessDate, Logger logger) ;

	public java.sql.Date getBussinessDate() ;

}