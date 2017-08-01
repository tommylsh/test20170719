package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.TaskJobLogDetailService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class TaskJobLogDetailDataModelQuery implements GenericDataModelQuery {

    private TaskJobLogDetailService taskJobLogDetailService;
    private CommonCriteria criteria;

    public TaskJobLogDetailDataModelQuery() {}
    
    public TaskJobLogDetailDataModelQuery(TaskJobLogDetailService taskJobLogDetailService, CommonCriteria criteria) {
        this.taskJobLogDetailService = taskJobLogDetailService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return taskJobLogDetailService.findTaskJobLogDetailByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return taskJobLogDetailService.getTaskJobLogDetailCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}