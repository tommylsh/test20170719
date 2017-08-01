package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.TaskJobLogService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.common.value.TaskJobLogQueryCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class TaskJobLogDataModelQuery implements GenericDataModelQuery {

    private TaskJobLogService taskJobLogService;
    private TaskJobLogQueryCriteria criteria;

    public TaskJobLogDataModelQuery() {}
    
    public TaskJobLogDataModelQuery(TaskJobLogService taskJobLogService, TaskJobLogQueryCriteria criteria) {
        this.taskJobLogService = taskJobLogService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        if("".equals(criteria.getBranchCode())){
        	criteria.setBranchCode(null);
        }
        return taskJobLogService.findTaskJobLogByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return taskJobLogService.getTaskJobLogCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}