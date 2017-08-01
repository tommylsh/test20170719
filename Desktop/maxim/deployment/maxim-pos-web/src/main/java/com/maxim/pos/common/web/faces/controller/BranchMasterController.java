package com.maxim.pos.common.web.faces.controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.BranchMaster;
import com.maxim.pos.common.exception.PosException;
import com.maxim.pos.common.service.BranchMasterService;
import com.maxim.pos.common.value.BranchMasterQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.BranchMasterDataModelQuery;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

@Controller
@Scope("viewScope")
public class BranchMasterController implements Serializable {

    @Autowired
    private BranchMasterService branchMasterService;

    private GenericEntityLazyDataModel dataModel;
    private BranchMaster branchMaster;

    public void add() {
        branchMaster = new BranchMaster();
    }

    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save BranchMaster")
    public void save() {
        Auditer.audit(branchMaster, UserDetailsService.getUser());
        if(StringUtils.length(branchMaster.getBranchCode())>10){
            throw new PosException("Branch Code max length 10");
        }
        if(StringUtils.length(branchMaster.getBranchCname())>50){
            throw new PosException("Chinese Name max length 50");
        }
        if(StringUtils.length(branchMaster.getBranchEname())>50){
            throw new PosException("English Name max length 50");
        }
        if(StringUtils.length(branchMaster.getBranchType())>10){
            throw new PosException("Branch Type max length 10");
        }
        branchMasterService.save(branchMaster);
    }

    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete BranchMaster")
    public void delete() {
        branchMasterService.delete(branchMaster.getId());
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            BranchMasterQueryCriteria criteria = new BranchMasterQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(new BranchMasterDataModelQuery(branchMasterService, criteria));
        }
        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public BranchMaster getBranchMaster() {
        return branchMaster;
    }

    public void setBranchMaster(BranchMaster branchMaster) {
        this.branchMaster = branchMaster;
    }

}
