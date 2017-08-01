package com.maxim.pos.common.web.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.maxim.i18n.MessageSource;
import com.maxim.pos.security.entity.Permission;
import com.maxim.pos.security.entity.Role;
import com.maxim.pos.security.entity.User;
import com.maxim.pos.security.service.PermissionService;
import com.maxim.pos.security.service.UserService;
import com.maxim.pos.security.value.PermissionQueryCriteria;
import com.maxim.user.Principal;
import com.maxim.util.LoggerHelper;

@Service("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    public static final Logger logger = LoggerFactory.getLogger(UserDetailsService.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Value("${defaultSystemModule.alias}")
    private String systemAlias;

    @Autowired(required = false)
    private MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        LoggerHelper.logInfo(logger, "Loading user with username[%s]", userId);

        User user = userService.findUserDetailByUserId(userId);

        if (user == null) {
            throw new BadCredentialsException(
                    messageSource.getMessage("security.userNotExist", new Object[] { userId }));
        }

        PermissionQueryCriteria permissionCriteria = new PermissionQueryCriteria(systemAlias);
        permissionCriteria.setNotAdminUser(!user.isAdmin());
        permissionCriteria.setUserId(userId);
        List<Permission> permissions = permissionService.findAllPermissionsByUserId(permissionCriteria);

        HashSet<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        for (Permission permission : permissions) {
            authorities.add(new UserGrantedAuthority(permission.getAlias(), permission.isEnabled()));
        }

        LoggerHelper.logError(logger, "User[%s] -> Authorities[%s]", user.getUserId(), authorities);

        UserDetails userDetails = new UserDetails(user, authorities);
        return userDetails;
    }

    public static UserDetails getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetail = null;
        if (authentication != null) {
            if ((authentication.getPrincipal() instanceof UserDetails)) {
                userDetail = (UserDetails) authentication.getPrincipal();
            }
        }

        if (userDetail == null) {
            throw new BadCredentialsException("User not login!");
        }

        return userDetail;
    }

    public static Principal getUser() {
        return getUserDetails().getUser();
    }

    public static String getUserName() {
        if (getUserDetails() != null) {
            return getUserDetails().getUser().getUserName();
        } else {
            throw new BadCredentialsException("User not login!");
        }
    }

    public static String getUserId() {
        if (getUserDetails() != null) {
            return getUserDetails().getUser().getUserId();
        } else {
            throw new BadCredentialsException("User not login!");
        }
    }

    public boolean isGrantedAuthority(String code) {
        if (getUserDetails() == null) {
            return false;
        }

        Boolean isGrantedAuthority = false;

        boolean isSuperAdmin = getUserDetails().getUser().isAdmin();
        if (isSuperAdmin) {
            isGrantedAuthority = true;
        } else {
            UserDetails userDetails = getUserDetails();
            Collection<GrantedAuthority> authorities = userDetails.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                if (grantedAuthority.getAuthority().equals(code)) {
                    isGrantedAuthority = true;
                    break;
                }
            }
        }

        LoggerHelper.logInfo(logger, "User[%s] isGrantedAuthority[%s]: %s", getUserDetails().getUser().getUserId(),
                code, isGrantedAuthority);

        return isGrantedAuthority;
    }

    public boolean hasAnyRole(String alias) {
        if (getUserDetails() == null) {
            return false;
        }

        boolean hasAnyRole = false;

        User user = (User) getUserDetails().getUser();

        boolean isSuperAdmin = user.isAdmin();
        if (isSuperAdmin) {
            hasAnyRole = true;
        } else {
            for (Role role : user.getRoles()) {
                if (role.getAlias().equals(alias)) {
                    hasAnyRole = true;
                    break;
                }
            }
        }

        LoggerHelper.logInfo(logger, "User[%s] hasAnyRole[%s]: %s", user.getUserId(), alias, hasAnyRole);

        return hasAnyRole;
    }

}
