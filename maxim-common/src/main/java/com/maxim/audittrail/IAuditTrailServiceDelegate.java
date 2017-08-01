package com.maxim.audittrail;

import java.io.Serializable;

import com.maxim.user.User;

/**
 * Audit Trail delegate for automating audit trail process
 * Actual implementation
 * 
 * @author SPISTEV
 */
public interface IAuditTrailServiceDelegate {

	/**
	 * Return audit trail id
	 * @return
	 */
	public Long createAuditTrail(String sysId, User user, String action, Serializable payload);
	
	public void updateAuditTrail(Long auditTrailId, User user, AuditTrailStatus status);
	
	public void updateAuditTrail(Long auditTrailId, User user, AuditTrailStatus status, String resultText);
}
