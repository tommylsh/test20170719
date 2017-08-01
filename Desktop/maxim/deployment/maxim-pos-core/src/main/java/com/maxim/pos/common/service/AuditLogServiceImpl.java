package com.maxim.pos.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.pos.common.Auditer;
import com.maxim.pos.common.entity.AuditLog;
import com.maxim.pos.common.persistence.AuditLogDao;

@Service("auditLogService")
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogDao auditLogDao;

    @Override
    public AuditLog saveAuditLog(AuditLog auditLog) {
        Auditer.audit(auditLog);
        return (AuditLog) auditLogDao.save(auditLog);
    }

}
