package com.maxim.pos.common.web.faces.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.entity.SystemDashboard;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.value.SystemDashboardQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.SystemDashboardDataModelQuery;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller
@Scope("viewScope")
public class SystemDashboardController implements Serializable {
	private static final long serialVersionUID = 5713922123896848121L;

    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;

    private GenericEntityLazyDataModel dataModel;
    private SystemDashboard systemDashboard;

    public void add() {
    	systemDashboard = new SystemDashboard();
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
        	SystemDashboardQueryCriteria criteria = new SystemDashboardQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new SystemDashboardDataModelQuery(pollBranchSchemeService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

	public SystemDashboard getSystemDashboard() {
		return systemDashboard;
	}

	public void setSystemDashboard(SystemDashboard systemDashboard) {
		this.systemDashboard = systemDashboard;
	}

}
