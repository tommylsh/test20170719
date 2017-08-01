package com.maxim.pos.common.web.security;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Service;

import com.maxim.pos.security.entity.Link;
import com.maxim.pos.security.service.LinkService;
import com.maxim.util.LoggerHelper;

@Service("securityMetadataSource")
public class SecurityMetadataSourceServiceImpl implements FilterInvocationSecurityMetadataSource {

    public static final Logger logger = LoggerFactory.getLogger(SecurityMetadataSourceServiceImpl.class);

    @Autowired
    private LinkService linkService;
    
    public static String pageSuffixes = ".faces";

    /**
     * if return null, then without any auth check!!!
     * so the request url which exists in system should be validate 
     */
    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) {

        FilterInvocation invocation = (FilterInvocation) object;
		String requestUrl = invocation.getRequestUrl();
		
        if (!matches(requestUrl)) {
            return null;
        }
        
        LoggerHelper.logInfo(logger, "Access url: %s", requestUrl);
        
		Link link = linkService.findLinkByUrl(requestUrl);
		
        if (link != null) {
            Collection<ConfigAttribute> attributes = new ArrayList<ConfigAttribute>();
            attributes.add(new SecurityConfig(link.getUrl()));
            
            LoggerHelper.logInfo(logger, "Found url as: %s", attributes);
            
            return attributes;
        } else {
        	LoggerHelper.logInfo(logger, "Could not find url as: %s", requestUrl);
        }
        
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    public boolean matches(String url) {
        for (String subffix : pageSuffixes.split(",")) {
            return url.matches("^/\\S*(" + subffix + "){1}\\S*");
        }

        return false;
    }

}
