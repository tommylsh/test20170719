package com.maxim.pos.common.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.JobProcessStatus;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.persistence.ApplicationSettingDao;
import com.maxim.pos.common.persistence.SchemeScheduleJobDao;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.util.PollThreadPoolExecutor;
import com.maxim.pos.sales.service.FileCopyService;
import com.maxim.util.DateUtil;

@Service("schemeQuartzTaskExecutor")
public class SchemeQuartzTaskExecutor {
    public static final Logger LOGGER = LoggerFactory.getLogger(SchemeQuartzTaskExecutor.class);
    //	private static ExecutorService EXECUTOR_SERVICE;
    private static Map<String,PollThreadPoolExecutor>  poolMap = new HashMap<String,PollThreadPoolExecutor>();
    
    private static String STATE_LOCKING	= "LOCKING";
    private static String STATE_IDLE	= "IDLE";
    
    private static Map<Long,String>  controlMap = new HashMap<Long,String>();
    private static Map<Long,Timestamp>  controlTimestampMap = new HashMap<Long,Timestamp>();

    private static int poolSize = 100;
//    @Autowired
//    private PollBranchSchemeService pollBranchSchemeService;
    @Autowired
    private SchedulerJobLogService schedulerJobLogService;
    
    @Autowired
    private TaskJobLogService taskJobLogService;
    
    @Autowired
    private SchemeScheduleJobDao schemeScheduleJobDao;
//    @Autowired
//    private PosSystemService posSystemService;
    @Autowired
    private ApplicationSettingService applicationSettingService;
    
    @Autowired
    private FileCopyService fileCopyService;
    
    
    
//    @Autowired
//	private SchemeInfoDao  schemeInfoDao;
	
//    @Autowired
//    private TaskJobLogDao taskJobLogDao;
//	
	private @Value("${system.eod.batchProcessSize}") int eodBatchProcessSize;
	private @Value("${system.realTime.batchProcessSize}") int realTimeBatchProcessSize;
	private @Value("${system.master.batchProcessSize}") int masterBatchProcessSize;
	private @Value("${system.octopus.batchProcessSize}") int octopusBatchProcessSize;
	private @Value("${system.other.batchProcessSize}") int otherBatchProcessSize;
	
	private @Value("${system.eod.activeProcessSize}") int eodActiveProcessSize;
	private @Value("${system.realTime.activeProcessSize}") int realTimeActiveProcessSize;
	private @Value("${system.master.activeProcessSize}") int masterActiveProcessSize;
	private @Value("${system.octopus.activeProcessSize}") int octopusActiveProcessSize;
	private @Value("${system.other.activeProcessSize}") int otherActiveProcessSize;
	
	private @Value("${system.eod.taskTimeout}")			int eodTaskTimeout;
	private @Value("${system.realTime.taskTimeout}")	int realTimeTaskTimeout;
	private @Value("${system.master.taskTimeout}")		int masterTaskTimeout;
	private @Value("${system.octopus.taskTimeout}")		int octopusTaskTimeout;
	private @Value("${system.other.taskTimeout}")		int otherTaskTimeout;
	
	private @Value("${system.octopus.mode}")			String octopusMode;


    @PostConstruct
    public void init() throws Exception {
    	File file = new File(".");
    	System.out.println("*********************************************************");
    	System.out.println(file.getAbsolutePath());
    	System.out.println("*********************************************************");
        ApplicationSetting applicationSetting = applicationSettingService
                .findApplicationSettingByCode("BRANCH_EXECUTE_THREADPOOL_SIZE");
//        int poolSize = 100;
        if (applicationSetting != null) {
            String pool = applicationSetting.getCodeValue();
            try {
                poolSize = Integer.parseInt(pool);
            } catch (Exception e) {
                LOGGER.error("BRANCH_EXECUTE_THREADPOOL config Error,must number", e);
            }
        }
        
//        pool = new PollThreadPoolExecutor(poolSize, poolSize, 50,
//                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    }

    public void execute(SchemeScheduleJob scheduleJob) {
    	execute(scheduleJob, false);
    }
    public void execute(SchemeScheduleJob scheduleJob, boolean rerun) {

        boolean locking = false ;
        boolean scheulded = false ;

        Logger logger = null;
        SchemeJobLog schemeJobLog = null;
        List<Future<Boolean>> futures = null ;
        PollThreadPoolExecutor pool = null ;
        Map<Future<Boolean>, BranchSchemeExecutor> schemeMap = new HashMap<Future<Boolean>, BranchSchemeExecutor>();

        String lockType = ApplicationSettingDao.MAIN_LOCK ;
        int batchProcessSize = otherBatchProcessSize ;
        int activeProcessSize = otherActiveProcessSize ;
    	int timeout = otherTaskTimeout ;
        
        try {
            switch (scheduleJob.getPollSchemeType()) {
                case SALES_REALTIME:
                    logger = LogUtils.SALES_REALTIME_LOGGER;
                    batchProcessSize = realTimeBatchProcessSize ;
                    activeProcessSize = realTimeActiveProcessSize ;
                	timeout = realTimeTaskTimeout ;
                    break;
                case SALES_EOD:
                    logger = LogUtils.SALES_EOD_LOGGER;
                    batchProcessSize = eodBatchProcessSize ;
                    activeProcessSize = eodActiveProcessSize ;
                	timeout = eodTaskTimeout ;
                    break;
                case MASTER:
                    logger = LogUtils.MASTER_LOGGER;
                    batchProcessSize = masterBatchProcessSize ;
                    activeProcessSize = masterActiveProcessSize ;
                	timeout = masterTaskTimeout ;
                    break;
                case SMTP:
                    logger = LogUtils.MASTER_LOGGER;
                    break;
                case OCT_TO_POS:
                    logger = LogUtils.FILE_COPY_LOGGER;
                    batchProcessSize = octopusBatchProcessSize ;
                    activeProcessSize = octopusActiveProcessSize ;
                	timeout = octopusTaskTimeout ;
                    break;
                default:
                    logger = LOGGER;
                    break;
            }
            LogUtils.setCurrentThreadLogger(logger);
            
            SchemeScheduleJob curScheduleJob = schemeScheduleJobDao.getById(scheduleJob.getId());
            
            if (curScheduleJob == null || !curScheduleJob.isEnable()) {
                LogUtils.printLog(logger,
                        "job is not enable : jobId={},jobName={},pollSchemeType={},pollSchemeDirection={}",
                        scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
                        scheduleJob.getPollSchemeDirection());
                return;
            }
            
            if (activeProcessSize == 0)
            {
            	activeProcessSize = batchProcessSize ;
            }

        	Timestamp currentTimestamp = DateUtil.getCurrentTimestamp();
        	Timestamp lastTimestamp = controlTimestampMap.get(scheduleJob.getId());
        	long interval = 1000 * 60 * 5 ;
        	if (lastTimestamp != null)
        		interval = currentTimestamp.getTime() - lastTimestamp.getTime() ;
        	if (rerun)
        	{
        		interval = -1;
        	}

            synchronized(controlMap)
            {
            	String state = controlMap.get(scheduleJob.getId());
            	if (state== null)
            	{
            		state = STATE_IDLE ;
            	}
            	if (STATE_LOCKING.equals(state))
            	{
                    LogUtils.printLog(logger,
                            "Last Job is Running : jobId={},jobName={},pollSchemeType={},pollSchemeDirection={},state={},lastTimestamp={},interval={}",
                    scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
                    scheduleJob.getPollSchemeDirection(),state,lastTimestamp,interval);
                    return;
            	}
                if (!rerun)
                {
                	controlTimestampMap.put(scheduleJob.getId(), currentTimestamp);
                }
            	controlMap.put(scheduleJob.getId(), STATE_LOCKING);
            	locking = true ;
            	
                LogUtils.printLog(logger,
                        "job execute start... jobId={},jobName={},pollSchemeType={},pollSchemeDirection={},state={},lastTimestamp={},interval={}",
                        scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
                        scheduleJob.getPollSchemeDirection(),state,lastTimestamp,interval);
            }
            
            
            pool = poolMap.get(scheduleJob.getPollSchemeType().name()) ;
            if (pool == null)
            {
                pool = new PollThreadPoolExecutor(activeProcessSize, batchProcessSize, timeout,
                        TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
                
                poolMap.put(scheduleJob.getPollSchemeType().name(),pool);
            }
            
            schemeJobLog = null;
            int numberOfRecordProcessed = 0;
            List<BranchScheme> branchSchemes = null ;
            if (scheduleJob.getPollSchemeType().equals(PollSchemeType.SMTP) ||
            		scheduleJob.getPollSchemeType().equals(PollSchemeType.OCT_TO_POS) ||
            		scheduleJob.getPollSchemeType().equals(PollSchemeType.REPORT) )
            {
                LogUtils.printLog(logger, "submit branchScheme process: {} octopusMode {}", scheduleJob.getPollSchemeType().toString(), octopusMode);

                schemeJobLog = schedulerJobLogService.checkJobLog(scheduleJob, interval, logger);
                if (schemeJobLog == null) {
                    return;
                }

                BranchScheme branchScheme = new BranchScheme();
                branchScheme.setPollSchemeType(scheduleJob.getPollSchemeType());
                branchScheme.setDirection(scheduleJob.getPollSchemeDirection());
                branchScheme.setSchemeScheduleJob(scheduleJob);
                branchScheme.setSchemeJobLog(schemeJobLog);
                
                if (scheduleJob.getPollSchemeType().equals(PollSchemeType.OCT_TO_POS) && octopusMode.equals("MULTI"))
                {
                	fileCopyService.updatePollBrachSchemeList(scheduleJob, schemeJobLog);
                	branchSchemes = schemeJobLog.getBranchSchemeList();
                }
                else
                {
	                branchSchemes = new ArrayList<BranchScheme>(1);
	                branchSchemes.add(branchScheme);
                }
            }
            else
            {
            	lockType = scheduleJob.getPollSchemeType().name();
            	int aCount =  pool.getActiveCount() ;
            	int qCount =  pool.getQueue().size() ;
                LogUtils.printLog(logger, "Pool Size {} / {} / {} / {}",aCount , qCount, pool.getPoolSize() ,batchProcessSize);
                if (batchProcessSize - aCount - qCount <= 0)
                {
                    LogUtils.printLog(logger, "Pool is Full, exit {} {} / {} /{} ",aCount ,qCount, pool.getPoolSize() ,batchProcessSize);
                    return ;
                }
                schemeJobLog = schedulerJobLogService.accquireSchemeJob(scheduleJob, batchProcessSize - aCount - qCount, batchProcessSize);
                if (schemeJobLog == null) {
                    return;
                }
            	
                branchSchemes = schemeJobLog.getBranchSchemeList();
            }
            futures = new ArrayList<Future<Boolean>>(branchSchemes.size());
            for (BranchScheme branchScheme : branchSchemes) {
            	BranchSchemeExecutor branchSchemeExecutor = new BranchSchemeExecutor();
            	branchScheme.setSchemeScheduleJob(scheduleJob);
                branchSchemeExecutor.setSchemeJobLog(schemeJobLog);
                branchSchemeExecutor.setBranchScheme(branchScheme);
                branchSchemeExecutor.setLogger(logger);
                
                branchScheme.setSchemeJobLog(schemeJobLog);

                Future<Boolean> fReturn = pool.submit(branchSchemeExecutor, Boolean.TRUE);
                futures.add(fReturn);
                numberOfRecordProcessed++;
                
                schemeMap.put(fReturn, branchSchemeExecutor);
            }
        	int aCount =  pool.getActiveCount() ;
        	int qCount =  pool.getQueue().size() ;
            LogUtils.printLog(logger, "Pool Size {} / {} / {} / {}",aCount , qCount, pool.getPoolSize() ,batchProcessSize);
            
            schemeJobLog.setStartTime(new Date());
            schemeJobLog.setNumberOfRecordProcessed(numberOfRecordProcessed);
            schemeJobLog.setStatus(JobProcessStatus.PROGRESS);

            LogUtils.printLog(logger, "job execute end... jobId={},jobName={},pollSchemeType={},pollSchemeDirection={}",
                    scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
                    scheduleJob.getPollSchemeDirection());
            scheulded = true ;
        } catch (Exception e) {
            LogUtils.printException(logger, "job execte exception:", e);
            if (schemeJobLog != null) {
				schemeJobLog.setEndTime(new Date());
				schemeJobLog.setStatus(JobProcessStatus.FAILED);
            }
            return ;
        }
        finally
        {
            if (!scheulded ||
            		(!scheduleJob.getPollSchemeType().equals(PollSchemeType.SMTP) &&
            		!scheduleJob.getPollSchemeType().equals(PollSchemeType.REPORT) ))
            {

	            synchronized(controlMap)
	            {
	            	if (locking)
	            	{
	            		controlMap.put(scheduleJob.getId(), STATE_IDLE);
	            	}
	            }
            }
            if (schemeJobLog != null) {
				schedulerJobLogService.addOrUpdateSchemeJobLog(lockType, schemeJobLog);
            }
    	}

        boolean done = false ;
        try
        {
            LogUtils.printLog(logger, "job wait start ... jobId={},jobName={},pollSchemeType={},pollSchemeDirection={},futures size={}",
                    scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
                    scheduleJob.getPollSchemeDirection(),futures.size());
        	long start = System.currentTimeMillis();
	        for (Future<Boolean> fReturn : futures)
	        {
	        	long current = System.currentTimeMillis() - start ;
				try {
//		            LogUtils.printLog(logger, "job wait for {} ... jobId={},jobName={},pollSchemeType={},pollSchemeDirection={}",
//		            		current, scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
//		                    scheduleJob.getPollSchemeDirection());						
		            fReturn.get(timeout * 1000 - current, TimeUnit.MILLISECONDS);
				} catch (TimeoutException e) {
		            LogUtils.printLog(logger, "job wait timout ... jobId={},jobName={},pollSchemeType={},pollSchemeDirection={}",
		                    scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
		                    scheduleJob.getPollSchemeDirection());				
                    BranchSchemeExecutor branchSchemeExecutor = schemeMap.get(fReturn);
					if (!fReturn.cancel(true))
					{
	                    if (branchSchemeExecutor != null)
	                    {
	                    	LogUtils.printLog(logger, "job cannot cancel fail {} ... ",branchSchemeExecutor.getBranchScheme().getBranchMaster().getBranchCode());
	                    }
	                    else
	                    {
	                    	LogUtils.printLog(logger, "job cannot cancel fail {} ... ");
	                    }
					}
					
                    if (branchSchemeExecutor != null)
                    {
                    	pool.remove(branchSchemeExecutor);
                    	
                        TaskJobLog currentTaskLog = branchSchemeExecutor.getBranchScheme().getTaskLog() ;
                        if (currentTaskLog != null)
                        {
                        	taskJobLogService.updateTaskJobLogForTimeout(currentTaskLog.getId());
                        }
                    }
					
				}
	        }
            LogUtils.printLog(logger, "job wait end ... jobId={},jobName={},pollSchemeType={},pollSchemeDirection={}",
                    scheduleJob.getId(), scheduleJob.getJobName(), scheduleJob.getPollSchemeType(),
                    scheduleJob.getPollSchemeDirection());
            
            schemeJobLog.setStatus(JobProcessStatus.COMPLETE);
			if (schemeJobLog != null) {
				schemeJobLog.setEndTime(new Date());
				schedulerJobLogService.addOrUpdateSchemeJobLog(lockType, schemeJobLog);
			}
			done = true ;
		} catch (InterruptedException | ExecutionException e) {
            LogUtils.printException(logger, "job wait exception:", e);
			if (schemeJobLog != null) {
				schemeJobLog.setEndTime(new Date());
				schemeJobLog.setStatus(JobProcessStatus.FAILED);
				schedulerJobLogService.addOrUpdateSchemeJobLog(lockType, schemeJobLog);
			}
			done = true ;
		}
        finally
        {
            if (scheduleJob.getPollSchemeType().equals(PollSchemeType.SMTP) ||
            		scheduleJob.getPollSchemeType().equals(PollSchemeType.REPORT) )
            {

	            synchronized(controlMap)
	            {
	            	if (locking)
	            	{
	            		controlMap.put(scheduleJob.getId(), STATE_IDLE);
	            	}
	            }
            }
            if (!done)
        	{
    			if (schemeJobLog != null) {
    				schemeJobLog.setEndTime(new Date());
    				schemeJobLog.setStatus(JobProcessStatus.FAILED);
    				schedulerJobLogService.addOrUpdateSchemeJobLog(lockType, schemeJobLog);
    			}
        	}
        }

    }
 

}
