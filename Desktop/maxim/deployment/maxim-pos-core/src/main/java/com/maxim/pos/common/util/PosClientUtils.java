package com.maxim.pos.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PosClientUtils {
	public static final Logger logger = LoggerFactory.getLogger(PosClientUtils.class);
	
	

//	public static void main(String[] args) throws SQLException {
//		List<Map<String, Object>> lists ;//= readDBF("C:\\project\\Maxim\\6101\\M6101_160721_ITEM.DBF", null);
//		Connection conn = getJdbcConnection("com.microsoft.sqlserver.jdbc.SQLServerDriver", 
//				"jdbc:sqlserver://172.29.50.23:1433;DatabaseName=cg_sit_0810;SelectMethod=Cursor", 
//				"sa", "P@ssw0rd");
//		lists = execCliectQuery(conn,"SELECT CG_TOUCH_POINT_TEMPLATE_ID,touch_point_id, TEMPLATE_BODY FROM CG_TOUCH_POINT_TEMPLATE where message_type='EMAIL'",true);
//		for (Map<String, Object> map : lists) {
//			Iterator<String> it = map.keySet().iterator();
//			while (it.hasNext()) {
//					String key = it.next().toString();
//					String value = map.get(key).toString();
////					String newValue = delHTMLTag(value);
////					newValue = newValue.replace("&lt;", "<");
////					newValue = newValue.replace("&gt;", ">");
////					newValue = newValue.replace("&nbsp;", "");
//					
////					System.out.println(newValue);
//			
////					if(newValue.length()>4000 ){
//////						System.out.print(key + "=" + delHTMLTag(map.get(key).toString()) + ",");
////						System.out.println("CONTENT LENGTH:"+newValue.length()+" touch point id = "+map.get("touch_point_id") +" CG_TOUCH_POINT_TEMPLATE_ID = " +map.get("CG_TOUCH_POINT_TEMPLATE_ID"));
////						System.out.print(key + "=" + newValue + ",");
////					} 
//				
//			
//			
//			}
//		
//		}
//	}
	
//	public static String delHTMLTag(String htmlStr){ 
//        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
//        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
//        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式 
//         
//        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
//        Matcher m_script=p_script.matcher(htmlStr); 
//        htmlStr=m_script.replaceAll(""); //过滤script标签 
//         
//        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
//        Matcher m_style=p_style.matcher(htmlStr); 
//        htmlStr=m_style.replaceAll(""); //过滤style标签 
//         
//        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
//        Matcher m_html=p_html.matcher(htmlStr); 
//        htmlStr=m_html.replaceAll(""); //过滤html标签 
//
//        return htmlStr.trim(); //返回文本字符串 
//    } 
	
	
	

	public static Connection getJdbcConnection(String driver, String url, String user, String password) {
		try {
			Class.forName(driver);
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			logger.error("JDBC Connection Exception:", e);
			throw new RuntimeException("JDBC Connection Exception:" + url,e);
		}
	}
	


	public static List<Map<String, Object>> execCliectQuery(Connection conn, String query,boolean closeConn) throws SQLException {
//		logger.info("---execCliectQuery---:"+query);
//		LogUtils.printLog("execCliectQuery,{}",query);
		Statement stmt = null;
		ResultSet rs = null;
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			ResultSetMetaData rsmt = rs.getMetaData();
			
			Map<String, Object> map;
			String columnLabel;

			while (rs.next()) {
				map = new HashMap<String, Object>();
				for (int i = 1; i <= rsmt.getColumnCount(); i++) {
					columnLabel = rsmt.getColumnLabel(i);
					if (StringUtils.isBlank(columnLabel))
					{
						columnLabel = String.valueOf(i);
					}
					map.put(columnLabel, rs.getObject(i));
				}
				datas.add(map);

			}
//			LogUtils.printLog("execCliectQuery execute datas size : {}",datas.size());

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null && closeConn) {
				conn.close();
			}

		}

		return datas;
	}

	public static int updateTable(Connection conn, String query) throws SQLException {
//		LogUtils.printLog("---updateTable---{}",query);
//		boolean bl = false;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(query);
			return stmt.executeUpdate();
//			if(count > 0) {
//				bl = true;
//			}
		}catch(Exception e){
			LogUtils.printException("---updateTable error---:",e);
			return -1 ;
		} finally {
			if (stmt != null) {
				stmt.close();
			}
//			if (conn != null && closeConn) {
//				stmt.close();
//				conn.close();
//			
//			}
		}
//		return bl;
	}
//	
//	public static boolean updateTable(Connection conn, String toTable, boolean closeConn, Object...objs) throws SQLException, ParseException {
////		logger.info("---updateTable---:"+query);
//		boolean bl = false;
//		PreparedStatement stmt = null;
//		try {
//			String query = "update " + toTable + " set business_date = ? where rowguid = ?";
//			stmt = conn.prepareStatement(query);
//			stmt.setObject(1, objs[0]);
//			stmt.setObject(2, objs[1]);
//			
//			int count = stmt.executeUpdate();
//			if(count > 0) {
//				bl = true;
//			}
//		} catch(Exception e){
//			LogUtils.printException("update faile", e);
//		}finally {
//			if (stmt != null) {
//				stmt.close();
//			}
//			if (conn != null && closeConn) {
//				stmt.close();
//				conn.close();
//			
//			}
//		}
//		return bl;
//	}

}