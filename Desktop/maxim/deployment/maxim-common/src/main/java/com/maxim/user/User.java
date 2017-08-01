/*
 * Created on Jun 17, 2015
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maxim.user;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

import com.maxim.data.DTO;
import com.maxim.data.UserStatus;

/**
 * User DTO
 */
@XmlRootElement
public class User implements DTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130626;

	/** User ID*/
	private String userId;

	/** User name */
	private String userName;

	/** Department the user belongs to */
	private String dept;

	/** Status of the user account */
	private UserStatus status;

	/** Roles of the user by sysCode */
	private Collection<Role> roles;

	public User() {
		super();
	}

	public User(String userId, String userName, String dept, UserStatus status,
			Collection<Role> roles) {
		super();
		this.userId = userId;
		this.userName = userName;
		this.dept = dept;
		this.status = status;

		if (roles == null)
			this.roles = Collections.emptyList();
		else
			this.roles = Collections.unmodifiableCollection(roles);
	}

	public String getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getDept() {
		return dept;
	}

	public UserStatus getStatus() {
		return status;
	}

	public Collection<Role> getRoles() {
		return roles;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}

	public Role getRole(String sysCode){

		if (roles == null) {
			return null;
		}

		for (Role role : roles) {
			if (sysCode.equals(role.getSysCode())) {
				return role;
			}
		}
		return null;
	}
}
