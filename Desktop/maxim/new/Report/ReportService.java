package com.maxim.pos.report.service;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import com.maxim.pos.report.enumeration.ReportType;

public interface ReportService {

	/**
	 * Export Error Count Report
	 * 
	 * @param businessDate	Business date
	 * @param parameters	parameters
	 * @return	Export file byte array output stream
	 */
	public ByteArrayOutputStream exportErrorCountReportFile(Date businessDate, String parameters);
	
	/**
	 * Export Convert Monitor Report
	 * 
	 * @param businessDate	Business date
	 * @param parameters	parameters
	 * @return	Export file byte array output stream
	 */
	public ByteArrayOutputStream exportConvertMonitorReportFile(Date businessDate, String parameters);

	/**
	 * Export Error Log Report
	 * 
	 * @param businessDate	Business date
	 * @param parameters	parameters
	 * @return	Export file byte array output stream
	 */
	public ByteArrayOutputStream exportErrorLogReportFile(Date businessDate, String parameters);

	/**
	 * Export Error Client Report
	 * 
	 * @param businessDate	Business date
	 * @param parameters	parameters
	 * @return	Export file byte array output stream
	 */
	public ByteArrayOutputStream exportErrorClientReportFile(Date businessDate, String parameters);
}
