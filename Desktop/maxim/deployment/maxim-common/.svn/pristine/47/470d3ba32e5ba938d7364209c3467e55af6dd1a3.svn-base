package com.maxim.util;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.maxim.util.meta.DataColumn;
import com.maxim.util.meta.DataType;

public class PoiExportHelper {

	private static final String DEFAULT_FONT_NAME = "Arial";
	public static final int HEIGHT_LINE = 14;
	public static final int NORMAL_FONT_SIZE = 10;
	public static final int DEFAULT_CELL_WIDTH = 2632 * 2;
	public static final String DATE_RANGE_HEADER = "";

	public static void insertCriteriaRow(String criteriaText, XSSFSheet sheet, XSSFCellStyle cellStyle, int rowIndex,
			int colIndex) {
		XSSFRow row = sheet.createRow(rowIndex++);
		row.setHeightInPoints(PoiExportHelper.HEIGHT_LINE);
		XSSFCell cell = row.createCell(colIndex);
		sheet.autoSizeColumn(colIndex);
		cell.setCellStyle(cellStyle);
		cellStyle.setFont(createBoldFont(sheet.getWorkbook()));
		cell.setCellValue(criteriaText);
		colIndex++;
	}

	public static void iterateData(List<DataColumn<? extends Object>> dataColumns, List<Map<String, Object>> records,
			XSSFSheet sheet, XSSFCellStyle cellStyle, int rowIndex, int colIndex) {

		XSSFRow row = sheet.createRow(rowIndex++);
		row.setHeightInPoints(PoiExportHelper.HEIGHT_LINE);
		XSSFCell cell = null;

		for (int i = 0; i < dataColumns.size(); i++) {
			cell = row.createCell(colIndex);
			sheet.autoSizeColumn(colIndex);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(dataColumns.get(i).getHeaderName());
			colIndex++;
		}

		int dataRow = 1;
		for (Map<String, Object> statistic : records) {
			colIndex = 0;

			row = sheet.createRow(rowIndex++);
			row.setHeightInPoints(PoiExportHelper.HEIGHT_LINE);

			for (DataColumn<?> cellColumn : dataColumns) {
				cell = row.createCell(colIndex);
				cell.setCellStyle(cellStyle);
				if (cellColumn.isSequenceType()) {
					cell.setCellValue(dataRow);
					cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
				} else if (DataType.DECIMAL.equals(cellColumn.getDataType())) {
					cell.setCellValue(
							NumberHelper.updateDecimal(new BigDecimal(cellColumn.format(statistic))).doubleValue());
					cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
				} else {
					cell.setCellValue(cellColumn.format(statistic));
				}
				sheet.autoSizeColumn(colIndex);
				colIndex++;
			}

			dataRow++;
		}
	}

	public static void exportTo(List<DataColumn<? extends Object>> dataColumns, List<Map<String, Object>> records,
			String reportName, String suffix, HttpServletResponse response) throws IOException {
		exportTo(dataColumns, records, reportName, true, suffix, response);
	}

	public static void exportTo(List<DataColumn<? extends Object>> dataColumns, List<Map<String, Object>> records,
			String reportName, boolean dateRequired, String suffix, HttpServletResponse response) throws IOException {
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(reportName);

		XSSFPrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setPaperSize(XSSFPrintSetup.A4_PAPERSIZE);
		sheet.setMargin(XSSFSheet.TopMargin, (double) 0.2);
		sheet.setMargin(XSSFSheet.LeftMargin, (double) 0.0);

		XSSFCellStyle cellStyle = PoiExportHelper.createCellStyle(wb);

		int rowIndex = 0;
		int colIndex = 0;

		iterateData(dataColumns, records, sheet, cellStyle, rowIndex, colIndex);

		export(wb, ((dateRequired ? getReportNameWithDate(reportName) : reportName) + suffix), response);
	}

	public static void export(XSSFWorkbook wb, String reportName, HttpServletResponse response) throws IOException {
		OutputStream outputStream = response.getOutputStream();
		response.setContentType("application/x-excel");
		response.setHeader("Content-Disposition",
				"attachment; filename=" + new String(reportName.getBytes(), "ISO-8859-1"));
		wb.write(outputStream);
		outputStream.close();
	}

	public static void setBorder(XSSFCellStyle style1) {
		style1.setBorderTop(CellStyle.BORDER_NONE);
		style1.setBorderRight(CellStyle.BORDER_NONE);
		style1.setBorderBottom(CellStyle.BORDER_NONE);
		style1.setBorderLeft(CellStyle.BORDER_NONE);
	}

	public static XSSFFont createNormalFont(XSSFWorkbook wb) {
		XSSFFont font = wb.createFont();
		font.setFontName(DEFAULT_FONT_NAME);
		font.setFontHeightInPoints((short) NORMAL_FONT_SIZE);
		return font;
	}

	public static XSSFFont createBoldFont(XSSFWorkbook wb) {
		XSSFFont font = wb.createFont();
		font.setFontName(DEFAULT_FONT_NAME);
		font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) NORMAL_FONT_SIZE);
		return font;
	}

	public static XSSFCellStyle createCellStyle(XSSFWorkbook wb) {
		XSSFFont font = createNormalFont(wb);
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setFont(font);
		PoiExportHelper.setBorder(style);

		return style;
	}

	public static XSSFCellStyle createAlignLeftStyle(XSSFWorkbook wb) {
		XSSFFont font = createNormalFont(wb);
		XSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(XSSFCellStyle.ALIGN_LEFT);
		style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);
		style.setFont(font);
		PoiExportHelper.setBorder(style);

		return style;
	}

	public static String getReportNameWithDate(String reportName) {
		return getReportNameWithDate(reportName, null);
	}

	public static String getReportNameWithDate(String reportName, Date date) {
		if (date == null) {
			date = new Date();
		}

		return new StringBuffer().append(reportName).append("_")
				.append(DateHelper.format(date, DateHelper.DATE_FORMAT_2)).toString();
	}

}
