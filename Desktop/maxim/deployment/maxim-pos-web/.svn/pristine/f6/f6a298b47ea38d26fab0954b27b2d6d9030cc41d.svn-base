package com.maxim.pos.report.web.faces.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.maxim.pos.report.data.ReportConfig;
import com.maxim.pos.report.data.ReportGroupConfig;
import com.maxim.pos.report.service.ReportService;


@Controller("downloadManagementController")
@Scope("viewScope")
public class DownloadManagementController implements ApplicationContextAware{

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Autowired
	private ReportService reportService;

	@Autowired
	private ApplicationContext appContext;

	private Map<String, ReportGroupConfig> reportExportConfiguration ;
	
	
	
	@SuppressWarnings("unchecked")
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
		this.appContext = applicationContext; 
		
		this.reportExportConfiguration = (Map<String, ReportGroupConfig>) this.appContext.getBean("reportExportConfiguration");
		
    }
//	@Autowired
	public void setReportExportConfiguration(Map<String, ReportGroupConfig> reportExportConfiguration) {
		this.reportExportConfiguration = reportExportConfiguration;
	}

	//	private List<String> reportTypeList;
	private String reportGroup;
	private String reportID;
//	private String businessDate;
	private String[] parameters;
	
//	
//	public List<String> getReportTypeList()
//	{
//		reportTypeList = new ArrayList<>();
//		for (ReportType type : ReportType.values()) {
//			reportTypeList.add(type.toString());
//		}
//		
//		return reportTypeList;
//	}
//
	
	public String getReportGroup() {
		return reportGroup;
	}

	public void setReportGroup(String reportGroup) {
		this.reportGroup = reportGroup;
	}

	public String getReportID() {
		return reportID;
	}

	public void setReportID(String reportID) {
		this.reportID = reportID;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

//
//	public String getReportType()
//	{
//		return reportType;
//	}
//	
//	public void setReportType(String reportType)
//	{
//		this.reportType = reportType;
//	}
//	
//	public String getBusinessDate() 
//	{
//		return businessDate;
//	}
//
//	public void setBusinessDate(String businessDate) 
//	{
//		this.businessDate = businessDate;
//	}
//
//	public String getParameter() 
//	{
//		return parameter;
//	}
//
//	public void setParameter(String parameter) 
//	{
//		this.parameter = parameter;
//	}

	public void downloadReport() throws ParseException 
	{
		this.parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterValuesMap().get("parameters");
		
		System.out.println("reportExportConfiguration : " + reportExportConfiguration);
		System.out.println("reportGroup : " + reportGroup);
		System.out.println("reportID : " + reportID);
		if (parameters == null)
		{
			System.out.println("parameters : null");
		}
		else
		{
			for(String p : parameters)
			{
				System.out.println("parameters : " + p);
			}
		}
		
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ReportGroupConfig reportGroupConfig = this.reportExportConfiguration.get(reportGroup);
		
		int parameterIndex=0;
		if (reportGroupConfig.getParameters() != null)
		{
			for (String paramName : reportGroupConfig.getParameters())
			{
				paramMap.put(paramName, parameters[parameterIndex++]);
			}
		}
		
		ReportConfig downlaodReportConfig = null ;
		for (ReportConfig reportConfig : reportGroupConfig.getReportList())
		{
			if (reportConfig.getReportID().equals(reportID))
			{
				downlaodReportConfig = reportConfig ;
				if (reportConfig.getParameters() != null)
				{
					for (String paramName : reportConfig.getParameters())
					{
						paramMap.put(paramName, parameters[parameterIndex++]);
					}
				}
				break;
			}
		}
		
		if (downlaodReportConfig.getFileType().equals("xlsx"))
		{
			SXSSFWorkbook workbook = reportService.getWorkbook(downlaodReportConfig, paramMap) ;
			
			
			FacesContext fc = FacesContext.getCurrentInstance();
			ExternalContext context = fc.getExternalContext();
			context.responseReset();
//			context.setResponseContentLength(exportFileByteAry.size());
			context.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			context.setResponseHeader("content-Disposition", String.format("attachment; filename=\"%s.xlsx\"", 
					downlaodReportConfig.getFileName()));

			if (workbook!= null)
			{
				OutputStream output = null;
				try 
				{
					output = context.getResponseOutputStream();
					workbook.write(output);
					
			    } 
				catch (IOException e) {
				
					e.printStackTrace();
				} 
				finally 
				{
					workbook.dispose();	
					try 
					{
						 if (null != output)
						 {
							 output.close();
						 }
					} 
					catch (IOException ioEx) {ioEx.printStackTrace();}
				}
			}

			fc.responseComplete();
			return ;
		}
		
//		ReportType type = ReportType.getReportTypeByStringName(reportType);
//		Date date = sdf.parse(businessDate);
//		
//		ByteArrayOutputStream exportFileByteAry = null;
//		switch (type) {
//			case ERROR_COUNT_REPORT: exportFileByteAry = reportService.exportErrorCountReportFile(date, parameter);
//			break;
//			case CONVERT_MONITOR_REPORT: exportFileByteAry = reportService.exportConvertMonitorReportFile(date, parameter);
//			break;
//			case ERROR_LOG_REPORT: exportFileByteAry = reportService.exportErrorLogReportFile(date, parameter);
//			break;
//			case ERROR_CLIENT_REPORT: exportFileByteAry = reportService.exportErrorClientReportFile(date, parameter);
//			break;
//		}
//		
		
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext context = fc.getExternalContext();
		context.responseReset();
//		context.setResponseContentLength(exportFileByteAry.size());
//		context.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
//		context.setResponseHeader("content-Disposition", String.format("attachment; filename=\"%s.xlsx\"", 
//																		type.name().toLowerCase()));
//		
//		OutputStream output = null;
//		try {
//			output = context.getResponseOutputStream();
//			output.write(exportFileByteAry.toByteArray());
//			output.flush();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (null != exportFileByteAry)
//					exportFileByteAry.close();
//			} catch (IOException ioEx) {}
//			try {
//				if (null != output)
//					output.close();
//			} catch (IOException ioEx) {}
//		}
		
		fc.responseComplete();
	}
	
}

