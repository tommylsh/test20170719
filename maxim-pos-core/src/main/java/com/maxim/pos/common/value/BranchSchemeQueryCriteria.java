package com.maxim.pos.common.value;

public class BranchSchemeQueryCriteria extends CommonCriteria {

    private String branchCode;
    private String branchCodeKeyword;

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchCodeKeyword() {
        return branchCodeKeyword;
    }

    public void setBranchCodeKeyword(String branchCodeKeyword) {
        this.branchCodeKeyword = branchCodeKeyword;
    }
}
