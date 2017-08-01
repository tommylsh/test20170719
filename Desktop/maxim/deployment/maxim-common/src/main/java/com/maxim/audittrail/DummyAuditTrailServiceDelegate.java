package com.maxim.audittrail;

import java.io.Serializable;

import com.maxim.user.User;

/**
 * Dummy Audit Trail
 * 
 * @author SPISTEV
 */
public class DummyAuditTrailServiceDelegate implements IAuditTrailServiceDelegate {

	public Long createAuditTrail(String sysId, User user, String action, Serializable payload) {
		return new Long(1);
	}
	
	public void updateAuditTrail(Long auditTrailId, User user, AuditTrailStatus status, String resultText) {
		return;
	}
	
	public void updateAuditTrail(Long auditTrailId, User user, AuditTrailStatus status) {
		return;
	}

}
