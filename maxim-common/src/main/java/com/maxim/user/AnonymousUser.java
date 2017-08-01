/*
 * Created on Jun 17, 2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.maxim.user;

import java.util.Collections;

import javax.xml.bind.annotation.XmlRootElement;

import com.maxim.data.UserStatus;

/**
 * Anonymous DTO
 */
@XmlRootElement
public final class AnonymousUser extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = 130626;

	public AnonymousUser() {
		super();
		setUserId("ANONYM");
		setUserName("Anonymous User");
		setDept("DV");
		setStatus(UserStatus.ACTIVE);
		setRoles(Collections.singletonList(new Role()));
	}
}
