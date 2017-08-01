package com.maxim.pos.report.persistence;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.maxim.pos.report.entity.ErrorCountReport;

@Repository
public class ReportDao {

	
	public List<ErrorCountReport> getErrorCountReportList()
	{
		List<ErrorCountReport> errorCountReports = new ArrayList<>();
		ErrorCountReport report_1 = new ErrorCountReport();
		report_1.setName("Store-1");
		report_1.setCount(10);
		ErrorCountReport report_2 = new ErrorCountReport();
		report_2.setName("Store-2");
		report_2.setCount(50);
		
		errorCountReports.add(report_1);
		errorCountReports.add(report_2);
		
		return errorCountReports;
	}
}
