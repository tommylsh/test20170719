package com.maxim.pos.common.enumeration;

public enum CommonDataStatus {
	C("COMPLETE"), O("OBSOLETTE");
	
	private final String value;
	
	CommonDataStatus(String newValue){
		value = newValue;
	}
	
	public String getValue(){return value;}
	
}
