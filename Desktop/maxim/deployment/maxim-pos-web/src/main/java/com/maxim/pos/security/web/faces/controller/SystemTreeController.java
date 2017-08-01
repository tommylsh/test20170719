package com.maxim.pos.security.web.faces.controller;

import java.io.Serializable;
import java.util.List;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.springframework.beans.factory.annotation.Autowired;

import com.maxim.pos.common.web.faces.controller.CommonController;
import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.pos.security.service.FolderService;
import com.maxim.pos.security.service.LinkService;
import com.maxim.pos.security.value.FolderQueryCriteria;
import com.maxim.web.faces.model.LazyTreeNode;

public class SystemTreeController implements Serializable {

    private static final long serialVersionUID = -311475547528807394L;

    public enum ResourceTreeType {
        SYSTEM, FOLDER, LINK
    }

    @Autowired
    protected LinkService linkService;

    @Autowired
    protected FolderService folderService;

    @Autowired
    protected CommonController commonController;

    protected LazyTreeNode root;
    protected LazyTreeNode selectedNode;

    public LazyTreeNode getRoot() {
        if (root == null) {

            root = LazyTreeNode.createRoot();
            root.setExpanded(true);

            LazyTreeNode lazyTreeNode = new LazyTreeNode(ResourceTreeType.SYSTEM.toString(),
                    commonController.getDefaultSystemModule(), root);
            lazyTreeNode.setExpanded(false);
            lazyTreeNode.setLeaf(false);
        }

        return root;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        setSelectedNode((LazyTreeNode) event.getTreeNode());
    }

    public void onNodeExpand(NodeExpandEvent event) {

        LazyTreeNode parentNode = (LazyTreeNode) event.getTreeNode();

        if (ResourceTreeType.SYSTEM.toString().equals(parentNode.getType())) {
            SystemModule system = (SystemModule) parentNode.getData();
            List<Folder> folders = folderService
                    .findFoldersBySystemAlias(new FolderQueryCriteria(system.getAlias()));

            for (Folder folder : folders) {
                LazyTreeNode subNode = new LazyTreeNode(ResourceTreeType.FOLDER.toString(), folder, parentNode);
                subNode.setExpanded(false);
                subNode.setLeaf(false);
            }

        } else if (ResourceTreeType.FOLDER.toString().equals(parentNode.getType())) {
            Folder folder = (Folder) parentNode.getData();
            List<Link> links = linkService.findLinksByFolderId(folder.getId());

            for (Link link : links) {
                LazyTreeNode subNode = new LazyTreeNode(ResourceTreeType.LINK.toString(), link, parentNode);
                subNode.setExpanded(false);
                subNode.setLeaf(true);
            }
        }
    }

    public void onNoCollapse(NodeCollapseEvent event) {
        LazyTreeNode parentNode = (LazyTreeNode) event.getTreeNode();
        parentNode.getChildren().clear();
        parentNode.setLeaf(false);
    }

    public void setRoot(LazyTreeNode root) {
        this.root = root;
    }

    public LazyTreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(LazyTreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

}
