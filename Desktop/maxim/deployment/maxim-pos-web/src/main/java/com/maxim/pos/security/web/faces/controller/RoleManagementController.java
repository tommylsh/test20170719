package com.maxim.pos.security.web.faces.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.event.CloseEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.web.faces.controller.CommonController;
import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.entity.Permission;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.service.FolderService;
import com.maxim.pos.security.service.LinkService;
import com.maxim.pos.security.service.PermissionService;
import com.maxim.pos.security.service.RoleService;
import com.maxim.pos.security.value.PermissionQueryCriteria;
import com.maxim.pos.security.value.RoleQueryCriteria;
import com.maxim.pos.security.value.UpdateRoleDTO;
import com.maxim.pos.security.web.faces.controller.SystemTreeController.ResourceTreeType;
import com.maxim.pos.security.web.faces.datamodel.RoleDataModelQuery;
import com.maxim.pos.security.web.faces.support.ResourceTreeHolder;
import com.maxim.util.LoggerHelper;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.annotation.OperationMessage.OperationType;
import com.maxim.web.faces.model.GenericEntityLazyDataModel;

@Controller("roleManagementController")
@Scope("viewScope")
public class RoleManagementController implements Serializable {

    public static final Logger logger = LoggerFactory.getLogger(RoleManagementController.class);

    private static final long serialVersionUID = -624850281732356014L;

    @Autowired
    private RoleService roleService;

    @Autowired
    protected LinkService linkService;

    @Autowired
    protected FolderService folderService;

    @Autowired
    protected PermissionService permissionService;

    @Autowired
    protected CommonController commonController;

    private GenericEntityLazyDataModel dataModel;

    private Role roleObject;
    private ResourceTreeHolder resourceTreeHolder;
    private DualListModel<Permission> permissionListModel;

    @Secured(ContollerConstants.CREATE_ROLE)
    public void add() {
        roleObject = new Role(commonController.getDefaultSystemModule());
    }

    @Secured(ContollerConstants.EDIT_ROLE)
    public void edit() {
        roleObject = roleService.findRoleDetailById(roleObject.getId());

        getResourceTreeHolder().setSelectedFolders(new ArrayList<Folder>(roleObject.getFolders()));
        getResourceTreeHolder().setSelectedLinks(new ArrayList<Link>(roleObject.getLinks()));
    }

    @Secured({ ContollerConstants.CREATE_ROLE, ContollerConstants.EDIT_ROLE })
    @OperationMessage(type = OperationType.SAVE, operationName = "Save Role")
    public void save() {
        TreeNode[] selectedNodes = resourceTreeHolder.getSelectedNodes();
        List<Folder> selecteFolders = new ArrayList<Folder>();
        List<Link> selecteLinks = new ArrayList<Link>();

        for (TreeNode treeNode : selectedNodes) {
            if (ResourceTreeType.LINK.toString().equals(treeNode.getType())) {
                Link link = (Link) treeNode.getData();

                selecteLinks.add(link);

                if (!selecteFolders.contains(link.getFolder())) {
                    selecteFolders.add(link.getFolder());
                }
            }
        }

        boolean isAndyFolderUpdated = CommonController.isAnyUpdated(roleObject.getFolders(), selecteFolders);
        boolean isAndyLinkUpdated = CommonController.isAnyUpdated(roleObject.getLinks(), selecteLinks);

        if (!isAndyFolderUpdated) {
            selecteFolders = null;
        }

        if (!isAndyLinkUpdated) {
            selecteLinks = null;
        }

        List<Permission> selectedPermissions = permissionListModel.getTarget();
        boolean isAnyPermissionUpdated = CommonController.isAnyUpdated(roleObject.getPermissions(),
                permissionListModel.getTarget());
        if (!isAnyPermissionUpdated) {
            selectedPermissions = null;
        }

        Auditer.audit(roleObject);
        roleObject = roleService.saveRoleWithDetails(roleObject,
                new UpdateRoleDTO(selecteFolders, selecteLinks, selectedPermissions));

        resourceTreeHolder.setRoot(null);
    }

    @Secured(ContollerConstants.DELETE_ROLE)
    @OperationMessage(type = OperationType.DELETE, operationName = "Delete Role")
    public void delete() {
        roleService.deleteRoleById(roleObject.getId());
        resourceTreeHolder.setRoot(null);
    }

    public Role getRoleObject() {
        LoggerHelper.logInfo(logger, "Reading RoleObject %s", roleObject);
        return roleObject;
    }

    public void setRoleObject(Role roleObject) {
        this.roleObject = roleObject;
    }

    public ResourceTreeHolder getResourceTreeHolder() {
        if (resourceTreeHolder == null) {
            WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
            resourceTreeHolder = new ResourceTreeHolder(webApplicationContext);
        }

        return resourceTreeHolder;
    }
    
    public void onDialogClose(CloseEvent event) {
        resourceTreeHolder.setRoot(null);
    }

    public GenericEntityLazyDataModel getDataModel() {
        if (dataModel == null) {
            RoleQueryCriteria criteria = new RoleQueryCriteria(commonController.getSystemAlias());
            dataModel = new GenericEntityLazyDataModel(new RoleDataModelQuery(roleService, criteria));
        }

        return dataModel;
    }

    public void setDataModel(GenericEntityLazyDataModel dataModel) {
        this.dataModel = dataModel;
    }

    public DualListModel<Permission> getPermissionListModel() {
        if (roleObject != null) {
            PermissionQueryCriteria criteria = new PermissionQueryCriteria(commonController.getSystemAlias());
            criteria.setMaxResult(CommonController.MAX_RESULT);
            List<Permission> sourcePermissions = permissionService.findPermissionByCriteria(criteria);
            permissionListModel = new DualListModel<Permission>(sourcePermissions, new ArrayList<Permission>());

            permissionListModel.getTarget().addAll(roleObject.getPermissions());
            permissionListModel.getSource().removeAll(permissionListModel.getTarget());
        }

        return permissionListModel;
    }

    public void setPermissionListModel(DualListModel<Permission> permissionListModel) {
        this.permissionListModel = permissionListModel;
    }

}
