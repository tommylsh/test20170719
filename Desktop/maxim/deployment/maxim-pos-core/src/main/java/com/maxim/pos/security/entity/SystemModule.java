package com.maxim.pos.security.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.maxim.entity.AbstractEntity;

@Entity
@Table(name = "SECURITY_SYSTEM")
public class SystemModule extends AbstractEntity {

	private static final long serialVersionUID = 475845073192336170L;

	private String name;

	private String alias;

	private boolean enabled = true;

	private String description;

	@Override
	@Id
	@Column(name = "SECURITY_SYSTEM_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long getId() {
		return super.getId();
	}

	@Column(name = "NAME", unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "ALIAS", unique = true, nullable = false)
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

}
