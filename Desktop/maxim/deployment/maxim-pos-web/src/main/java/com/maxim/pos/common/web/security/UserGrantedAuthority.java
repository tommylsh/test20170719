package com.maxim.pos.common.web.security;

import org.springframework.security.core.GrantedAuthority;

public class UserGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = 1047708982655459642L;

    private String permission;
    private boolean enabled;

    public UserGrantedAuthority(String permission, boolean enabled) {
        this.permission = permission;
        this.enabled = enabled;
    }

    @Override
    public String getAuthority() {
        return permission;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof UserGrantedAuthority) {
            return permission.equals(((UserGrantedAuthority) obj).permission);
        }

        return false;
    }

    public int hashCode() {
        return this.permission.hashCode();
    }

    public String toString() {
        return this.permission;
    }
}
