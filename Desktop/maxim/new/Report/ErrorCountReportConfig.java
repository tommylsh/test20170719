package com.maxim.pos.report.config;

public class ErrorCountReportConfig {

	private String reportTitle;
	private String fileName;
	private String fileType;
	private String queryType;
	private String query;
	
	
	public String getReportTitle() {
		return reportTitle;
	}
	
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileType() {
		return fileType;
	}
	
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	
	public String getQueryType() {
		return queryType;
	}
	
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = (null != query ? query.trim() : null);
	}
	
}
