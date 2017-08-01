package com.maxim.pos.security.value;

import com.maxim.pos.common.value.CommonCriteria;

public class RoleQueryCriteria extends CommonCriteria {

    private static final long serialVersionUID = 2209649725205067532L;

    private String systemAlias;
    private String aliasKeyword;
    private String nameKeyword;

    public RoleQueryCriteria() {
    }

    public RoleQueryCriteria(String systemAlias) {
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
