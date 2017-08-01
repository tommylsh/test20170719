package com.maxim.pos.test.common.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.dao.QueryFileHandler;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.service.SchemeTableColumnService;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.common.value.SchemeInfoQueryCriteria;
import com.maxim.pos.sales.service.BranchInfoService;
import com.maxim.pos.test.common.BaseTest;


public class PollSchemeInfoServiceTest extends BaseTest {

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

    @Test
    public void testFindSchemeInfoByCriteria(){
        SchemeInfoQueryCriteria commonCriteria = new SchemeInfoQueryCriteria();
        //commonCriteria.setEntityId(9L);
        commonCriteria.setPollSchemeType("");
        commonCriteria.setClientType(ClientType.SQLSERVER);
        List<SchemeInfo> list = pollSchemeInfoService.findSchemeInfoByCriteria(commonCriteria);
        PollSchemeType pollSchemeType = null;
//        for(SchemeInfo s:list){
//             pollSchemeType = s.getPollSchemeType();
//        }
        LogUtils.printLog(logger,"===={}==={}==",pollSchemeType,list.size());
    }

    @Test
    public void testFindSchemeInfoBySchemeTypeAndClientType(){
        List<SchemeInfo> schemeInfos = pollSchemeInfoService.findSchemeInfoBySchemeTypeAndClientType("SALES_REALTIME", ClientType.SQLSERVER);
        LogUtils.printLog(logger,"===={}====",schemeInfos.size());
    }
    
    @Test
    @Transactional
    public void getTableColumnInfoTest(){
//    	List<Map<String, Object>> columnInfoList = 
//    			pollSchemeInfoService.getTableColumnInfo("maxim_staging", "orders");
//    	
//    	for(Map<String, Object> columnInfo: columnInfoList){
//    		System.out.print(columnInfo.get("seq"));
//    		System.out.print(columnInfo.get("columnName"));
//    		System.out.print(columnInfo.get("columnFormat"));
//    		System.out.print(columnInfo.get("columnLength"));
//    		System.out.print(columnInfo.get("columnPrecision"));
//    		System.out.println();
//    	}
    	
    	
		CommonCriteria criteria = new CommonCriteria(50L);
		criteria.setMaxResult(1);
		SchemeInfo schemeInfo = pollSchemeInfoService.findSchemeInfoByCriteria(criteria).get(0);
		
		System.out.println("Poll Scheme Info ID = " + schemeInfo.getId() + "; table = " + schemeInfo.getDestination());
		List<SchemeTableColumn> schemeTableColumnList = pollSchemeInfoService.generateSchemeTableColumnData(schemeInfo);
    	System.out.println("Scheme Table Column List Size = " + schemeTableColumnList.size());
    	schemeTableColumnService.saveSchemeTableColumns(schemeTableColumnList);
		
    	
    }
    
}