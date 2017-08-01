package com.maxim.pos.security.value;

import com.maxim.pos.common.value.CommonCriteria;

public class UserQueryCriteria extends CommonCriteria {

    private static final long serialVersionUID = 2677009189597417639L;

    private String userIdKeyword;
    private String userNameKeyword;
    private boolean joinRoles;

    public String getUserIdKeyword() {
        return userIdKeyword;
    }

    public void setUserIdKeyword(String userIdKeyword) {
        this.userIdKeyword = emptyToNull(userIdKeyword);
    }

    public String getUserNameKeyword() {
        return userNameKeyword;
    }

    public void setUserNameKeyword(String userNameKeyword) {
        this.userNameKeyword = emptyToNull(userNameKeyword);
    }

    public boolean isJoinRoles() {
        return joinRoles;
    }

    public void setJoinRoles(boolean joinRoles) {
        this.joinRoles = joinRoles;
    }

}
