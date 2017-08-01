package com.maxim.pos.security.entity;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;

import com.maxim.entity.AbstractEntity;

@Entity
@Table(name = "SECURITY_ROLE")
public class Role extends AbstractEntity implements Comparable<Role> {

	private static final long serialVersionUID = 1240343203163308741L;

	private SystemModule systemModule;

	private String name;

	private String alias;

	private SortedSet<Permission> permissions = new TreeSet<Permission>();

	private SortedSet<Folder> folders = new TreeSet<Folder>();

	private SortedSet<Link> links = new TreeSet<Link>();

	private boolean enabled = true;

	private String description;

	public Role() {

	}

	public Role(SystemModule system) {
		this.systemModule = system;
	}

	@Override
	@Id
	@Column(name = "SECURITY_ROLE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "SYSTEM_ID")
	public SystemModule getSystemModule() {
		return systemModule;
	}

	public void setSystemModule(SystemModule systemModule) {
		this.systemModule = systemModule;
	}

	@Column(name = "NAME", length = 100, unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ALIAS", length = 100, unique = true, nullable = false)
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinTable(name = "SECURITY_ROLE_PERMISSIONS", joinColumns = {
			@JoinColumn(name = "ROLE_ID", referencedColumnName = "SECURITY_ROLE_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "PERMISSION_ID", referencedColumnName = "SECURITY_PERMISSION_ID") })
	@Sort(type = SortType.NATURAL)
	public SortedSet<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(SortedSet<Permission> permissions) {
		this.permissions = permissions;
	}

	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinTable(name = "SECURITY_ROLE_FOLDERS", joinColumns = {
			@JoinColumn(name = "ROLE_ID", referencedColumnName = "SECURITY_ROLE_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "FOLDER_ID", referencedColumnName = "SECURITY_FOLDER_ID") })
	@Sort(type = SortType.NATURAL)
	public SortedSet<Folder> getFolders() {
		return folders;
	}

	public void setFolders(SortedSet<Folder> folders) {
		this.folders = folders;
	}

	@ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
	@JoinTable(name = "SECURITY_ROLE_LINKS", joinColumns = {
			@JoinColumn(name = "ROLE_ID", referencedColumnName = "SECURITY_ROLE_ID") }, inverseJoinColumns = {
					@JoinColumn(name = "LINK_ID", referencedColumnName = "SECURITY_LINK_ID") })
	@Sort(type = SortType.NATURAL)
	public SortedSet<Link> getLinks() {
		return links;
	}

	public void setLinks(SortedSet<Link> links) {
		this.links = links;
	}

	@org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "ENABLED", nullable = false, columnDefinition = "TINYINT", length = 1)
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "DESCRIPTION", length = 500)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(Role o) {
		return name.compareTo(o.name);
	}

}
