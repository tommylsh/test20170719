package com.maxim.ws;

import java.io.IOException;

import com.maxim.user.User;
import com.google.gson.Gson;

/**
 * 
 * @author Steven
 *
 */
public class WebServiceUtil {

	private Gson gson;
	
	private static WebServiceUtil ptr;

	private WebServiceUtil() {
		gson = new Gson();
	}

	public static synchronized WebServiceUtil getInstance() {
		if (ptr == null) {
			ptr = new WebServiceUtil();
		}
		return ptr;
	}

	
	public String getUserInJson(User user) throws IOException {		
		return gson.toJson(user);
	}
}
