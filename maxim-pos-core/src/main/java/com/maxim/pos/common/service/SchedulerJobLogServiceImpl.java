package com.maxim.pos.common.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.SchemeScheduleJob;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.JobProcessStatus;
import com.maxim.pos.common.enumeration.LatestJobInd;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.TaskProcessStatus;
import com.maxim.pos.common.persistence.ApplicationSettingDao;
import com.maxim.pos.common.persistence.SchemeJobLogDao;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.sales.persistence.SchemeInfoDao;
import com.maxim.pos.security.entity.User;
import com.maxim.util.DateUtil;

@Service("schedulerJobLogService")
public class SchedulerJobLogServiceImpl implements SchedulerJobLogService {

	@PersistenceContext
	private EntityManager entityManager;
//	
//	@Autowired
//	private ApplicationContext appContext;

    @Autowired
    private SchemeJobLogDao schemeJobLogDao;
    
	@Autowired
	private SchemeInfoDao  schemeInfoDao;
	
    @Autowired
    TaskJobLogService taskJobLogService ;
    
    @Autowired
    ApplicationSettingDao applicationSettingDao ;
    
	@Resource(name="systemPrincipal")
	private User systemPrincipal;

	private @Value("${system.eod.batchProcessSize}")		int eodBatchProcessSize;
	private @Value("${system.realTime.batchProcessSize}")	int realTimeBatchProcessSize;
	private @Value("${system.master.batchProcessSize}")		int masterBatchProcessSize;
	private @Value("${system.other.batchProcessSize}")		int otherBatchProcessSize;
	private @Value("${system.numberOfMachine}")				int numberOfMachine;
	
	private @Value("${system.eod.taskTimeout}")			int eodTaskTimeout;
	private @Value("${system.realTime.taskTimeout}")	int realTimeTaskTimeout;
	private @Value("${system.master.taskTimeout}")		int masterTaskTimeout;
	private @Value("${system.octopus.taskTimeout}")		int octopusTaskTimeout;
	private @Value("${system.other.taskTimeout}")		int otherTaskTimeout;
	
	public Map<String, Boolean> eodPriorityMap = new HashMap<String, Boolean>();
	
//	@PostConstruct
//    public void init() throws Exception {
//		schedulerJobLogService = (SchedulerJobLogService) AopContext.currentProxy();
//	}
    @Override
    @Transactional(readOnly = true)
    public List<SchemeJobLog> findSchemeJobLogByCriteria(CommonCriteria criteria) {
       return schemeJobLogDao.findSchemeJobLogByCriteria(criteria);
    }

    @Override
    @Transactional(readOnly = true)
    public SchemeJobLog findLatestSchemeJobLog(Long schedulerJobId) {
        return schemeJobLogDao.findLatestSchemeJobLog(schedulerJobId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public SchemeJobLog addOrUpdateSchemeJobLog(String lockType, SchemeJobLog schemeJobLog) {
//    	applicationSettingDao.getApplicationLock();
        return schemeJobLogDao.addOrUpdateSchemeJobLog(schemeJobLog);
    }
    
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public SchemeJobLog accquireSchemeJob(SchemeScheduleJob scheduleJob, Integer avaiable, Integer poolSize){
    	
////    	Integer obj = applicationSettingDao.getApplicationLock(scheduleJob.getPollSchemeType().name());
//    	Integer obj = applicationSettingDao.getApplicationLock();
//    	if (obj == null)
//    	{
//    		String code = "SCHEDULE" ;
//    		ApplicationSetting setting = new ApplicationSetting();
//    		setting.setCode(code);
//    		setting.setCodeDescription("Lock for " + code);
//    		setting.setCodeValue("LOCK");
//    		Auditer.audit(setting);
//    		applicationSettingDao.insert(setting);
//    	}
    	
		String displayMessage = applicationSettingDao.getApplicationValue("SCHEDULE_INFO");

    	applicationSettingDao.getApplicationLock();
    	applicationSettingDao.getApplicationLock(scheduleJob.getPollSchemeType().name());
    	
    	Direction direction				= scheduleJob.getPollSchemeDirection();
    	PollSchemeType pollSchemeType	= scheduleJob.getPollSchemeType();
    	
    	boolean displayTaskInfo			= "DISPLAY".equals(displayMessage) ;

    	long timeout = otherTaskTimeout ;
        if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
        {
        	timeout = eodTaskTimeout ;
        } else if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
        {
        	timeout = realTimeTaskTimeout ;
        } else if (PollSchemeType.MASTER.equals(pollSchemeType))
        {
        	timeout = masterTaskTimeout ;
        	displayTaskInfo = true;
        } else if (PollSchemeType.OCT_TO_POS.equals(pollSchemeType))
        {
        	timeout = octopusTaskTimeout ;
        }

    timeout = timeout * 1000 ;
        
        timeout = timeout * 1000 ;


    	
    	schemeJobLogDao.updateOtherTaskJobLogNotLatest(scheduleJob.getId(), systemPrincipal.getUserId());
//        SchemeJobLog lastSchemeJobLog = schemeJobLogDao.findLatestSchemeJobLog(scheduleJob.getId(), systemPrincipal.getUserId());
//        if (lastSchemeJobLog != null) {
//            LogUtils.printLog("lastSchemeJobLog={} {} {} {}", lastSchemeJobLog.getStatus(), lastSchemeJobLog.getLastUpdateTime() , System.currentTimeMillis() , lastSchemeJobLog.getLastUpdateTime().getTime());
////            if (lastSchemeJobLog.getStatus().equals(JobProcessStatus.PROGRESS) || lastSchemeJobLog.getStatus().equals(JobProcessStatus.PENDING)) {
////				if(System.currentTimeMillis() - lastSchemeJobLog.getLastUpdateTime().getTime() > 3600000) {
////					LogUtils.printLog("scheduleJob={}  PROGRESS Continue 1 Hour", scheduleJob.getId());
////					lastSchemeJobLog.setStatus(JobProcessStatus.FAILED);
////					lastSchemeJobLog.setLastJobInd(LatestJobInd.N);
////					schemeJobLogDao.addOrUpdateSchemeJobLog(lastSchemeJobLog);
////				} else {
////                    LogUtils.printLog("scheduleJob={}  PROGRESS ING...", scheduleJob.getId());
////                    return null;
////                }
////            } else {
////            	lastSchemeJobLog.setLastJobInd(LatestJobInd.N);
////                schemeJobLogDao.addOrUpdateSchemeJobLog(lastSchemeJobLog);
////            }
//        	lastSchemeJobLog.setLastJobInd(LatestJobInd.N);
//            schemeJobLogDao.addOrUpdateSchemeJobLog(lastSchemeJobLog);
//        }

    	Timestamp currentDate = DateUtil.getCurrentTimestamp();
    	
        SchemeJobLog schemeJobLog = new SchemeJobLog();
        
        schemeJobLog.setLastJobInd(LatestJobInd.Y);
        schemeJobLog.setStatus(JobProcessStatus.PENDING);
        schemeJobLog.setStartTime(currentDate);
        schemeJobLog.setScheduleJobId(scheduleJob.getId());
        getschedulerJobLogService().insertSchemeJobLog(schemeJobLog);
    
  	    LogUtils.printLog("{} {} Prepare to get the Latest Branch : ", pollSchemeType, direction );

		List<Map<String, Object>> schemes = schemeInfoDao.findLatestPollBrachScheme(pollSchemeType, direction);

  	    LogUtils.printLog("{} {} Get the Latest Branch : {}", pollSchemeType, direction, schemes.size() );

		List<BranchScheme> branchSchemeList = new ArrayList<BranchScheme>();
        
        long machineCount = 0 ;

        if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
        {
    	    machineCount = schemeJobLogDao.countCreateUserByScheduleJobIdAndCreateUser(scheduleJob.getId(), systemPrincipal.getUserId());
      	    LogUtils.printLog("Active machineCount : {}", machineCount);
	
	        Collections.sort(schemes, new Comparator<Map<String, Object>>()
				{
					@Override
					public int compare(Map<String, Object> map1 ,Map<String, Object> map2) {
			            String branchCode1 = (String) map1.get("branchCode");
			            String branchCode2 = (String) map2.get("branchCode");
						if (eodPriorityMap.containsKey(branchCode1))
							return eodPriorityMap.containsKey(branchCode2) ? 0 : -1 ;
						else
							return eodPriorityMap.containsKey(branchCode2) ? 1 : 0 ;
					}
				}
	        );
        }
        Long currentTime	= System.currentTimeMillis() ;
		LocalDateTime now	= LocalDateTime.now();
        List<String>     branchList = new ArrayList<String>();
        List<TaskJobLog> taskJobLogList = new ArrayList<TaskJobLog>();
        for (Map<String, Object> map : schemes)
        {
            if (branchSchemeList.size() >= avaiable )
            {
            	break;
            }	
            
            
//        	Long id = decID.longValue() ;
//        	BranchScheme branchScheme = schemeInfoDao.getSingle(BranchScheme.class, id);
        	
        	BigDecimal pollBranchSchemeId = (BigDecimal) map.get("pollBranchSchemeId"); 
        	String pollSchemeName = (String) map.get("pollSchemeName");
        	String pollSchemeDesc = (String) map.get("pollSchemeDesc");
        	
        	BigDecimal pollBranchInfoId = (BigDecimal) map.get("pollBranchInfoId"); 
        	
        	Byte schemeEnabled = (Byte) map.get("schemeEnabled"); 
        	Byte infoEnabled = (Byte) map.get("infoEnabled"); 
        	Timestamp startTime = (Timestamp) map.get("startTime");
        	Timestamp endTime = (Timestamp) map.get("endTime");
            String branchCode = (String) map.get("branchCode");
            BigDecimal lastJobLogId = (BigDecimal) map.get("lastJobLogId");
            BigDecimal taskJobLogId = (BigDecimal) map.get("taskJobLogId");
            BigDecimal taskJobLogId2 = (BigDecimal) map.get("taskJobLogId2");
            String lastStatus = (String) map.get("lastStatus");
            String taskStatus = (String) map.get("status");
            String taskStatus2 = (String) map.get("status2");
            Timestamp taskLastUpdateTime = (Timestamp) map.get("lastUpdateTime");
            
            if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
            {
	            if (branchSchemeList.size() >= (eodPriorityMap.size() / machineCount) + 1)
	            {
	    			if (eodPriorityMap.containsKey(branchCode))
	    			{
	    				continue ;
	    			}
	            }
            }
	            
			eodPriorityMap.remove(branchCode);

        	
//            if (!branchScheme.isEnabled()) {
            if (schemeEnabled == 0) {
            	if (displayTaskInfo) 
            	{
            		LogUtils.printLog("branchScheme is not enable :{}=={}=={}",
                		pollBranchSchemeId, pollSchemeType, direction);
//                		branchScheme.getId(), branchScheme.getPollSchemeName(),branchScheme.getPollSchemeType());
            	}
                continue;
//            } else if (!branchScheme.getBranchInfo().isEnable()) {
            } else if (infoEnabled == 0) {
            	if (displayTaskInfo) 
            	{
            		LogUtils.printLog("BranchInfo is not enable :{}=={}",
                		pollBranchSchemeId, pollSchemeType, direction);
//                		branchScheme.getBranchInfo().getId(), branchScheme.getBranchInfo().getClientDB());
            	}
                continue;
            }
//            String branchCode = branchScheme.getBranchMaster().getBranchCode();

//			if (branchScheme.getStartTime() != null)
			if (startTime != null)
			{
				LocalDateTime start = startTime.toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				if (now.isBefore(start) ) {
					if (displayTaskInfo)
					{
						LogUtils.printLog("branchScheme {}=={}=={}  invald execute datetime: startTime={},endTime={}",
                    		pollBranchSchemeId, pollSchemeType, direction, startTime, endTime);
					}
//                            branchScheme.getId(),
//                            branchScheme.getPollSchemeName(),
//                            branchScheme.getPollSchemeType(),
//                            branchScheme.getStartTime(),
//                            branchScheme.getEndTime());
					continue;
				}
			}
			if (endTime != null)
			{
				LocalDateTime end = endTime.toInstant().atZone(ZoneId.systemDefault())
						.toLocalDateTime();
				if ( now.isAfter(end)) {
					if (displayTaskInfo) 
					{
	                    LogUtils.printLog("branchScheme {}=={}=={}  invald execute datetime: startTime={},endTime={}",
	                    		pollBranchSchemeId, pollSchemeType, direction, startTime, endTime);
	//                            branchScheme.getId(),
	//                            branchScheme.getPollSchemeName(),
	//                            branchScheme.getPollSchemeType(),
	//                            branchScheme.getStartTime(),
	//                            branchScheme.getEndTime());
					}
					continue;
				}
			}
			
//	        TaskJobLog lastTaskJobLog = taskJobLogDao.findLatestTaskJobLog(direction, id, pollSchemeType, branchCode);
//			TaskJobLog lastTaskJobLog = taskJobLogDao.findByKey(taskJobLogId);

			if (displayTaskInfo) 
				LogUtils.printLog("lastTaskJobLog={} {} {} {} {}", lastJobLogId, lastStatus, taskLastUpdateTime , currentTime);

	        if (lastJobLogId != null) {
//	            LogUtils.printLog("lastTaskJobLog={} {} {} {}", lastTaskJobLog.getStatus(), lastTaskJobLog.getLastUpdateTime() , System.currentTimeMillis() , lastTaskJobLog.getLastUpdateTime().getTime());
	        	if (displayTaskInfo)
	        	{
	        		LogUtils.printLog("lastTaskJobLog={} {} {} {} {}", lastJobLogId, lastStatus, taskLastUpdateTime , currentTime , taskLastUpdateTime.getTime());
	        	}
            	if(taskLastUpdateTime.getTime() > currentTime){
            		if (displayTaskInfo) 
            			LogUtils.printLog("{} branch code is already Process {} ,skipped ",
							branchCode,taskLastUpdateTime);
					continue;
            	}
	            if (lastStatus.equals(TaskProcessStatus.PROGRESS.name()) || lastStatus.equals(TaskProcessStatus.PENDING.name())) {
	            	if(currentTime - taskLastUpdateTime.getTime() > timeout){
	            		if (displayTaskInfo) 
	            			LogUtils.printLog("{} branch code process continue > {} ,auto update status  to failed ",
								branchCode,timeout);
//						lastTaskJobLog.setStatus(TaskProcessStatus.FAILED);
////						lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
//				        Auditer.audit(lastTaskJobLog);
//			            taskJobLogDao.update(lastTaskJobLog);
					} else {
						if (displayTaskInfo) 
							LogUtils.printLog("{} branch code process < {} ,skipped ",
								branchCode,timeout);
						continue;
					}
	            }
	        }
	        if (taskJobLogId != null) {
//	            LogUtils.printLog("lastTaskJobLog={} {} {} {}", lastTaskJobLog.getStatus(), lastTaskJobLog.getLastUpdateTime() , System.currentTimeMillis() , lastTaskJobLog.getLastUpdateTime().getTime());
	        	if (displayTaskInfo) 
	        		LogUtils.printLog("taskJobLogId={} {} {} {} {}", taskJobLogId, taskStatus, taskLastUpdateTime , currentTime , taskLastUpdateTime.getTime());
            	if(taskLastUpdateTime.getTime() > currentTime){
            		if (displayTaskInfo) 
            			LogUtils.printLog("{} branch code is already Process {} ,skipped ",
							branchCode,taskLastUpdateTime);
					continue;
            	}
	            if (taskStatus.equals(TaskProcessStatus.PROGRESS.name()) || taskStatus.equals(TaskProcessStatus.PENDING.name())) {
	            	if(currentTime - taskLastUpdateTime.getTime() > timeout){
	            		if (displayTaskInfo) 
	            			LogUtils.printLog("{} branch code process continue > {} ,auto update status  to failed ",
								branchCode,timeout);
//						lastTaskJobLog.setStatus(TaskProcessStatus.FAILED);
////						lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
//				        Auditer.audit(lastTaskJobLog);
//			            taskJobLogDao.update(lastTaskJobLog);
					} else {
						if (displayTaskInfo) 
							LogUtils.printLog("{} branch code process < {} ,skipped ",
								branchCode,timeout);
						continue;
					}
	            }
	        }
	        if (taskJobLogId2 != null) {
	            if (taskStatus2.equals(TaskProcessStatus.PROGRESS.name()) || taskStatus2.equals(TaskProcessStatus.PENDING.name())) {
	            	if(currentTime - taskLastUpdateTime.getTime() > timeout){
	            		if (displayTaskInfo) 
	            			LogUtils.printLog("{} branch code process continue > {} ,auto update status  to failed ",
								branchCode,timeout);
					} else {
						if (displayTaskInfo) 
							LogUtils.printLog("{} branch code process < {} ,skipped ",
								branchCode,timeout);
						continue;
					}
	            }
	        }
	            
	        TaskJobLog taskLog = new TaskJobLog();
	        taskLog.setLastestJobInd(LatestJobInd.P);
	        taskLog.setStatus(TaskProcessStatus.PENDING);
	        taskLog.setCreateUser(systemPrincipal.getUserId());
	        taskLog.setCreateTime(currentDate);
	        taskLog.setLastUpdateUser(systemPrincipal.getUserId());
	        taskLog.setLastUpdateTime(currentDate);
	        taskLog.setStartTime(currentDate);
//	        Auditer.audit(taskLog);
//	        taskLog.setSchemeScheduleJob(scheduleJob);
	        taskLog.setScheduleJobId(scheduleJob.getId());
	        if (pollBranchSchemeId != null)
	        {
	        	taskLog.setPollSchemeID(pollBranchSchemeId.longValue());
	        }
	        taskLog.setDirection(direction);
	        taskLog.setPollSchemeType(pollSchemeType);
//	        taskLog.setSchemeJobLog(schemeJobLog);
	        taskLog.setPollSchemeJobLogId(schemeJobLog.getId());
	        
	        taskLog.setBranchCode(branchCode);
	        taskLog.setPollBranchId(pollBranchInfoId.longValue());
	        taskLog.setPollSchemeName(pollSchemeName);
	        taskLog.setPollSchemedesc(pollSchemeDesc);	        
	        
//	        taskJobLogDao.insert(taskLog);
//	        taskJobLogService.createTaskJobLog(taskLog);
	        branchList.add(branchCode);
	        taskJobLogList.add(taskLog);
//	        taskJobLogDao.purgeTaskJobLog(direction, branchScheme.getId(), pollSchemeType, branchCode, TaskProcessStatus.NONE);

	        
//	        taskLog.setLastTaskJobLog(lastTaskJobLog);
	        if (taskJobLogId != null)
	        {
	        	taskLog.setLastTaskJobLogId(taskJobLogId.longValue());
	        }
	        if (taskJobLogId2 != null)
	        {
	        	taskLog.setLastDependTaskJobLogId(taskJobLogId2.longValue());
	        }

        	BranchScheme branchScheme = new BranchScheme() ;
        	
        	if (pollBranchSchemeId != null)
        	{
        		branchScheme.setId(pollBranchSchemeId.longValue());
        	}
            branchScheme.setSchemeScheduleJob(scheduleJob);
            branchScheme.setTaskLog(taskLog);
            
            branchSchemeList.add(branchScheme);
            
            if (displayTaskInfo) 
            	LogUtils.printLog("Prepare Submit Branch : {} ",
					branchCode);
			
		
//
//            
//            if (PollSchemeType.SALES_EOD.equals(pollSchemeType))
//            {
//                if (branchSchemeList.size() >= eodBatchProcessSize)
//                {
//                	break;
//                }
//            }
//            else if (PollSchemeType.SALES_REALTIME.equals(pollSchemeType))
//            {
//                if (branchSchemeList.size() >= realTimeBatchProcessSize)
//                {
//                	break;
//                }
//            }
//            else if (PollSchemeType.MASTER.equals(pollSchemeType))
//            {
//                if (branchSchemeList.size() >= masterBatchProcessSize)
//                {
//                	break;
//                }
//            }
//            else
//            {
//                if (branchSchemeList.size() >= otherBatchProcessSize)
//                {
//                	break;
//                }
//            }
            

        }
        
		LogUtils.printLog("{} {} Batch Insert start {} {}",pollSchemeType, direction, currentDate, branchList);
        taskJobLogService.createTaskJobLogList(taskJobLogList);
		LogUtils.printLog("Batch Insert end");
        schemeJobLog.setBranchSchemeList(branchSchemeList);
        
        entityManager.flush();
        entityManager.clear();    	

    	return schemeJobLog;
    }
    
	/**
	 * checkJobLog
	 * 
	 * Use the schedule ID to look up the Scheme Job Log
	 * 
	 * If there are no log, create a new one with Status = "PROGRESS"
	 * 
	 * If there are log there, check whether it is in "PROGRESS"
	 * 
	 *     Status = "PROGRESS"    ->   If the job within 1 hour return null
	 *                                 else marked last job LastJobInd as "N"
	 *                                                      Status as "Fail"
	 *                                 
	 *     Status <> "PROGRESS"   ->   Marked LastJobInd as "N"
	 *                                 
	 *     Create a new one with Status = "PROGRESS"
	 * 
	 * @param scheduleJob
	 * @return SchemeJobLog
	 */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public SchemeJobLog checkJobLog(SchemeScheduleJob scheduleJob, long interval, Logger logger) {
//        SchemeJobLogQueryCriteria schemeJobLogQueryCriteria = new SchemeJobLogQueryCriteria();
//        schemeJobLogQueryCriteria.setScheduleJobId(scheduleJob.getId());

//        List<SchemeJobLog> schemeJobLogs = schedulerJobLogService.findSchemeJobLogByCriteria(schemeJobLogQueryCriteria);
        Long currentTime = System.currentTimeMillis() ;
    	Timestamp currentTimetamp = DateUtil.getCurrentTimestamp();

        long timeout = otherTaskTimeout * 1000 ;
        if (PollSchemeType.OCT_TO_POS.equals(scheduleJob.getPollSchemeType()))
        {
        	timeout = octopusTaskTimeout ;
        }    	
        applicationSettingDao.getApplicationLock();
//    	applicationSettingDao.getApplicationLock(scheduleJob.getPollSchemeType().name());

        SchemeJobLog schemeJobLog = schemeJobLogDao.findLatestSchemeJobLog(scheduleJob.getId());
        if (schemeJobLog == null) {
            SchemeJobLog jobLog = new SchemeJobLog();
            jobLog.setLastJobInd(LatestJobInd.Y);
            jobLog.setStatus(JobProcessStatus.PROGRESS);
            jobLog.setStartTime(currentTimetamp);
            jobLog.setScheduleJobId(scheduleJob.getId());
            return schemeJobLogDao.addOrUpdateSchemeJobLog(jobLog);
        } else {
        	Date lastSchemeTime = schemeJobLog.getCreateTime() ;
			LogUtils.printLog(logger, "scheduleJob={} currentTimetamp={}  lastSchemeTime ={} currentTimetamp={} lastSchemeTime={} diff: {} / {}", 
					scheduleJob.getId(), currentTimetamp, lastSchemeTime, currentTimetamp.getTime(), lastSchemeTime.getTime(), currentTimetamp.getTime() - lastSchemeTime.getTime(), interval);
        	if (interval > -1)
        	{
	        	if (currentTimetamp.getTime() - lastSchemeTime.getTime() < interval / 2)
	        	{
	                LogUtils.printLog(logger, "scheduleJob={}  PROGRESSED...", scheduleJob.getId());
	                return null;
	        	}
        	}
        	
            if (schemeJobLog.getStatus() == JobProcessStatus.PROGRESS) {
				if(currentTime - schemeJobLog.getLastUpdateTime().getTime() > timeout) {
					LogUtils.printLog(logger, "scheduleJob={}  PROGRESS Continue > {}", scheduleJob.getId(), timeout);
					schemeJobLog.setStatus(JobProcessStatus.FAILED);
//					schemeJobLog.setLastJobInd(LatestJobInd.N);
					schemeJobLogDao.addOrUpdateSchemeJobLog(schemeJobLog);
				} else {
                    LogUtils.printLog(logger, "scheduleJob={}  PROGRESSING in {}...", scheduleJob.getId(), timeout);
                    return null;
                }

//            } else {
//                schemeJobLog.setLastJobInd(LatestJobInd.N);
//                schemeJobLogDao.addOrUpdateSchemeJobLog(schemeJobLog);
            }
            
        	schemeJobLogDao.updateOtherTaskJobLogNotLatest(scheduleJob.getId(), null);

            SchemeJobLog jobLog = new SchemeJobLog();
            jobLog.setLastJobInd(LatestJobInd.Y);
            jobLog.setStatus(JobProcessStatus.PROGRESS);
            jobLog.setStartTime(currentTimetamp);
            jobLog.setScheduleJobId(scheduleJob.getId());
            getschedulerJobLogService().insertSchemeJobLog(jobLog);
            return jobLog;
        }

    }
    
    public void higherEodPriority(String branchCode)
    {
    	LogUtils.printLog(LogUtils.SALES_EOD_LOGGER, "{} HigherEodPriority  due to EOD Completede", branchCode);
    	eodPriorityMap.put(branchCode, Boolean.TRUE);
    }
    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void insertSchemeJobLog(SchemeJobLog schemeJobLog) {
        Auditer.audit(schemeJobLog);
        schemeJobLogDao.insert(schemeJobLog);
        System.out.println(schemeJobLog.getId());
        entityManager.flush();
        entityManager.clear();    	
    }
    
    public SchedulerJobLogService getschedulerJobLogService()
    {
    	return (SchedulerJobLogService) AopContext.currentProxy();
    }





}
