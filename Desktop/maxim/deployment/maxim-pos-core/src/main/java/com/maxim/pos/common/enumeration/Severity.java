package com.maxim.pos.common.enumeration;

public enum Severity {	
	CRITICAL(1),
	ERROR(2),
	WARNING(3),
	INFO(4),
	NONE(5);
	
	private final Integer value;
	
	Severity(Integer value){
		this.value = value;
	}
	
	public Integer getValue(Severity severity){
		return severity.value;
	}
}
