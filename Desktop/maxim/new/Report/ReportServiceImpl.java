package com.maxim.pos.report.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.util.CreateExcelUtil;
import com.maxim.pos.report.config.ErrorCountReportConfig;
import com.maxim.pos.report.entity.ErrorCountReport;
import com.maxim.pos.report.enumeration.ReportType;
import com.maxim.pos.report.persistence.ReportDao;

@Service
public class ReportServiceImpl implements ReportService {

	@Autowired
	private ApplicationContext appContext;
	@Autowired
	private ReportDao reportDao;
	
	
	@Override
	public ByteArrayOutputStream exportErrorCountReportFile(Date businessDate, String parameters)
	{
		ErrorCountReportConfig config = (ErrorCountReportConfig) appContext.getBean(ReportType.ERROR_COUNT_REPORT.getReportConfigId());
		List<ErrorCountReport> errorCountReports = reportDao.getErrorCountReportList();
		
		
		// --- 1 way ---
//		XSSFWorkbook workbook = new XSSFWorkbook();
//        XSSFSheet sheet = workbook.createSheet(config.getReportTitle());
//        
//        //Set Title.
//        String[] title = {"Store Name", "Count"};
//        
//        XSSFCellStyle style = workbook.createCellStyle();
//        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
//        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        style.setAlignment(HorizontalAlignment.CENTER);
//        
//        Row titleRow = sheet.createRow(0);
//        for (int i = 0; i < title.length; i++) {
//        	Cell cell = titleRow.createCell(i);
//        	cell.setCellValue(title[i]);
//        	cell.setCellStyle(style);
//        }
//        
//        //Set Body.
//        int rowCount = 0, total = 0;
//        for (ErrorCountReport report : errorCountReports) {
//        	Row row = sheet.createRow(++rowCount);
//        	
//        	Cell cell = row.createCell(0);
//        	cell.setCellValue(report.getName());
//        	cell = row.createCell(1);
//        	cell.setCellValue(report.getCount());
//        	total += report.getCount();
//        }
//        
//        //Set Total.
//        Row row = sheet.createRow(++rowCount);
//        Cell totalCell = row.createCell(0);
//        totalCell.setCellValue("total");
//        totalCell = row.createCell(1);
//        totalCell.setCellValue(total);
//        
//        //Write File.
//        ByteArrayOutputStream exportFileByteAry = new ByteArrayOutputStream();
//        try {
//        	workbook.write(exportFileByteAry);
//        } catch (IOException e) {
//        	e.printStackTrace();
//        } finally {
//        	try {
//        		if (null != workbook)
//        			workbook.close();
//        	} catch (IOException ioEx) {}
//        }
        // -------------
        
        // --- 2 way ---
        ByteArrayOutputStream exportFileByteAry = null;
        try {
        	exportFileByteAry = CreateExcelUtil.createXlsx(ReportType.ERROR_COUNT_REPORT, errorCountReports);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        // -------------
        
        return exportFileByteAry;
	}

	@Override
	public ByteArrayOutputStream exportConvertMonitorReportFile(Date businessDate, String parameters) 
	{
		return null;
	}


	@Override
	public ByteArrayOutputStream exportErrorLogReportFile(Date businessDate, String parameters) 
	{
		return null;
	}


	@Override
	public ByteArrayOutputStream exportErrorClientReportFile(Date businessDate, String parameters) 
	{
		return null;
	}
	
}
