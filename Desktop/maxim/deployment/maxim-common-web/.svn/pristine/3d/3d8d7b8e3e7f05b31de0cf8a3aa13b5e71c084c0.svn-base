package com.maxim.web.faces.utils;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class FacesUtils {

	public static FacesContext getInstance() {
		return FacesContext.getCurrentInstance();
	}

	public static String getContextPath() {
		return getInstance().getExternalContext().getRequestContextPath();
	}

	public static String getContext() {
		return getInstance().getExternalContext().getRequestContextPath()
				+ getInstance().getExternalContext().getContextName();
	}

	public static HttpServletRequest getRequest() {
		return (HttpServletRequest) getInstance().getExternalContext().getRequest();
	}

	public static HttpServletResponse getResponse() {
		return (HttpServletResponse) getInstance().getExternalContext().getResponse();
	}

    public static HttpSession getSession() {
        return getRequest().getSession(true);
    }

    public static Object getSessionScope(String name) {
        return getSession().getAttribute(name);
    }
    
    public static void putSessionScope(String name, Object value) {
        HttpSession session = getSession();
        session.setAttribute(name, value);
    }

    public static void validationFailed() {
    	getInstance().validationFailed();
    }
    
    
}
