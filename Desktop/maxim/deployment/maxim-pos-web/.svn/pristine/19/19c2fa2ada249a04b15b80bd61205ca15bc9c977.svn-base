package com.maxim.pos.security.web.faces.controller;

import java.util.Iterator;

import org.primefaces.model.TreeNode;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.web.faces.annotation.OperationMessage;
import com.maxim.web.faces.model.LazyTreeNode;
import com.maxim.web.faces.utils.FacesUtils;

@Controller("resourceManagementController")
@Scope("viewScope")
public class ResourceManagementController extends SystemTreeController {

    private static final long serialVersionUID = -7892218575201327420L;

    protected Folder folderObject;
    protected Link linkObject;

    @Secured(ContollerConstants.CREATE_MENU)
    public void addFolder() {
        if (ResourceTreeType.SYSTEM.toString().equals(selectedNode.getType())) {
            SystemModule system = (SystemModule) selectedNode.getData();
            folderObject = new Folder(system);

            linkObject = null;
        }
    }

    @Secured(ContollerConstants.EDIT_MENU)
    public void edit() {
        if (ResourceTreeType.FOLDER.toString().equals(selectedNode.getType())) {
            editFolder();
        } else if (ResourceTreeType.LINK.toString().equals(selectedNode.getType())) {
            editLink();
        }
    }
    
    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete Folder/Link")
    public void delete() {
        if (ResourceTreeType.FOLDER.toString().equals(selectedNode.getType())) {
            deleteFolder();
        } else if (ResourceTreeType.LINK.toString().equals(selectedNode.getType())) {
            deleteLink();
        }
        
        clearFolderSession();
    }

    @Secured(ContollerConstants.EDIT_MENU)
    public void editFolder() {
        if (ResourceTreeType.FOLDER.toString().equals(selectedNode.getType())) {
            folderObject = (Folder) selectedNode.getData();
            linkObject = null;
        }
    }

    @Secured(ContollerConstants.DELETE_MENU)
    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete Folder")
    public void deleteFolder() {
        if (ResourceTreeType.FOLDER.toString().equals(selectedNode.getType())) {
            folderObject = (Folder) selectedNode.getData();
            folderService.deleteFolder(folderObject.getId());
            
            for (Iterator<TreeNode> iter = selectedNode.getParent().getChildren().iterator(); iter.hasNext();) {
                LazyTreeNode treeNode = (LazyTreeNode) iter.next();
                Folder folder = (Folder) treeNode.getData();
                if (folder.getId().equals(folderObject.getId())) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    protected void clearFolderSession() {
        String key = commonController.getSystemAlias() + "_folders";
        FacesUtils.putSessionScope(key, null);
    }

    @Secured({ ContollerConstants.CREATE_MENU, ContollerConstants.EDIT_MENU })
    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save Folder")
    public void saveFolder() {
        boolean created = (folderObject.getId() == null);
        Auditer.audit(folderObject);
        folderObject = (Folder) folderService.saveFolder(folderObject);

        if (created) {
            if (selectedNode.isExpanded()) {
                LazyTreeNode treeNode = new LazyTreeNode(ResourceTreeType.FOLDER.toString(), folderObject,
                        selectedNode);
                treeNode.setExpanded(false);
                treeNode.setLeaf(false);
                selectedNode.getChildren().add(treeNode);
            }
        } else {
            selectedNode.setData(folderObject);
        }
        
        clearFolderSession();
    }

    @Secured(ContollerConstants.CREATE_MENU)
    public void addLink() {
        if (ResourceTreeType.FOLDER.toString().equals(selectedNode.getType())) {
            Folder folder = (Folder) selectedNode.getData();
            
            folder = folderService.findFolderDetailById(folder.getId());
            
            linkObject = new Link(folder);

            selectedNode.setData(folder);
            folderObject = null;
        }
    }

    @Secured(ContollerConstants.EDIT_MENU)
    public void editLink() {
        if (ResourceTreeType.LINK.toString().equals(selectedNode.getType())) {
            linkObject = (Link) selectedNode.getData();
            folderObject = null;
        }
    }

    @Secured(ContollerConstants.DELETE_MENU)
    @OperationMessage(type = OperationMessage.OperationType.DELETE, operationName = "Delete Link")
    public void deleteLink() {
        if (ResourceTreeType.LINK.toString().equals(selectedNode.getType())) {
            linkObject = (Link) selectedNode.getData();
            linkService.deleteLink(linkObject.getId());
            
            for (Iterator<TreeNode> iter = selectedNode.getParent().getChildren().iterator(); iter.hasNext();) {
                LazyTreeNode treeNode = (LazyTreeNode) iter.next();
                Link link = (Link) treeNode.getData();
                if (link.getId().equals(linkObject.getId())) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    @Secured({ ContollerConstants.CREATE_MENU, ContollerConstants.EDIT_MENU })
    @OperationMessage(type = OperationMessage.OperationType.UPDATE, operationName = "Save Link")
    public void saveLink() {
        boolean created = (linkObject.getId() == null);
        
        Auditer.audit(linkObject);
        
        linkObject = (Link) linkService.saveLink(linkObject);

        if (created) {
            if (selectedNode.isExpanded()) {
                selectedNode.getChildren()
                        .add(new LazyTreeNode(ResourceTreeType.LINK.toString(), linkObject, selectedNode));
            }
        } else {
            selectedNode.setData(linkObject);
        }
        
        clearFolderSession();
    }

    public Folder getFolderObject() {
        return folderObject;
    }

    public Link getLinkObject() {
        return linkObject;
    }

}
