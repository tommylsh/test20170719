package com.maxim.pos.security.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.maxim.entity.AbstractEntity;

@Entity
@Table(name = "SECURITY_PERMISSION")
public class Permission extends AbstractEntity implements Comparable<Permission> {

    private static final long serialVersionUID = -5814109741802610286L;

    private SystemModule systemModule;

    private String name;

    private String alias;

    private boolean enabled = true;

    private String description;

    public Permission() {
    }

    public Permission(SystemModule system) {
        this.systemModule = system;
    }

    @Override
    @Id
    @Column(name = "SECURITY_PERMISSION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SYSTEM_ID")
    public SystemModule getSystemModule() {
        return systemModule;
    }

    public void setSystemModule(SystemModule systemModule) {
        this.systemModule = systemModule;
    }

    @Column(name = "NAME", length = 100, unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "ALIAS", length = 100, unique = true, nullable = false)
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "ENABLED", nullable = false, columnDefinition = "TINYINT", length = 1)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(name = "DESCRIPTION", length = 500)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(Permission o) {
        return name.compareTo(o.name);
    }

}
