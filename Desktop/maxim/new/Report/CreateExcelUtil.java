package com.maxim.pos.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.maxim.pos.report.enumeration.ReportType;

public class CreateExcelUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ByteArrayOutputStream createXlsx(ReportType reportType, List<? extends Object> datas) throws Exception
	{	
		if (datas.size() < 1)
			return null;
		
		//Prepare Report Data.
		Map<Integer, String> title = new TreeMap<>();
		Object firstData = datas.get(0);
		Class firstClazz = firstData.getClass();
		for (Field field : firstClazz.getDeclaredFields()) {
			for (Annotation annotation : field.getDeclaredAnnotations()) {
				if (annotation instanceof ExcelColumn) {
					ExcelColumn excelColumn = (ExcelColumn) annotation;
					title.put(excelColumn.index(), excelColumn.name());
				}
			}
		}
		
		List<Map<Integer, Object>> reportData = new ArrayList<>();
		for (Object obj : datas) {
			Class clazz = obj.getClass();
			
			Map<Integer, Object> eachRowData = new TreeMap<>();
			for (Field field : clazz.getDeclaredFields()) {
				for (Annotation anno : field.getDeclaredAnnotations()) {
					if (anno instanceof ExcelColumn) {
						ExcelColumn excelColumn = (ExcelColumn) anno;
						int index = excelColumn.index();
						
						Object value = null;
						String fieldName = field.getName();
						String fileGetter = String.format("get%s%s", fieldName.substring(0, 1).toUpperCase(), 
																	 fieldName.substring(1));
						try {
							Method method = clazz.getMethod(fileGetter);
							value = method.invoke(obj);
						} catch (Exception e) {
							throw new RuntimeException("field : '" + fieldName + "' not Getter method");
						}
						
						eachRowData.put(index, value);
					}
				}
			}
			reportData.add(eachRowData);
		}
		
		//Create Excel.
		XSSFWorkbook workbook = null;
		ByteArrayOutputStream exportFileByteAry = null;
		try {
			workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet(reportType.toString());
	        
	        //Set Title.
	        XSSFCellStyle style = workbook.createCellStyle();
	        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        style.setAlignment(HorizontalAlignment.CENTER);
	        
	        Row titleRow = sheet.createRow(0);
	        int horizontalIdx = 0;
	        for (String _title : title.values()) {
	        	Cell cell = titleRow.createCell(horizontalIdx++);
	        	cell.setCellValue(_title);
	        	cell.setCellStyle(style);
	        }
	 
	        //Set Body.
	        int rowCount = 0;
	        for (Map<Integer, Object> rowData : reportData) {
	        	Row row = sheet.createRow(++rowCount);
	        	
	        	horizontalIdx = 0;
	        	for (Object value : rowData.values()) {
	        		Cell cell = row.createCell(horizontalIdx++);
	        		
	        		if (value instanceof Integer) {
	                    cell.setCellValue((Integer) value);
	                } else {
	                	cell.setCellValue((String) value);
	                }
	        	}
	        }
	        
	        exportFileByteAry = new ByteArrayOutputStream();
        	workbook.write(exportFileByteAry);
        	
        } finally {
        	try {
        		if (null != workbook)
        			workbook.close();
        	} catch (IOException ioEx) {}
        }
        
        return exportFileByteAry;
	}
	
}
