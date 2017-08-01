package com.maxim.pos.security.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.maxim.pos.security.enumeration.ResourceType;

@Entity
@Table(name = "SECURITY_LINK")
public class Link extends Resource {

	private static final long serialVersionUID = -4982181598017907996L;

	private Folder folder;

	private int index;

	private String url;

	public Link() {
	}

	public Link(Folder folder) {
		setFolder(folder);
		setSystemModule(getFolder().getSystemModule());
		setType(ResourceType.LINK);
		setIndex(getFolder().getLinks().size());
	}

	@Override
	@Id
	@Column(name = "SECURITY_LINK_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "FOLDER_ID", nullable = false)
	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}

	@Column(name = "INDEX1", nullable = false)
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Column(name = "URL", length = 255)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
