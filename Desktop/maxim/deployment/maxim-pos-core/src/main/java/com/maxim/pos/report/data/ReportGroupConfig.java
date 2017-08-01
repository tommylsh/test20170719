/**
 * Class ReportConfig
 * 
 * Created by Edward Wu
 * Created on 15 Mar 2017
 *  
 * Amendment History
 * 
 * Name                  Modified on  Comment
 * --------------------  -----------  ----------------------------------------
 * 
 * 
 */
package com.maxim.pos.report.data;

import java.util.List;

/**
 * 
 * @author Enlightening
 *
 * The data class store the Configuration of a Group of Report
 *
 */
public class ReportGroupConfig {

	
	private String reportGroupName;
	private List<String> parameters;
	private List<ReportConfig> reportList;
	
	public String getReportGroupName() {
		return reportGroupName;
	}
	public void setReportGroupName(String reportGroupName) {
		this.reportGroupName = reportGroupName;
	}
	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}
	public List<ReportConfig> getReportList() {
		return reportList;
	}
	public void setReportList(List<ReportConfig> reportList) {
		this.reportList = reportList;
	}

	

}
