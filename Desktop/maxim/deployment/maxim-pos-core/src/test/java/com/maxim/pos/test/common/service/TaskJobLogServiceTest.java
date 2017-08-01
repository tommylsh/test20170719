package com.maxim.pos.test.common.service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.service.TaskJobLogService;
import com.maxim.pos.common.value.CommonCriteria;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:pos-core-context.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TaskJobLogServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskJobLogServiceTest.class);

    @Autowired
    private TaskJobLogService taskJobLogService;

    @Before
    public void setUp() throws Exception {
        Assert.assertNotNull(taskJobLogService);
    }

    @Test
    @Transactional(readOnly = true)
    public void testFindTaskJobLogByCriteria() throws Exception {
        CommonCriteria criteria = new CommonCriteria();
        // criteria.setEntityId(9L);
        // criteria.setUserId("Test");
        List<TaskJobLog> taskJobLogs = taskJobLogService.findTaskJobLogByCriteria(criteria);
        LOGGER.info("========== {} ==========", taskJobLogs);
    }

    @Test
    @Transactional(readOnly = true)
    public void testFindLatestTaskJobLog() throws Exception {
        BranchScheme branchScheme = new BranchScheme();
        branchScheme.setDirection(Direction.STG_TO_POS);
        branchScheme.setPollSchemeType(PollSchemeType.MASTER);
        branchScheme.setId(42L);
        SchemeScheduleJob schemeScheduleJob = new SchemeScheduleJob();
        schemeScheduleJob.setId(13L);
        branchScheme.setSchemeScheduleJob(schemeScheduleJob);
        TaskJobLog taskJobLog = taskJobLogService.findLatestTaskJobLog(branchScheme);
        LOGGER.info("========== {} ==========", taskJobLog);
    }

}
