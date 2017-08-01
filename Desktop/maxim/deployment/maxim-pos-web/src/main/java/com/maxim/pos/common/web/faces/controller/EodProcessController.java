package com.maxim.pos.common.web.faces.controller;

import java.io.Serializable;
import java.util.Date;

import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.service.BranchSchemeExecutor;
import com.maxim.pos.common.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.value.TaskJobLogQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.EodProcessDataModelQuery;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller
@Scope("viewScope")
public class EodProcessController implements Serializable {
	private static final long serialVersionUID = 5713922123896848121L;
    private static final Logger LOGGER = LoggerFactory.getLogger(EodProcessController.class);


    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;

    private GenericEntityLazyDataModel dataModel;
    private TaskJobLog taskJobLog;
    private Date businessDate;

    public void add() {
    	taskJobLog = new TaskJobLog();
    }

    public void reRun(){
        BranchScheme branchScheme = pollBranchSchemeService.getBranchScheme(taskJobLog.getPollSchemeType(), taskJobLog.getDirection(), null, taskJobLog.getBranchCode());
        branchScheme.setBusinessDate(businessDate);
        Logger logger ;
        switch (taskJobLog.getPollSchemeType()) {
            case SALES_REALTIME:
                logger = LogUtils.SALES_REALTIME_LOGGER;
                break;
            case SALES_EOD:
                logger = LogUtils.SALES_EOD_LOGGER;
                break;
            case MASTER:
                logger = LogUtils.MASTER_LOGGER;
                break;
            case SMTP:
                logger = LogUtils.MASTER_LOGGER;
                break;
            default:
                logger = LOGGER;
                break;
        }

        if(branchScheme ==null || branchScheme.getBusinessDate()==null) {
            throw new RuntimeException("Business Date is must");
        }
        BranchSchemeExecutor branchSchemeExecutor = new BranchSchemeExecutor();
        branchSchemeExecutor.setBranchScheme(branchScheme);
        branchSchemeExecutor.setLogger(logger);
        branchScheme.setReRun(true);
        branchSchemeExecutor.run();

    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
        	TaskJobLogQueryCriteria criteria = new TaskJobLogQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new EodProcessDataModelQuery(pollBranchSchemeService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

	public TaskJobLog getTaskJobLog() {
		return taskJobLog;
	}

	public void setTaskJobLog(TaskJobLog taskJobLog) {
		this.taskJobLog = taskJobLog;
	}

    public Date getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate) {
        this.businessDate = businessDate;
    }
}
