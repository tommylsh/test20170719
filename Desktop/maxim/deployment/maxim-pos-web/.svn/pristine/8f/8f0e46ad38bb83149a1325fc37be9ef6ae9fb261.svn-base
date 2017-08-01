package com.maxim.pos.common.web.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import com.maxim.user.Principal;

public class UserDetails extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 356669796610983712L;

    private Principal user;

    private final Set<GrantedAuthority> authorities;

    public UserDetails(Principal user, Collection<? extends GrantedAuthority> authorities) {
        this(user.getUserId(), user.getPassword(), true, true, true, true, authorities);
        this.user = user;
    }

    public UserDetails(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.authorities = new HashSet<GrantedAuthority>(authorities);
    }

    public Principal getUser() {
        return user;
    }

    public void setUser(Principal user) {
        this.user = user;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

}
