package com.maxim.pos.security.web.faces.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.web.faces.controller.CommonController;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.pos.security.entity.Permission;
import com.maxim.pos.security.service.PermissionService;
import com.maxim.pos.security.value.PermissionQueryCriteria;
import com.maxim.pos.security.web.faces.datamodel.PermissionDataModelQuery;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller("permissionManagementController")
@Scope("viewScope")
public class PermissionManagementController implements Serializable {

    private static final long serialVersionUID = 2870491271308250713L;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private CommonController commonController;

    private GenericEntityLazyDataModel dataModel;
    private Permission permissionObject;

    @Secured(ContollerConstants.CREATE_PERMISSION)
    public void add() {
        permissionObject = new Permission(commonController.getDefaultSystemModule());
    }

    @Secured(ContollerConstants.EDIT_PERMISSION)
    public void edit() {
    }

    @Secured({ ContollerConstants.CREATE_PERMISSION, ContollerConstants.EDIT_PERMISSION })
    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save Permission")
    public void save() {
        Auditer.onCreate(permissionObject, UserDetailsService.getUser());
        permissionService.savePermission(permissionObject);
    }

    @Secured(ContollerConstants.DELETE_PERMISSION)
    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete Permission")
    public void delete() {
        permissionService.deletePermissionById(permissionObject.getId());
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            PermissionQueryCriteria criteria = new PermissionQueryCriteria();
            criteria.setSystemAlias(commonController.getSystemAlias());
            dataModel = new GenericEntityLazyDataModel(new PermissionDataModelQuery(permissionService, criteria));
        }

        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public Permission getPermissionObject() {
        return permissionObject;
    }

    public void setPermissionObject(Permission permissionObject) {
        this.permissionObject = permissionObject;
    }

}
