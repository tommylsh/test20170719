package com.maxim.pos.common.web.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;

public class FilterSecurityInterceptor extends AbstractSecurityInterceptor implements Filter {

	public static final Logger logger = LoggerFactory.getLogger(AccessDecisionManagerImpl.class);

	private FilterInvocationSecurityMetadataSource securityMetadataSource;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String uri = httpServletRequest.getRequestURI();
		
		logger.info("Access page[{}] Start", uri);

		if (matchesUrl(uri)) {
			try {
				UserDetailsService.getUser();
			} catch (Exception e) {
				logger.error("access to url[{}] failed, the reason is not login!~", uri);
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
				httpServletResponse.sendRedirect(httpServletRequest.getContextPath());
				return;
			}
		}

		FilterInvocation fi = new FilterInvocation(request, response, chain);
		invoke(fi);

//		LoggerHelper.logInfo(logger, "Access page[%s] End", uri);

	}

	public static boolean matchesUrl(String url) {
		for (String subffix : SecurityMetadataSourceServiceImpl.pageSuffixes.split(",")) {
			return url.matches("^/\\S*(" + subffix + "){1}\\S*");
		}

		return false;
	}

	@Override
	public Class<? extends Object> getSecureObjectClass() {
		return FilterInvocation.class;
	}

	@Override
	public SecurityMetadataSource obtainSecurityMetadataSource() {
		return this.securityMetadataSource;
	}

	@Override
	public void destroy() {
	}

	@Override
	public void init(FilterConfig filterconfig) throws ServletException {
	}

	public void invoke(FilterInvocation fi) throws IOException, ServletException {
		InterceptorStatusToken token = super.beforeInvocation(fi);
		try {
			fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
		} finally {
			super.afterInvocation(token, null);
		}
	}

	public void setSecurityMetadataSource(FilterInvocationSecurityMetadataSource securityMetadataSource) {
		this.securityMetadataSource = securityMetadataSource;
	}

}
