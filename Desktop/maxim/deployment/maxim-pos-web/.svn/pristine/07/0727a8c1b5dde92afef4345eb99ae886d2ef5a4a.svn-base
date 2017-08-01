package com.maxim.pos.security.web.faces.datamodel;

import java.util.List;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.service.PermissionService;
import com.maxim.web.faces.model.GenericDataModelQuery;

public class PermissionDataModelQuery implements GenericDataModelQuery {

    private PermissionService permissionService;
    private CommonCriteria criteria;

    public PermissionDataModelQuery() {
    }

    public PermissionDataModelQuery(PermissionService PermissionService, CommonCriteria criteria) {
        this.permissionService = PermissionService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return permissionService.findPermissionByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return ((Long) permissionService.getPermissionCountByCriteria(criteria)).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }

}
