package com.maxim.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.maxim.AbstractService;
import com.maxim.audittrail.AuditTrailStatus;
import com.maxim.audittrail.DummyAuditTrailServiceDelegate;
import com.maxim.audittrail.IAuditTrailServiceDelegate;
import com.maxim.data.Query;
import com.maxim.exception.UnknownUserException;
import com.maxim.user.AnonymousUser;
import com.maxim.user.User;

/**
 * Aspect for service to perform standard routine - Put User into UserContext
 * thread local - Perform JSR-303 checking
 * 
 * @author SPISTEV
 * 
 */
public abstract class ServiceAspect {

	@Autowired(required = false)
	private IAuditTrailServiceDelegate auditTrailServiceDelegate = new DummyAuditTrailServiceDelegate();

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ServiceAspect.class);

	private static AnonymousUser anonymousUser = new AnonymousUser();

	protected abstract void anonymousServicePointcut(AbstractService service, ServiceMethod serviceMethod, Query query);

	protected abstract void servicePointcut(AbstractService service, ServiceMethod serviceMethod, Query query, User user);

	protected abstract Object anonymousServiceMethod(ProceedingJoinPoint joinPoint,
			AbstractService service, ServiceMethod serviceMethod, Query query) throws Throwable ;

	protected abstract Object serviceMethod(ProceedingJoinPoint joinPoint,
			AbstractService service, ServiceMethod serviceMethod, Query query,
			User user) throws Throwable ;
	
	protected Object doService(ProceedingJoinPoint joinPoint,
			AbstractService service, ServiceMethod serviceMethod, Query query,
			User user) throws Throwable {

		// User not found
		if (user == null) {
			throw new UnknownUserException();
		}

		// put user into new thread
		UserContext.setUser(user);

		return serve(joinPoint, service, serviceMethod, query, user);
	}

	protected Object doAnonymousService(ProceedingJoinPoint joinPoint,
			AbstractService service, ServiceMethod serviceMethod, Query query) throws Throwable {

		return serve(joinPoint, service, serviceMethod, query, anonymousUser);
	}

	private Object serve(ProceedingJoinPoint joinPoint,
			AbstractService service, ServiceMethod serviceMethod, Query query,
			User user) throws Throwable {

		String action = joinPoint.getSignature().toShortString();

		// Logging
		if (serviceMethod.log()) {
			LOGGER.info("Attempt to invoke: " + action + ", User: "
					+ user.getUserId() + ", Query: " + query);
		}

		// Audit Trail
		Long auditTrailId = null;
		if (serviceMethod.auditTrail()) {
			auditTrailId = auditTrailServiceDelegate.createAuditTrail(
					service.getSysId(), user, action, query);
		}

		// Validate query, this may throw ValidationException
		validate(service, query);

		Object retVal;
		try {
			retVal = joinPoint.proceed();
		} catch (Throwable e) {
			if (serviceMethod.auditTrail()) {
				auditTrailServiceDelegate.updateAuditTrail(auditTrailId, user,
						AuditTrailStatus.FAILED, e.getMessage());
			}
			// re-throw exception
			throw e;
		}

		// update audit trail status
		if (serviceMethod.auditTrail()) {
			auditTrailServiceDelegate.updateAuditTrail(auditTrailId, user,
					AuditTrailStatus.SUCCESS);
		}

		// return the object by the target method
		return retVal;
	}

	private void validate(AbstractService service, Query query) {
		service.validate(query);
	}

	public void setAuditTrailServiceDelegate(
			IAuditTrailServiceDelegate auditTrailServiceDelegate) {
		this.auditTrailServiceDelegate = auditTrailServiceDelegate;
	}

}
