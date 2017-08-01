package com.maxim.pos.security.web.faces.datamodel;

import java.util.List;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.service.RoleService;
import com.maxim.web.faces.model.GenericDataModelQuery;

public class RoleDataModelQuery implements GenericDataModelQuery {

    private RoleService RoleService;
    private CommonCriteria criteria;

    public RoleDataModelQuery() {
    }

    public RoleDataModelQuery(RoleService RoleService, CommonCriteria criteria) {
        this.RoleService = RoleService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return RoleService.findRoleByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return ((Long) RoleService.getRoleCountByCriteria(criteria)).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }

}
