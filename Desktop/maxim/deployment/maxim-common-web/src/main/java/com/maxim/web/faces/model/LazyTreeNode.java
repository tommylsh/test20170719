package com.maxim.web.faces.model;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

public class LazyTreeNode extends DefaultTreeNode {

	private static final long serialVersionUID = -143530797580191916L;
	
	private Boolean leaf = null;

	public LazyTreeNode() {
	}

	public LazyTreeNode(Object data, TreeNode parent) {
		super(data, parent);
	}

	public LazyTreeNode(String type, Object data, TreeNode parent) {
		super(type, data, parent);
	}

	public static LazyTreeNode createRoot() {
		return new LazyTreeNode("root", null);
	}

	public void setLeaf(boolean leaf) {
		this.leaf = Boolean.valueOf(leaf);
	}

	public boolean isLeaf() {
		if (this.leaf != null) {
			return this.leaf.booleanValue();
		}
		return super.isLeaf();
	}

}