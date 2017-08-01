package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.PollSchemeInfoService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class SchemeInfoDataModelQuery implements GenericDataModelQuery {

    private PollSchemeInfoService pollSchemeInfoService;
    private CommonCriteria criteria;

    public SchemeInfoDataModelQuery() {}
    
    public SchemeInfoDataModelQuery(PollSchemeInfoService pollSchemeInfoService, CommonCriteria criteria) {
        this.pollSchemeInfoService = pollSchemeInfoService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return pollSchemeInfoService.findSchemeInfoByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return pollSchemeInfoService.getSchemeInfoCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}