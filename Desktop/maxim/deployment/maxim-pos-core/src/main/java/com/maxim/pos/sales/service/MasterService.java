package com.maxim.pos.sales.service;

import java.sql.BatchUpdateException;
import java.sql.SQLException;

import org.slf4j.Logger;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.microsoft.sqlserver.jdbc.SQLServerException;

public interface MasterService {
	/**
	 * process Master / price data Staging To Pos
	 * @param branchScheme
	 * @return process result message
	 */
	public String processStagingToPos(BranchScheme branchScheme ,Logger logger);

	/**
	 * 
	 * @param branchScheme
	 * @param taskJobLog
	 * @param logger
	 * @return
	 * @throws SQLException
	 * @throws SQLServerException
	 * @throws BatchUpdateException
	 */
	public boolean processMasterServerToStaging(BranchScheme branchScheme, 
			SchemeJobLog schemeJobLog,
			Logger logger);
	
	public boolean processFolderCopy(BranchScheme branchScheme, 
			SchemeJobLog schemeJobLog,
			Logger logger);
	
}