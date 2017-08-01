package com.maxim.pos.common.web.faces.datamodel;

import java.util.List;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.value.SystemDashboardQueryCriteria;
import com.maxim.pos.common.value.TaskJobLogQueryCriteria;
import com.maxim.web.faces.model.GenericDataModelQuery;

public class SystemDashboardDataModelQuery implements GenericDataModelQuery {

    private PollBranchSchemeService pollBranchSchemeService;
    private SystemDashboardQueryCriteria criteria;

    public SystemDashboardDataModelQuery() {}

    public SystemDashboardDataModelQuery(PollBranchSchemeService pollBranchSchemeService, SystemDashboardQueryCriteria criteria) {
        this.pollBranchSchemeService = pollBranchSchemeService;
        this.criteria = criteria;
    }

    @Override
    public List<? extends AbstractEntity> getDataSource(int first, int pageSize) {
        criteria.setStartFrom(first);
        criteria.setMaxResult(pageSize);
        criteria.setQueryRecord(true);
        return pollBranchSchemeService.findSystemDashboard(criteria);
    }

	public SystemDashboardQueryCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(SystemDashboardQueryCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	public int getTotalCount() {
		criteria.setQueryRecord(false);
        return pollBranchSchemeService.getSystemDashboardCountByCriteria(criteria).intValue();
	}

    
}
