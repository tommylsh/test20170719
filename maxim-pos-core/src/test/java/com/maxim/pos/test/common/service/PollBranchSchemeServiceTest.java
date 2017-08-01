package com.maxim.pos.test.common.service;

import java.util.Date;
import java.util.List;

import com.maxim.pos.common.service.BranchSchemeExecutor;
import com.maxim.pos.common.value.CommonCriteria;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.enumeration.ClientType;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.test.common.BaseTest;


public class PollBranchSchemeServiceTest extends BaseTest{
	@Autowired
	private PollBranchSchemeService pollBranchSchemeService;
	 @Test
		public void testProcessBranchScheme(){

		 BranchScheme bs  =  pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_EOD,Direction.POS_TO_STG,ClientType.SQLPOS,"1284");
		 bs.setEnabled(true);
		  bs.setBusinessDate(new Date());
		 bs.setReRun(true);
		 System.out.println("*******"+bs.getBranchMaster().getBranchCode());
		 BranchSchemeExecutor branchSchemeExecutor = new BranchSchemeExecutor();
		 branchSchemeExecutor.setBranchScheme(bs);
		 branchSchemeExecutor.setLogger(logger);
		 branchSchemeExecutor.run();

		}
	 @Test
		public void testProcessBranchFindBypollBranchScheme(){
//			pollBranchSchemeService.processBranchScheme(1l);
		 	SchemeScheduleJob job = new SchemeScheduleJob();
		 	job.setPollSchemeType(PollSchemeType.valueOf("SALES_REALTIME"));
		 	job.setPollSchemeDirection(Direction.valueOf("POS_TO_STG"));
		 	List<BranchScheme> list = pollBranchSchemeService.getBranchSchemeByScheduleJob(job);
		 	Assert.assertFalse(list.size()<0);
	 }
	 
	 @Test
	 public void testBranchScheme(){
		 BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(PollSchemeType.SALES_REALTIME, Direction.STG_TO_EDW,ClientType.ORACLE,"0000");
		 
		 Assert.assertNotNull(branchScheme);
		 
		 System.out.println(branchScheme.toString()+">>>>>>>>>>>>>>>>>>>");
	 }
	 
}
