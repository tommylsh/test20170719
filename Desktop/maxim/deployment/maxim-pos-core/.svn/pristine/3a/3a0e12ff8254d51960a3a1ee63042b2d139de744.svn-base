package com.maxim.pos.test.common.persistence;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.JobProcessStatus;
import com.maxim.pos.common.enumeration.LatestJobInd;
import com.maxim.pos.common.enumeration.TaskProcessStatus;
import com.maxim.pos.common.persistence.ScheduleJobDao;
import com.maxim.pos.common.persistence.TaskJobLogDao;

import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:pos-core-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskJobLogDaoTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskJobLogDaoTest.class);

    @Autowired
    private TaskJobLogDao taskJobLogDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Before
    public void setUp() throws Exception {
        Assert.assertNotNull(taskJobLogDao);
        Assert.assertNotNull(scheduleJobDao);
    }

    private void setOperationInfo(AbstractEntity entity) {
        Date sysDate = Calendar.getInstance().getTime();
        entity.setCreateTime(sysDate);
        entity.setLastUpdateTime(sysDate);
        entity.setCreateUser("Test");
        entity.setLastUpdateUser("Test");
    }

    @Test
    @Transactional
    @Rollback(false)
    public void testSave() throws Exception {
        TaskJobLog taskJobLog = new TaskJobLog();
        taskJobLog.setLastestJobInd(LatestJobInd.Y);
        taskJobLog.setStatus(TaskProcessStatus.COMPLETE);
        setOperationInfo(taskJobLog);
//        SchemeScheduleJob schemeScheduleJob = scheduleJobDao.getSingle(SchemeScheduleJob.class, 1L);
        SchemeScheduleJob schemeScheduleJob = scheduleJobDao.findById(30L);
        System.out.println("**************************** "+schemeScheduleJob);
        Assert.assertNotNull(schemeScheduleJob);
        taskJobLog.setScheduleJobId(schemeScheduleJob.getId());
//        taskJobLogDao.save(taskJobLog);
        taskJobLogDao.insert(taskJobLog);
        System.out.println(taskJobLog.getId());
    }

    @Test
    @Transactional(readOnly = true)
    public void testGetSingle() throws Exception {
//        TaskJobLog taskJobLog = scheduleJobDao.getSingle(TaskJobLog.class, 9L);
//        
//        Assert.assertNotNull(taskJobLog);
//        LOGGER.info("========== {} ==========", taskJobLog.getTaskJobLogDetails());
    }

}
