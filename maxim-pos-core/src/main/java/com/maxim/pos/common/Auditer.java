package com.maxim.pos.common;

import java.util.Date;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.security.entity.User;
import com.maxim.user.Principal;

public class Auditer {

	public static final String SYSTEM_RESERVED_USER_ID = "MAX_POS_SYSTEM";
	
	public static User systemPrincipal; 
	public static void setSystemPrincipal(User systemPrincipal)
	{
		Auditer.systemPrincipal = systemPrincipal ;
	}

	public static void audit(AbstractEntity auditable) {
	    audit(auditable, null);
    }
	
    public static void audit(AbstractEntity auditable, Principal principal) {
		if (auditable.getId() == null) {
			onCreate(auditable, principal);
		} else {
			onUpdate(auditable, principal);
		}
	}

	public static void onCreate(AbstractEntity auditable, Principal principal) {
		if (auditable.getId() == null) {
			if (auditable.getCreateUser() == null) {
				auditable.setCreateUser(principal != null ? principal.getUserId() : 
					systemPrincipal != null ? systemPrincipal.getUserId() : 
						SYSTEM_RESERVED_USER_ID);
			}
			if (auditable.getCreateTime() == null) {
			    auditable.setCreateTime(new Date());
			}
			onUpdate(auditable, principal);
		}
	}

	public static void onUpdate(AbstractEntity auditable, Principal principal) {
		auditable.setLastUpdateUser(principal != null ? principal.getUserId() : 
			systemPrincipal != null ? systemPrincipal.getUserId() : 
				SYSTEM_RESERVED_USER_ID);
		auditable.setLastUpdateTime(new Date());
	}
	
	public static User getSystemUser() {
		if (systemPrincipal != null)
			return systemPrincipal ;
		
        User user = new User();
        user.setUserId(SYSTEM_RESERVED_USER_ID);
        user.setUserName(SYSTEM_RESERVED_USER_ID);
        return user;
    }

}
