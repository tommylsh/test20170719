package com.maxim.pos.test.common.service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.Key;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.crypto.KeyGenerator;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.util.StringUtils;

import com.luhuiguo.chinese.ChineseUtils;
import com.maxim.dao.QueryFileHandler;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.persistence.PollSchemeInfoDao;
import com.maxim.pos.common.persistence.SchemeJobLogDao;
import com.maxim.pos.common.persistence.SchemeTableColumnDao;
import com.maxim.pos.common.persistence.TaskJobLogDao;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.ChineseConverionServiceImpl;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.service.SchemeTableColumnService;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.master.persistence.BranchInventoryInfoDao;
import com.maxim.pos.report.service.ReportService;
import com.maxim.pos.sales.persistence.BranchInfoDao;
import com.maxim.pos.sales.persistence.BranchSchemeDao;
import com.maxim.pos.sales.persistence.SchemeInfoDao;
import com.maxim.pos.sales.service.BranchInfoService;
import com.maxim.pos.sales.service.MasterServiceImpl;
import com.maxim.pos.security.entity.User;
import com.maxim.util.EncryptionUtil;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:pos-core-context.xml" })
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class SchemeTableColumnGenerationTest {

    @Autowired
    private PollSchemeInfoService pollSchemeInfoService;
    
    @Autowired
    private SchemeTableColumnService schemeTableColumnService;
    
    @Autowired
    private BranchInfoService branchInfoService;
    
    @Autowired
    private QueryFileHandler queryFileHandler;
    
    @Autowired
    private ApplicationSettingService applicationSettingService;
    
    @Autowired
    private PollSchemeInfoDao pollSchemeInfoDao;

    @Autowired
    private com.mchange.v2.c3p0.ComboPooledDataSource dataSource;
    
	@Autowired
	private MailSender mailSender;
    
	@Autowired
	private String  mailSenderAddress;
	
	@Autowired
	private SchemeTableColumnDao  schemeTableColumnDao;
	
	@Autowired
	private ReportService reportService;

	@Autowired
	private BranchInventoryInfoDao branchInventoryInfoDao ;
	@Autowired
	private MasterServiceImpl masterService;
	
    
    protected @Value("${sales.enableArchive}") boolean enableArchive ;

	
	private @Value("${connection.username}") String username;
	
	private @Value("${aesKey}") String asekey;
	
    public Logger logger;
    
	@Autowired
	private TaskJobLogDao  taskJobLogDao;
	 
	@Autowired
	private SchemeInfoDao  schemeInfoDao;
	
	@Autowired
	private SchemeJobLogDao schemeJobLogDao;
	
	@Autowired
	private ChineseConverionServiceImpl chineseConverionService;
	
	@Resource(name="systemPrincipal")
	private User systemPrincipal;
	
    @Autowired
    private BranchInfoDao branchInfoDao;

    @Autowired
    private BranchSchemeDao branchSchemeDao;


	private @Value("${system.eod.batchProcessSize}") int eodBatchProcessSize;
	private @Value("${system.realTime.batchProcessSize}") int realTimeBatchProcessSize;
	private @Value("${system.master.batchProcessSize}") int masterBatchProcessSize;
	private @Value("${system.other.batchProcessSize}") int otherBatchProcessSize;

	public Map<String, Boolean> eodPriorityMap = new HashMap<String, Boolean>();

    
    @Before
    public void setup() {
        logger = LoggerFactory.getLogger(getClass());
        logger.info("setup at : {}", new Date());
    }
    
    @Test
//    @Transactional
    public void getTableColumnInfoTest() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SQLException{

    	if (1==1){
    		
    		BranchScheme scheme = branchSchemeDao.getById((long) 16837);

   			Connection posConn = applicationSettingService.getJDBCConection(scheme, true);
   			
   			String deleteDateConditionStr = "CONVERT(varchar(16),business_date,23) > '2017-07-09' and CONVERT(varchar(16),business_date,23) <= '2017-07-10'";
   			String sql = "select * from hist_orders_pay where branch_code='6155' and " + deleteDateConditionStr;
   			
   		    Statement stmt = posConn.createStatement();
   		    System.out.println("sql : "+sql);
   		    ResultSet rs = stmt.executeQuery(sql) ;
            int deleteCount = JDBCUtils.deleteByBranchAndBizDate(posConn, "hist_orders_pay",
        			"6155", deleteDateConditionStr);
            LogUtils.printLog(logger, "{} SALES_EOD schemeInfo{} delete table :{} {} ", "6155", "hist_orders_pay", deleteCount, deleteDateConditionStr);

   		    int i = 0;
   		    while (rs.next())
   		    {
   		    	i ++;
   		    	System.out.println(i + ":" +rs.getString("business_date"));
   		    }

//    		List<TaskJobLog> list = new ArrayList<TaskJobLog>();
//
//    		Timestamp currentDate = Timestamp.valueOf("2017-07-06 18:08:01.332");
//    		System.out.println(currentDate);
//    		System.out.println(currentDate.getTime());
//    		
//    		TaskJobLog taskLog = new TaskJobLog();
//            taskLog.setBranchCode("4103");
//	        taskLog.setLastestJobInd(LatestJobInd.P);
//	        taskLog.setStatus(TaskProcessStatus.PENDING);
//	        taskLog.setCreateUser("MAX_POS_SYSTEM_2");
//	        taskLog.setCreateTime(currentDate);
//	        taskLog.setLastUpdateUser("MAX_POS_SYSTEM_2");
//	        taskLog.setLastUpdateTime(currentDate);
//	        taskLog.setStartTime(currentDate);
//	        taskLog.setScheduleJobId(32L);
//	        taskLog.setPollSchemeID(1007L);
//	        taskLog.setDirection(Direction.POS_TO_STG);
//	        taskLog.setPollSchemeType(PollSchemeType.SALES_REALTIME);
//	        taskLog.setPollSchemeJobLogId(323874L);
//
//            list.add(taskLog);
//    		taskJobLogDao.batchInsertWithoutReturnGenKey(list);
//
//    		taskLog = taskJobLogDao.findTaskJobLog(taskLog);
//    		System.out.println(taskLog);
//    		System.out.println(taskLog.getId());
//    		System.out.println(taskLog.getLastUpdateTime());
//    		System.out.println(taskLog.getStartTime());
//    		System.out.println(taskLog.getStartTime().getTime());
//    		System.out.println(currentDate);
//    		System.out.println(currentDate.getTime());

//    		for(int i = 0 ; i < 1 ; i++)
//    		{
//	    		TaskJobLog taskLog = new TaskJobLog();
//	            Auditer.audit(taskLog);
//	            SchemeJobLog schemeJobLog = new SchemeJobLog();
//	            schemeJobLog.setId(323874L);
//		        taskLog.setLastestJobInd(LatestJobInd.P);
//		        taskLog.setStatus(TaskProcessStatus.PENDING);
//		        taskLog.setCreateUser(systemPrincipal.getUserId());
//		        taskLog.setCreateTime(currentDate);
//		        taskLog.setLastUpdateUser(systemPrincipal.getUserId());
//		        taskLog.setLastUpdateTime(currentDate);
//		        taskLog.setStartTime(currentDate);
////		        Auditer.audit(taskLog);
////		        taskLog.setSchemeScheduleJob(scheduleJob);
//		        taskLog.setScheduleJobId(32L);
//		        taskLog.setPollSchemeID(120L);
//		        taskLog.setDirection(Direction.EDW_TO_STG);
//		        taskLog.setPollSchemeType(PollSchemeType.MASTER);
//		        taskLog.setSchemeJobLog(schemeJobLog);
//		        
//		        taskLog.setBranchCode("6101");
//		        taskLog.setPollBranchId(41L);
//		        taskLog.setPollSchemeName("MASTER");
//		        taskLog.setPollSchemedesc("6101 MASTER EDW_TO_STG DESC");	        
//		        
//	            list.add(taskLog);
//    		}
//    		taskJobLogDao.batchInsertWithoutReturnGenKey(list);
    		
//    		TaskJobLog taskLog = taskJobLogDao.findTaskJobLog(list.get(0));
//    		System.out.println(taskLog);
//    		System.out.println(taskLog.getId());
//    		System.out.println(taskLog.getLastUpdateTime());
    		return ;
    	}
    		
    	if (1==1){
    		Connection fromConn = applicationSettingService.getCurrentJDBCConnection();
    		Connection toConn = applicationSettingService.getEdwJDBCConnection();
    		SchemeInfo schemeInfo = pollSchemeInfoDao.getById((long) 1577);
        	JDBCUtils.bulkCopyFromSQLToOracle(fromConn, toConn, schemeInfo, 1000, null,
					new String[]{"branch_code='1121'"}, null, true );
//    		String selectCountSQL = SQLStmtUtils.getCountTableStmt("hist_orders",null);
//            List<Map<String, Object>> list = PosClientUtils.execCliectQuery(con, selectCountSQL, false);
//            for (Map<String, Object> map : list) {
//            	System.out.println(map);
//            	System.out.println(map.get("1"));
//            	System.out.println(map.get("1").getClass());
////    			returnInts = new int[] {((BigDecimal) map.get("1")).intValue(), 0};
//            }
            
            return ;
    		
    	}

    	if (1==1){

    		
//    		Connection con = applicationSettingService.getEdwJDBCConnection();
//    		System.out.println(con);
    		
    		BranchInfo info = branchInfoDao.getById(23L);
    		
    		BranchMaster master = new BranchMaster();
    		master.setBranchCode("10.104.17.66");
    		master.setBranchType("test");
    		
    		BranchInfo posInfo = new BranchInfo();
    		posInfo.setClientDB("pos1284");
    		posInfo.setClientHost("{BRANCH_CODE}");
    		posInfo.setClientPort(1433);
    		posInfo.setClientType(ClientType.SQLPOS);
    		posInfo.setPassword("P@ssw0rd");
    		posInfo.setUser("${{BRANCH_TYPE}.user}");
    		BranchScheme scheme = new BranchScheme();
    		scheme.setBranchInfo(info);
    		scheme.setBranchMaster(master);
    		
   			Connection posConn = applicationSettingService.getJDBCConection(scheme, true);
   			System.out.println(posConn);
    			
//    		Connection con = applicationSettingService.getJDBCConection(info, true);
    		Connection con = applicationSettingService.getCurrentJDBCConnection();
//    		C3P0NativeJdbcExtractor cp30NativeJdbcExtractor = new C3P0NativeJdbcExtractor();
//			con = cp30NativeJdbcExtractor.getNativeConnection(con);

			System.out.println("select * from dbo.hist_possystem where branch_code='4117' and business_date='2017-04-07'");
			PreparedStatement prest = con.prepareStatement("select * from dbo.hist_possystem where branch_code=? and business_date=?");
//    		ResultSet rs = con.createStatement().executeQuery("select * from dbo.hist_possystem where branch_code='4117' and business_date='2017-04-26'");
			prest.setString(1, "4117");
			prest.setString(2, "2017-04-26");
			ResultSet rs = prest.executeQuery();
			System.out.println("ROW : " +rs.getRow());
    		if (rs.next())
    		{
    			System.out.println(rs.getString(2));
    		}
    		rs = con.createStatement().executeQuery("select * from dbo.hist_possystem where branch_code='4117'");
			System.out.println("ROW : " +rs.getRow());
    		if (rs.next())
    		{
    			System.out.println(rs.getString(2));
    		}
    		con.close();

    		masterService.getClass();
    		chineseConverionService.getClass();
//    		String str = "枫糖甜心酥";
    		String str = "广州东站(3线)饼店";
    		
    		System.out.println(str);
    		String str2 = ChineseUtils.toTraditional(str);
    	    		System.out.println(str2);
//    		BranchScheme branchScheme = new BranchScheme();
//    		branchScheme.setPollSchemeType(PollSchemeType.REPORT);
//    		branchScheme.setDirection(Direction.DAYEND_REPORT);
//    		reportService.sendReportMail(branchScheme, logger);
//    		
//    		this.branchInventoryInfoDao.findAll();
//    		BranchInventoryInfo info = new BranchInventoryInfo();
//    		info.setBranchCode("XXXX");
//    		info.setBusinessDate(java.sql.Date.valueOf("2017-01-01"));
//    		
//    		info = this.branchInventoryInfoDao.find(info);
//    		System.out.println(info);
//    		
//    		info = new BranchInventoryInfo();
//    		info.setBranchCode("XXXX");
//    		info.setBusinessDate(java.sql.Date.valueOf("2017-01-01"));
//    		info.setLastUpdateDateTime(new java.util.Date());
//    		
//    		this.branchInventoryInfoDao.insert(info);
//    		System.out.println(info);
//
//    		info = this.branchInventoryInfoDao.find(info);
//    		System.out.println(info);
//    		
//    		List dates = this.branchInventoryInfoDao.getPosEODStockDateList("XXXX", java.sql.Date.valueOf("2015-01-01"));
//
//    		System.out.println(dates);
//    		
//    		info.setLastUpdateDateTime(new java.util.Date());
//    		this.branchInventoryInfoDao.update(info);
//    		System.out.println(info);
//    		
//    		this.branchInventoryInfoDao.delete(info);
//    		System.out.println(info);
//    		
//    		
//    		System.out.println("username : " +enableArchive);
//    		System.out.println("username : " +username);
//    		System.out.println("username : " +systemPrincipal.getUserId());
//    		System.out.println("username : " +Auditer.systemPrincipal.getUserId());
//    		
////    		List list = taskJobLogDao.findLatestPollBrachScheme(PollSchemeType.SALES_EOD, Direction.POS_TO_STG);
//    		List<Map<String, Object>> list = schemeInfoDao.findLatestPollBrachScheme(PollSchemeType.SALES_EOD, Direction.POS_TO_STG);
//    		
//    		System.out.println(schemeJobLogDao.findLatestSchemeJobLog(30L, "MAX_POS_SYSTEM_2"));
//    		System.out.println(schemeJobLogDao.countCreateUserByScheduleJobIdAndCreateUser(30L, "MAX_POS_SYSTEM_2"));
//    		
//    		eodPriorityMap.put("4117", true);
//    		eodPriorityMap.put("4459", true);
//    		System.out.println("list : " +list.size());
//    		List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>(list.size());
//    		
//    		
//			System.out.println("eodPriorityMap : " + eodPriorityMap);
//            Collections.sort(list, new Comparator<Map<String, Object>>()
//    			{
//					@Override
//					public int compare(Map<String, Object> map1 ,Map<String, Object> map2) {
//			            String branchCode1 = (String) map1.get("branchCode");
//			            String branchCode2 = (String) map2.get("branchCode");
//						if (eodPriorityMap.containsKey(branchCode1))
//							return eodPriorityMap.containsKey(branchCode2) ? 0 : -1 ;
//						else
//							return eodPriorityMap.containsKey(branchCode2) ? 1 : 0 ;
//					}
//    			}
//            );
//
//    		for (Object o : list)
//    		{
//    			System.out.println(o.getClass() + " : " + o);
//    			
//    			Map<String, Object> map = (Map<String, Object>) o ; 
//    			Map<String, Object> newMap = new HashMap<String, Object>(); 
//    			for (String key : map.keySet())
//    			{
//    				Object value = map.get(key) ;
//    				if (value != null)
//    				{
//    					System.out.println(key + " : " + value.getClass() + " : " + value);
//    				}
//    				else
//    				{
//    					System.out.println(key + " : " + value);
//    				}
//    				if (key.equals("isEnabled"))
//    				{
//    					System.out.println(key + " : " + ((byte)value));
//    				}
//    				if (value != null)
//    					newMap.put(key, value);
//    			}
//    			newList.add(newMap);
//    		}
//    		
//    		for (Object o : newList)
//    		{
//    			System.out.println(o.getClass() + " : " + o);
//    			
//    			Map<String, Object> map = (Map<String, Object>) o ; 
//    		
//            	BranchScheme branchScheme = new BranchScheme() ;
//            	BeanUtils.populate(branchScheme, map);
//
//            	
//    			System.out.println(BeanUtils.describe(branchScheme));
//
//    		}
    		
    		
    		return ;
    	}
    	if (1==1){
			 SimpleMailMessage message = new SimpleMailMessage();
			  
			  message.setFrom(mailSenderAddress);
			  message.setTo("tommy.leung@buzz-it.com.hk");
			  message.setSubject("testing");

			  message.setText("Test 123");
			  
			  mailSender.send(message);
    		System.out.println(dataSource.getJdbcUrl());
    		System.out.println(dataSource.getUser());
    		System.err.println(dataSource.getJdbcUrl());
    		System.err.println(dataSource.getUser());
    		System.err.println(dataSource.getInitialPoolSize());
    		System.err.println(dataSource.getMaxPoolSize());
//    		System.err.println(dataSource.getUser());
//    		System.err.println(dataSource.getUser());
    		
    		
    		return ;
    		
    	}

    	if (1==1){
    		SchemeInfo schemeInfo = pollSchemeInfoDao.getById((long) 105);
			System.out.println("Poll Scheme Info ID = " + schemeInfo.getId() + "; table = " + schemeInfo.getDestination());
			List<SchemeTableColumn> schemeTableColumnList = pollSchemeInfoService.generateSchemeTableColumnData(schemeInfo);
	    	System.out.println("Scheme Table Column List Size = " + schemeTableColumnList.size());
	    	schemeTableColumnService.saveSchemeTableColumns(schemeTableColumnList);
	    	
//			for(SchemeTableColumn schemeTableColumn: schemeTableColumnList){
//				System.out.println(schemeTableColumn.getFromColumnInfo());
//				System.out.println(schemeTableColumn.getFromColumnInfo());
//			}

	    	return ;
    	}
    	
		List<SchemeInfo> schemeInfoList = pollSchemeInfoService.findSchemeInfoBySchemeTypeAndClientType("SALES_EOD", ClientType.CSV);
		System.out.println("getTableColumnInfoTest size : " +schemeInfoList.size());
		for (SchemeInfo schemeInfo : schemeInfoList){

			
			System.out.println("schemeInfo : " + BeanUtils.describe(schemeInfo).toString() ) ;

//			if(schemeInfo.getClientType().equals(ClientType.DBF)
//					&& schemeInfo.getPollSchemeType().equals(PollSchemeType.SALES_EOD)
//					&& schemeInfo.getDestination().equalsIgnoreCase("hist_orders"))
			{
				System.out.println("Poll Scheme Info ID = " + schemeInfo.getId() + "; table = " + schemeInfo.getDestination());
				List<SchemeTableColumn> schemeTableColumnList = pollSchemeInfoService.generateSchemeTableColumnData(schemeInfo);
		    	System.out.println("Scheme Table Column List Size = " + schemeTableColumnList.size());
		    	schemeTableColumnService.saveSchemeTableColumns(schemeTableColumnList);
			}
		}

    }
    
    public  static void main(String[] arg) throws Exception
    {
    	if (1==1)
    	{
    		System.out.println(org.apache.commons.lang.StringUtils.startsWith(ClientType.SQLPOS.name(), "SQLPOS"));
    		
//			String url = "smb://10.20.30.166/";
			
			String url = "smb://10.20.30.166/export/6101/";
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "poll", "poll12345");

//			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "poll", "poll12345");
			SmbFile dir = new SmbFile(url, auth);
			System.out.println(dir.getURL());
			System.out.println(dir.exists());
			
			for (SmbFile file :dir.listFiles())
			{
				System.out.println(file.getName());
				System.out.println(file.length());
			}
			
			System.out.println(dir.list());
    		System.out.println(EncryptionUtil.aesEncrypt("P@ssw0rd1234" , "90206f7a4fc149b592a14b7629caad5e"));
    		System.out.println(new String(EncryptionUtil.aesEncryptToBytes("P@ssw0rd1234" , "90206f7a4fc149b592a14b7629caad5e")));

    		Key key;
    		SecureRandom rand = new SecureRandom();
    		KeyGenerator generator = KeyGenerator.getInstance("AES");
    		generator.init(rand);
    		generator.init(256);
    		key = generator.generateKey();
    		System.out.println(EncryptionUtil.base64Encode(key.getEncoded()));
//    		
//    		byte[] b= new byte[20];
//    		b[0] = -17;
//    		b[1] = -65;
//    		b[2] = -25;
//    		b[3] = -112;
//    		b[4] = -92;
//    		b[5] = -24;
//    		b[6] = ;
//    		b[7] = ;
//    		b[8] = ;
//    		b[9] = ;
//    		b[10] = ;
//    		b[0] = ;
//    		b[0] = ;
//    		b[0] = ;
//    		b[0] = ;
//    		b[0] = ;
//    		b[0] = ;
//    		
//    		pay_cdesc :[-17]
//    				pay_cdesc :[-65]
//    				pay_cdesc :[-67]
//    				pay_cdesc :[-25]
//    				pay_cdesc :[-112]
//    				pay_cdesc :[-92]
//    				pay_cdesc :[-24]
//    				pay_cdesc :[-65]
//    				pay_cdesc :[-117]
//    				pay_cdesc :[-17]
//    				pay_cdesc :[-65]
//    				pay_cdesc :[-67]
//
    		
    		
//    		-26
//    		-127
//    		-110
//    		-25
//    		-108
//    		-97
//    		-27
//    		-110
//    		-83
    		
//    		pay_cdesc :[-27]
//    				pay_cdesc :[-123]
//    				pay_cdesc :[-125]
//    				pay_cdesc :[-26]
//    				pay_cdesc :[-80]
//    				pay_cdesc :[-93]
//    				pay_cdesc :[-27]
//    				pay_cdesc :[-115]
//    				pay_cdesc :[-95]
    		
//    		-27
//    		-123
//    		-125
//    		-26
//    		-80
//    		-93
//    		-27
//    		-115
//    		-95
    						
//    		String filePath= "C:/UserData/M5120_170523_ORDERSPY.DBF";
//    		String toDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
//    	            "databaseName=esb_sit;user=esb_sit;password=P@ssw0rd"; 
//
//    		SchemeInfo schemeInfo = new SchemeInfo();
//    		schemeInfo.setDestination("hist_orders_pay");
//    		try(	
//    			Connection destinationConnection = DriverManager
//    					.getConnection(toDS)){
//
//    			JavaDBFUtils.bulkCopyFromDBFToSQL(filePath, destinationConnection, schemeInfo, 1000, null,JDBCUtils.CONV_NONE);
//    		}
//    		catch(Exception e){
//    			e.printStackTrace();
//    		}

    		String str = "恒生咭";
//    		24658
//    		29983
//    		21677
//    		String str = "元氣卡";
//    		20803
//    		27683
//    		21345
    		for (byte c :  str.getBytes() )
    		{
    			System.out.println( c );
    		}
    		System.out.println(str);
    		String str2 = ChineseUtils.toTraditional(str);
    	    		System.out.println(str2);

    		StandardEnvironment e = new StandardEnvironment();
    		System.out.println(e.resolvePlaceholders("file:C:/UserData/weblogic/chinese_conversion_table.json"));
    		FileSystemResource resource = new FileSystemResource("file:C:/UserData/weblogic/chinese_conversion_table.json");
    		System.out.println(resource.exists());
    		System.out.println(resource.getFilename());
    		System.out.println(resource.getPath());
    		System.out.println(resource.getFile().exists());
    		System.out.println(resource.getFile().getCanonicalPath());
    		
    	    		
    		System.out.println(EncryptionUtil.aesEncrypt("P@ssw0rd1234" , "90206f7a4fc149b592a14b7629caad5e"));
    		System.out.println(EncryptionUtil.aesEncryptToBytes("P@ssw0rd1234" , "90206f7a4fc149b592a14b7629caad5e"));
    		
    		String txt="M{BB}M";
    		System.out.println(txt.replaceAll("/^BB/", "A"));
    		
    		System.out.println(StringUtils.replace(txt, "{BB}", "A"));
    				
			return ;
    	}
    	if (1==1)
    	{


			  
			String url = "smb://10.20.30.166/export/6101/";
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "poll", "poll12345");
		       List<SmbFile> fileList = new ArrayList<SmbFile>();

				byte[] bs= new byte[1024];
			SmbFile dir = new SmbFile(url, auth);
			for (SmbFile file : dir.listFiles())
			{
				fileList.add(file);
			    System.out.println(file.getCanonicalPath());
			    if (file.getName().equals("M6101_000000_TRANSMODI.DBF"))
			    {
			    
	    		   FileOutputStream f = new FileOutputStream("C:/UserData/"+file.getName());
	    		   InputStream in = file.getInputStream();
	    		   int i = in.read(bs);
	    		   while (i > 0)
	    		   {
	    			   f.write(bs, 0, i);
	    			   i = in.read(bs);
	    		   }
	    		   f.close();
	    		   in.close();
	    		   
	    		   file.delete();
			    }

			}

		       for (SmbFile file : fileList)
		       {
	    		   file.getInputStream().reset();
	    		   
	    		   
	    	  }
			return ;
    	}
    	
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        System.out.println(dateFormat.parse("170908"));
    	String[] dates = new String[] {"111","222"};
    	int idx = 0;
    	StringBuffer dateStrBuf = new StringBuffer("(");
    	for (String date : dates)
    	{
    		System.out.println(idx);
    		System.out.println(dates.length);
    		System.out.println(date);
    		dateStrBuf.append("'").append(date);
    		if (++idx == dates.length)
    		{
    			dateStrBuf.append("')");
    		}
    		else
    		{
    			dateStrBuf.append("',");
    		}
    	}
    	
    	System.out.println(dateStrBuf.toString());
    	
    	return ;
    }
    
//    @Test
//    @Transactional
//    public void addPollSchmeInfoTest(){
//
//    }
//    
//    @Test
//    @Transactional
//    public void batchSizeValidationTest(){
//    	Integer batchSize = new Integer(1000);
//    	System.out.println(batchSize > 900);
//    	
//    	String str = "1001";
//    	Integer newBatchSize = Integer.valueOf(str);
//    	System.out.println(newBatchSize.compareTo(batchSize));
//    	System.out.println(newBatchSize);
//    }
}
