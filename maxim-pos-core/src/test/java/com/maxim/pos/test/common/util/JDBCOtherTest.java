package com.maxim.pos.test.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import com.maxim.pos.test.common.BaseTest;

public class JDBCOtherTest extends BaseTest{

	@Test
	public void testReadNull() throws ClassNotFoundException{
		String sql = "select event_no from hist_trans where rowguid = \'B5BD6043-762E-449F-8900-8C87C671C5C1\'";
		
		String fromDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61809;"
		+ "databaseName=pos_0000;user=sa;password=123456";
		
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		try(Connection conn = DriverManager.getConnection(fromDS)){
			try(Statement stmt = conn.createStatement()){
				Object eventNo = null;
				try(ResultSet rs = stmt.executeQuery(sql)){
					rs.next();
					 eventNo = rs.getObject(1);
				}
				if(null == eventNo){
					System.out.println("Event No is Null");
				}
				else
					System.out.println("Event No:" + eventNo + ", length = " + eventNo.toString().length());
			}
			
		}
		catch(SQLException e){
			
		}
	}
}
