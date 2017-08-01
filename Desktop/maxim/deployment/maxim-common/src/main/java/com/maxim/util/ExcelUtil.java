package com.maxim.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;

import com.maxim.util.excel.annotation.ExcelColumn;

public class ExcelUtil {

	public static SXSSFWorkbook createXlsx(String reportTitle, List<? extends Object> datas) 
	{
		return createXlsx(reportTitle, datas, null);
	}

	public static SXSSFWorkbook createXlsx(String reportTitle, List<? extends Object> datas, Map<String, String> columnMap) 
	{
		//Create Excel.
		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet(reportTitle);

		if (datas.size() < 1)
		{
			if (columnMap != null)
			{
				populateTitleByColunmMap(workbook, sheet, columnMap);
			}
			return workbook;
		}
		// Get the Class of the Object List
		Object firstData = datas.iterator().next();
		Class<?> clazz = firstData.getClass();
		
		//Prepare Report Data.
		Map<Integer, String> title = new TreeMap<>();
		List<Map<Integer, Object>> reportData = new ArrayList<>();
		
		if (columnMap != null)
		{
			// Store the PropertyDescriptor of the defined column
			PropertyDescriptor[] pds = new PropertyDescriptor[columnMap.size()];
			int idx = 1;
			for (String key : columnMap.keySet())
			{
				String prop = columnMap.get(key);
				pds[idx-1] = BeanUtils.getPropertyDescriptor(clazz, prop);
				title.put(idx++, key);
	        }

			// Store the value to reportData
			for (Object obj : datas) 
			{
				idx = 1;
				Map<Integer, Object> eachRowData = new TreeMap<>();
				for (PropertyDescriptor pd : pds)
				{
					if (pd != null)
					{
						try {
							eachRowData.put(idx++,  pd.getReadMethod().invoke(obj));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException("Field/Column : '" + title.get(idx-1) + "' process error",e);
						}
					}
				}
				reportData.add(eachRowData);
			}
		}
		else
		{
			Field fields[] = clazz.getDeclaredFields() ;
			PropertyDescriptor[] pds = new PropertyDescriptor[fields.length];
			ExcelColumn[] excelColumns = new ExcelColumn[fields.length];

			for (int i = 0; i < fields.length; i++) {
				for (Annotation annotation : fields[i].getDeclaredAnnotations()) {
					if (annotation instanceof ExcelColumn) {
						pds[i] = BeanUtils.getPropertyDescriptor(clazz, fields[i].getName());
						excelColumns[i] = (ExcelColumn) annotation;
						title.put(excelColumns[i].index(), excelColumns[i].name());
					}
				}
			}
			
			for (Object obj : datas) {
				Map<Integer, Object> eachRowData = new TreeMap<>();
				for (int i = 0; i < fields.length; i++) {
					if (pds[i] != null)
					{
						try {
							int index = excelColumns[i].index();
							eachRowData.put(index,  pds[i].getReadMethod().invoke(obj));
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							throw new RuntimeException("Field : '" + fields[i].getName() + "' process error",e);
						}
					}
				}
//				for (Field field : fields) {
//					for (Annotation anno : field.getDeclaredAnnotations()) {
//						if (anno instanceof ExcelColumn) {
//							ExcelColumn excelColumn = (ExcelColumn) anno;
//							int index = excelColumn.index();
//							
//							Object value = null;
//							String fieldName = field.getName();
//							String fileGetter = String.format("get%s%s", fieldName.substring(0, 1).toUpperCase(), 
//																		 fieldName.substring(1));
//							try {
//								Method method = clazz.getMethod(fileGetter);
//								value = method.invoke(obj);
//							} catch (Exception e) {
//								throw new RuntimeException("field : '" + fieldName + "' not Getter method");
//							}
//							
//							eachRowData.put(index, value);
//						}
//					}
//				}
				reportData.add(eachRowData);
			}
		}
        
        //Set Title.
		CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        
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
        		
        		setCellValue(cell, value);
        	}
        }
        
        return workbook;
	}
	
	
	public static SXSSFWorkbook createXlsxByMapList(String reportTitle, List<Map<String,Object>> datas) 
	{
		return createXlsxByMapList(reportTitle, datas, null);
	}

	public static SXSSFWorkbook createXlsxByMapList(String reportTitle, List<Map<String,Object>> datas, Map<String, String> columnMap) 
	{	
		//Create Excel.
		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet(reportTitle);
		
		if (datas.size() < 1)
		{
			if (columnMap != null)
			{
				populateTitleByColunmMap(workbook, sheet, columnMap);
			}
			return workbook;
		}
        
		CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        
        Row titleRow = sheet.createRow(0);
        
		if (columnMap != null)
		{
	        //Set Title.
	        int horizontalIdx = 0;
			for (String key : columnMap.keySet())
			{
	        	Cell cell = titleRow.createCell(horizontalIdx++);
	        	cell.setCellValue(key);
	        	cell.setCellStyle(style);
	        }
			
	        //Set Body.
	        int rowCount = 0;
	        for (Map<String, Object> rowData : datas) {
	        	Row row = sheet.createRow(++rowCount);
	        	
	        	horizontalIdx = 0;
	        	for (Object value : columnMap.values()) 
	        	{
	        		value = rowData.get(value);
	        		
	        		Cell cell = row.createCell(horizontalIdx++);
	        		setCellValue(cell, value);
	        	}
	        }
		}
		else
		{
	        //Set Title.
	        int horizontalIdx = 0;
			Map<String,Object> firstData = datas.iterator().next();
			for (String key : firstData.keySet())
			{
	        	Cell cell = titleRow.createCell(horizontalIdx++);
	        	cell.setCellValue(key);
	        	cell.setCellStyle(style);
	        }
			
	 
	        //Set Body.
	        int rowCount = 0;
	        for (Map<String, Object> rowData : datas) {
	        	Row row = sheet.createRow(++rowCount);
	        	
	        	horizontalIdx = 0;
	        	for (Object value : rowData.values()) 
	        	{
	        		Cell cell = row.createCell(horizontalIdx++);
	        		setCellValue(cell, value);
	        	}
	        }
		}
        
        return workbook;
	}
	
	public static SXSSFWorkbook createXlsxByObjectArrayList(String reportTitle, List<Object[]> datas) 
	{	
		//Create Excel.
		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
		Sheet sheet = workbook.createSheet(reportTitle);
        
        //Set Title.
		CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        
        int rowCount = 0;
        int horizontalIdx = 0;
        for (Object[] rowData : datas) {
        	Row row = sheet.createRow(++rowCount);
        	
        	horizontalIdx = 0;
        	for (Object value : rowData) {
        		Cell cell = row.createCell(horizontalIdx++);
        		
        		setCellValue(cell, value);
        	}
        }

	        
        
        return workbook;
	}
	
	public static void setCellValue(Cell cell, Object value)
	{
    	if (value != null)
    	{
			if (value instanceof Integer) {
	            cell.setCellValue((Integer) value);
	        } 
			else if (value instanceof BigDecimal) {
	            cell.setCellValue(((BigDecimal)value).doubleValue());
	        } 
	        else {
	        	cell.setCellValue(value.toString());
	        }
    	}
	}
	
	public static void populateTitleByColunmMap(Workbook workbook, Sheet sheet, Map<String, String> columnMap)
	{
		CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        
        Row titleRow = sheet.createRow(0);
        
		if (columnMap != null)
		{
	        //Set Title.
	        int horizontalIdx = 0;
			for (String key : columnMap.keySet())
			{
	        	Cell cell = titleRow.createCell(horizontalIdx++);
	        	cell.setCellValue(key);
	        	cell.setCellStyle(style);
	        }
			
		}
	
	}
}
