package com.maxim.security;

import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.JoinPoint;

import com.maxim.AbstractService;
import com.maxim.data.Query;
import com.maxim.exception.PrivilegeException;
import com.maxim.exception.UnknownUserException;
import com.maxim.user.Role;
import com.maxim.user.User;

/**
 * Aspect for security checking
 * 
 * @author CPPPAA
 * 
 */
public abstract class SecurityAspect {

	protected abstract String getSysCode();

	protected abstract void securityPointcut(AbstractService service,
			RequiredRoles requiredRoles, Query query, User user);

	protected abstract void securityCheck(JoinPoint joinPoint,
			AbstractService service, RequiredRoles requiredRoles, Query query,
			User user) throws Throwable;

	protected void doSecurityCheck(JoinPoint joinPoint,
			AbstractService service, RequiredRoles requiredRoles, Query query,
			User user) throws Throwable {

		// User not found
		if (user == null) {
			throw new UnknownUserException();
		}

		List<String> roles = Arrays.asList(requiredRoles.roles());
		Role role = user.getRole(getSysCode());

		// No role or no required role
		if (role == null || !roles.contains(role.getRole())) {
			throw new PrivilegeException(user.getUserId(), joinPoint
					.getSignature().toShortString());
		}
	}
}
