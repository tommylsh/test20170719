package com.maxim.pos.security.value;

import com.maxim.pos.common.value.CommonCriteria;

public class PermissionQueryCriteria extends CommonCriteria {

    private static final long serialVersionUID = 1442148807162326540L;

    private String systemAlias;
    private String aliasKeyword;
    private String nameKeyword;

    public PermissionQueryCriteria() {
    }

    public PermissionQueryCriteria(String systemAlias) {
        this.systemAlias = systemAlias;
    }

    public String getSystemAlias() {
        return systemAlias;
    }

    public void setSystemAlias(String systemAlias) {
        this.systemAlias = systemAlias;
    }

    public String getAliasKeyword() {
        return aliasKeyword;
    }

    public void setAliasKeyword(String aliasKeyword) {
        this.aliasKeyword = emptyToNull(aliasKeyword);
    }

    public String getNameKeyword() {
        return nameKeyword;
    }

    public void setNameKeyword(String nameKeyword) {
        this.nameKeyword = emptyToNull(nameKeyword);
    }

}
