package com.maxim.pos.security.value;

import com.maxim.pos.common.value.CommonCriteria;

public class FolderQueryCriteria extends CommonCriteria {

    private static final long serialVersionUID = -8582401594839433371L;

    private String systemAlias;

    public FolderQueryCriteria() {
    }

    public FolderQueryCriteria(String systemAlias) {
        this.systemAlias = systemAlias;
    }

    public String getSystemAlias() {
        return systemAlias;
    }

    public void setSystemAlias(String systemAlias) {
        this.systemAlias = systemAlias;
    }

}
