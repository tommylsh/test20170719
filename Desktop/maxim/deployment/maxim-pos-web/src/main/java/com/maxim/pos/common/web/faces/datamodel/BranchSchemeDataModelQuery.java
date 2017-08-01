package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class BranchSchemeDataModelQuery implements GenericDataModelQuery {

    private PollBranchSchemeService pollBranchSchemeService;
    private CommonCriteria criteria;

    public BranchSchemeDataModelQuery() {}

    public BranchSchemeDataModelQuery(PollBranchSchemeService pollBranchSchemeService, CommonCriteria criteria) {
        this.pollBranchSchemeService = pollBranchSchemeService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return pollBranchSchemeService.findBranchSchemeByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return pollBranchSchemeService.getBranchSchemeCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }

}
