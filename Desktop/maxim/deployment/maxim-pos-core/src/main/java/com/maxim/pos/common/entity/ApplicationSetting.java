package com.maxim.pos.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.maxim.entity.AbstractEntity;

@Entity
@Table(name = "POLL_APPLICATION_SETTING", uniqueConstraints = { @UniqueConstraint(columnNames = { "CODE" }) })
public class ApplicationSetting extends AbstractEntity {

    private static final long serialVersionUID = -1421766392667274138L;

    private String code;

    private String codeValue;

    private String codeDescription;

    @Override
    @Id
    @Column(name = "POLL_APPLICATION_SETTING_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return super.getId();
    }

    @Column(name = "CODE", length = 100)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "CODE_VALUE", length = 500)
    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    @Column(name = "CODE_DESCRIPTION")
    public String getCodeDescription() {
        return codeDescription;
    }

    public void setCodeDescription(String codeDescription) {
        this.codeDescription = codeDescription;
    }

}
