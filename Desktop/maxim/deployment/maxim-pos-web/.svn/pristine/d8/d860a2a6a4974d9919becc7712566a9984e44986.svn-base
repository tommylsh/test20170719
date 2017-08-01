package com.maxim.pos.common.web.spring.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/secure")
public class LoginController {

    public static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private static final String LOGOUT_MSG = "logoutMsg";

    public LoginController() {
        logger.info("initializing LoginController...");
    }

    @RequestMapping("/login")
    public String login(Model model) {
        return "secure/login";
    }

    @RequestMapping("/logout")
    public String logout(Model model, HttpServletRequest request, HttpSession session) {
        SecurityContextImpl securityContextImpl = (SecurityContextImpl)session.getAttribute("SPRING_SECURITY_CONTEXT");
        if (securityContextImpl != null) {
            logger.info("{} logout...", securityContextImpl.getAuthentication().getName());
        }
        
        session.removeAttribute("SPRING_SECURITY_CONTEXT");
        session.invalidate();
        
        if (request.getParameter(LOGOUT_MSG) != null) {
            model.addAttribute(LOGOUT_MSG, request.getParameter(LOGOUT_MSG));
        }
        return "secure/logout";
    }

    @RequestMapping("/accessDenied")
    public String accessDenied(Model model) {
        return "secure/accessDenied";
    }

    @RequestMapping("/sessionExpired")
    public String sessionExpired(Model model) {
        return "secure/sessionExpired";
    }

    @RequestMapping("/timeout")
    public String timeout(Model model) {
        return "secure/timeout";
    }

}
