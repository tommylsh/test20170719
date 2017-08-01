package com.maxim.pos.sales.service;

import org.slf4j.Logger;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.SchemeScheduleJob;

public interface FileCopyService {
	public void fileCopy(BranchScheme branchScheme, Logger logger);
	public SchemeJobLog updatePollBrachSchemeList(SchemeScheduleJob scheduleJob, SchemeJobLog schemeJobLog);
	public void fileCopyOneTarget(BranchScheme branchScheme, Logger logger) ;
	
}
