package com.maxim.pos.report.web.faces.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.maxim.pos.report.enumeration.ReportType;
import com.maxim.pos.report.service.ReportService;


@Controller("downloadManagementController")
@ViewScoped
public class DownloadManagementController {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private ReportService reportService;
	private List<String> reportTypeList;
	private String reportType;
	private String businessDate;
	private String parameter;
	
	
	public List<String> getReportTypeList()
	{
		reportTypeList = new ArrayList<>();
		for (ReportType type : ReportType.values()) {
			reportTypeList.add(type.toString());
		}
		
		return reportTypeList;
	}
	
	public String getReportType()
	{
		return reportType;
	}
	
	public void setReportType(String reportType)
	{
		this.reportType = reportType;
	}
	
	public String getBusinessDate() 
	{
		return businessDate;
	}

	public void setBusinessDate(String businessDate) 
	{
		this.businessDate = businessDate;
	}

	public String getParameter() 
	{
		return parameter;
	}

	public void setParameter(String parameter) 
	{
		this.parameter = parameter;
	}

	public void downloadReport() throws ParseException 
	{
		ReportType type = ReportType.getReportTypeByStringName(reportType);
		Date date = sdf.parse(businessDate);
		
		ByteArrayOutputStream exportFileByteAry = null;
		switch (type) {
			case ERROR_COUNT_REPORT: exportFileByteAry = reportService.exportErrorCountReportFile(date, parameter);
			break;
			case CONVERT_MONITOR_REPORT: exportFileByteAry = reportService.exportConvertMonitorReportFile(date, parameter);
			break;
			case ERROR_LOG_REPORT: exportFileByteAry = reportService.exportErrorLogReportFile(date, parameter);
			break;
			case ERROR_CLIENT_REPORT: exportFileByteAry = reportService.exportErrorClientReportFile(date, parameter);
			break;
		}
		
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext context = fc.getExternalContext();
		context.responseReset();
		context.setResponseContentLength(exportFileByteAry.size());
		context.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		context.setResponseHeader("content-Disposition", String.format("attachment; filename=\"%s.xlsx\"", 
																		type.name().toLowerCase()));
		
		OutputStream output = null;
		try {
			output = context.getResponseOutputStream();
			output.write(exportFileByteAry.toByteArray());
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != exportFileByteAry)
					exportFileByteAry.close();
			} catch (IOException ioEx) {}
			try {
				if (null != output)
					output.close();
			} catch (IOException ioEx) {}
		}
		
		fc.responseComplete();
	}
	
}

