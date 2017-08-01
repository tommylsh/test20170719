/*
 * Created on Jun 17, 2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maxim.user;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import com.maxim.data.UserStatus;

/**
 * User as the System itself
 */
@XmlRootElement
public final class SystemUser extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130626;

	public SystemUser() {
		super();
		setUserId("SYSTEM");
		setUserName("System");
		setDept("DV");
		setStatus(UserStatus.ACTIVE);
		setRoles(new ArrayList<Role>());
	}
}
