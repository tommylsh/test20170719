package com.maxim.util;

import com.maxim.user.User;

/**
 * Provide static method for accessing Thread local variable User
 * 
 * @author SPISTEV
 */
public class UserContext {

	private static final ThreadLocal<User> userContext = new ThreadLocal<User>();

	public static void setUser(User user) {
		userContext.set(user);
	}

	public static User getUser() {
		return userContext.get();
	}
}
