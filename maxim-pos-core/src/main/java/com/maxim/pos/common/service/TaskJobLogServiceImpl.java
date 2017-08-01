package com.maxim.pos.common.service;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.maxim.dao.HibernateDAO;
import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.TaskJobExceptionDetail;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.entity.TaskJobLogDetail;
import com.maxim.pos.common.enumeration.Direction;
import com.maxim.pos.common.enumeration.ExceptionDetailStatus;
import com.maxim.pos.common.enumeration.LatestJobInd;
import com.maxim.pos.common.enumeration.PollSchemeType;
import com.maxim.pos.common.enumeration.TaskProcessStatus;
import com.maxim.pos.common.persistence.ApplicationSettingDao;
import com.maxim.pos.common.persistence.BranchMasterDao;
import com.maxim.pos.common.persistence.TaskJobExceptionDetailDao;
import com.maxim.pos.common.persistence.TaskJobLogDao;
import com.maxim.pos.common.persistence.TaskJobLogDetailDao;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.util.DateUtil;

@Transactional
@Service("taskJobLogService")
public class TaskJobLogServiceImpl implements TaskJobLogService {

	@PersistenceContext
	private EntityManager entityManager;

    @Autowired
    private TaskJobLogDao taskJobLogDao;
    
    @Autowired
    private TaskJobLogDetailDao taskJobLogDetailDao;
    
    @Autowired
    private TaskJobExceptionDetailDao taskJobExceptionDetailDao;
    
    @Autowired
    BranchMasterDao branchMasterDao ;
    
    @Resource(name="hibernateDAO")
    HibernateDAO dao ;
    
    @Autowired
    ApplicationSettingDao applicationSettingDao ;

//    TaskJobLogService taskJobLogService ;
//    
	private @Value("${system.eod.taskTimeout}")			int eodTaskTimeout;
	private @Value("${system.realTime.taskTimeout}")	int realTimeTaskTimeout;
	private @Value("${system.master.taskTimeout}")		int masterTaskTimeout;
	private @Value("${system.octopus.taskTimeout}")		int octopusTaskTimeout;
	private @Value("${system.other.taskTimeout}")		int otherTaskTimeout;
    
//    @Autowired
//    private ApplicationSettingService applicationSettingService;

//	@PostConstruct
//    public void init() throws Exception {
//		taskJobLogService = (TaskJobLogService) AopContext.currentProxy();
//	}
	
    public TaskJobLogService getTaskJobLogService()
    {
    	return (TaskJobLogService) AopContext.currentProxy();
    }
    @Override
    public List<TaskJobLog> findTaskJobLogByCriteria(CommonCriteria criteria) {
        return taskJobLogDao.findTaskJobLogByCriteria(criteria);
    }

    @Override
    public TaskJobLog findLatestTaskJobLog(BranchScheme branchScheme) {
        Assert.notNull(branchScheme, "'branchScheme' can't be null.");

        Direction direction = branchScheme.getDirection();
        Long branchSchemeId = branchScheme.getId();
        PollSchemeType pollSchemeType = branchScheme.getPollSchemeType();
        Assert.notNull(direction, "'direction' can't be null.");
        Assert.notNull(branchSchemeId, "'branchSchemeId' can't be null.");
        Assert.notNull(pollSchemeType, "'pollSchemeType' can't be null.");

//        SchemeScheduleJob schemeScheduleJob = branchScheme.getSchemeScheduleJob();
        String branchCode = "" ;
        if (branchScheme.getBranchMaster() != null)
        {
        	branchCode = branchScheme.getBranchMaster().getBranchCode();
        }
//        Assert.notNull(schemeScheduleJob, "'schemeScheduleJob' can't be null.");
//        Long schemeScheduleJobId = schemeScheduleJob.getId();
//        Assert.notNull(schemeScheduleJobId, "'schemeScheduleJobId' can't be null.");
        return taskJobLogDao.findLatestTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode);
//        return taskJobLogDao.findLatestTaskJobLog(direction, branchSchemeId, pollSchemeType, schemeScheduleJob==null?null:schemeScheduleJob.getId());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false, noRollbackFor = Exception.class)
    public TaskJobLog acquireTaskJobLog(BranchScheme branchScheme, SchemeJobLog schemeJobLog) 
    {
        Long schemeJobLogId 			= schemeJobLog == null ? null : schemeJobLog.getId() ;
        Direction direction				= branchScheme.getDirection();
        Long branchSchemeId				= branchScheme.getId();
        PollSchemeType pollSchemeType	= branchScheme.getPollSchemeType();
        TaskJobLog currentTaskLog		= branchScheme.getTaskLog() ;
        String branchCode				= "" ;
        if (branchScheme.getBranchMaster() != null)
        {
        	branchCode = branchScheme.getBranchMaster().getBranchCode();
        }
        
		LogUtils.printLog("{} branch lock {} {} {}",
				branchCode,branchSchemeId, schemeJobLogId, currentTaskLog);
		String displayMessage	= applicationSettingDao.getApplicationValue("SCHEDULE_INFO");
		String lockMethod		= applicationSettingDao.getApplicationValue("LOCK");
 
    	boolean displayTaskInfo			= "DISPLAY".equals(displayMessage) ;
        if (displayTaskInfo)
        {
    		LogUtils.printLog("{} branch displayMessage{} lockMethod {}",
    				branchCode,displayMessage, lockMethod);
        }
    	
		if (lockMethod == null || !lockMethod.equals("BRANCH_ONLY"))
		{
			applicationSettingDao.getApplicationLock(pollSchemeType.name());
		}
		else if (lockMethod.equals("BRANCH_ONLY"))
		{
			applicationSettingDao.getApplicationRptReadLock(pollSchemeType.name());
		}
		if (!branchCode.equals(""))
		{
			branchMasterDao.getBranchLock(branchCode);
		}


		Long taskJobLogId		= null ;
    	Long lastTaskJobLogId	= null ;
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
        
    	if (currentTaskLog != null)
    	{
    		taskJobLogId		= currentTaskLog.getId();
            lastTaskJobLogId	= currentTaskLog.getLastTaskJobLogId();
            
            if (displayTaskInfo)
            {
	    		LogUtils.printLog("{} branch taskJobLogId {} lastTaskJobLogId {}",
	    				branchCode,taskJobLogId, lastTaskJobLogId);
            }

            TaskJobLog newCurrentTaskLog = null ;
            if (taskJobLogId == null)
            {
            	newCurrentTaskLog = taskJobLogDao.findTaskJobLogBySchemeJobLogIdAndBranchCode(schemeJobLogId, branchCode);
            }
            else
            {
            	newCurrentTaskLog = taskJobLogDao.findByKey(taskJobLogId);
            }
            if (newCurrentTaskLog == null)
            {
                TaskJobLog lastTaskJobLog = taskJobLogDao.findLatestTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode);
                if (lastTaskJobLog == null)
                {
		    		LogUtils.printLog("{} branch no lastTaskJobLog",
		    				branchCode);
                }
                else
                {
		    		LogUtils.printLog("{} branch lastTaskJobLog {} getLastUpdateTime {}",
		    				branchCode,lastTaskJobLog.getId(), lastTaskJobLog.getLastUpdateTime());
                }

            	return null;
            }
        	taskJobLogId = newCurrentTaskLog.getId() ;
        	if (lastTaskJobLogId != null && lastTaskJobLogId == Long.MAX_VALUE)
        	{
                TaskJobLog lastTaskJobLog = taskJobLogDao.findLatestTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode);
                if (displayTaskInfo)
                {
        	        if (lastTaskJobLog != null) {
        	        	LogUtils.printLog("{} branch code found Latest {} {}",
        					branchCode, lastTaskJobLog.getId(), lastTaskJobLog.getStatus());
        	        }
        	        else
        	        {
        	    		LogUtils.printLog("{} branch code no Latest ",
        	    				branchCode);
        	        }
                }
               
                lastTaskJobLogId = null ;
                if (lastTaskJobLog != null) {
                	lastTaskJobLogId = lastTaskJobLog.getId() ;
                    if (lastTaskJobLog.getStatus() == TaskProcessStatus.PROGRESS || lastTaskJobLog.getStatus() == TaskProcessStatus.PENDING) {
                    	if(System.currentTimeMillis() - lastTaskJobLog.getLastUpdateTime().getTime() > timeout){
                            if (displayTaskInfo)
                            {
                            	LogUtils.printLog("{} branch code process continue > {} ,auto update status  to failed ",
        							branchCode,timeout);
                            }
        					lastTaskJobLog.setStatus(TaskProcessStatus.FAILED);
           		            getTaskJobLogService().addOrUpdateTaskJobLog(lastTaskJobLog);
        				} else {
        		            if (displayTaskInfo)
        		            {
        		            	LogUtils.printLog("{} branch code process < {} ,skipped ",
        							branchCode,timeout);
        		            }
        					return null ;
        				}
                    }
                }
        	}
            newCurrentTaskLog.setLastTaskJobLogId(lastTaskJobLogId);
            newCurrentTaskLog.setLastDependTaskJobLogId(currentTaskLog.getLastDependTaskJobLogId());
            if (lastTaskJobLogId != null)
            {
    			TaskJobLog lastTaskJobLog = taskJobLogDao.findByKey(lastTaskJobLogId);
    			newCurrentTaskLog.setLastTaskJobLog(lastTaskJobLog);
    			
                if (lastTaskJobLog.getStatus() == TaskProcessStatus.PROGRESS || lastTaskJobLog.getStatus() == TaskProcessStatus.PENDING) {
   					lastTaskJobLog.setStatus(TaskProcessStatus.FAILED);
   		            Auditer.audit(lastTaskJobLog);
   		            getTaskJobLogService().addOrUpdateTaskJobLog(lastTaskJobLog);
                }
            }
            
            try
            {

	            taskJobLogDao.purgeTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode, TaskProcessStatus.NONE);
	            if (displayTaskInfo)
	            {
		    		LogUtils.printLog("{} branch purgeTaskJobLog {} {}",
		    				branchCode,branchSchemeId, TaskProcessStatus.NONE);
	            }
	//    		String purgeMethod = applicationSettingDao.getApplicationValue("PURGE");
	//    		if (purgeMethod != null && purgeMethod.equals("NEW"))
	//    		{
		            taskJobLogDao.purgeTaskJobLogWithoutLatestTask(direction, branchSchemeId, pollSchemeType, branchCode, TaskProcessStatus.PENDING, taskJobLogId);
		            if (displayTaskInfo)
		            {
		            	LogUtils.printLog("{} branch purgeTaskJobLog {} {}",
		    				branchCode,branchSchemeId, TaskProcessStatus.PENDING);
		            }
		            taskJobLogDao.updateOtherTaskJobLogNotLatest(direction, branchSchemeId, pollSchemeType, branchCode, taskJobLogId, lastTaskJobLogId);
		            if (displayTaskInfo)
		            {
		            	LogUtils.printLog("{} branch updateOtherTaskJobLogNotLatest {} {} {} {}",
		    				branchCode,branchSchemeId, currentTaskLog,taskJobLogId, lastTaskJobLogId);
		            }
	//    		}
		            
            }
            catch (Exception e)
            {
            	LogUtils.printException("purgeTaskJobLog Excpeiton", e);
            	try{
    	            taskJobLogDao.updateTaskJobLogUndeletableNoneToFail(direction, branchSchemeId, pollSchemeType, branchCode, taskJobLogId);
            	}
                catch (Exception e2)
                {
                	LogUtils.printException("purgeTaskJobLog Excpeiton", e2);
                }
            }
            
            branchScheme.setTaskLog(newCurrentTaskLog);

            return newCurrentTaskLog ;
    	}
    	
        if (displayTaskInfo)
        {
        	LogUtils.printLog("{} branch code find Latest {}",
				branchCode,branchSchemeId);
        }
        TaskJobLog lastTaskJobLog = taskJobLogDao.findLatestTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode);
        if (displayTaskInfo)
        {
	        if (lastTaskJobLog != null) {
	        	LogUtils.printLog("{} branch code found Latest {} {}",
					branchCode, lastTaskJobLog.getId(), lastTaskJobLog.getStatus());
	        }
	        else
	        {
	    		LogUtils.printLog("{} branch code no Latest ",
	    				branchCode);
	        }
        }
       
        if (lastTaskJobLog != null) {
            if (lastTaskJobLog.getStatus() == TaskProcessStatus.PROGRESS || lastTaskJobLog.getStatus() == TaskProcessStatus.PENDING) {
            	if(System.currentTimeMillis() - lastTaskJobLog.getLastUpdateTime().getTime() > timeout){
                    if (displayTaskInfo)
                    {
                    	LogUtils.printLog("{} branch code process continue > {} ,auto update status  to failed ",
							branchCode,timeout);
                    }
					lastTaskJobLog.setStatus(TaskProcessStatus.FAILED);
//					lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
//		            Auditer.audit(lastTaskJobLog);
   		            getTaskJobLogService().addOrUpdateTaskJobLog(lastTaskJobLog);
				} else {
		            if (displayTaskInfo)
		            {
		            	LogUtils.printLog("{} branch code process < {} ,skipped ",
							branchCode,timeout);
		            }
					return null ;
				}
            }
//            else
//            {
//				lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
//	            taskJobLogDao.update(lastTaskJobLog);
//            }
        }
        Timestamp currentDate = DateUtil.getCurrentTimestamp();

        TaskJobLog taskLog = new TaskJobLog();
        taskLog.setLastestJobInd(LatestJobInd.P);
        taskLog.setStatus(TaskProcessStatus.PENDING);
        taskLog.setStartTime(currentDate);
        Auditer.audit(taskLog);
        taskLog.setPollSchemeID(branchScheme.getId());
        taskLog.setDirection(branchScheme.getDirection());
        taskLog.setPollSchemeType(branchScheme.getPollSchemeType());
//        taskLog.setSchemeJobLog(schemeJobLog);
        taskLog.setPollSchemeJobLogId(schemeJobLogId);

        taskLog.setBranchCode(branchCode);
        taskLog.setPollSchemeName(branchScheme.getPollSchemeName());
        taskLog.setPollSchemedesc(branchScheme.getPollSchemeDesc());
        if (branchScheme.getBranchInfo() != null)
        {
        	taskLog.setPollBranchId(branchScheme.getBranchInfo().getId());
        }

        if (branchScheme.getSchemeScheduleJob() != null)
        {
        	taskLog.setScheduleJobId(branchScheme.getSchemeScheduleJob().getId());
        }
        TaskJobLog dependOnTaskLog = branchScheme.getDependOnTaskLog();
        if (dependOnTaskLog != null)
        {
        	taskLog.setDependOn(dependOnTaskLog.getId());
        }
        if (lastTaskJobLog != null)
        {
        	taskLog.setLastTaskJobLog(lastTaskJobLog);
            lastTaskJobLogId = lastTaskJobLog.getId();
        }

        getTaskJobLogService().createTaskJobLog(taskLog);

        taskJobLogId = taskLog.getId() ;
        
        try
        {
	//        taskJobLogDao.purgeTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode, TaskProcessStatus.NONE, taskJobLogId,lastTaskJobLogId);
	//        taskJobLogDao.purgeTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode, TaskProcessStatus.PENDING, taskJobLogId,lastTaskJobLogId);
	//        taskJobLogDao.updateOtherTaskJobLogNotLatest(direction, branchSchemeId, pollSchemeType, branchCode, taskJobLogId, lastTaskJobLogId);
	        taskJobLogDao.purgeTaskJobLog(direction, branchSchemeId, pollSchemeType, branchCode, TaskProcessStatus.NONE);
	        if (displayTaskInfo)
	        {
	        	LogUtils.printLog("{} branch purgeTaskJobLog {} {}",
					branchCode,branchSchemeId, TaskProcessStatus.NONE);
	        }
	//		String purgeMethod = applicationSettingDao.getApplicationValue("PURGE");
	//		LogUtils.printLog("{} branch purgeMethod {} {}",
	//				branchCode,branchSchemeId, purgeMethod);
	//		if (purgeMethod != null && purgeMethod.equals("NEW"))
	//		{
	            taskJobLogDao.purgeTaskJobLogWithoutLatestTask(direction, branchSchemeId, pollSchemeType, branchCode, TaskProcessStatus.PENDING, taskJobLogId);
	            if (displayTaskInfo)
	            {
	            	LogUtils.printLog("{} branch purgeTaskJobLog {} {}",
	    				branchCode,branchSchemeId, TaskProcessStatus.PENDING);
	            }
	            taskJobLogDao.updateOtherTaskJobLogNotLatest(direction, branchSchemeId, pollSchemeType, branchCode, taskJobLogId, lastTaskJobLogId);
	            if (displayTaskInfo)
	            {
	            	LogUtils.printLog("{} branch updateOtherTaskJobLogNotLatest {} {} {} {}",
		    				branchCode,branchSchemeId, currentTaskLog,taskJobLogId, lastTaskJobLogId);
	            }
	//		}
        }
        catch (Exception e)
        {
        	LogUtils.printException("purgeTaskJobLog Excpeiton", e);
        	try{
	            taskJobLogDao.updateTaskJobLogUndeletableNoneToFail(direction, branchSchemeId, pollSchemeType, branchCode, taskJobLogId);
        	}
            catch (Exception e2)
            {
            	LogUtils.printException("purgeTaskJobLog Excpeiton", e2);
            }
        }
        branchScheme.setTaskLog(taskLog);

        return taskLog;

    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public TaskJobLog startTaskJobLog(BranchScheme branchScheme, TaskJobLog taskLog)
    {
        Long branchSchemeId = branchScheme.getId();
        PollSchemeType pollSchemeType = branchScheme.getPollSchemeType();
        String branchCode = "" ;
        if (branchScheme.getBranchMaster() != null)
        {
        	branchCode = branchScheme.getBranchMaster().getBranchCode();
        }
//        TaskJobLog currentTaskLog = branchScheme.getTaskLog() ;
		LogUtils.printLog("{} {} startTaskJobLog nolock {} {}",
				branchCode,pollSchemeType, branchSchemeId, taskLog.getId());
//
//		applicationSettingDao.getApplicationLock(pollSchemeType.name());
//
    	TaskJobLog lastTaskJobLog = taskLog.getLastTaskJobLog();
    	if (lastTaskJobLog != null)
    	{
			lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
	        taskJobLogDao.save(lastTaskJobLog);
    	}

    	Timestamp currentDate = DateUtil.getCurrentTimestamp();;

    	taskLog.setStartTime(currentDate);
        taskLog.setLastestJobInd(LatestJobInd.Y);
        taskLog.setStatus(TaskProcessStatus.PROGRESS);
        taskLog.setTaskJobLogDetails(new HashSet<TaskJobLogDetail>());

        Auditer.audit(taskLog);
        taskLog=  (TaskJobLog) taskJobLogDao.save(taskLog);
        taskLog.setLastTaskJobLog(lastTaskJobLog);

        return taskLog;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void createJobExceptionDetail(TaskJobLog taskJobLog, String fromTable, String toTable, Exception e) {
    	createJobExceptionDetail(taskJobLog, fromTable, toTable, LogUtils.getStackTrace(e));
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void createJobExceptionDetail(TaskJobLog taskJobLog, String fromTable, String toTable, String msg) {
        taskJobLog.setStatus(TaskProcessStatus.FAILED);
        TaskJobExceptionDetail taskJobExceptionDetail = new TaskJobExceptionDetail();
        taskJobExceptionDetail.setSource(fromTable);
        taskJobExceptionDetail.setDestination(toTable);
        taskJobExceptionDetail.setExceptionContent(msg);
        taskJobExceptionDetail.setSeverity(2);
        taskJobExceptionDetail.setStatus(ExceptionDetailStatus.P);        
        taskJobExceptionDetail.setTaskJobLog(taskJobLog);
        Auditer.audit(taskJobExceptionDetail);

        if (taskJobLog.getTaskJobExceptionDetails() == null) {
            taskJobLog.setTaskJobExceptionDetails(new HashSet<TaskJobExceptionDetail>());
        }
        taskJobLog.getTaskJobExceptionDetails().add(taskJobExceptionDetail);
        
        dao.save(taskJobExceptionDetail);
//        Auditer.audit(taskJobLog);
//        taskJobLog = taskJobLogDao.update(taskJobLog);
        // if (taskJobLog.getTaskJobExceptionDetails() == null) {
        // taskJobLog.setTaskJobExceptionDetails(new
        // TreeSet<TaskJobExceptionDetail>());
        // }
        //
        // taskJobLog.getTaskJobExceptionDetails().add(taskJobExceptionDetail);
    }
    
    /**
     * 
     * @param taskJobLog
     * @param fromTable
     * @param toTable
     * @param row
     * @param returnInts (must be an array length > 1 and <= 2)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void createJobLogDetail(TaskJobLog taskJobLog, String fromTable, String toTable, int row, int...returnInts) {
        TaskJobLogDetail taskJobLogDetail = new TaskJobLogDetail();
        taskJobLogDetail.setTaskJobLog(taskJobLog);
        taskJobLogDetail.setSource(fromTable);
        taskJobLogDetail.setDestination(toTable);
        taskJobLogDetail.setNumOfRecDelete(row);
        if(returnInts != null)
        {
        	if ( returnInts.length > 1)
        	{
	            taskJobLogDetail.setNumOfRecProcessed(returnInts[0] + returnInts[1]);
    	        taskJobLogDetail.setNumOfRecInsert(returnInts[0]);
    	        taskJobLogDetail.setNumOfRecUpdate(returnInts[1]);
        	}
        	else
        	{
	            taskJobLogDetail.setNumOfRecProcessed(returnInts[0]);
    	        taskJobLogDetail.setNumOfRecInsert(returnInts[0]);
    	        taskJobLogDetail.setNumOfRecUpdate(0);
        	}
        }
        else
        {
            taskJobLogDetail.setNumOfRecProcessed(0);
	        taskJobLogDetail.setNumOfRecInsert(0);
	        taskJobLogDetail.setNumOfRecUpdate(0);
        }
        Auditer.audit(taskJobLogDetail);

        if (taskJobLog.getTaskJobLogDetails() == null) {
            taskJobLog.setTaskJobLogDetails(new HashSet<TaskJobLogDetail>());
        }
        taskJobLog.getTaskJobLogDetails().add(taskJobLogDetail);

        dao.save(taskJobLogDetail);
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public TaskJobLog updateTaskJobLogForTimeout(long taskJobLogId) {
    	
    	TaskJobLog taskJobLog = taskJobLogDao.findByKey(taskJobLogId);
    			
        String branchCode =taskJobLog.getBranchCode();
        PollSchemeType pollSchemeType = taskJobLog.getPollSchemeType();
		LogUtils.printLog("{} {} updateTaskJobLogToNone nolock {} {} {}",
				branchCode,pollSchemeType,taskJobLog.getPollSchemeID(), taskJobLog.getId(), taskJobLog.getStatus());
//
//		applicationSettingDao.getApplicationLock(pollSchemeType.name());
//
//
		if (taskJobLog.getStatus().equals(TaskProcessStatus.FAILED))
		{
			// Do nothing
		}
		if (taskJobLog.getStatus().equals(TaskProcessStatus.COMPLETE))
		{
			// Do nothing
		}
		if (taskJobLog.getStatus().equals(TaskProcessStatus.PENDING))
		{
		    taskJobLog.setStatus(TaskProcessStatus.NONE);
		    if (taskJobLog.getPollSchemeType() == PollSchemeType.SALES_REALTIME) {
		        taskJobLog.setLastestJobInd(LatestJobInd.Y);
		    }
		    else
		    {
		        taskJobLog.setLastestJobInd(LatestJobInd.N);
		    }
	        Auditer.audit(taskJobLog);
	        taskJobLog=  (TaskJobLog) taskJobLogDao.save(taskJobLog);
	        entityManager.flush();
	        entityManager.clear();
		}
		if (taskJobLog.getStatus().equals(TaskProcessStatus.PROGRESS))
		{
			createJobExceptionDetail(taskJobLog,"","","Branch : " + branchCode + " Timeout !! ");
	        Auditer.audit(taskJobLog);
	        taskJobLog=  (TaskJobLog) taskJobLogDao.save(taskJobLog);
	        entityManager.flush();
	        entityManager.clear();
		}
        return taskJobLog;
        
    }

    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public TaskJobLog addOrUpdateTaskJobLog(TaskJobLog taskJobLog) {
    	
        String branchCode =taskJobLog.getBranchCode();
        PollSchemeType pollSchemeType = taskJobLog.getPollSchemeType();
		LogUtils.printLog("{} {} addOrUpdateTaskJobLog nolock {} {}",
				branchCode,pollSchemeType,taskJobLog.getPollSchemeID(), taskJobLog.getId());
//
//		applicationSettingDao.getApplicationLock(pollSchemeType.name());
//
//		
        Assert.notNull(taskJobLog, "'taskJobLog' can't be null.");
        Auditer.audit(taskJobLog);
        taskJobLog=  (TaskJobLog) taskJobLogDao.save(taskJobLog);
        entityManager.flush();
        entityManager.clear();    	
        return taskJobLog;
        
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void createTaskJobLog(TaskJobLog taskJobLog) {
        taskJobLogDao.insert(taskJobLog);
        entityManager.flush();
        entityManager.clear();    	
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public void createTaskJobLogList(List<TaskJobLog> taskJobLogList) {
//    	for (TaskJobLog taskJobLog: taskJobLogList)
//    	{
//    		taskJobLogDao.insert(taskJobLog);
//    	}
		taskJobLogDao.batchInsertWithoutReturnGenKey(taskJobLogList);
        entityManager.flush();
        entityManager.clear();    	
    }

//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
//    public TaskJobLogDetail addOrUpdateTaskJobLogDetail(TaskJobLogDetail taskJobLogDetail) {
//        Assert.notNull(taskJobLogDetail, "'taskJobLogDetail' can't be null.");
//        Auditer.audit(taskJobLogDetail);
//        return (TaskJobLogDetail) dao.save(taskJobLogDetail);
//    }
//
//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
//    public TaskJobExceptionDetail addOrUpdateTaskJobExceptionDetail(TaskJobExceptionDetail taskJobExceptionDetail) {
//        Assert.notNull(taskJobExceptionDetail, "'taskJobExceptionDetail' can't be null.");
//        Auditer.audit(taskJobExceptionDetail);
//        return (TaskJobExceptionDetail) dao.save(taskJobExceptionDetail);
//    }

    @Override
    public Long getTaskJobLogCountByCriteria(CommonCriteria criteria) {
        return taskJobLogDao.getTaskJobLogCountByCriteria(criteria);
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
	public void removeTaskJobLog(TaskJobLog taskJobLog)
	{
        taskJobLogDao.delete(taskJobLog);
	}
    
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    public TaskJobLog updateTaskJobLogForEnd(TaskJobLog taskJobLog, boolean isComplete) {
    	
        TaskJobLog lastTaskJobLog = taskJobLog.getLastTaskJobLog();

    	Long taskJobLogId = taskJobLog.getId() ;
        TaskJobLog currentTaskJobLog = taskJobLogDao.findByKey(taskJobLogId);
//        currentTaskJobLog.setLastTaskJobLog(taskJobLog.getLastTaskJobLog());
        

        if (TaskProcessStatus.FAILED.equals(currentTaskJobLog.getStatus())) {
//            TaskJobLog lastTaskJobLog = taskJobLog.getLastTaskJobLog();
            if (lastTaskJobLog != null) {
                if (!LatestJobInd.N.equals(lastTaskJobLog.getLastestJobInd())) {
                    lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
                    taskJobLogDao.save(lastTaskJobLog);
                }
            }
            currentTaskJobLog.setLastestJobInd(LatestJobInd.Y);
        } else {
            if (isComplete) {
                if (TaskProcessStatus.PROGRESS.equals(currentTaskJobLog.getStatus())) {
                	currentTaskJobLog.setStatus(TaskProcessStatus.COMPLETE);
                }
            } else {
//                TaskJobLog lastTaskJobLog = taskJobLog.getLastTaskJobLog();
                boolean markFail = false;

            	long detailCount = taskJobLogDetailDao.getTaskJobLogDetailCount(taskJobLogId) ;
            	long exceptionCount = taskJobExceptionDetailDao.getTaskJobExceptionDetailCount(taskJobLogId) ;
                if ( detailCount > 0 || exceptionCount > 0)
                {
                    markFail = true;
                }
                if (markFail)
                {
                	createJobExceptionDetail(currentTaskJobLog,"","","Branch : " + currentTaskJobLog.getBranchCode() + " Fail !! ");
                    if (lastTaskJobLog != null) {
                        if (!LatestJobInd.N.equals(lastTaskJobLog.getLastestJobInd())) {
                            lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
                            taskJobLogDao.save(lastTaskJobLog);
                        }
                    }
                    currentTaskJobLog.setStatus(TaskProcessStatus.FAILED);
                    currentTaskJobLog.setLastestJobInd(LatestJobInd.Y);
                }
                else
                {
                	currentTaskJobLog.setStatus(TaskProcessStatus.NONE);
	                if (currentTaskJobLog.getPollSchemeType() == PollSchemeType.SALES_REALTIME) {
	                    if (lastTaskJobLog != null) {
	                        if (!LatestJobInd.N.equals(lastTaskJobLog.getLastestJobInd())) {
	                            lastTaskJobLog.setLastestJobInd(LatestJobInd.N);
	                            taskJobLogDao.save(lastTaskJobLog);
	                        }
	                    }
	                    currentTaskJobLog.setLastestJobInd(LatestJobInd.Y);
	                }
	                else
	                {
	                    if (lastTaskJobLog != null) {
	                        if (!LatestJobInd.Y.equals(lastTaskJobLog.getLastestJobInd())) {
	                            lastTaskJobLog.setLastestJobInd(LatestJobInd.Y);
	                            taskJobLogDao.save(lastTaskJobLog);
	                        }
	                    }
	                    currentTaskJobLog.setLastestJobInd(LatestJobInd.N);
	                }
                }
            }
        }

//        if (TaskProcessStatus.PROGRESS.equals(taskJobLog.getStatus())) {
//            if (isComplete)
//                taskJobLog.setStatus(TaskProcessStatus.COMPLETE);
//            else
//                taskJobLog.setStatus(TaskProcessStatus.NONE);
//        }
        currentTaskJobLog.setEndTime(DateUtil.getCurrentTimestamp());
//        addOrUpdateTaskJobLog(currentTaskJobLog);
        Auditer.audit(currentTaskJobLog);
        currentTaskJobLog=  (TaskJobLog) taskJobLogDao.save(currentTaskJobLog);
        
        return currentTaskJobLog;

    }



}
