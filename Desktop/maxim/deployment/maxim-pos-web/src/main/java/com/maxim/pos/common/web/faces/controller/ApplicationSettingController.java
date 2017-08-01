package com.maxim.pos.common.web.faces.controller;

import java.io.Serializable;

import com.maxim.pos.common.exception.PosException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.ApplicationSetting;
import com.maxim.pos.common.service.ApplicationSettingService;
import com.maxim.pos.common.value.ApplicationSettingQueryCriteria;
import com.maxim.pos.common.web.faces.datamodel.ApplicationSettingDataModelQuery;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller("applicationSettingController")
@Scope("viewScope")
public class ApplicationSettingController implements Serializable {

    private static final long serialVersionUID = 5713922123896848121L;

    @Autowired
    private ApplicationSettingService applicationSettingService;

    private GenericEntityLazyDataModel dataModel;
    private ApplicationSetting applicationSettingObject;

    public void add() {
        applicationSettingObject = new ApplicationSetting();
    }
    
    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save Application Setting")
    public void save() {
        if(StringUtils.length(applicationSettingObject.getCode())>100){
            throw new PosException("Code max length 100");
        }
        if(StringUtils.length(applicationSettingObject.getCodeValue())>500){
            throw new PosException("Code value length 500");
        }
        if(StringUtils.length(applicationSettingObject.getCodeDescription())>500){
            throw new PosException("Code Description length 500");
        }
        Auditer.audit(applicationSettingObject, UserDetailsService.getUser());
        applicationSettingService.saveApplicationSetting(applicationSettingObject);
    }

    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete Application Setting")
    public void delete() {
        applicationSettingService.deleteApplicationSettingById(applicationSettingObject.getId());
    }
    
    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            ApplicationSettingQueryCriteria criteria = new ApplicationSettingQueryCriteria();
            dataModel = new GenericEntityLazyDataModel(
                    new ApplicationSettingDataModelQuery(applicationSettingService, criteria));
        }

        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public ApplicationSetting getApplicationSettingObject() {
        return applicationSettingObject;
    }

    public void setApplicationSettingObject(ApplicationSetting applicationSettingObject) {
        this.applicationSettingObject = applicationSettingObject;
    }

}
