package com.maxim.pos.report.entity;

import com.maxim.pos.common.util.ExcelColumn;

/**
 * Error Count Report data model
 * 
 * @author edward.wu@enlightening-it.com
 */
public class ErrorCountReport {
	
	@ExcelColumn(name = "Store Name", index = 1)
	private String name;
	@ExcelColumn(name = "Count", index = 2)
	private int count;

	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
}

