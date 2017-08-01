package com.maxim.pos.common.service;

import java.util.List;

import org.slf4j.Logger;

import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.value.CommonCriteria;

public interface SchedulerJobLogService {
	public List<SchemeJobLog> findSchemeJobLogByCriteria(CommonCriteria criteria);
	public SchemeJobLog findLatestSchemeJobLog(Long schedulerJobId);
	public SchemeJobLog addOrUpdateSchemeJobLog(String lockType, SchemeJobLog schemeJobLog);
    public SchemeJobLog accquireSchemeJob(SchemeScheduleJob scheduleJob, Integer avaiable, Integer poolSize) ;
    public SchemeJobLog checkJobLog(SchemeScheduleJob scheduleJob, long interval, Logger logger) ;
    public void higherEodPriority(String branchCode);
    public void insertSchemeJobLog(SchemeJobLog schemeJobLog) ;

}
