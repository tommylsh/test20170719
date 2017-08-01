package com.maxim.pos.common.web.faces.controller;

import com.maxim.pos.common.entity.TaskJobExceptionDetail;
import com.maxim.pos.common.service.TaskJobExceptionDetailService;
import com.maxim.pos.common.value.TaskJobExceptionDetailQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.TaskJobExceptionDetailDataModelQuery;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
@Scope("viewScope")
public class TaskJobExceptionDetailController implements Serializable {

    @Autowired
    private TaskJobExceptionDetailService taskJobExceptionDetailService;

    private TaskJobExceptionDetail taskJobExceptionDetail;

    private GenericEntityLazyDataModel dataModel;

    private Long taskJobLogId;

    public GenericEntityLazyDataModel getDataModel() {
            if(dataModel==null) {
                TaskJobExceptionDetailQueryCriteria criteria = new TaskJobExceptionDetailQueryCriteria();
                criteria.setTaskJobLogId(taskJobLogId);
                dataModel = new GenericEntityLazyDataModel(new TaskJobExceptionDetailDataModelQuery(taskJobExceptionDetailService, criteria));
            } else {
                TaskJobExceptionDetailDataModelQuery taskJobExceptionDetailDataModelQuery = (TaskJobExceptionDetailDataModelQuery)dataModel.getDataModelQuery();
                TaskJobExceptionDetailQueryCriteria taskJobExceptionDetailQueryCriteria = (TaskJobExceptionDetailQueryCriteria)taskJobExceptionDetailDataModelQuery.getCriteria();
                taskJobExceptionDetailQueryCriteria.setTaskJobLogId(taskJobLogId);
            }
             return  dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public TaskJobExceptionDetail getTaskJobExceptionDetail() {
        return taskJobExceptionDetail;
    }

    public void setTaskJobExceptionDetail(TaskJobExceptionDetail taskJobExceptionDetail) {
        this.taskJobExceptionDetail = taskJobExceptionDetail;
    }

    public Long getTaskJobLogId() {
        return taskJobLogId;
    }

    public void setTaskJobLogId(Long taskJobLogId) {
        this.taskJobLogId = taskJobLogId;
    }
}
