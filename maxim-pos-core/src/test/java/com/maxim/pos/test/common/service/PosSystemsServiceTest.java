package com.maxim.pos.test.common.service;

import java.util.Calendar;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeInfo;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.entity.SchemeTableColumn;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.persistence.ScheduleJobDao;
import com.maxim.pos.common.service.BranchMasterService;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.service.PollEodControlService;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.service.PosSystemService;
import com.maxim.pos.sales.service.MasterService;
import com.maxim.pos.test.common.BaseTest;

public class PosSystemsServiceTest extends BaseTest {
	@Autowired
	private PosSystemService posSystemService;

	@Autowired
	private MasterService masterService;
	
	@Autowired
	private PollSchemeInfoService pollSchemeInfoService;
	
	@Autowired
	private PollBranchSchemeService pollBranchSchemeService;
	
	@Autowired
    private ScheduleJobDao scheduleJobDao;
	
    @Autowired
    private BranchMasterService branchMasterService;
    
    @Autowired
    private PollEodControlService pollEodControlService;
    
//	@Test
//	public void testProcessBranchScheme() {
////		Assert.assertNull(posSystemService);
//		boolean bl = posSystemService.checkEodComplete("1282", Calendar.getInstance().getTime());
//		Assert.assertFalse(bl);
//	}
	
	@Test
	public void testMaster(){
//		Assert.assertNull(masterService);branchInfo:8,21,22
		SchemeScheduleJob schemeScheduleJob = scheduleJobDao.findById(22L);
		List<BranchScheme> branchSchemes = pollBranchSchemeService.getBranchSchemeByScheduleJob(schemeScheduleJob);
		for (BranchScheme branchScheme : branchSchemes) {
			branchScheme.setSchemeScheduleJob(schemeScheduleJob);
			System.out.println("branchScheme: " + branchScheme.getId());
			masterService.processStagingToPos(branchScheme, logger);
		}
		
	}
	
	@Test
	public void testMasterCopy(){
		
		BranchScheme branchScheme = new BranchScheme();
		BranchInfo branchInfo = new BranchInfo();
		BranchMaster branchMaster = new BranchMaster();
		
		branchMaster.setBranchCode("XXXX");
		branchInfo.setClientHost("10.20.30.166");
		branchInfo.setClientDB("export/dest");
		branchInfo.setUser("poll");
		branchInfo.setPassword("poll12345");
		branchScheme.setBranchInfo(branchInfo);
		branchScheme.setBranchMaster(branchMaster);
		branchScheme.setDirection(Direction.FILE_POINTSOFT);
		masterService.processFolderCopy(branchScheme, null, logger);
		
	}
	
	@Test
	public void testFindSchemeInfo() {
		List<SchemeInfo> schemeInfoList = pollSchemeInfoService
				.findSchemeInfoBySchemeTypeAndClientType("MASTER",
						ClientType.SQLSERVER);
		
			for (int i = 0; i < schemeInfoList.size(); i++) {
				SchemeInfo schemeInfo = schemeInfoList.get(i);
				List<SchemeTableColumn> schemeTableColumns = schemeInfo
						.getSchemeTableColumns();
				System.out.println(schemeInfo.getId() + ": schemeTableColumns: " + schemeTableColumns);
			}
		
	}
	
	@Test
	public void testDelimiter() {
		String delimiter = ",";
		if(delimiter.toCharArray()[0] == ',') {
			System.out.println("yes");
		}
	}
	
	@Test
	public void testMasterToStagingByCsv(){
		BranchMaster branchMaster = branchMasterService.getBranchMaster("5014");
		BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.MASTER, Direction.MST_TO_STG, ClientType.SQLSERVER, branchMaster.getBranchCode());
		masterService.processMasterServerToStaging(branchScheme, null, logger);
	}
	
	@Test
	public void testMasterToStagingByDbf(){
		BranchMaster branchMaster = branchMasterService.getBranchMaster("5014");
		BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.MASTER, Direction.MST_TO_STG, ClientType.SQLSERVER, branchMaster.getBranchCode());
		masterService.processMasterServerToStaging(branchScheme, null, logger);
	}
	
	@Test
	public void testPollEodControl(){
		BranchMaster branchMaster = branchMasterService.getBranchMaster("6666");
		BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_EOD, Direction.STG_TO_EDW, ClientType.ORACLE, branchMaster.getBranchCode());
//		boolean bl = pollEodControlService.findConvertLogByBusinessDate(branchScheme);
//		Assert.assertTrue(bl);
	}
	
	@Test
	public void testGetBranchScheme(){
		BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.MASTER, Direction.STG_TO_POS, ClientType.SQLPOS, "6666");
		Assert.assertNotNull(branchScheme);
	}
}
