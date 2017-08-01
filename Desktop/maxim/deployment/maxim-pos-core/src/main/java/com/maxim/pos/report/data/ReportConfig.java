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
import java.util.Map;

/**
 * 
 * @author Enlightening
 *
 * The data class store the Configuration of Report
 *
 */
public class ReportConfig implements org.springframework.beans.factory.BeanNameAware {

	private String reportTitle;
	private List<String> parameters;

	private String fileName;
	private String fileType;
	private String queryType;
	private String query;
	private String targetMethod;
	private String targetBeanId;
	private Object targetObject;
	private Map<String, String> colunmMap;
	
	private String reportID;
	
	@Override
	public void setBeanName(String name) {
		
		
		System.out.println("ReportConfig SET NAME "+name);
		this.reportID = name;
		
	}

	public String getReportID() {
		return reportID;
	}

	
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
	
	public String getTargetMethod() {
		return targetMethod;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}

	public Object getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}

	public List<String> getParameters() {
		return parameters;
	}
	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	public String getTargetBeanId() {
		return targetBeanId;
	}

	public void setTargetBeanId(String targetBeanId) {
		this.targetBeanId = targetBeanId;
	}
	public Map<String, String> getColunmMap() {
		return colunmMap;
	}

	public void setColunmMap(Map<String, String> colunmMap) {
		this.colunmMap = colunmMap;
	}

	
}
