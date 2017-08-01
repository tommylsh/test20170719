package com.maxim.pos.common.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.util.LogUtils;
import com.maxim.pos.sales.service.MasterService;

/**
 * Created by Lotic on 2017-03-10.
 */
@Service("processStgToPosService")
@Scope("prototype")
public class ProcessStgToPosService implements Runnable{
    private Logger logger;
    private BranchScheme branchScheme;
//    private TaskJobLog taskJobLog;

    @Autowired
    private MasterService masterService;
    
	@Override
    public void run() {
        LogUtils.printLog(logger,"ProcessStgToPosService start...");
        String result = masterService.processStagingToPos(branchScheme, logger);
        LogUtils.printLog(logger,result);
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public BranchScheme getBranchScheme() {
        return branchScheme;
    }

    public void setBranchScheme(BranchScheme branchScheme) {
        this.branchScheme = branchScheme;
    }

//    public TaskJobLog getTaskJobLog() {
//        return taskJobLog;
//    }
//
//    public void setTaskJobLog(TaskJobLog taskJobLog) {
//        this.taskJobLog = taskJobLog;
//    }
}