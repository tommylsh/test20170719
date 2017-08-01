package com.maxim.pos.common.value;

public class ApplicationSettingQueryCriteria extends CommonCriteria {

    private static final long serialVersionUID = 245426256955171897L;

    private String code;
    private String codeKeyword;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = emptyToNull(code);
    }

    public String getCodeKeyword() {
        return codeKeyword;
    }

    public void setCodeKeyword(String codeKeyword) {
        this.codeKeyword = emptyToNull(codeKeyword);
    }

}
