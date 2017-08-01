package com.maxim.pos.security.web.faces.datamodel;

import java.util.List;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.value.CommonCriteria;
import com.maxim.pos.security.service.UserService;
import com.maxim.web.faces.model.GenericDataModelQuery;

public class UserDataModelQuery implements GenericDataModelQuery {

    private UserService userService;
    private CommonCriteria criteria;

    public UserDataModelQuery() {
    }

    public UserDataModelQuery(UserService userService, CommonCriteria criteria) {
        this.userService = userService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return userService.findUserByCriteria(criteria);
    }

    @Override
    public int getTotalCount() {
        criteria.setQueryRecord(false);
        return ((Long) userService.getUserCountByCriteria(criteria)).intValue();
    }

    public CommonCriteria getCriteria() {
        return criteria;
    }

}
