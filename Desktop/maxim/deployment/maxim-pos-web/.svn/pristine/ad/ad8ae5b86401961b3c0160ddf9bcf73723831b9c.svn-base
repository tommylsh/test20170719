package com.maxim.pos.common.web.security;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public static final String SPRING_SECURITY_LAST_USERNAME_KEY = "SPRING_SECURITY_LAST_USERNAME";
    public static final String SPRING_SECURITY_LAST_PASSWORD_KEY = "SPRING_SECURITY_LAST_PASSWORD";

    public AuthenticationFailureHandler() {
        setUseForward(true);
    }

    public void saveLastUsername(HttpServletRequest request, String username) {
        request.setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, username);
    }

    public void saveLastPassword(HttpServletRequest request, String password) {
        request.setAttribute(SPRING_SECURITY_LAST_PASSWORD_KEY, password);
    }

}
