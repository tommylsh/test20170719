package com.maxim.pos.sales.service;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.JavaDBFUtils;

/**
 * Class SalesServiceDbfImpl
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

@Service("dbfSalesService")
public class SalesServiceDbfImpl extends  SalesServiceFileImpl {
	
	protected @Value("${sales.dbfFileMethod}") String fileMethod = null;
	protected @Value("${sales.dbfFilePattern}") String filePattern = null;
	protected @Value("${sales.dbfZipPattern}") String zipPattern = null;
	
	@Override
    protected String getFileMethod(){
		return fileMethod ;
	}
	@Override
    protected String getFilePattern(){
		return filePattern ;
	}
	@Override
    protected String getZipPattern()
    {
    	return zipPattern ;
    }	
	@Override
	protected int processToStagingTable(BranchScheme branchScheme, SchemeInfo schemeInfo, Date bizDate, 
			InputStream in , Connection conn, int defaultTransactionBatchSize ) throws SQLException, IOException
	{
    	String conversion = JDBCUtils.CONV_NONE ;
    	if (JDBCUtils.CONV_CHI_BRANCH_LIST.contains(branchScheme.getBranchMaster().getBranchCode()))
    	{
    		conversion = JDBCUtils.CONV_SIMPLIFIED_TO_TRADTION ;
    	}

        return JavaDBFUtils.bulkCopyFromDBFToSQL(in, conn, schemeInfo, defaultTransactionBatchSize, null,conversion);
	}


}
