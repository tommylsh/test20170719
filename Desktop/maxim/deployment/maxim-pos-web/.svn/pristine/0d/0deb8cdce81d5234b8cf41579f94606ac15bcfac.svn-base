package com.maxim.pos.security.web.faces.support;

import java.io.Serializable;
import java.util.List;

import org.primefaces.model.TreeNode;
import org.springframework.context.ApplicationContext;

import com.maxim.pos.common.web.faces.controller.CommonController;
import com.maxim.pos.security.entity.Folder;
import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.entity.SystemModule;
import com.maxim.pos.security.service.FolderService;
import com.maxim.pos.security.service.LinkService;
import com.maxim.pos.security.value.FolderQueryCriteria;
import com.maxim.pos.security.web.faces.controller.SystemTreeController.ResourceTreeType;
import com.maxim.web.faces.model.LazyTreeNode;

public class ResourceTreeHolder implements Serializable {

    private static final long serialVersionUID = -3013677863217284896L;

    protected FolderService folderService;
    protected LinkService linkService;
    protected CommonController commonController;

    private LazyTreeNode root;
    private LazyTreeNode selectedNode;
    private TreeNode[] selectedNodes;
    private List<Folder> selectedFolders;
    private List<Link> selectedLinks;

    public ResourceTreeHolder(ApplicationContext context) {
        folderService = (FolderService) context.getBean(FolderService.class);
        linkService = (LinkService) context.getBean(LinkService.class);
        commonController = (CommonController) context.getBean(CommonController.class);
    }

    public LazyTreeNode getRoot() {
        if (root == null) {

            root = LazyTreeNode.createRoot();
            root.setExpanded(true);
            SystemModule defaultSystemModule = commonController.getDefaultSystemModule();
            LazyTreeNode lazyTreeNode = new LazyTreeNode(ResourceTreeType.SYSTEM.toString(), defaultSystemModule, root);
            lazyTreeNode.setExpanded(true);
            lazyTreeNode.setLeaf(false);

            List<Folder> folders = folderService
                    .findFoldersBySystemAlias(new FolderQueryCriteria(defaultSystemModule.getAlias()));

            for (Folder folder : folders) {
                LazyTreeNode folderNode = new LazyTreeNode(ResourceTreeType.FOLDER.toString(), folder, lazyTreeNode);
                folderNode.setExpanded(true);
                folderNode.setLeaf(false);
                if (selectedFolders != null && selectedFolders.contains(folder)) {
                    folderNode.setSelected(true);
                }

                List<Link> links = linkService.findLinksByFolderId(folder.getId());
                for (Link link : links) {
                    LazyTreeNode linkNode = new LazyTreeNode(ResourceTreeType.LINK.toString(), link, folderNode);
                    linkNode.setExpanded(false);
                    linkNode.setLeaf(true);

                    if (selectedLinks != null && selectedLinks.contains(link)) {
                        linkNode.setSelected(true);
                    }
                }
            }
        }

        return root;
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

    public TreeNode[] getSelectedNodes() {
        return selectedNodes;
    }

    public void setSelectedNodes(TreeNode[] selectedNodes) {
        this.selectedNodes = selectedNodes;
    }

    public List<Folder> getSelectedFolders() {
        return selectedFolders;
    }

    public void setSelectedFolders(List<Folder> selectedFolders) {
        this.selectedFolders = selectedFolders;
    }

    public List<Link> getSelectedLinks() {
        return selectedLinks;
    }

    public void setSelectedLinks(List<Link> selectedLinks) {
        this.selectedLinks = selectedLinks;
    }

}
