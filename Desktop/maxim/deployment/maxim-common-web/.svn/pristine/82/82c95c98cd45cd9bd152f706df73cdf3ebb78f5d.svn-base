package com.maxim.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.maxim.util.SysParamsConstant;

public class SysParamsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        Map<String, String> sysParams = SysParamsConstant.getParams();
        if (sysParams != null && !sysParams.isEmpty()) {
            for (Map.Entry<String, String> entry : sysParams.entrySet()) {
                request.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
