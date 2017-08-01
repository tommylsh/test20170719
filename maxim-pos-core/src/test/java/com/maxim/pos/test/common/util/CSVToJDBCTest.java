package com.maxim.pos.test.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.service.ScheduleJobService;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.JavaCSVUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.test.common.BaseTest;

public class CSVToJDBCTest extends BaseTest{

	@Autowired
	private PollSchemeInfoService pollSchemeInfoService;
//    @Autowired
//    private SalesServiceOld salesService;
    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;
    @Autowired
    private ScheduleJobService scheduleJobService;

	@Test
	@Transactional
//	public void readCSVTest() throws Exception {
//	
//		String fileRoot = "D:\\Maxim_test\\csv\\"; //6804\\M6804_161214_orders.dbf
//
//		String filePath = JDBCUtils.getCSVFilePath(fileRoot, "3710", "extra", DateUtil.parse("20161214", "yyyyMMdd"));
//		
//		System.out.println(filePath);
//		
//		String toDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61807;" +  
//	            "databaseName=hopos;user=sa;password=P@ssw0rd"; 
//		
//		CommonCriteria criteria = new CommonCriteria(60L);
//		
//		List<SchemeInfo> list = 
//				pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
//
//		SchemeInfo schemeInfo = list.get(0);
//		
//		System.out.println(schemeInfo.getPollSchemeType());
//		System.out.println(schemeInfo.getClientType());
//		System.out.println(schemeInfo.getDelimiter());
//		
//		Hibernate.initialize(schemeInfo);
//		
//		System.out.println(schemeInfo.getSchemeTableColumns().size());
//		
//		try{
//			JDBCUtils.bulkCopyFromCSV(filePath, toDS, null, schemeInfo, null, null, false);
//		}
//		catch(Exception e){
//			e.printStackTrace();;
//		}
//	}
	
    public  static void main(String[] arg) throws Exception
    {
		System.out.println(new String("中國".getBytes("big5"), "gb2312"));
//		readCSVTest();
    }
	
	public static void readCSVTest() throws Exception {
		
//		String fileRoot = "D:\\Maxim_test\\csv\\"; //6804\\M6804_161214_orders.dbf

//		String filePath = JDBCUtils.getCSVFilePath(fileRoot, "3710", "extra", DateUtil.parse("20161214", "yyyyMMdd"));
		String filePath= "C:/UserData/4041/SUPP.TXT";
		//String filePath= "C:/UserData/PAYMENT.TXT";
		
		System.out.println(filePath);
		
//		String toDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61807;" +  
//	            "databaseName=hopos;user=sa;password=P@ssw0rd"; 
		
		String toDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
	            "databaseName=esb_sit;user=esb_sit;password=P@ssw0rd"; 

		
		CommonCriteria criteria = new CommonCriteria(1652L);
		
//		List<SchemeInfo> list = 
//				pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
//
//		SchemeInfo schemeInfo = list.get(0);
//		
//		System.out.println(schemeInfo.getPollSchemeType());
//		System.out.println(schemeInfo.getClientType());
//		System.out.println(schemeInfo.getDelimiter());
//		
//		Hibernate.initialize(schemeInfo);
//		
//		System.out.println(schemeInfo.getSchemeTableColumns().size());
//		schemeInfo.setSchemeTableColumns(new ArrayList<SchemeTableColumn>());
//		schemeInfo.setDestination("test");
//		SchemeTableColumn s = schemeInfo.getSchemeTableColumns().get(14);
//		List <SchemeTableColumn> ss = new ArrayList<SchemeTableColumn>();
//		ss.add(s);
//		s.setSeq(0);
//		s.setToColumn("txt");
//		schemeInfo.setSchemeTableColumns(ss);	
		
		SchemeInfo schemeInfo = new SchemeInfo();
		schemeInfo.setSource("SUPP");
		schemeInfo.setDestination("hist_supp");
		schemeInfo.setClientType(ClientType.SQLPOS);
		
//        String[] conditions =  new String[]{" branch_code  in ('2863','2833','4466')"};

        
		try(Connection conn = DriverManager.getConnection(toDS)){
			JavaCSVUtils.bulkCopyFromCSVToSQL(filePath, conn, schemeInfo, null, null, false, JDBCUtils.CONV_NONE);
		}
		catch(Exception e){
			e.printStackTrace();;
		}
	}
	
//	@Test
//	public void readCSVJobTest() throws Exception {
//        SchemeScheduleJob schemeScheduleJob = scheduleJobService.getTaskById(2L);
//        List<BranchScheme> list = pollBranchSchemeService.getBranchSchemeByScheduleJob(schemeScheduleJob);
//        for(BranchScheme branchScheme:list){
//        	if (branchScheme.getId() == 58)
//        		salesService.processPosToStaging(branchScheme, logger);
//        }
//	}
}