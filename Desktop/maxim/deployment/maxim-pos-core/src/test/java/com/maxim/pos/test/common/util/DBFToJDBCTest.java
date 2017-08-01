package com.maxim.pos.test.common.util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ColumnFormat;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.util.JDBCUtils;
import com.maxim.pos.common.util.JavaDBFUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.test.common.BaseTest;

public class DBFToJDBCTest extends BaseTest{

	@Autowired
	PollSchemeInfoService pollSchemeInfoService;
	
//	@Test
//	public void readDBFTest(){
//		try(FileInputStream fis = new FileInputStream("D:\\Maxim_test\\Infrasys_Dbf\\6804\\M6804_161214_orders.dbf")){
//			
//			List<Map> data = new ArrayList<Map>();
//			
//			DBFReader reader = new DBFReader(fis);
//			reader.setCharactersetName("BIG5");
//            
//            Object[] rowValues;
//            Map<String, Object> map = new HashMap<>();
//			while ((rowValues = reader.nextRecord()) != null) {
//				map = new HashMap<>();
//				for (int i = 0; i < rowValues.length; i++) {
//					Object obj = rowValues[i];
////					System.out.println(String.format("%s, %s, %s, %s", reader.getField(i).getName(),obj,obj.getClass().getName()));
//					if(reader.getField(i).getDataType() == Byte.parseByte(Integer.toString(ColumnFormat.DBF_FIELD_TYPE_C.getValue()))){
//						System.out.println(i+": "+ColumnFormat.DBF_FIELD_TYPE_C);
//					}
//					else if(reader.getField(i).getDataType() == Byte.parseByte(Integer.toString(ColumnFormat.DBF_FIELD_TYPE_L.getValue()))){
//						System.out.println(i+": " + ColumnFormat.DBF_FIELD_TYPE_L);
//					}
//					else if(reader.getField(i).getDataType() == Byte.parseByte(Integer.toString(ColumnFormat.DBF_FIELD_TYPE_N.getValue()))){
//						System.out.println(i+": " + ColumnFormat.DBF_FIELD_TYPE_N);
//					}
//					else if(reader.getField(i).getDataType() == Byte.parseByte(Integer.toString(ColumnFormat.DBF_FIELD_TYPE_F.getValue()))){
//						System.out.println(i+": " + ColumnFormat.DBF_FIELD_TYPE_F);
//					}
//					else if(reader.getField(i).getDataType() == Byte.parseByte(Integer.toString(ColumnFormat.DBF_FIELD_TYPE_D.getValue()))){
//						System.out.println(i+": " + ColumnFormat.DBF_FIELD_TYPE_D);
//					}
//					else if(reader.getField(i).getDataType() == Byte.parseByte(Integer.toString(ColumnFormat.DBF_FIELD_TYPE_M.getValue()))){
//						System.out.println(i+": " + ColumnFormat.DBF_FIELD_TYPE_M);
//					}
//					map.put(reader.getField(i).getName(), rowValues[i]);
//				}
//				data.add(map);
//			}
//		}
//		catch(Exception e){
//			LogUtils.printException(logger, "File Input Stream Error While reading DBF", e);
//			throw new RuntimeException(e);
//		}
//	}
	
	
	@Test
	public void testColumnFormatMapping(){
		System.out.println(JavaDBFUtils.getDBFFormatByJDBCFormat("JDBC_VARCHAR"));
	}
	
    public  static void main(String[] arg) throws Exception
    {
    	new DBFToJDBCTest().testDBFRead();
    }

	@Test
	@Transactional
	public void testDBFRead(){
		System.out.println(Long.parseLong("3000001067"));
//		String fileRoot = "D:\\Maxim_test\\Infrasys_Dbf\\"; //6804\\M6804_161214_orders.dbf
//
//		String filePath = JavaDBFUtils.getFilePathByScheme(fileRoot, "6804", "orders", DateUtil.parse("20161214", "yyyyMMdd"));
		
		String filePath= "C:/UserData/M5123_000000_ORDERSEX.DBF";
//		String toDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61807;" +  
//	            "databaseName=hopos;user=sa;password=P@ssw0rd"; 
//		
		String toDS = "jdbc:sqlserver://10.10.31.73:1433;" +  
	            "databaseName=esb_uat;user=esb_uat;password=P@ssw0rd"; 
		
		
		CommonCriteria criteria = new CommonCriteria(1677L);
		
//		List<SchemeInfo> schemeInfos = pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
		SchemeInfo schemeInfo = new SchemeInfo();
		schemeInfo.setDestination("orders_extra");
		System.out.println(schemeInfo.getPollSchemeType());
		System.out.println(schemeInfo.getClientType());
		
		System.out.println(schemeInfo.getSchemeTableColumns().size());
		schemeInfo.setSchemeTableColumns(new ArrayList<SchemeTableColumn>());

//		try(Connection destinationConnection = applicationSettingService.getCurrentJDBCConnection()){
		try(	
			Connection destinationConnection = DriverManager
					.getConnection(toDS)){

			JavaDBFUtils.bulkCopyFromDBFToSQL(filePath, destinationConnection, schemeInfo, 1000, null,JDBCUtils.CONV_NONE);
		}
		catch(Exception e){
			e.printStackTrace();
		}
			
	}

//	@Test
//	public void testReadFileName(){
//		String fileName = "M6804_161214_orders.dbf";
//		
//		String[] str = JavaDBFUtils.getTableFromFileName(fileName);
//		for(int i=0; i < str.length; i++){
//			System.out.println(str[i]);
//		}
//	}
	
//	@Test
//	public void testExportDBF(){
//		
//		String fileRoot = "D:\\Maxim_test\\Infrasys_Dbf\\";
//		String fileName = JavaDBFUtils.getFilePathByScheme(fileRoot, "1234", "orders", new Date());
//		System.out.println(fileName);
//		
//		String fromDS = "jdbc:sqlserver://enlightening-it.eatuo.com:61807;" +  
//	            "databaseName=hopos;user=sa;password=P@ssw0rd"; 
//		
//		List<SchemeInfo> schemeInfos = pollSchemeInfoService.findSchemeInfoBySchemeTypeAndClientType(PollSchemeType.SALES_REALTIME, ClientType.DBF);
//		
//		SchemeInfo schemeInfo = schemeInfos.get(1);
//		System.out.println(schemeInfo.getPollSchemeType());
//		System.out.println(schemeInfo.getClientType());
//		
//		Hibernate.initialize(schemeInfo);
//		System.out.println(schemeInfo.getSchemeTableColumns().size());
//		String source = schemeInfo.getDestination();
//		String destination = schemeInfo.getSource();
//		
//		try{
//			JavaDBFUtils.bulkCopyFromSQLToDBF(fromDS, fileName, source, destination, schemeInfo.getSchemeTableColumns(), null, null);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	@Test
	public void readDBFFields(){
		try (FileInputStream fis = new FileInputStream("D:\\Maxim_test\\Infrasys_Dbf\\6804\\M6804_161214_orders.dbf")) {

			DBFReader reader = new DBFReader(fis);
			reader.setCharactersetName("BIG5");

			for(int i = 0; i < 41; i++){
				DBFField field = reader.getField(i);
				System.out.println("name=" + field.getName() + 
						": dataType=" + ColumnFormat.touch(Integer.parseInt(Byte.toString(field.getDataType()))) + 
						": length=" + field.getFieldLength());
			}
		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}
}
