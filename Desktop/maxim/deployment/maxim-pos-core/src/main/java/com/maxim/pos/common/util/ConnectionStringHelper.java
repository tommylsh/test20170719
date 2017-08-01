package com.maxim.pos.common.util;

public class ConnectionStringHelper {

	public static String SQLSERVER_CONNECTION_PATTERN = "jdbc:sqlserver://{0}:{1};databaseName={2};user={3};password={4}";
	public static String ORACLE_CONNECTION_PATTERN = "jdbc:oracle:thin:{3}/{4}@//{0}:{1}/{2}";
	public static String JTDS_CONNECTION_PATTERN = "jdbc:jtds:sqlserver://{0}:{1}/{2};user={3};password={4}";
	
	public static String SQLSERVER_CONNECTION_PATTERN_2 = "jdbc:sqlserver://{0};databaseName={2};user={3};password={4}";
	public static String ORACLE_CONNECTION_PATTERN_2 = "jdbc:oracle:thin:{3}/{4}@//{0}/{2}";
	public static String JTDS_CONNECTION_PATTERN_2 = "jdbc:jtds:sqlserver://{0}/{2};user={3};password={4}";

	public static String connectionString = "{0};user={1};password={2}";
    
//	public static String getSQLServerConnectionString(BranchInfo info) {
//		if (ClientType.SQLSERVER == info.getClientType()) {
//			String password = "";
//			try {
//				password = EncryptionUtil.aesDecrypt(info.getPassword(), encryptKey);
//			} catch (Exception e) {
//				LogUtils.printException("aesDecrypt error in ConnectonStrinHelper's getSQLServerConnectionString method", e);
//			}
//			return MessageFormat.format(SQLSERVER_CONNECTION_PATTERN, info
//					.getClientHost(),
//					info.getClientPort() == null ? "" : info.getClientPort().toString(),
//					info.getClientDB(), info.getUser(), password);
//		} else {
//			return null;
//		}
//
//	}

//	public static String getOracleConnectionString(BranchInfo info) {
//		if (ClientType.ORACLE == info.getClientType()) {
//			String password = "";
//			try {
//				password = EncryptionUtil.aesDecrypt(info.getPassword(), encryptKey);
//			} catch (Exception e) {
//				LogUtils.printException("aesDecrypt error in ConnectonStrinHelper's getOracleConnectionString method", e);
//			}
//			return MessageFormat.format(ORACLE_CONNECTION_PATTERN,
//					info.getClientHost(), info.getClientPort()+"",
//					info.getClientDB(), info.getUser(), password);
//		} else {
//			return null;
//		}
//
//	}
//
//	public static String getDBFConnectonString(String filePath,
//			String driverClassName) {
//		StringBuilder sb = new StringBuilder();
//
//		return sb.toString();
//	}
//	
//	
//	public static String getConnectionStringByDataSource(com.mchange.v2.c3p0.ComboPooledDataSource dataSource) {
//	    String jdbcUrl = dataSource.getJdbcUrl();
//        Properties properties = dataSource.getProperties();
//        String user = (String) properties.get("user");
//        String password = (String) properties.get("password");
//        return MessageFormat.format(connectionString, jdbcUrl, user, password);
//	}
//	
//	public static String getJTDSConnectionString(BranchInfo info){
//
//		if (StringUtils.startsWith(info.getClientType().name(),"SQLPOS")) {
//			String password = "";
//			try {
//				password = EncryptionUtil.aesDecrypt(info.getPassword(), encryptKey);
//			} catch (Exception e) {
//				LogUtils.printException("aesDecrypt error in ConnectonStrinHelper's getConnectionStringByDataSource method", e);
//			}
//			return MessageFormat.format(JTDS_CONNECTION_PATTERN, info
//					.getClientHost(),
//					info.getClientPort() == null ? "" : info.getClientPort().toString(),
//					info.getClientDB(), 
//					info.getUser(), 
//					password);
//		} else {
//			return null;
//		}
//		
//	}
	

//	 public static void main(String[] args) throws Exception {
//	 // Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//	  BranchInfo info = new BranchInfo();
//	 // info.setClientType(ClientType.SQLSERVER);
//	 // info.setClientHost("enlightening-it.eatuo.com");
//	 //// info.setClientPort("61809");
//	 // info.setClientPort(1433);
//	 // info.setUser("sa");
//	 // info.setPassword("123456");
//	 // info.setClientDB("pos");
//	 // System.out.println(getSQLServerConnectionString(info));
//	 //
////	 System.out.println(DriverManager.getConnection(getSQLServerConnectionString(info)));
//	
//	  Class.forName("oracle.jdbc.OracleDriver");
//	  info.setClientType(ClientType.ORACLE);
//	  info.setClientHost("192.168.1.56");
//	  info.setClientPort(1521);
//	  info.setUser("MAXIM_EDW");
//	  info.setPassword("123456");
//	  info.setClientDB("orcl");
//	  System.out.println(getOracleConnectionString(info));
//	 //
//	 System.out.println(DriverManager.getConnection(getOracleConnectionString(info)));
//	
//	
//	
//	
//	 }
}
