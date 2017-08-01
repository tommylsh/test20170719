package com.maxim.pos.report.service;

import java.util.Map;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.report.data.ReportConfig;

public interface ReportService {

	
	/**
	 * Get Workbook by the Configuration and Parameter
	 * 
	 * @param downlaodReportConfig	Report Config
	 * @param paramMap	            parameters Map
	 * @return	                    Export Workbook
	 */
	public SXSSFWorkbook getWorkbook(ReportConfig downlaodReportConfig, Map<String, Object> paramMap) ;
	public boolean sendReportMail(BranchScheme branchScheme, Logger logger) ;

}
