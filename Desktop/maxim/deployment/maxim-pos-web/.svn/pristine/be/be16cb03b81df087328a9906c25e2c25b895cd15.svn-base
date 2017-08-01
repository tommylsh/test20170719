package com.maxim.pos.common.web.faces.controller;

import com.maxim.pos.common.entity.TaskJobLog;
import com.maxim.pos.common.entity.TaskJobLogDetail;
import com.maxim.pos.common.service.TaskJobLogDetailService;
import com.maxim.pos.common.value.TaskJobLogDetailQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.TaskJobLogDetailDataModelQuery;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
@Scope("viewScope")
public class TaskJobLogDetailController implements Serializable {

    @Autowired
    private TaskJobLogDetailService taskJobLogDetailService;

    private TaskJobLogDetail taskJobLogDetail;
    private GenericEntityLazyDataModel dataModel;

    private Long taskJobLogId;

    public GenericEntityLazyDataModel getDataModel() {
        if(dataModel==null) {
            TaskJobLogDetailQueryCriteria criteria = new TaskJobLogDetailQueryCriteria();
            criteria.setTaskJobLogId(taskJobLogId);
            dataModel = new GenericEntityLazyDataModel(new TaskJobLogDetailDataModelQuery(taskJobLogDetailService, criteria));
        } else {
            TaskJobLogDetailDataModelQuery taskJobLogDetailDataModelQuery = (TaskJobLogDetailDataModelQuery)dataModel.getDataModelQuery();
            TaskJobLogDetailQueryCriteria taskJobLogDetailQueryCriteria = (TaskJobLogDetailQueryCriteria)taskJobLogDetailDataModelQuery.getCriteria();
            taskJobLogDetailQueryCriteria.setTaskJobLogId(taskJobLogId);
        }
        return dataModel;
    }

    public TaskJobLogDetail getTaskJobLogDetail() {
        return taskJobLogDetail;
    }

    public void setTaskJobLogDetail(TaskJobLogDetail taskJobLogDetail) {
        this.taskJobLogDetail = taskJobLogDetail;
    }

    public Long getTaskJobLogId() {
        return taskJobLogId;
    }

    public void setTaskJobLogId(Long taskJobLogId) {
        this.taskJobLogId = taskJobLogId;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }
}
