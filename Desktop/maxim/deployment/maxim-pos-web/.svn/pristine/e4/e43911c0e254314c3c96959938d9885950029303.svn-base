package com.maxim.pos.common.web.faces.datamodel;

import java.util.List;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.value.TaskJobLogQueryCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

public class EodProcessDataModelQuery implements GenericDataModelQuery {

    private PollBranchSchemeService pollBranchSchemeService;
    private TaskJobLogQueryCriteria criteria;

    public EodProcessDataModelQuery() {}

    public EodProcessDataModelQuery(PollBranchSchemeService pollBranchSchemeService, TaskJobLogQueryCriteria criteria) {
        this.pollBranchSchemeService = pollBranchSchemeService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return pollBranchSchemeService.findEodProcess(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return pollBranchSchemeService.getEodProcessCountByCriteria(criteria).intValue();
    }

    public TaskJobLogQueryCriteria getCriteria() {
        return criteria;
    }

}
