package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.BranchMasterService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class BranchMasterDataModelQuery implements GenericDataModelQuery {

    private BranchMasterService branchMasterService;
    private CommonCriteria criteria;

    public BranchMasterDataModelQuery() {
    }

    public BranchMasterDataModelQuery(BranchMasterService branchMasterService, CommonCriteria criteria) {
        this.branchMasterService = branchMasterService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return branchMasterService.findBranchMasterByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return branchMasterService.getBranchMasterCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}