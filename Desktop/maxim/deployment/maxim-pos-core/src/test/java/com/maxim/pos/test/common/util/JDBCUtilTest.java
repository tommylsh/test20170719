package com.maxim.pos.test.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.Hibernate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.nativejdbc.C3P0NativeJdbcExtractor;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.SQLStmtUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.test.common.BaseTest;

public class JDBCUtilTest extends BaseTest {

	@Autowired
	private DataSource dataSource;
	
	@Autowired
	private ApplicationSettingService applicationSettingService;
	
	@Autowired
	PollSchemeInfoService pollSchemeInfoService;
	
	private static final String SQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String JTDS_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	
	@Test
	public void test() throws Exception {
		

//		String fileRoot = "D:\\Maxim_test\\csv\\"; //6804\\M6804_161214_orders.dbf

//		String filePath = JDBCUtils.getCSVFilePath(fileRoot, "3710", "extra", DateUtil.parse("20161214", "yyyyMMdd"));
//		String filePath= "C:/UserData/SUPP.TXT";
		//String filePath= "C:/UserData/PAYMENT.TXT";
		
//		System.out.println(filePath);
		
//		String toDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61807;" +  
//	            "databaseName=hopos;user=sa;password=P@ssw0rd"; 
		String fromDS = "jdbc:sqlserver://10.10.31.85:1433;" +  
	            "databaseName=esb_prd;user=esb_prd;password=P@ssw0rd5678"; 
//		String toDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
//	            "databaseName=esb_sit;user=esb_sit;password=P@ssw0rd"; 
//		String toDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
//	            "databaseName=esb_sit;user=esb_sit;password=P@ssw0rd"; 
//		String toDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
//	            "databaseName=esb_uat;user=esb_uat;password=P@ssw0rd"; 
//		String fromDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
//	            "databaseName=Pricing_Staging;user=esb_uat;password=P@ssw0rd"; 
//		String fromDS = "jdbc:jtds:sqlserver://10.104.17.66:1433/pos4117;user=esb_sit;password=P@ssw0rd";
		String toDS = "jdbc:jtds:sqlserver://10.104.17.70:1433/pos;user=esb_sit;password=Passw@rd";
		
//		String fromDS = MessageFormat.format(ConnectionStringHelper.JTDS_CONNECTION_PATTERN, 
//				"10.104.17.66",
//				"1433",
//				"pos_uat", 
//				"esb_sit", 
//				"P@ssw0rd");
		
//		String fromDS = MessageFormat.format(ConnectionStringHelper.JTDS_CONNECTION_PATTERN, 
//		"10.10.31.73",
//		"1433",
//		"esb_uat", 
//		"esb_uat", 
//		"P@ssw0rd");

//		String fromDS = MessageFormat.format(ConnectionStringHelper.JTDS_CONNECTION_PATTERN, 
//		"10.10.31.73",
//		"1433",
//		"esb_sit", 
//		"esb_sit", 
//		"P@ssw0rd");

//		String fromDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
//	            "databaseName=esb_sit;user=esb_sit;password=P@ssw0rd"; 



		
		CommonCriteria criteria = new CommonCriteria(111L);
		

		
		String[] tables = new String[] {
//		 "orders",       
//		 "Orders_Pay",     
//		 "Trans",          
//		 "Hist_orders",    
//		 "Hist_orders_pay",
//		 "Hist_trans",
//		 "Hist_Item"};
		
		"hist_check_logs"
		,"hist_coupon_sales"
		,"hist_item"
		,"hist_itemstock"
		,"hist_orders"
		,"hist_orders_extra"
		,"hist_orders_pay"
		,"hist_orders_pay_progress"
		,"hist_payfig"
		,"hist_paysum"
		,"hist_possystem"
		,"hist_redeemed_coupon"
//		,"hist_safeboxcheck"
//		,"hist_safeboxchecktender"
//		,"hist_safeboxinout"
//		,"hist_safeboxinoutextendinfo"
//		,"hist_safeboxpickup"
//		,"hist_sessioninfo"
//		,"hist_sessiontender"
//		,"hist_sold_out_sales_trans"
		,"hist_stock_movement"
		,"hist_supp"
		,"hist_trans"
		,"hist_trans_ecard"
		,"hist_trans_modifier"
		};
		
		tables = new String[] {
				"currency",
				"accounts"
		};

		Thread[] ts = new Thread[tables.length];
		Class.forName(SQL_DRIVER);
		System.out.println("tables " + tables.length);
		C3P0NativeJdbcExtractor cp30NativeJdbcExtractor = new C3P0NativeJdbcExtractor();

		try(Connection from1 = DriverManager.getConnection(fromDS))
//		try(Connection from1 = applicationSettingService.getMasterJDBCConnection())
		{
			System.out.println("from " + from1);
			Connection from = cp30NativeJdbcExtractor.getNativeConnection(from1);
			System.out.println("from " + from);
			Class.forName(SQL_DRIVER);
			System.exit(1);
//			try(Connection to = DriverManager.getConnection(toDS) ){
			try(Connection to1 = applicationSettingService.getCurrentJDBCConnection()){
//			try{

				Connection to = cp30NativeJdbcExtractor.getNativeConnection(to1);
				System.out.println("to " + to);
				
//				for (int n = 7002 ; n < 7022; n++)
//				{
				int i = 0 ;
				for (String table : tables )
				{
//					System.out.println("table" + table);					
//					List<SchemeInfo> 	list = 
//							pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
//					System.out.println("list " + list);

					SchemeInfo schemeInfo = new SchemeInfo();
				
//					System.out.println(schemeInfo.getPollSchemeType());
//					System.out.println(schemeInfo.getClientType());
//					System.out.println(schemeInfo.getDelimiter());
//					System.out.println(schemeInfo.getSource());
					
//					Hibernate.initialize(schemeInfo);
					schemeInfo.setSource(table);
					schemeInfo.setDestination(table);
					schemeInfo.setClientType(ClientType.SQLSERVER);
					
//			        String[] conditions =  new String[]{" branch_code  in ('2863','2833','4466')"};
			        String[] conditions =  new String[]{" branch_code  = '4459' "};

//					System.out.println("structureConsistentBulkCopy");
//					String newCode = ""+n;
		
					ts[i] = new Thread(){
						public void run()
						{
							int[] r;
							long now = System.currentTimeMillis();
							try {
//								JDBCUtils.deleteByBranchAndBizDate(to, schemeInfo.getDestination(), "2833",(String) null);
								
//								JDBCUtils.CURRENT_THREAD_BRANCH_CODE_MAP.put("7001", ""+newCode);
//								System.out.println("JDBCUtils.CURRENT_THREAD_BRANCH_CODE_MAP " + JDBCUtils.CURRENT_THREAD_BRANCH_CODE_MAP);
								r = JDBCUtils.structureConsistentBulkCopy(from, to, schemeInfo, 1000, conditions, false, JDBCUtils.CONV_NONE  );
//								System.out.println("structureConsistentBulkCopy " + r[0]);
//								System.out.println("structureConsistentBulkCopy " + r[1]);
							} catch (Exception e) {
								e.printStackTrace();
							}
							//time1 60149
//							System.out.println(schemeInfo.getSource() + " - time1 " + (System.currentTimeMillis() - now) );
						}
					};
					
					ts[i].start();
					i++;
				}
				
				for (Thread t : ts)
				{
					t.join();
				}
				
//				}

			}
		}
		

////		System.out.println(schemeInfo.getSchemeTableColumns().size());
////		schemeInfo.setDestination("test");
////		SchemeTableColumn s = schemeInfo.getSchemeTableColumns().get(14);
////		List <SchemeTableColumn> ss = new ArrayList<SchemeTableColumn>();
////		ss.add(s);
////		s.setSeq(0);
////		s.setToColumn("txt");
////		schemeInfo.setSchemeTableColumns(ss);
//        String[] conditions1 = new String[]{" branch_code  = '1000'"};
//        String[] conditions2 = new String[]{" branch_code  = '1001'"};
//        String[] conditions3 = new String[]{" branch_code  = '1002'"};
//		Class.forName(JTDS_DRIVER);
//
//		try(Connection from = DriverManager.getConnection(fromDS))
//		{
//			Class.forName(SQL_DRIVER);
//			try(Connection to = DriverManager.getConnection(toDS) ){
//				System.out.println("structureConsistentBulkCopy");
//				
//				Thread t1 = new Thread(){
//					public void run()
//					{
//						int[] r;
//						long now = System.currentTimeMillis();
//						try {
//							JDBCUtils.deleteByBranchAndBizDate(to, schemeInfo.getDestination(), "1000", (String) null);
//							r = JDBCUtils.structureConsistentBulkCopy(from, to, schemeInfo, 1000, conditions1, false);
//							System.out.println("structureConsistentBulkCopy " + r[0]);
//							System.out.println("structureConsistentBulkCopy " + r[1]);
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						//time1 60149
//						System.out.println("time1 " + (System.currentTimeMillis() - now) );
//					}
//				};
//				Thread t2 = new Thread(){
//					public void run()
//					{
//						int[] r;
//						long now = System.currentTimeMillis();
//						try {
//							JDBCUtils.deleteByBranchAndBizDate(to, schemeInfo.getDestination(), "1002", (String) null);
//							r = JDBCUtils.structureConsistentBulkCopy(from, to, schemeInfo, 1000, conditions2, false);
//							System.out.println("structureConsistentBulkCopy " + r[0]);
//							System.out.println("structureConsistentBulkCopy " + r[1]);
//							/*
//structureConsistentBulkCopy 12490
//structureConsistentBulkCopy 0
//time1 95557
//2017-04-27 12:18:52.411 [com.maxim.pos.common.util.JDBCUtils]-[INFO]-[Thread-8] commit: 490 / 1000
//structureConsistentBulkCopy 12490
//structureConsistentBulkCopy 0
//time2 96539							 * 
//							 */
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						System.out.println("time2 " + (System.currentTimeMillis() - now) );
//					}
//				};
//				Thread t3 = new Thread(){
//					public void run()
//					{
//						int[] r;
//						long now = System.currentTimeMillis();
//						try {
//							JDBCUtils.deleteByBranchAndBizDate(to, schemeInfo.getDestination(), "1003", (String) null);
//							r = JDBCUtils.structureConsistentBulkCopy(from, to, schemeInfo, 1000, conditions3, false);
//							System.out.println("structureConsistentBulkCopy " + r[0]);
//							System.out.println("structureConsistentBulkCopy " + r[1]);
//							/*
//structureConsistentBulkCopy 12490
//structureConsistentBulkCopy 0
//time3 136933
//2017-04-27 12:40:01.835 [com.maxim.pos.common.util.JDBCUtils]-[INFO]-[Thread-8] commit: 490 / 1000
//structureConsistentBulkCopy 12490
//structureConsistentBulkCopy 0
//time2 138675
//2017-04-27 12:40:02.199 [com.maxim.pos.common.util.JDBCUtils]-[INFO]-[Thread-7] commit: 490 / 1000
//structureConsistentBulkCopy 12490
//structureConsistentBulkCopy 0
//time1 139060
//							 */
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						System.out.println("time3 " + (System.currentTimeMillis() - now) );
//					}
//				};
//				
//				t1.start();
//				t2.start();
//				t3.start();
//				t1.join();
//				t2.join();
//				t3.join();
//				
//
//			}
//			catch(Exception e){
//				e.printStackTrace();;
//			}
//		}
//		catch(Exception e){
//			e.printStackTrace();;
//		}
	}
	


//	@Test
//	@Transactional
//	public void testBulkCopy() throws ClassNotFoundException, SQLException {
//
//		String fromDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61809;"
//				+ "databaseName=pos_1282;user=sa;password=123456";
//
//		String toDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61807;"
//				+ "databaseName=maxim_staging_test;user=sa;password=P@ssw0rd";
//
//		CommonCriteria criteria = new CommonCriteria(1L);
//		
//		List<SchemeInfo> schemeInfos = pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
//		SchemeInfo schemeInfo = schemeInfos.get(0);
//		
//		Hibernate.initialize(schemeInfo);
//		
//		String fromTable = schemeInfo.getSource();
//		String toTable = schemeInfo.getDestination();
//		
//		List<String> fromCols = new ArrayList<String>();
//		List<String> toCols = new ArrayList<String>();
//		
//		for(SchemeTableColumn col: schemeInfo.getSchemeTableColumns()){
//			fromCols.add(col.getFromColumn());
//			toCols.add(col.getToColumn());
//		}
//		
//		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		try(Connection fromConn = DriverManager.getConnection(fromDS);
//			Connection toConn = DriverManager.getConnection(toDS);)
//		{
//		
//			try {
//				String sql = "SELECT branch_cname from hist_possystem where branch_type = 'GEN'";
//				System.out.println("Test Bulk Copy SQL");
//				JDBCUtils.bulkCopyFromSQLConn(fromConn, toConn, fromTable, toTable, fromCols, toCols, null, null);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}

//	@Test
//	public void testStructureConsistentbulkCopy() throws Exception {
//
//		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		BranchInfo info = new BranchInfo();
//		info.setClientType(ClientType.SQLSERVER);
//		info.setClientHost("192.168.1.251");
//		info.setClientPort(1433);
//		info.setUser("sa");
//		info.setPassword("123456");
//		info.setClientDB("pos_0000");
//		// ;
//		Connection from = DriverManager.getConnection(ConnectionStringHelper.getSQLServerConnectionString(info));
//		SchemeInfo schemeInfo = new SchemeInfo();
//		schemeInfo.setSource("orders_pay_lotic");
//		schemeInfo.setDestination("orders_pay_lotic");
//		schemeInfo.setOverride(false);
//		schemeInfo.setConsistentStructure(true);
//
//		if (schemeInfo.isConsistentStructure()) {
//			int[] rs = JDBCUtils.StructureConsistentbulkCopy(from, applicationSettingService.getCurrentJDBCConnection(),
//					schemeInfo, new String[] {});
//
//			System.out.println("insert:" + rs[0]);
//			System.out.println("update:" + rs[1]);
//		}
//
//	}
	
	
	
//	@Test
//	public void testBulkCopyFromSQLConn() throws Exception {
//
//		Class.forName("oracle.jdbc.OracleDriver");
//		BranchInfo info = new BranchInfo();
//		info.setClientType(ClientType.ORACLE);
//		info.setClientHost("192.168.1.56");
//		info.setClientPort(1521);
//		info.setUser("MAXIM_EDW");
//		info.setPassword("123456");
//		info.setClientDB("orcl");
//		// ;
//		Connection from = applicationSettingService.getCurrentJDBCConnection();
//		
//		String toString = ConnectionStringHelper.getOracleConnectionString(info);
//		System.out.println(toString);
//		Connection to = DriverManager.getConnection(toString);
//		
//		
//		SchemeInfo schemeInfo = new SchemeInfo();
//		schemeInfo.setSource("order_test");
//		schemeInfo.setDestination("order_test");
//		schemeInfo.setOverride(false);
//		schemeInfo.setConsistentStructure(true);
//		
//		 List<SchemeTableColumn> schemeTableColumns = new ArrayList<SchemeTableColumn>();
//		 SchemeTableColumn stc1= new SchemeTableColumn();
//		 stc1.setFromColumn("branch_code");
//		 stc1.setFromColumnFormat("String");
//		 stc1.setToColumn("branch_code");
//		 stc1.setToColumnFormat("VARCHAR2");
//		 schemeTableColumns.add(stc1);
//		 
//		 
//		 SchemeTableColumn stc2= new SchemeTableColumn();
//		 stc2.setFromColumn("order_no");
//		 stc2.setFromColumnFormat("NUMBER");
//		 stc2.setToColumn("order_no");
//		 stc2.setToColumnFormat("NUMBER");
//		 schemeTableColumns.add(stc2);
//		 
//		 
//		 SchemeTableColumn stc3= new SchemeTableColumn();
//		 stc3.setFromColumn("order_amt");
//		 stc3.setFromColumnFormat("NUMBER");
//		 stc3.setToColumn("order_amt");
//		 stc3.setToColumnFormat("NUMBER");
//		 schemeTableColumns.add(stc3);
//		 
//		 SchemeTableColumn stc4= new SchemeTableColumn();
//		 stc4.setFromColumn("status");
//		 stc4.setFromColumnFormat("String");
//		 stc4.setToColumn("status");
//		 stc4.setToColumnFormat("VARCHAR2");
//		 schemeTableColumns.add(stc4);
//		 
//		 
//		 SchemeTableColumn stc5= new SchemeTableColumn();
//		 stc1.setFromColumn("order_gad");
//		 stc1.setFromColumnFormat("NUMBER");
//		 stc1.setToColumn("order_gad");
//		 stc1.setToColumnFormat("NUMBER");
//		 schemeTableColumns.add(stc5);
//		 
//		 
//		 
//		 
//		 
//		 schemeInfo.setSchemeTableColumns(schemeTableColumns);
//		
//		JDBCUtils.bulkCopyFromSQLConn(from,to,schemeInfo,null,null,new String[]{});
//
//		if (schemeInfo.isConsistentStructure()) {
//			int[] rs = JDBCUtils.StructureConsistentbulkCopy(from, applicationSettingService.getCurrentJDBCConnection(),
//					schemeInfo, new String[] {});
//
//			System.out.println("insert:" + rs[0]);
//			System.out.println("update:" + rs[1]);
//		}
//
//	}
	
	
//	@Test
//	public void testGetBusinessDate1(){
//		
//		Date testDate = DateUtil.parse("20170302 035900", "yyyyMMdd HHmmss");
//		System.out.println(JDBCUtils.getBusinessDate(testDate));
//		
//		Assert.assertEquals("20170303", JDBCUtils.getBusinessDate(testDate));
//	}
//	
//	@Test
//	public void testGetBusinessDate2(){
//		
//		Date testDate = DateUtil.parse("20170302 035900", "yyyyMMdd HHmmss");
//		System.out.println(JDBCUtils.getBusinessDate(testDate));
//		
//		Assert.assertEquals("20170303", JDBCUtils.getBusinessDate(new Date()));
//	}
	
    public  static void main(String[] arg) throws Exception
    {
    	new JDBCUtilTest().testBulkCopyFromSQLToOracle();
//    	test();
    }

	@Test
	public void testBulkCopyFromSQLToOracle() throws ClassNotFoundException{
		
		CommonCriteria criteria = new CommonCriteria(1585L);
		
//		List<SchemeInfo> schemeInfos = pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
//		SchemeInfo schemeInfo = schemeInfos.get(0);
//
//		schemeInfo.setSchemeTableColumns(new ArrayList<SchemeTableColumn>());
//
//		System.out.println(schemeInfo.getDestination() + " - "
//				+ schemeInfo.getPollSchemeType() +" - " 
//				+ schemeInfo.getClientType()+ " - " 
//				+ schemeInfo.getSchemeTableColumns().size());
		
//		Hibernate.initialize(schemeInfo);
//		System.out.println(schemeInfo.getSchemeTableColumns().size());
//		
//		schemeInfo.setSchemeTableColumns(new ArrayList<SchemeTableColumn>());
	
//		BranchInfo info = new BranchInfo();
//		info.setClientType(ClientType.ORACLE);
//		info.setClientHost("10.10.33.45");
//		info.setClientPort(1521);
//		info.setUser("BUS_SYS");
//		info.setPassword("maximsesb");
//		info.setClientDB("EDWUAT");
//		BranchScheme scheme = new BranchScheme();
//		scheme.setBranchInfo(info);
		
		String toDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
	            "databaseName=esb_uat;user=esb_uat;password=P@ssw0rd"; 

		
//		System.out.println(ConnectionStringHelper.getOracleConnectionString(info));
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		try(Connection fromConn = DriverManager.getConnection("jdbc:sqlserver://enlightening-it.eatuo.com:61807;"
//				+ "databaseName=maxim_staging;user=sa;password=P@ssw0rd")){
		try(Connection fromConn = DriverManager.getConnection(toDS)){
			
			Class.forName("oracle.jdbc.driver.OracleDriver");
			String oracle= "jdbc:oracle:thin:BUS_SYS/maximsesb@//10.10.33.45:1521/EDWUAT";

//			try(Connection toConn = DriverManager.getConnection(ConnectionStringHelper.getOracleConnectionString(info))){
			try(Connection toConn = DriverManager.getConnection(oracle)){
				
				
				toConn.setAutoCommit(false);
				
				String[] criteria2 = new String[]{"branch_code='5101'"};
				
				SchemeInfo schemeInfo = new SchemeInfo();
				schemeInfo.setSource("possystem");
				schemeInfo.setDestination("POSSYSTEM");
				schemeInfo.setSrcKeyColumns("branch_code, rowguid");
				schemeInfo.setDestKeyColumns("BRANCH_CODE, ROWGUID");
//				String[] criteria2 = new String[]{
//						 String.format("status<>\'%s\'",CommonDataStatus.C)
//						,String.format("convert(varchar(8),business_date,112)=\'%s\'", "20161220")
//						,String.format("branch_code=\'%s\'", "1234")
//					};
				JDBCUtils.bulkCopyFromSQLToOracle(fromConn, toConn, schemeInfo, 1000, null, criteria2 );
				System.out.println(JDBCUtils.CURRENT_THREAD_STATUS_NULLABLE.get());
				
                Boolean statusNullable = JDBCUtils.CURRENT_THREAD_STATUS_NULLABLE.get();
                String toOrcl = "SQLStmtUtils.getUpdateNullSqlByStatus(toTable, oracleConditions);";
                if (statusNullable != null && !statusNullable.booleanValue())
                {
                	toOrcl = "SQLStmtUtils.getUpdateSpaceSqlByStatus(toTable, oracleConditions);";
                }
                System.out.println(toOrcl);
                

				toConn.commit();
				
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
//	@Test
//	public void testBulkCopyBySchemeInfo() {
//		String filePath = "";
//		String toDS = "";
//
//		String fields = "";
//
//		String source = "";
//		String destination = "";
//
//	}
	
//	@Test
//	public void testDeleteByBranchAndDate() throws ClassNotFoundException, SQLException{
//		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		BranchInfo info = new BranchInfo();
//		info.setClientType(ClientType.SQLSERVER);
//		info.setClientHost("enlightening-it.eatuo.com");
//		info.setClientPort(61807);
//		info.setUser("sa");
//		info.setPassword("P@ssw0rd");
//		info.setClientDB("hopos");
//		// ;
//		try(Connection conn = DriverManager.getConnection(ConnectionStringHelper.getSQLServerConnectionString(info))){
//			int deleteCount = JDBCUtils.deleteByBranchAndBizDate
//					(conn, "orders_extra", "3710", DateUtil.parse("2016-12-14 00:00:00", "yyyy-MM-dd HH:mm:ss"));
//			System.out.println();
//			
//			conn.commit();
//		}
//
//	}
	
//	@Test
//	@Transactional
//	public void testHandleDuplicatedRecords() throws ClassNotFoundException, SQLException{
//		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		BranchInfo toInfo = new BranchInfo();
//		toInfo.setClientType(ClientType.SQLSERVER);
//		toInfo.setClientHost("enlightening-it.eatuo.com");
//		toInfo.setClientPort(61807);
//		toInfo.setUser("sa");
//		toInfo.setPassword("P@ssw0rd");
//		toInfo.setClientDB("hopos");
//		// ;
//		
//		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		BranchInfo fromInfo = new BranchInfo();
//		fromInfo.setClientType(ClientType.SQLSERVER);
//		fromInfo.setClientHost("enlightening-it.eatuo.com");
//		fromInfo.setClientPort(61809);
//		fromInfo.setUser("sa");
//		fromInfo.setPassword("123456");
//		fromInfo.setClientDB("pos_1282");
//		
//		CommonCriteria criteria = new CommonCriteria(156L);
//		
//		List<SchemeInfo> schemeInfos = pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
//		SchemeInfo schemeInfo = schemeInfos.get(0);
//		
//		int[] counts = new int[2];
//		
//		try(Connection toConn = DriverManager.getConnection(ConnectionStringHelper.getSQLServerConnectionString(toInfo));
//				Connection fromConn = DriverManager.getConnection(ConnectionStringHelper.getSQLServerConnectionString(fromInfo))){
//			toConn.setAutoCommit(false);
//			counts = JDBCUtils.handleDuplicatedRecords(fromConn, toConn, schemeInfo, null);
//			System.out.println(counts[0] +" - " + counts[1]);
//			
//			toConn.commit();
//		}
//
//	}
	
//	@Test(expected=IllegalArgumentException.class)
//	public void testDeleteMethodException() throws SQLException{
//		// ;
//		try(Connection conn = null){
//			int deleteCount = JDBCUtils.deleteByBranchAndBizDate
//					(conn, "orders_extra", "3710", DateUtil.parse("2016-12-14 00:00:00", "yyyy-MM-dd HH:mm:ss"));
//			System.out.println();
//			
//			conn.commit();
//		}
//	}
	

	
}