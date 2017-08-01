package com.maxim.pos.common.web.faces.datamodel;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.SchemeScheduleJobService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

import java.util.List;

public class SchemeScheduleJobDataModelQuery implements GenericDataModelQuery {

    private SchemeScheduleJobService schemeScheduleJobService;
    private CommonCriteria criteria;

    public SchemeScheduleJobDataModelQuery() {}

    public SchemeScheduleJobDataModelQuery(SchemeScheduleJobService schemeScheduleJobService, CommonCriteria criteria) {
        this.schemeScheduleJobService = schemeScheduleJobService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return schemeScheduleJobService.findSchemeScheduleJobByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return schemeScheduleJobService.getSchemeScheduleJobCountByCriteria(criteria).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }
}
