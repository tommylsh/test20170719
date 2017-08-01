package com.maxim.pos.sales.service;

import java.io.File;
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
import com.maxim.pos.common.util.JavaCSVUtils;

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

@Service("csvSalesService")
public class SalesServiceCsvImpl extends  SalesServiceFileImpl {
	
//    protected @Value("${sales.csvTempDirectory}") String csvTempDirectory ;
	protected @Value("${sales.csvFileMethod}") String fileMethod = null;
	protected @Value("${sales.csvFilePattern}") String filePattern = null;
	protected @Value("${sales.csvZipPattern}") String zipPattern = null;
	protected @Value("${sales.csvSTZipPattern}") String stZipPattern = null;
	
	protected ThreadLocal<File> tmpFile = new ThreadLocal<File>();
	
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
    protected String getSTZipPattern()
    {
    	return stZipPattern ;
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
        return JavaCSVUtils.bulkCopyFromCSVToSQL(in, conn, schemeInfo, defaultTransactionBatchSize, null, false, conversion);
	}

}
