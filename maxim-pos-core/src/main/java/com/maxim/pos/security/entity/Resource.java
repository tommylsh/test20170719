package com.maxim.pos.security.entity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.maxim.entity.AbstractEntity;
import com.maxim.pos.security.enumeration.ResourceType;

@MappedSuperclass
public abstract class Resource extends AbstractEntity implements Comparable<Resource> {

    private static final long serialVersionUID = -4250689273241726657L;

    private SystemModule systemModule;

    private ResourceType type;

    private String name;

    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SYSTEM_ID", nullable = false)
    public SystemModule getSystemModule() {
        return systemModule;
    }

    public void setSystemModule(SystemModule systemModule) {
        this.systemModule = systemModule;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 10, nullable = false)
    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    @Column(name = "NAME", length = 100, unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @org.hibernate.annotations.Type(type = "org.hibernate.type.NumericBooleanType")
    @Column(name = "ENABLED", nullable = false, columnDefinition = "TINYINT", length = 1)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int compareTo(Resource o) {
        if (ResourceType.FOLDER.equals(getType()) && ResourceType.LINK.equals(o.getType())) {
            return -1;
        } else if (ResourceType.LINK.equals(getType()) && ResourceType.FOLDER.equals(o.getType())) {
            return 1;
        } else if (ResourceType.FOLDER.equals(getType()) && ResourceType.FOLDER.equals(o.getType())) {
            return getName().compareTo(o.getName());
        } else if (ResourceType.LINK.equals(getType()) && ResourceType.LINK.equals(o.getType())) {
            return getName().compareTo(o.getName());
        }
        return 0;
    }

}
