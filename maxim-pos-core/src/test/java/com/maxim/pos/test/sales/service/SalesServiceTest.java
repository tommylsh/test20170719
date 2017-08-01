package com.maxim.pos.test.sales.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.persistence.ApplicationSettingDao;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.service.ScheduleJobService;
import com.maxim.pos.common.service.SpringBeanUtil;
import com.maxim.pos.sales.service.SalesService;
import com.maxim.pos.test.common.BaseTest;

public class SalesServiceTest extends BaseTest {

//    @Autowired
//    private SalesServiceOld salesService;

    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;

    @Autowired
    private ScheduleJobService scheduleJobService;
    
    @Autowired
    private ApplicationSettingDao applicationSettingDao ;

//    @Test
//    public void testProcessPosToStaging() {
//        SchemeScheduleJob schemeScheduleJob = scheduleJobService.getTaskById(1L);
//        List<BranchScheme> list = pollBranchSchemeService.getBranchSchemeByScheduleJob(schemeScheduleJob);
//        for (BranchScheme branchScheme : list) {
//            branchScheme.setSchemeScheduleJob(schemeScheduleJob);
//            salesService.processPosDataToStg(branchScheme, logger);
//        }
//    }
    
    @Test
    public void acquireTaskJobLog() throws InterruptedException 
    {
    	Thread[] threads = new Thread[10];
    	for (int i = 0 ; i < 10 ; i++)
    	{
    		Thread t = new Thread()
    				{
    			          public void run() {
    			        	 
    			        	  applicationSettingDao.acquireTaskJobLog();
    			          }

    				};
    				
    				threads[i] = t;
    				t.start();
    				

    	}
    	
    	for (Thread t:threads)
    	{
    		t.join();
    	}
    	
    }

    	
    @Test
    public void testProcessPosToStagingSQL() throws InterruptedException {
//        SchemeScheduleJob schemeScheduleJob = scheduleJobService.getTaskById(1L);
//        List<BranchScheme> list = pollBranchSchemeService.getBranchSchemeByScheduleJob(schemeScheduleJob);
//        for (BranchScheme branchScheme : list) {
//            branchScheme.setSchemeScheduleJob(schemeScheduleJob);
//            salesService.processPosDataToStg(branchScheme, logger);
//        }
//        BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_REALTIME,Direction.POS_TO_STG, ClientType.SQLPOS,"1284");
//        ((SalesService)SpringBeanUtil.context.getBean("sqlSalesService")).processPosDataToStg(branchScheme, logger);
    	
    	try
    	{
//            BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_EOD,Direction.POS_TO_STG, ClientType.DBF,"5120");
//            ((SalesService)SpringBeanUtil.context.getBean("dbfSalesService")).processPosDataToStg(branchScheme, logger);
//            BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_EOD,Direction.POS_TO_STG, ClientType.CSV,"4041");
//            ((SalesService)SpringBeanUtil.context.getBean("csvSalesService")).processPosDataToStg(branchScheme, logger);
            BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_REALTIME,Direction.POS_TO_STG, ClientType.SQLPOS,"4117");
//            branchScheme.setReRun(true);
//            branchScheme.setBusinessDate(Date.valueOf("2017-04-25"));
            ((SalesService)SpringBeanUtil.context.getBean("sqlSalesService")).processPosDataToStg(branchScheme, null ,logger);
    	}
    	finally
    	{
    		Thread.sleep(10000);
    	}
    }
    
   
//
//    @Test
//    public void testCopyBySQL() {
//        SchemeScheduleJob schemeScheduleJob = scheduleJobService.getTaskById(1L);
//        List<BranchScheme> list = pollBranchSchemeService.getBranchSchemeByScheduleJob(schemeScheduleJob);
//        for (BranchScheme branchScheme : list) {
//            branchScheme.setSchemeScheduleJob(schemeScheduleJob);
//            if (branchScheme.getBranchInfo().getClientType().equals(ClientType.SQLSERVER)) {
//                salesService.processPosDataToStg(branchScheme, logger);
//            }
//        }
//    }
//
//    @Test
//    public void testCopyByDBF() {
//        SchemeScheduleJob schemeScheduleJob = scheduleJobService.getTaskById(1L);
//        List<BranchScheme> list = pollBranchSchemeService.getBranchSchemeByScheduleJob(schemeScheduleJob);
//        for (BranchScheme branchScheme : list) {
//            branchScheme.setSchemeScheduleJob(schemeScheduleJob);
//            if (branchScheme.getBranchInfo().getClientType().equals(ClientType.DBF)) {
//                salesService.processPosDataToStg(branchScheme, logger);
//            }
//        }
//    }
//
//    @Test
//    public void testCopyByCSV() {
//        SchemeScheduleJob schemeScheduleJob = scheduleJobService.getTaskById(1L);
//        List<BranchScheme> list = pollBranchSchemeService.getBranchSchemeByScheduleJob(schemeScheduleJob);
//        for (BranchScheme branchScheme : list) {
//            branchScheme.setSchemeScheduleJob(schemeScheduleJob);
//            if (branchScheme.getBranchInfo().getClientType().equals(ClientType.CSV)) {
//                salesService.processPosDataToStg(branchScheme, logger);
//            }
//        }
//    }

}
