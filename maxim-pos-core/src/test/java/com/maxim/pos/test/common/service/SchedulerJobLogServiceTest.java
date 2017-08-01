package com.maxim.pos.test.common.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.persistence.ScheduleJobDao;
import com.maxim.pos.common.service.SchedulerJobLogService;
import com.maxim.pos.common.service.SchemeQuartzTaskExecutor;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.test.common.BaseTest;


public class SchedulerJobLogServiceTest extends BaseTest {

    @Autowired
    private SchedulerJobLogService schedulerJobLogService;
    
    @Autowired
    SchemeQuartzTaskExecutor schemeQuartzTaskExecutor ;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Test
    public void testAddOrUpdateSchemeJobLog(){
/*        SchemeJobLog schemeJobLog = new SchemeJobLog();
        schemeJobLog.setId(123456789L);
        schemeJobLog.setEndTime(new Date());
        schemeJobLog.setScheduleJobId(6554879L);
        schemeJobLog.setStatus(JobProcessStatus.COMPLETE);
        schemeJobLog.setCreateTime(new Date());
        schemeJobLog.setCreateUser("admin");
        schemeJobLog.setLastUpdateTime(new Date());
        schemeJobLog.setLastUpdateUser("admin");
        schedulerJobLogService.addOrUpdateSchemeJobLog(schemeJobLog); */
    	SchemeScheduleJob scheduleJob = scheduleJobDao.findByKey(36L);
    	schemeQuartzTaskExecutor.execute(scheduleJob);
    	
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Test
    public  void testFindLatestSchemeJobLog(){
        SchemeJobLog schemeJobLog =  schedulerJobLogService.findLatestSchemeJobLog(123L);
        LogUtils.printLog(logger, "id===={}: process complete", schemeJobLog.getId());
    }

    @Test
    public  void testFindSchemeJobLogByCriteria(){
        CommonCriteria  commonCriteria = new CommonCriteria();
        commonCriteria.setEntityId(8L);
         List<SchemeJobLog> list = schedulerJobLogService.findSchemeJobLogByCriteria(commonCriteria);
        LogUtils.printLog(logger,"list.size()==={}",list.size());
    }

}
