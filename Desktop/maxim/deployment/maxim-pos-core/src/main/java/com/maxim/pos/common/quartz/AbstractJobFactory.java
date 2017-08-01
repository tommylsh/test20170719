package com.maxim.pos.common.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.service.ScheduleJobService;
import com.maxim.pos.common.service.SchemeQuartzTaskExecutor;
import com.maxim.pos.common.service.SpringBeanUtil;
import com.maxim.pos.common.util.LogUtils;

public class AbstractJobFactory implements Job {

	protected final static Logger logger = LoggerFactory.getLogger(AbstractJobFactory.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SchemeScheduleJob scheduleJob = (SchemeScheduleJob) context.getMergedJobDataMap()
				.get(ScheduleJobService.SCHEDULE_JOB);
		LogUtils.printLog(logger, "Job start...{}", scheduleJob);
		if (scheduleJob != null) { 
			process(scheduleJob);
		} else {
			LogUtils.printLog(logger, "Could not get scheduleJob from quartz!");
		}
	}

	protected void process(SchemeScheduleJob scheduleJob) {
		SchemeQuartzTaskExecutor taskExecutor = SpringBeanUtil.context.getBean(SchemeQuartzTaskExecutor.class);
		try {
			taskExecutor.execute(scheduleJob);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtils.printException(logger, "execute job excepiton:" + scheduleJob, e);
		}

	}

}