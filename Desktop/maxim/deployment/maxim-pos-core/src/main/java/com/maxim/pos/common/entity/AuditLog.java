package com.maxim.pos.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.security.enumeration.AuditAction;

@Entity
@Table(name = "POLL_AUDIT_LOG")
public class AuditLog extends AbstractEntity {

    private static final long serialVersionUID = -3696073450083184774L;

    public AuditLog() {
    }

    public AuditLog(AuditAction action, String content, String createUser) {
        super(createUser);
        this.action = action;
        this.content = content;
    }

    private AuditAction action;

    private String content;

    @Override
    @Id
    @Column(name = "CG_AUDIT_LOG_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "ACTION", length = 20, nullable = false)
    public AuditAction getAction() {
        return action;
    }

    public void setAction(AuditAction action) {
        this.action = action;
    }

    @Column(name = "CONTENT", length = 5000, nullable = false)
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
