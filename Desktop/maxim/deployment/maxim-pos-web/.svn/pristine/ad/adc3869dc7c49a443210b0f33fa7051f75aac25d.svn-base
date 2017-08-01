package com.maxim.pos.common.web.faces.datamodel;

import java.util.List;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.PollSchemeTableColumnService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

public class SchemeTableColumnDataModelQuery implements GenericDataModelQuery {

    private PollSchemeTableColumnService pollSchemeTableColumnService;
    private CommonCriteria criteria;

    public SchemeTableColumnDataModelQuery() {}
    
    public SchemeTableColumnDataModelQuery(PollSchemeTableColumnService schemeTableColumnService, CommonCriteria criteria) {
        this.pollSchemeTableColumnService = schemeTableColumnService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return pollSchemeTableColumnService.findSchemeTableColumnByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return pollSchemeTableColumnService.getSchemeTableColumnCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}