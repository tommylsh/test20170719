package com.maxim.pos.common.enumeration;

public enum ColumnFormat {
	JDBC_VARCHAR(12),
	JDBC_CHAR(1),
	JDBC_NVARCHAR(-9),
	JDBC_INTEGER(4),
	JDBC_BIGINT(-5),
	JDBC_SMALLINT(5),
	JDBC_TINYINT(-6),
	JDBC_DOUBLE(8),
	JDBC_DECIMAL(3),
	JDBC_MONEY(3),
	JDBC_SMALLMONEY(3),
	JDBC_FLOAT(6),
	JDBC_DATE(91), 
	JDBC_TIME(92),
	JDBC_TIMESTAMP(93),
	JDBC_NULL(0),
	DBF_FIELD_TYPE_C(67),	//the int value of 'C' String
	DBF_FIELD_TYPE_L(76),	//the int value of 'L' Boolean
	DBF_FIELD_TYPE_N(78),	//the int value of 'N' Numeric
	DBF_FIELD_TYPE_F(70),	//the int value of 'F' Float
	DBF_FIELD_TYPE_D(68),	//the int value of 'D' Date
	DBF_FIELD_TYPE_M(77);	//the int value of 'M' Byte
	
	private final int value;
	
	ColumnFormat(int newValue){
		value = newValue;
	}
	
	public static ColumnFormat touch(int value){
	    for(ColumnFormat columnFormatEnum : ColumnFormat.values()){
	        if(columnFormatEnum.getValue() == value)
	           return columnFormatEnum;
	    }
		return null;
	}
	
	public int getValue(){return value;}
	
}
