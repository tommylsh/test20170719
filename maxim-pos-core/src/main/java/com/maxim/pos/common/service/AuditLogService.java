package com.maxim.pos.common.service;

import com.maxim.pos.common.entity.AuditLog;

public interface AuditLogService {

    public AuditLog saveAuditLog(AuditLog auditLog);
    
}
