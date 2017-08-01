package com.maxim.pos.test.common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.test.common.BaseTest;

public class JTDSTest extends BaseTest{

	
	@Autowired
	PollBranchSchemeService pollBranchSchemeService;
	
	@Autowired
	ApplicationSettingService appSettingService;
	
	@Test
	public void testGetJdbcConnection(){
		
		String sql = "select count(*) from hist_trans";
		
		BranchInfo posInfo = new BranchInfo();
		posInfo.setClientDB("pos1284");
		posInfo.setClientHost("10.104.17.66");
		posInfo.setClientPort(1433);
		posInfo.setClientType(ClientType.SQLPOS);
		posInfo.setPassword("P@ssw0rd");
		posInfo.setUser("esb_sit");
		BranchScheme scheme = new BranchScheme();
		scheme.setBranchInfo(posInfo);
		
		try{
			Connection posConn = appSettingService.getJDBCConection(scheme, true);
			try(Statement stmt = posConn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)){
				try(ResultSet rs = stmt.executeQuery(sql)){
					rs.next();
					int count = rs.getInt(1);
					System.out.println("" + count);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		BranchInfo edwInfo = new BranchInfo();
		edwInfo.setClientDB("EDWUAT");
		edwInfo.setClientHost("xp01oda01-scan.maxims.com");
		edwInfo.setClientPort(1521);
		edwInfo.setClientType(ClientType.ORACLE);
		edwInfo.setPassword("maximsesb");
		edwInfo.setUser("BUS_SYS");
		BranchScheme edwScheme = new BranchScheme();
		edwScheme.setBranchInfo(edwInfo);
		
		try{
			Connection edwConn = appSettingService.getJDBCConection(edwScheme, true);
			try(Statement stmt = edwConn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY)){
				try(ResultSet rs = stmt.executeQuery(sql)){
					rs.next();
					int count = rs.getInt(1);
					System.out.println("EDW-Oracle Select Count(*) from hist_trans" + count);
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
//	@Test
//	public void sqlserver2000Test(){
//		
//		String sql = "select count(*) hist_trans";
//		try{
//			Class.forName("net.sourceforge.jtds.jdbc.Driver");
//			try(Connection conn = DriverManager.getConnection("")){
//				String url = "jdbc:jtds:sqlserver://10.104.17.66:1433/pos1284;user=esb_sit;password=P@ssw0rd";
//				try(Statement stmt = conn.createStatement()){
//					try(ResultSet rs = stmt.executeQuery(sql)){
//						rs.next();
//						int count = rs.getInt(1);
//						System.out.println("" + count);
//					}
//				}
//
//			}
//			catch(SQLException e){
//				e.printStackTrace();
//			}
//		}
//		catch(ClassNotFoundException e){
//			e.printStackTrace();
//		}
//	}
}
