package com.maxim.pos.common.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

import com.maxim.pos.common.config.SecurityConfig;
import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.persistence.ApplicationSettingDao;
import com.maxim.pos.common.util.ConnectionStringHelper;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Service("applicationSettingService")
public class ApplicationSettingServiceImpl implements ApplicationSettingService, EmbeddedValueResolverAware {
	
    public Logger logger = LoggerFactory.getLogger(ApplicationSettingServiceImpl.class);
	
	private static final String SQL_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String JTDS_DRIVER = "net.sourceforge.jtds.jdbc.Driver";
	
	// value of time
	public static final String APPLICATION_SETTING_CODE_CONNECTION_RETRY_COUNT = "CONNECTION_RETRY_COUNT";
	
	// value in secondspollEodControlDao
	public static final String APPLICATION_SETTING_CODE_CONNECTION_RETRY_DELAY = "CONNECTION_RETRY_DELAY";
	
	
	private static String defaultRetryCount = "3";			//default 3
	private static String defaultRetryDelay = "5";			//default 5s
	private Properties connProperties = new Properties();	
	private Map<String, ApplicationSetting> applicationSettingMap ;
	
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DataSource masterDataSource;
	@Autowired
	private DataSource edwDataSource;

	@Autowired
	private ApplicationSettingDao applicationSettingDao;
	
    @Autowired
    private SecurityConfig securityConfig ;
	
	private StringValueResolver resolver;
	public void setEmbeddedValueResolver(StringValueResolver resolver)
	{
		this.resolver = resolver ;
	}

	@PostConstruct
	public void init() {
		
		refreshMap() ;
		
		ApplicationSetting retryCountSetting = 
				this.findApplicationSettingByCode(APPLICATION_SETTING_CODE_CONNECTION_RETRY_COUNT);
		ApplicationSetting retryDelaySetting = 
				this.findApplicationSettingByCode(APPLICATION_SETTING_CODE_CONNECTION_RETRY_DELAY);
		if(retryCountSetting != null && retryDelaySetting != null){
			connProperties.put(APPLICATION_SETTING_CODE_CONNECTION_RETRY_COUNT, retryCountSetting.getCodeValue());
			connProperties.put(APPLICATION_SETTING_CODE_CONNECTION_RETRY_DELAY, retryDelaySetting.getCodeValue());
		}
		else{
			connProperties.put(APPLICATION_SETTING_CODE_CONNECTION_RETRY_COUNT, defaultRetryCount);
			connProperties.put(APPLICATION_SETTING_CODE_CONNECTION_RETRY_DELAY, defaultRetryDelay);
		}
		logger.info("ApplicationSettingService: CONNECTION_RETRY_COUNT {}",this.connProperties.getProperty(APPLICATION_SETTING_CODE_CONNECTION_RETRY_COUNT));
		logger.info("ApplicationSettingService: CONNECTION_RETRY_DELAY {}",this.connProperties.getProperty(APPLICATION_SETTING_CODE_CONNECTION_RETRY_DELAY));

	}

	private void refreshMap()
	{
		List<ApplicationSetting> list = applicationSettingDao.findAll();
		HashMap<String, ApplicationSetting> newApplicationSettingMap = new HashMap<String, ApplicationSetting>(list.size()) ;
		for (ApplicationSetting setting :list)
		{
			if (newApplicationSettingMap.containsKey(setting.getCode()))
			{
				logger.error("ApplicationSettingService: Duplicate Application Setting for {} ", setting.getCode());
			}
			newApplicationSettingMap.put(setting.getCode(), setting);
		}
		this.applicationSettingMap = newApplicationSettingMap;
		logger.info("ApplicationSettingService: ApplicationSettingService Cache Size{}, Database Size{}",this.applicationSettingMap.size(), list.size());
	}


	/**
	 *  getApplicationSettingCodeValue
	 * 
	 *  Lookup the code value in the cache
	 * 
	 */
	@Override
	public String getApplicationSettingCodeValue(String code) {
		ApplicationSetting setting = this.applicationSettingMap.get(code);
		if (setting != null)
		{
			return setting.getCodeValue();
		}
		return null;
	}

	@Transactional(readOnly = true)
	@Override
	public ApplicationSetting findApplicationSettingByCode(String code) {
		return applicationSettingDao.findApplicationSettingByCode(code);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ApplicationSetting> findApplicationSettingByCriteria(CommonCriteria criteria) {
		return applicationSettingDao.findApplicationSettingByCriteria(criteria);
	}

	@Transactional(readOnly = true)
	@Override
	public Long getApplicationSettingCountByCriteria(CommonCriteria criteria) {
		return applicationSettingDao.getApplicationSettingCountByCriteria(criteria);
	}

	@Override
	public ApplicationSetting saveApplicationSetting(ApplicationSetting applicationSetting) {
		try {
			return (ApplicationSetting) applicationSettingDao.save(applicationSetting);
		}
		finally
		{
			init();
		}

	}

	@Override
	public void deleteApplicationSettingById(Long applicationSettingId) {
//		applicationSettingDao.delete(applicationSettingDao.getSingle(ApplicationSetting.class, applicationSettingId));
//		applicationSettingDao.delete(applicationSettingDao.findByKey(applicationSettingId));
		ApplicationSetting applicationSetting = applicationSettingDao.deleteByKey(applicationSettingId);
		this.applicationSettingMap.remove(applicationSetting.getCode());
	}

	@Override
	public String getCurrentJDBCConnectionString() {
		ComboPooledDataSource cpd = (ComboPooledDataSource) dataSource;
		return MessageFormat.format("{0};user={1};password={2}", cpd.getJdbcUrl(), cpd.getUser(), cpd.getPassword());
	}

	@Override
	public Connection getCurrentJDBCConnection() {
		return DataSourceUtils.getConnection(dataSource);
//		C3P0NativeJdbcExtractor cp30NativeJdbcExtractor = new C3P0NativeJdbcExtractor();
//		try {
//			return cp30NativeJdbcExtractor.getNativeConnection(DataSourceUtils.getConnection(dataSource));
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	@Override
	public Connection getMasterJDBCConnection() {
		return DataSourceUtils.getConnection(masterDataSource);
	}
	
	@Override
	public Connection getEdwJDBCConnection() {
		return DataSourceUtils.getConnection(edwDataSource);
	}
	
	@Override
	public Connection getJDBCConection(BranchScheme scheme, boolean retry){
		
		int count = 0 ;
		BranchInfo info = scheme.getBranchInfo();
		BranchMaster master = scheme.getBranchMaster() ;
		info.setUser(getResolvedValue(info.getUser(), master));
		info.setPassword(getResolvedValue(info.getPassword(), master));
		info.setClientHost(getResolvedValue(info.getClientHost(), master));
		info.setClientDB(getResolvedValue(info.getClientDB(), master));

		try{
			Connection conn = null;
			
			//Default SQLDriver
			String className = "";
			String url = "";
			
			ClientType clientType = info.getClientType() ;
//			if (clientType.name().startsWith("SQLPOS"))
//			{
//				clientType = ClientType.SQLPOS ;
//			}

			switch(clientType){
			case ORACLE:
				className = ORACLE_DRIVER;
//				url = ConnectionStringHelper.getOracleConnectionString(info);
				
				url = MessageFormat.format(
						info.getClientHost().contains(":") ? ConnectionStringHelper.ORACLE_CONNECTION_PATTERN_2 :  
															 ConnectionStringHelper.ORACLE_CONNECTION_PATTERN,
						info.getClientHost(), 
						info.getClientPort() == null ? "" : info.getClientPort().toString(),
						info.getClientDB(), 
						securityConfig.decrypt(info.getUser()), 
						securityConfig.decrypt(info.getPassword()));
				break;
			case SQLPOS:
				className = JTDS_DRIVER;
//				url = ConnectionStringHelper.getJTDSConnectionString(info);
				url = MessageFormat.format(
						info.getClientHost().contains(":") ? ConnectionStringHelper.JTDS_CONNECTION_PATTERN_2 :  
							 ConnectionStringHelper.JTDS_CONNECTION_PATTERN,
						info.getClientHost(),
						info.getClientPort() == null ? "" : info.getClientPort().toString(),
						info.getClientDB(), 
						securityConfig.decrypt(info.getUser()), 
						securityConfig.decrypt(info.getPassword()));
			
				break;
			default:
				className = SQL_DRIVER;
//				url = ConnectionStringHelper.getSQLServerConnectionString(info);
				
				url =  MessageFormat.format(
						info.getClientHost().contains(":") ? ConnectionStringHelper.SQLSERVER_CONNECTION_PATTERN_2 :  
							 ConnectionStringHelper.SQLSERVER_CONNECTION_PATTERN,
						info.getClientHost(),
						info.getClientPort() == null ? "" : info.getClientPort().toString(),
						info.getClientDB(), 
						securityConfig.decrypt(info.getUser()), 
						securityConfig.decrypt(info.getPassword()));
				break;
			}
			
			if (master != null)
			{
		        String branchType				= master.getBranchType();
		        String branchCode				= master.getBranchCode();
				url = StringUtils.replace(url,"{BRANCH_CODE}", branchCode);
				url = StringUtils.replace(url,"{BRANCH_TYPE}", branchType);
			}

			for (count = 0; count < 3 ; count ++)
			{
				try
				{
					Class.forName(className);
					conn = DriverManager.getConnection(url, connProperties);
	//				conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					return conn;
				}catch(Exception e){
					e.printStackTrace();
					if (count == 2)
					{
						throw e;
					}
					Thread.sleep(5000);
				}
			}

		}catch(SQLException e){
			LogUtils.printException("create connection SQL exception["+e.getErrorCode()+","+e.getSQLState()+"] ", e);
			throw new RuntimeException("Fail ["+e.getErrorCode()+","+e.getSQLState()+"] to Get Connection "+ (retry ? "Retry["+count+"] ":"" )+"to "+info.getClientDB() + "@" +info.getClientHost()+":"+info.getClientPort(),e);
		}catch(Exception e){
			LogUtils.printException("create connection exception ", e);
			throw new RuntimeException("Fail to Get Connection "+ (retry ? "Retry["+count+"] ":"" )+"to "+info.getClientDB() + "@" +info.getClientHost()+":"+info.getClientPort(),e);
		}
		return null;
	}
	
	@Override
	  public boolean checkConnection(BranchScheme scheme) throws IOException
	  {
			BranchInfo info = scheme.getBranchInfo();
			BranchMaster master = scheme.getBranchMaster() ;
			info.setUser(getResolvedValue(info.getUser(), master));
			info.setPassword(getResolvedValue(info.getPassword(), master));
			info.setClientHost(getResolvedValue(info.getClientHost(), master));
			info.setClientDB(getResolvedValue(info.getClientDB(), master));

			
		  Integer port = info.getClientPort() ;
		  if (info.getClientType() == null)
		  {
			  if (port == null)
			  {
				  port = 445 ;
			  }
		  }
		  else if (info.getClientType().equals(ClientType.DBF ) || 
		      info.getClientType().equals(ClientType.CSV ) || 
		      info.getClientType().equals(ClientType.TEXT ))
		  {
			  port = 445;
		  }
		  
		    try (Socket socket = new Socket()) {
		        socket.connect(new InetSocketAddress(info.getClientHost(), port), 30000);
		        return true ;
		    }
		}


	private String getResolvedValue(String value, BranchMaster master)
	{
		if (master != null)
		{
	        String branchType				= master.getBranchType();
	        String branchCode				= master.getBranchCode();
	        value = StringUtils.replace(value,"{BRANCH_CODE}", branchCode);
	        value = StringUtils.replace(value,"{BRANCH_TYPE}", branchType);
		}
		if (value != null && value.startsWith("${") && value.endsWith("}"))
		{
			value = this.resolver.resolveStringValue(value) ;
		}
		return value ;
	}

}