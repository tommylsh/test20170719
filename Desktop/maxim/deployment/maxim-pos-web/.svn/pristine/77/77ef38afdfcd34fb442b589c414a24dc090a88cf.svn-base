package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.sales.service.BranchInfoService;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class BranchInfoDataModelQuery implements GenericDataModelQuery {

    private BranchInfoService branchInfoService;
    private CommonCriteria criteria;

    public BranchInfoDataModelQuery() {}
    
    public BranchInfoDataModelQuery(BranchInfoService branchInfoService, CommonCriteria criteria) {
        this.branchInfoService = branchInfoService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return branchInfoService.findBranchInfoByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return branchInfoService.getBranchInfoCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}