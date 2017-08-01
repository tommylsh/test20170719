package com.maxim.pos.report.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.MethodInvoker;

import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.report.ConvertMonitorReport;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.report.data.ReportConfig;
import com.maxim.pos.report.data.ReportGroupConfig;
import com.maxim.pos.report.persistence.ReportGenerationDao;
import com.maxim.pos.report.service.ReportService;
import com.maxim.util.DateUtil;
import com.maxim.util.ExcelUtil;

@Service("reportService")
public class ReportServiceImpl implements ReportService, ApplicationContextAware {

	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private ReportGenerationDao reportGenerationDao;
	@Autowired
	private ApplicationSettingService applicationSettingService; 
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private String  mailSenderAddress;
	
    private @Value("${mail.reportTempDirectory}") String reportTempDirectory = null;


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

	@Override
	public boolean sendReportMail(BranchScheme branchScheme, Logger logger) {
		boolean result = false;
		try {
			  
			ApplicationSetting applicationSetting = applicationSettingService.findApplicationSettingByCode("REPORT_EMAIL");
			
			if (applicationSetting.getCodeValue()!= null)
			{
				String reportGroup = branchScheme.getDirection().name();
				String bizDate = null ;
				
				Map<String, Object> paramMap = new HashMap<String, Object>();
				ReportGroupConfig reportGroupConfig = this.reportExportConfiguration.get(reportGroup);
				for (String paramName : reportGroupConfig.getParameters())
				{
					if (paramName.equals("businessDate"))
					{
						Calendar cal = Calendar.getInstance();
						
						int hour = cal.get(Calendar.HOUR_OF_DAY);
						if (hour < 18)
						{
							cal.add(Calendar.DATE, -1);
						}
						bizDate = DateUtil.format(cal.getTime(), "yyyy-MM-dd");							
						paramMap.put(paramName, bizDate);
					}
				}
				
				File tempDir = null;
				if (!StringUtils.isEmpty(reportTempDirectory))
				{
					tempDir = new File(reportTempDirectory);
					if (!tempDir.exists())
					{
						tempDir.mkdirs();
					}
				}
				ArrayList<File> reportFileList = new ArrayList<File>();
				ArrayList<String> reportFileNameList = new ArrayList<String>();
				for (ReportConfig reportConfig : reportGroupConfig.getReportList())
				{
					if (reportConfig.getFileType().equals("xlsx"))
					{
						SXSSFWorkbook workbook = getWorkbook(reportConfig, paramMap) ;
						
						File tempFile = File.createTempFile("rpt", ".xlsx", tempDir);
						try(FileOutputStream out = new FileOutputStream(tempFile))
						{
							workbook.write(out);
						}
						reportFileNameList.add(reportConfig.getFileName()+".xlsx");
						reportFileList.add(tempFile);
					}
				}
//				MimeMessage.
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				String subject = "Report["+bizDate+"] Send at :" + df.format(new Date());
				
				MimeMessage message = mailSender.createMimeMessage();
				MimeMessageHelper helper = new MimeMessageHelper(message,true);

				helper.setFrom(mailSenderAddress);
				helper.setSubject(subject);
				helper.setText(subject);

				String[] emails = applicationSetting.getCodeValue().split(";");
				{
					for (String email :emails)
					{
						helper.addTo(email);
					}
				}
				Iterator<String> it = reportFileNameList.iterator();
				for (File file : reportFileList)
				{
		            FileSystemResource res = new FileSystemResource(file);

					String filename = it.next();
					helper.addAttachment(filename, res);
				}

				 mailSender.send(message);
				 
				 result = true ;
				 
				 for (File file : reportFileList)
				 {
					 file.delete();
				 }
			}

		} catch (Exception e) {
			LogUtils.printException(logger, "send email result is {}", e);
		}
		return result;
	}

	
	public List<ConvertMonitorReport> getErrorCountReportList(Map<String, Object> paramMap)
	{
		List<ConvertMonitorReport> errorCountReports = new ArrayList<>();
		ConvertMonitorReport report_1 = new ConvertMonitorReport();
		report_1.setBranchType("Store-1");
		report_1.setTime("21:00");
		report_1.setConverted(10);
		report_1.setExpected(10);
		ConvertMonitorReport report_2 = new ConvertMonitorReport();
		report_2.setBranchType("Store-1");
		report_2.setTime("22:00");
		report_2.setConverted(20);
		report_2.setExpected(20);
		
		errorCountReports.add(report_1);
		errorCountReports.add(report_2);
		
		return errorCountReports;
	}
	
	public List<ConvertMonitorReport> getConvertMonitorReportConfig(Map<String, Object> paramMap)
	{
		List<ConvertMonitorReport> convertMonitorReports = new ArrayList<>();
		
		String businessDate = (String) paramMap.get("businessDate");
		
		Timestamp startTime2 = Timestamp.valueOf(businessDate+ " 18:00:00");
		Calendar cal2 = Calendar.getInstance();

		cal2.setTimeInMillis(startTime2.getTime());
		
		cal2.add(Calendar.DATE, 1);
		cal2.set(Calendar.AM_PM, Calendar.AM);
		cal2.set(Calendar.HOUR, 7);

		Timestamp endTime2 = new Timestamp(cal2.getTimeInMillis());

		Calendar cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(startTime2.getTime());
//		cal1.set(Calendar.AM_PM, Calendar.AM);
//		cal1.set(Calendar.HOUR, 7);
		
//		Timestamp endTime1 = new Timestamp(cal1.getTimeInMillis());
		cal1.add(Calendar.DATE, -7);
		cal1.set(Calendar.AM_PM, Calendar.PM);
		cal1.set(Calendar.HOUR, 6);
		Timestamp startTime1 = new Timestamp(cal1.getTimeInMillis());
//		String lastWeekBusinessDate = cal1.get(Calendar.YEAR)+"-"+ (cal1.get(Calendar.MONTH)+1)+"-"+cal1.get(Calendar.DATE);
		String lastWeekBusinessDate = DateUtil.format(cal1.getTime(), "yyyy-MM-dd");							
		
		List<Map<String, Object>> completeTimeList1 = reportGenerationDao.getEDOCompletedBranchByBusinessDate(java.sql.Date.valueOf(lastWeekBusinessDate));
		List<Map<String, Object>> completeTimeList2 = reportGenerationDao.getEDOCompletedBranchByBusinessDate(java.sql.Date.valueOf(businessDate));

		Map<String, List<Map<String, Object>>> branchTypeMap1 = new TreeMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> map :completeTimeList1)
		{
			String branchType = (String) map.get("BRANCH_TYPE");
			List<Map<String, Object>> list = branchTypeMap1.get(branchType);
			if (list == null)
			{
				list = new ArrayList<Map<String, Object>>();
				branchTypeMap1.put(branchType, list);
			}
			list.add(map);
		}
		
		Map<String, List<Map<String, Object>>> branchTypeMap2 = new TreeMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> map :completeTimeList2)
		{
			String branchType = (String) map.get("BRANCH_TYPE");
			List<Map<String, Object>> list = branchTypeMap2.get(branchType);
			if (list == null)
			{
				list = new ArrayList<Map<String, Object>>();
				branchTypeMap2.put(branchType, list);
			}
			list.add(map);
		}
		
		for (String branchType :  branchTypeMap2.keySet())
		{
			cal1.setTimeInMillis(startTime1.getTime());
			cal1.set(Calendar.AM_PM, Calendar.PM);
			cal1.set(Calendar.HOUR, 9);
			cal2.setTimeInMillis(startTime2.getTime());
			cal2.set(Calendar.AM_PM, Calendar.PM);
			cal2.set(Calendar.HOUR, 9);

			while (cal2.getTimeInMillis() <= endTime2.getTime())
			{
				int converted = 0 ;
				int expected = 0 ;
				
				List<Map<String, Object>> list2 = branchTypeMap2.get(branchType) ;
				for (Map<String, Object> map : list2)
				{
					Timestamp endTime = (Timestamp) map.get("END_TIME");
					if (endTime != null && endTime.getTime() <= cal2.getTimeInMillis())
					{
						converted ++;
					}
				}
				List<Map<String, Object>> list1 = branchTypeMap1.get(branchType) ;
				if (list1 != null)
				{
					for (Map<String, Object> map : list1)
					{
						Timestamp endTime = (Timestamp) map.get("END_TIME");
						if (endTime != null && endTime.getTime() <= cal1.getTimeInMillis())
						{
							expected ++;
						}
					}
				}
				StringBuffer timeString = new StringBuffer() ;
				int hour = cal2.get(Calendar.HOUR);
				if (hour < 10)
				{
					timeString = timeString.append("0").append(hour).append(":00");
				}
				else
				{
					timeString = timeString.append(hour).append(":00");
				}
				if (cal2.get(Calendar.AM_PM) == Calendar.AM)
				{
					timeString = timeString.append("am");
				}
				else
				{
					timeString = timeString.append("pm");
				}

				ConvertMonitorReport report = new ConvertMonitorReport();
				report.setBranchType(branchType);
				report.setTime("Up to " + timeString.toString());
				report.setConverted(converted);
				report.setExpected(expected);
				cal1.add(Calendar.HOUR, 1);
				cal2.add(Calendar.HOUR, 1);
				convertMonitorReports.add(report);
			}

		}
		
		return convertMonitorReports;
	}
	
	@Override
	public SXSSFWorkbook getWorkbook(ReportConfig downlaodReportConfig, Map<String, Object> paramMap)
	{
		if (downlaodReportConfig.getQueryType().equals("method"))
		{
			MethodInvoker mi = new MethodInvoker() ;
			Object obj = downlaodReportConfig.getTargetObject() ;
			if (obj == null)
			{
				obj = appContext.getBean(downlaodReportConfig.getTargetBeanId());
				if (obj == null)
				{
					throw new IllegalArgumentException("Incorrect Report Config Bean ID : " + downlaodReportConfig.getTargetBeanId());
				}
			}

			mi.setTargetObject(obj);
			mi.setTargetMethod(downlaodReportConfig.getTargetMethod());
			mi.setArguments(new Object[] {paramMap});
			
			try 
			{
				mi.prepare();
				
				@SuppressWarnings("unchecked")
				List<Object> reportList = (List<Object>) mi.invoke();
				
				return ExcelUtil.createXlsx(downlaodReportConfig.getReportTitle(), reportList, downlaodReportConfig.getColunmMap());
			} 
			catch (InvocationTargetException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
				e.printStackTrace();
				return null ;
			}

		}
		else if (downlaodReportConfig.getQueryType().equals("sql"))
		{
			List<Map<String, Object>> data = reportGenerationDao.getListBySQL(downlaodReportConfig.getQuery(), paramMap);
			return ExcelUtil.createXlsxByMapList(downlaodReportConfig.getReportTitle(), data, downlaodReportConfig.getColunmMap());
//			List<Object[]> data = reportGenerationDao.getObectArrayListBySQL(downlaodReportConfig.getQuery(), paramMap);
//			System.out.println("data size" + data.size());
//			return ExcelUtil.createXlsxByObjectArrayList(downlaodReportConfig.getReportTitle(), data);
		}
		else if (downlaodReportConfig.getQueryType().equals("hql"))
		{
			List<?> data = reportGenerationDao.getListByHQL(downlaodReportConfig.getQuery(), paramMap);
			if (data.size() > 0)
			{
				Object obj = data.iterator().next();
				if (obj instanceof Map)
				{
				
					@SuppressWarnings("unchecked")
					List<Map<String,Object>> mapList = (List<Map<String,Object>>) data ;
					
					return ExcelUtil.createXlsxByMapList(downlaodReportConfig.getReportTitle(), mapList, downlaodReportConfig.getColunmMap());
				}
				else
				{
					return ExcelUtil.createXlsx(downlaodReportConfig.getReportTitle(), data, downlaodReportConfig.getColunmMap());
				}
			}
			else
			{
				return ExcelUtil.createXlsx(downlaodReportConfig.getReportTitle(), data, downlaodReportConfig.getColunmMap());
			}
			
		}
		
		return null;
	}
	
}
