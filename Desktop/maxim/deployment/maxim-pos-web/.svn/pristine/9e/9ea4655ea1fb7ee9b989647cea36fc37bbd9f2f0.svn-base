package com.maxim.pos.common.web.security;

import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.service.LinkService;
import com.maxim.user.Principal;
import com.maxim.util.LoggerHelper;

@Service("accessDecisionManager")
public class AccessDecisionManagerImpl implements AccessDecisionManager {

    public static final Logger logger = LoggerFactory.getLogger(AccessDecisionManagerImpl.class);

    @Autowired
    private LinkService linkService;

    @Value("${indexPageUrl}")
    private String indexPageUrl;

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {

        Object principal = authentication.getPrincipal();

        LoggerHelper.logInfo(logger, "[%s] Decide to access", principal);

        Principal user = UserDetailsService.getUserDetails().getUser();
        if (configAttributes != null) {
            Iterator<ConfigAttribute> iters = configAttributes.iterator();

            while (iters.hasNext()) {
                ConfigAttribute ca = iters.next();

                if (ca.getAttribute() != null) {
                    String attribute = ((SecurityConfig) ca).getAttribute();

                    // check the user whether has the permission to access the
                    // url
                    if (FilterSecurityInterceptor.matchesUrl(attribute)) {
                        
                        Link link = linkService.findLinkByUrl(attribute);
                        
                        if (link.isEnabled()) {
                            if (user.isAdmin()) {
                                return;
                            }
                            
                            Long linkCountByUserIdAndUrl = linkService.getLinkCountByUserIdAndUrl(user.getUserId(),
                                    attribute);
                            logger.info("linkCountByUserIdAndUrl: {}, userId: {}, url: {} ", linkCountByUserIdAndUrl,
                                    user.getUserId(), attribute);
                            if (linkCountByUserIdAndUrl > 0) {
                                return;
                            }
                        }
                        
                    } else { // check the user whether has the permission to do
                             // the operation

                        logger.info("access: {}, userId: {}", attribute, user.getUserId());
                        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

                        for (GrantedAuthority authority : authorities) {
                            
                            // check permission is enabled
                            boolean enabled = ((UserGrantedAuthority) authority).isEnabled();
                            if (enabled) {
                                if (authorities.contains((new UserGrantedAuthority(ca.getAttribute(), enabled)))) {
                                    return;
                                }
                            }
                        }

                    }
                }
            }

            throw new AccessDeniedException("No permission, Access Denied!");
        }
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

}
