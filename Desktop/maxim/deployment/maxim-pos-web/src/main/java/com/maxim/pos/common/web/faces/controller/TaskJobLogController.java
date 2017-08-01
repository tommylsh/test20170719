package com.maxim.pos.common.web.faces.controller;

import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.service.TaskJobLogService;
import com.maxim.pos.common.value.TaskJobLogQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.TaskJobLogDataModelQuery;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
@Scope("viewScope")
public class TaskJobLogController implements Serializable {

    @Autowired
    private TaskJobLogService taskJobLogService;

    private GenericEntityLazyDataModel dataModel;
    private TaskJobLog taskJobLog;

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            TaskJobLogQueryCriteria criteria = new TaskJobLogQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new TaskJobLogDataModelQuery(taskJobLogService, criteria));
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

}
