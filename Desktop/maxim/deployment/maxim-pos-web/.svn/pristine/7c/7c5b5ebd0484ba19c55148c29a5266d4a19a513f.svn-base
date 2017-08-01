package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.TaskJobExceptionDetailService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class TaskJobExceptionDetailDataModelQuery implements GenericDataModelQuery {

    private TaskJobExceptionDetailService taskJobExceptionDetailService;
    private CommonCriteria criteria;

    public TaskJobExceptionDetailDataModelQuery() {
    }

    public TaskJobExceptionDetailDataModelQuery(TaskJobExceptionDetailService taskJobExceptionDetailService, CommonCriteria criteria) {
        this.taskJobExceptionDetailService = taskJobExceptionDetailService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return taskJobExceptionDetailService.findTaskJobExceptionDetailByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return taskJobExceptionDetailService.getTaskJobExceptionDetailCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}