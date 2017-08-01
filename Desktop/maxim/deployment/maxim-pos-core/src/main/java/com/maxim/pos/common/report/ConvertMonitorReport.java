package com.maxim.pos.common.report;

import com.maxim.util.excel.annotation.ExcelColumn;

/**
 * Error Count Report data model
 * 
 * @author edward.wu@enlightening-it.com
 * 
 */
public class ConvertMonitorReport {
	@ExcelColumn(name = "Type", index = 1)
	private String branchType;
	@ExcelColumn(name = "Time", index = 2)
	private String time ;
	@ExcelColumn(name = "Converted", index = 3)
	private int converted;
	@ExcelColumn(name = "Expected", index = 4)
	private int expected;

	public String getBranchType() {
		return branchType;
	}
	public void setBranchType(String branchType) {
		this.branchType = branchType;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getConverted() {
		return converted;
	}
	public void setConverted(int converted) {
		this.converted = converted;
	}
	public int getExpected() {
		return expected;
	}
	public void setExpected(int expected) {
		this.expected = expected;
	}
}

