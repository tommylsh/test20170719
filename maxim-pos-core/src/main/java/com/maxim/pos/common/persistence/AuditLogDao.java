package com.maxim.pos.common.persistence;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maxim.dao.HibernateDAO;

@Repository("auditLogDao")
@Transactional
public class AuditLogDao extends HibernateDAO {

    public static final String HQL_findAuditLogByCriteria = "findAuditLogByCriteria";

}
