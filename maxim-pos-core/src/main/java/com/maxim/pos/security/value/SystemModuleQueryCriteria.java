package com.maxim.pos.security.value;

import com.maxim.pos.common.value.CommonCriteria;

public class SystemModuleQueryCriteria extends CommonCriteria {

    private static final long serialVersionUID = 4270498039118975099L;

    private String systemAlias;

    public SystemModuleQueryCriteria() {
    }

    public SystemModuleQueryCriteria(String systemAlias) {
        this.systemAlias = systemAlias;
    }

    public String getSystemAlias() {
        return systemAlias;
    }

    public void setSystemAlias(String systemAlias) {
        this.systemAlias = systemAlias;
    }

}
