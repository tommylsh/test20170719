package com.maxim.pos.security.web.faces.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.model.DualListModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.web.security.UserDetailsService;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.entity.User;
import com.maxim.pos.security.service.RoleService;
import com.maxim.pos.security.service.UserService;
import com.maxim.pos.security.value.UserQueryCriteria;
import com.maxim.pos.security.web.faces.datamodel.UserDataModelQuery;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.annotation.OperationMessage.OperationType;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller("userManagementController")
@Scope("viewScope")
public class UserManagementController implements Serializable {

    private static final long serialVersionUID = -1575760215415451010L;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private GenericEntityLazyDataModel dataModel;
    private User userObject;
    private DualListModel<Role> roleListModel;

    @Secured(ContollerConstants.CREATE_USER)
    public void add() {
        userObject = new User();
    }

    @Secured(ContollerConstants.EDIT_USER)
    public void edit() {
    }

    public void onSuperAdminChange() {

    }

    @Secured(ContollerConstants.DELETE_USER)
    @OperationMessage(type = OperationType.DELETE, operationName = "delete user")
    public void delete() {
        userService.deleteUserById(userObject.getId());
    }

    @Secured({ContollerConstants.CREATE_USER, ContollerConstants.EDIT_USER})
    @OperationMessage(type = OperationType.SAVE, operationName = "save user")
    public void save() {

        Auditer.onCreate(userObject, UserDetailsService.getUser());

        if (userObject.isAdmin()) {
            userObject = userService.updateUserWithRoles(userObject, new ArrayList<Role>());
        } else {
            userObject = userService.updateUserWithRoles(userObject, roleListModel.getTarget());
            roleListModel.getTarget().clear();
        }

        userObject = null;
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            UserQueryCriteria criteria = new UserQueryCriteria();
            criteria.setJoinRoles(true);
            dataModel = new GenericEntityLazyDataModel(new UserDataModelQuery(userService, criteria));
        }
        return dataModel;
    }

    public User getUserObject() {
        return userObject;
    }

    public void setUserObject(User userObject) {
        this.userObject = userObject;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DualListModel<Role> getRoleListModel() {
        if (userObject != null) {
            List<Role> sourceRoles = roleService.findRolesByDefaultSystemAlias();
            roleListModel = new DualListModel<Role>(sourceRoles, new ArrayList<Role>());

            roleListModel.getTarget().addAll(userObject.getRoles());
            roleListModel.getSource().removeAll(roleListModel.getTarget());
        }

        return roleListModel;
    }

    public void setRoleListModel(DualListModel<Role> roleListModel) {
        this.roleListModel = roleListModel;
    }

}
