package com.maxim.pos.report.enumeration;

public enum ReportType {

	ERROR_COUNT_REPORT, CONVERT_MONITOR_REPORT, ERROR_LOG_REPORT, ERROR_CLIENT_REPORT;
	
	
	public static ReportType getReportTypeByStringName(String strName)
	{
		strName = strName.replaceAll("[-_()\\s]", "");
		
		for (ReportType type : ReportType.values()) {
			if (type.name().replace("_", "").equalsIgnoreCase(strName)) {
				return type;
			}
		}
		
		throw new IllegalArgumentException("invalid-report-type");
	}
	
	public String getReportConfigId()
	{
		String[] partsWord = this.name().toLowerCase().split("_");
		
		StringBuilder sb = new StringBuilder();
		for (String word : partsWord) {
			String upperCase = String.valueOf(word.charAt(0)).toUpperCase();
			sb.append(upperCase).append(word.substring(1));
		}
		sb.append("Config");
		
		return sb.toString();
	}
	
	@Override
	public String toString() 
	{
		String[] partsWord = this.name().toLowerCase().split("_");
		
		StringBuilder sb = new StringBuilder();
		for (String word : partsWord) {
			String upperCase = String.valueOf(word.charAt(0)).toUpperCase();
			sb.append(upperCase).append(word.substring(1)).append(" ");
		}
		sb.deleteCharAt(sb.length() - 1);
		
		return sb.toString();
	}
	
}
