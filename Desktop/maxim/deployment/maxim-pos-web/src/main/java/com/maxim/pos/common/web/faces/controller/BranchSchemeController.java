package com.maxim.pos.common.web.faces.controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.BranchInfo;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.entity.BranchScheme;
import com.maxim.pos.common.exception.PosException;
import com.maxim.pos.common.service.BranchMasterService;
import com.maxim.pos.common.service.PollBranchSchemeService;
import com.maxim.pos.common.value.BranchSchemeQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.BranchSchemeDataModelQuery;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.pos.sales.service.BranchInfoService;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope("viewScope")
public class BranchSchemeController implements Serializable {

    private static final long serialVersionUID = 5713922123896848121L;

    @Autowired
    private PollBranchSchemeService pollBranchSchemeService;

    @Autowired
    private BranchMasterService branchMasterService;

    @Autowired
    private BranchInfoService branchInfoService;

    private GenericEntityLazyDataModel dataModel;
    private BranchScheme branchScheme;

    public void add() {
        branchScheme = new BranchScheme();
        branchScheme.setBranchMaster(new BranchMaster());
        branchScheme.setBranchInfo(new BranchInfo());
    }

    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save Branch Scheme")
    public void save() {
        Auditer.audit(branchScheme, UserDetailsService.getUser());
        Date startTime = branchScheme.getStartTime();
        Date endTime = branchScheme.getEndTime();
        if (startTime != null && startTime != null)
        {
	        if(startTime.after(endTime)){
	        	throw new PosException("Start time must be earlier than end time.");
	        }
        }
        if(StringUtils.length(branchScheme.getPollSchemeName())>100){
            throw new PosException("Scheme Name max length 100");
        }
        if(StringUtils.length(branchScheme.getPollSchemeDesc())>200){
            throw new PosException("Scheme Desc max length 200");
        }
        pollBranchSchemeService.savePollBranchScheme(branchScheme);
    }

    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete Branch Scheme")
    public void delete() {
        pollBranchSchemeService.delete(branchScheme.getId());
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            BranchSchemeQueryCriteria criteria = new BranchSchemeQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new BranchSchemeDataModelQuery(pollBranchSchemeService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public BranchScheme getBranchScheme() {
        return branchScheme;
    }

    public void setBranchScheme(BranchScheme branchScheme) {
        this.branchScheme = branchScheme;
    }

    public List<BranchMaster> getBranchMasterList() {
        return branchMasterService.getBranchMasterList();
    }

    public List<BranchInfo> getBranchInfoList() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("enable", Boolean.TRUE);
        return branchInfoService.findBranchInfoList(paramMap);
    }
}