package com.maxim.pos.common.web.faces.datamodel;

import java.util.List;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

public class ApplicationSettingDataModelQuery implements GenericDataModelQuery {

    private ApplicationSettingService applicationSettingService;
    private CommonCriteria criteria;

    public ApplicationSettingDataModelQuery() {
    }

    public ApplicationSettingDataModelQuery(ApplicationSettingService ApplicationSettingService, CommonCriteria criteria) {
        this.applicationSettingService = ApplicationSettingService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return applicationSettingService.findApplicationSettingByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return ((Long) applicationSettingService.getApplicationSettingCountByCriteria(criteria)).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }

}
