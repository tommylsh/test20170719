package com.maxim.pos.common.service;

import java.util.List;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.entity.SchemeJobLog;
import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.value.CommonCriteria;

/**
 * process POS task log
 * @author Lotic
 *
 */
public interface TaskJobLogService {
	
	public List<TaskJobLog> findTaskJobLogByCriteria(CommonCriteria criteria);
	
	public TaskJobLog findLatestTaskJobLog(BranchScheme branchScheme);
		
	public TaskJobLog addOrUpdateTaskJobLog(TaskJobLog taskJobLog);
	
	public void createTaskJobLog(TaskJobLog taskJobLog);
    public void createTaskJobLogList(List<TaskJobLog> taskJobLogList) ;
	public void removeTaskJobLog(TaskJobLog taskJobLog);
    public TaskJobLog updateTaskJobLogForTimeout(long taskJobLogId) ;
    public TaskJobLog updateTaskJobLogForEnd(TaskJobLog taskJobLog, boolean isComplete) ;


//	public TaskJobLogDetail addOrUpdateTaskJobLogDetail(TaskJobLogDetail taskJobLogDetail);
//	
//	public TaskJobExceptionDetail addOrUpdateTaskJobExceptionDetail(TaskJobExceptionDetail taskJobExceptionDetail);

    Long getTaskJobLogCountByCriteria(CommonCriteria criteria);
    
    public TaskJobLog acquireTaskJobLog(BranchScheme branchScheme, SchemeJobLog schemeJobLog) ;
    
    public TaskJobLog startTaskJobLog(BranchScheme branchScheme, TaskJobLog taskLog) ;
    
    public void createJobExceptionDetail(TaskJobLog taskJobLog, String fromTable, String toTable, Exception e) ;
    public void createJobExceptionDetail(TaskJobLog taskJobLog, String fromTable, String toTable, String msg) ;
    
    public void createJobLogDetail(TaskJobLog taskJobLog, String fromTable, String toTable, int row, int...returnInts) ;




}
